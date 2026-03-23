#!/usr/bin/python3

import os
import subprocess
import json

def is_stereo(file_path):
    """Check if the ogg file has more than 1 audio channel."""
    cmd = [
        'ffprobe', '-v', 'quiet', '-print_format', 'json', 
        '-show_streams', file_path
    ]
    result = subprocess.run(cmd, capture_output=True, text=True)
    data = json.loads(result.stdout)
    
    for stream in data.get('streams', []):
        if stream.get('codec_type') == 'audio':
            return int(stream.get('channels', 1)) > 1
    return False

def convert_to_mono(file_path):
    """Convert the file to mono using a high-quality downmix."""
    temp_file = file_path + ".tmp.ogg"
    # We use the pan filter to ensure L and R are mixed 50/50 so no data is lost
    cmd = [
        'ffmpeg', '-i', file_path, 
        '-af', 'pan=mono|c0=0.5*c0+0.5*c1', 
        '-y', temp_file
    ]
    
    try:
        subprocess.run(cmd, check=True, capture_output=True)
        os.replace(temp_file, file_path)
        print(f"✓ Converted: {file_path}")
    except subprocess.CalledProcessError as e:
        print(f"✗ Failed: {file_path} - {e}")
        if os.path.exists(temp_file):
            os.remove(temp_file)

def main():
    target_dir = os.getcwd()
    print(f"Scanning for stereo OGG files in: {target_dir}\n")

    for root, dirs, files in os.walk(target_dir):
        for file in files:
            if file.lower().endswith(".ogg"):
                full_path = os.path.join(root, file)
                if is_stereo(full_path):
                    convert_to_mono(full_path)
                else:
                    print(f"- Skipping (Already Mono): {file}")

if __name__ == "__main__":
    main()