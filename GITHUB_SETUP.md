# GitHub Repository Setup Instructions

## Create Repository on GitHub

1. Go to: https://github.com/new
2. **Make sure you're logged in as `jyoti369`**
3. Repository name: `membership-program`
4. Description: `Tiered membership system with automatic tier upgrades - Spring Boot + JPA`
5. Visibility: **Public**
6. **DO NOT** initialize with README, .gitignore, or license (we already have these)
7. Click "Create repository"

## Push Your Code

After creating the repository, run these commands:

```bash
cd /Users/debojyoti.mandal/personal/membership-program

# Add GitHub remote (replace with YOUR actual repo URL)
git remote add origin https://github.com/jyoti369/membership-program.git

# Rename branch to main (GitHub standard)
git branch -M main

# Push to GitHub
git push -u origin main
```

## Verify Upload

After pushing, your repository will be available at:
https://github.com/jyoti369/membership-program

## What Gets Pushed

All these files will be uploaded:
- ✅ 54 files (5,962 lines of code + docs)
- ✅ 39 Java source files
- ✅ 10 documentation files
- ✅ Maven configuration
- ✅ All README examples

## Troubleshooting

### If asked for credentials:
- Username: `jyoti369`
- Password: Use your GitHub Personal Access Token (not your password)
  - Get token at: https://github.com/settings/tokens

### If wrong account is used:
```bash
# Remove the remote
git remote remove origin

# Configure git to use jyoti369
git config user.name "jyoti369"
git config user.email "your-jyoti369-email@example.com"

# Add remote again with correct account
git remote add origin https://github.com/jyoti369/membership-program.git
```

### Switch GitHub account in terminal:
```bash
# Clear cached credentials
git credential-cache exit

# Or use SSH instead (if you have SSH keys set up for jyoti369)
git remote set-url origin git@github.com:jyoti369/membership-program.git
```
