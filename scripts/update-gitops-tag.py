#!/usr/bin/env python3
import sys
import os
import re

def main():
    if len(sys.argv) < 5:
        print("Usage: python update-gitops-tag.py <file_path> <tag> <repo> <target_revision>")
        sys.exit(1)

    file_path = sys.argv[1]
    tag = sys.argv[2]
    repo = sys.argv[3]
    target_revision = sys.argv[4]

    if not os.path.exists(file_path):
        print(f"Error: File {file_path} not found.")
        sys.exit(1)

    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Determine parameter names based on chart name or repo
    # UI charts use ui.image.*, backend charts use backend.image.*
    is_ui = "storefront-ui" in file_path or "backoffice-ui" in file_path
    tag_param = "ui.image.tag" if is_ui else "backend.image.tag"
    repo_param = "ui.image.repository" if is_ui else "backend.image.repository"

    # 1. Update targetRevision in spec.source
    # We find 'targetRevision: ...' and replace it
    content = re.sub(
        r'(\s+targetRevision:\s*[\'"]?)[^\'\"]*([\'\"]?)',
        rf'\g<1>{target_revision}\g<2>',
        content
    )

    # 2. Check if we should update helm parameters
    if tag.lower() == 'none' or repo.lower() == 'none':
        print(f"Skipping helm parameters update for {file_path}")
    elif "helm:" in content and "parameters:" in content:
        # Update existing parameters
        # Replace tag value
        tag_pattern = rf'(-\s*name:\s*{re.escape(tag_param)}\s*\n\s*value:\s*[\'"]?)[^\'\"]*([\'\"]?)'
        if re.search(tag_pattern, content):
            content = re.sub(tag_pattern, rf'\g<1>{tag}\g<2>', content)
        else:
            # If helm block exists but tag param is missing, append it under parameters:
            param_block = f"        - name: {tag_param}\n          value: '{tag}'\n"
            content = re.sub(r'(\s+parameters:\s*\n)', rf'\1{param_block}', content)

        # Replace repo value
        repo_pattern = rf'(-\s*name:\s*{re.escape(repo_param)}\s*\n\s*value:\s*[\'"]?)[^\'\"]*([\'\"]?)'
        if re.search(repo_pattern, content):
            content = re.sub(repo_pattern, rf'\g<1>{repo}\g<2>', content)
        else:
            # Append repo param under parameters:
            param_block = f"        - name: {repo_param}\n          value: '{repo}'\n"
            content = re.sub(r'(\s+parameters:\s*\n)', rf'\1{param_block}', content)
    else:
        # Find where 'path: ...' is, and append helm block right after it
        path_pattern = r'(\s+path:\s*[^\n]+)'
        helm_block = (
            f"\n    helm:\n"
            f"      parameters:\n"
            f"        - name: {tag_param}\n"
            f"          value: '{tag}'\n"
            f"        - name: {repo_param}\n"
            f"          value: '{repo}'"
        )
        content = re.sub(path_pattern, rf'\1{helm_block}', content, count=1)

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f"Successfully updated {file_path} with tag={tag}, repo={repo}, targetRevision={target_revision}")

if __name__ == "__main__":
    main()
