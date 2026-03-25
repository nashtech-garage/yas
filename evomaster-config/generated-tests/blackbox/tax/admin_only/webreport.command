#!/bin/zsh
cd "$(dirname "$0")"  # Navigates to the script's folder
python3 webreport.py || {
    echo "Error: Failed to run the Python script."
    echo "Possible fixes:"
    echo "1. Ensure 'python3' is installed (run 'python3 --version')."
    echo "2. Check if 'webreport.py' exists in the same folder."
}
read -r "?Press Enter to close..."