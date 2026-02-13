package club.pisquad.minecraft.csgrenades.entity

import club.pisquad.minecraft.csgrenades.compat.tacz.TaczGunDataProvider
import club.pisquad.minecraft.csgrenades.constants.DecoyConstants
import club.pisquad.minecraft.csgrenades.entity.decoy.DecoyGunData
import club.pisquad.minecraft.csgrenades.enums.GrenadeType
import club.pisquad.minecraft.csgrenades.network.CsGrenadePacketHandler
import club.pisquad.minecraft.csgrenades.network.message.DecoyShotMessage
import club.pisquad.minecraft.csgrenades.registry.ModDamageType
import club.pisquad.minecraft.csgrenades.registry.ModItems
import club.pisquad.minecraft.csgrenades.simulation.DecoyFirePatternGenerator
import net.minecraft.core.registries.Registries
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraftforge.network.PacketDistributor

/**
 * 诱饵弹实体类
 * 核心功能：模拟枪械射击声音，误导敌人
 */
class DecoyGrenadeEntity(pEntityType: EntityType<out DecoyGrenadeEntity>, pLevel: Level) : CounterStrikeGrenadeEntity(pEntityType, pLevel, GrenadeType.DECOY_GRENADE) {

    // 射击时间戳列表，记录何时应该射击
    private var fireTimestamps: List<Int> = emptyList()

    // 下一次射击的时间戳索引
    private var nextFireTimestampIndex: Int = 0

    // 激活时的tick计数
    private var activationTick: Int? = null

    // 枪械数据
    private var gunData: DecoyGunData? = null

    // 自定义声音
    private var customSound: String? = null

    // 旋转冻结相关变量
    private var hasSavedFinalRotation = false
    private var finalXRot = 0f
    private var finalYRot = 0f
    private var finalZRot = 0f

    companion object {
        // 用于同步枪械ID到客户端的访问器
        val GUN_DATA_ACCESSOR: EntityDataAccessor<String> = SynchedEntityData.defineId(DecoyGrenadeEntity::class.java, EntityDataSerializers.STRING)
    }

    /**
     * 定义同步数据
     */
    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(GUN_DATA_ACCESSOR, "")
    }

    /**
     * 获取默认物品
     */
    override fun getDefaultItem(): Item = ModItems.DECOY_GRENADE_ITEM.get()

    /**
     * 实体每tick执行的逻辑
     * 核心运行机制：
     * 1. 未落地时：运行正常物理和旋转
     * 2. 已落地时：
     *    - 客户端：冻结旋转，防止抽搐
     *    - 服务器：处理激活和射击调度
     */
    override fun tick() {
        // 如果未落地，运行正常物理和旋转
        if (!entityData.get(isLandedAccessor)) {
            super.tick()
            return
        }

        // --- 诱饵弹已落地 ---

        // 在客户端，强制冻结旋转以防止抽搐
        if (level().isClientSide) {
            freezeRotation()
            return
        }

        // 在服务器，处理激活和射击调度
        if (activationTick == null) {
            activate()
        }

        activationTick?.let { startTick ->
            // 计算当前生命周期的tick数
            val currentTickInLifetime = tickCount - startTick
            // 检查是否需要射击
            if (nextFireTimestampIndex < fireTimestamps.size) {
                val nextShotTick = fireTimestamps[nextFireTimestampIndex]
                if (currentTickInLifetime >= nextShotTick) {
                    fireShot()
                    nextFireTimestampIndex++
                }
            }

            // 检查是否到达生命周期结束
            if (currentTickInLifetime > DecoyConstants.TOTAL_DURATION_TICKS) {
                endOfLifeExplosion()
            }
        }
    }

    /**
     * 冻结旋转，防止客户端显示抽搐
     */
    private fun freezeRotation() {
        if (!hasSavedFinalRotation) {
            // 第一次落地时保存最终旋转
            finalXRot = this.customXRot
            finalYRot = this.customYRot
            finalZRot = this.customZRot
            hasSavedFinalRotation = true
        }
        // 强制设置旋转回保存的值
        this.customXRot = finalXRot
        this.customYRot = finalYRot
        this.customZRot = finalZRot
        this.customXRotO = finalXRot
        this.customYRotO = finalYRot
        this.customZRotO = finalZRot
    }

    /**
     * 激活诱饵弹
     * 核心逻辑：记录激活时间，生成射击时间戳
     */
    override fun activate() {
        // 防止重复激活或在客户端激活
        if (activationTick != null || level().isClientSide) return
        // 记录激活时的tick数
        activationTick = this.tickCount
        // 生成射击时间戳列表
        this.fireTimestamps = DecoyFirePatternGenerator.generateFireTimestamps(this)
    }

    /**
     * 执行射击逻辑
     * 核心机制：发送网络包通知客户端播放射击声音
     */
    private fun fireShot() {
        // 获取枪械ID
        val gunId = gunData?.gunId ?: return
        // 发送网络包到所有客户端
        CsGrenadePacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), DecoyShotMessage(this.id, gunId, customSound))
    }

    /**
     * 投掷时查找并设置TACZ枪械数据
     * 核心逻辑：从玩家背包中查找第一个TACZ枪械
     */
    fun findAndSetTaczGunIdOnThrow() {
        val owner = this.owner
        if (owner is Player) {
            // 从玩家背包中查找TACZ枪械数据
            gunData = TaczGunDataProvider.findGunDataFromPlayer(owner)
            // 同步枪械ID到客户端
            gunData?.let { data ->
                entityData.set(GUN_DATA_ACCESSOR, data.gunId)
            }
        }
    }

    /**
     * 设置自定义声音
     */
    fun setCustomSound(sound: String) {
        customSound = sound
    }

    /**
     * 获取枪械数据
     */
    fun getGunData(): DecoyGunData? = gunData

    /**
     * 生命周期结束时的爆炸
     * 核心逻辑：产生小爆炸效果并丢弃实体
     */
    private fun endOfLifeExplosion() {
        if (!level().isClientSide) {
            // 产生小爆炸（0.1f威力）
            this.level().explode(this, this.x, this.y, this.z, 0.1f, false, Level.ExplosionInteraction.NONE)
        }
        // 丢弃实体
        this.discard()
    }

    /**
     * 获取伤害源
     */
    override fun getHitDamageSource(hitEntity: LivingEntity): DamageSource {
        val registryAccess = this.level().registryAccess()
        val damageTypeHolder = registryAccess.lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(ModDamageType.DECOY_GRENADE_HIT)
        return if (hitEntity == this.owner) {
            DamageSource(damageTypeHolder, this)
        } else {
            DamageSource(damageTypeHolder, this, this.owner)
        }
    }
}
