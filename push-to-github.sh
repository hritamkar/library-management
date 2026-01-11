#!/bin/bash

# GitHub Push Script for Library Management System
# Run this after creating your GitHub repository

echo "======================================"
echo "📦 Preparing to push to GitHub"
echo "======================================"
echo ""

# Check if we're in the right directory
if [ ! -f "README.md" ] || [ ! -f "mta.yaml" ]; then
    echo "❌ Error: Not in the library-management directory"
    echo "Run: cd /Users/I578065/CAP/library-management"
    exit 1
fi

echo "✅ In correct directory"
echo ""

# Check git config
echo "📋 Current Git Configuration:"
echo "   Name: $(git config user.name)"
echo "   Email: $(git config user.email)"
echo ""

# Add all files
echo "📁 Adding files to git..."
git add .

# Show what will be committed
echo ""
echo "📊 Files to be committed:"
git status --short
echo ""

# Count files
FILE_COUNT=$(git ls-files --others --cached | wc -l | xargs)
echo "Total files: $FILE_COUNT"
echo ""

# Ask for confirmation
read -p "🤔 Does this look correct? (y/n) " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ Aborted. Review files and run again."
    exit 1
fi

# Commit
echo ""
echo "💾 Creating commit..."
git commit -m "Initial commit: Library Management System with SAP CAP Java

Features:
- Book, Member, and Loan management with CRUD operations
- Automatic fine calculation for overdue returns (₹10/day)
- Email validation and duplicate prevention
- Multitenancy-ready with SaaS Registry integration
- OData V4 API with Spring Boot 3.5.8
- Cloud Foundry deployment ready
- Comprehensive documentation and quick start guide

Tech Stack:
- SAP CAP Java
- Spring Boot
- SAP HANA Cloud / H2
- Maven
- Node.js"

echo ""
echo "✅ Commit created!"
echo ""

# Get GitHub repository URL
echo "======================================"
echo "🔗 GitHub Repository Setup"
echo "======================================"
echo ""
echo "Please enter your GitHub repository URL"
echo "Example: https://github.com/hritamkar03/library-management.git"
echo "Or: git@github.com:hritamkar03/library-management.git"
echo ""
read -p "Repository URL: " REPO_URL

if [ -z "$REPO_URL" ]; then
    echo "❌ No URL provided. Exiting."
    echo ""
    echo "To push manually later:"
    echo "  git remote add origin <your-repo-url>"
    echo "  git branch -M main"
    echo "  git push -u origin main"
    exit 1
fi

# Add remote
echo ""
echo "🔗 Adding remote repository..."
git remote add origin "$REPO_URL"

# Rename branch to main
echo "🔀 Renaming branch to main..."
git branch -M main

# Push to GitHub
echo ""
echo "🚀 Pushing to GitHub..."
echo ""
git push -u origin main

# Check if push was successful
if [ $? -eq 0 ]; then
    echo ""
    echo "======================================"
    echo "🎉 SUCCESS! 🎉"
    echo "======================================"
    echo ""
    echo "Your code is now on GitHub!"
    echo ""
    echo "Repository: $REPO_URL"
    echo ""
    echo "Next steps:"
    echo "1. Visit your repository on GitHub"
    echo "2. Add topics: sap-cap, java, spring-boot, library-management"
    echo "3. Set repository description"
    echo "4. Share with others!"
    echo ""
else
    echo ""
    echo "======================================"
    echo "❌ Push Failed"
    echo "======================================"
    echo ""
    echo "Common issues:"
    echo "1. Authentication - Setup GitHub PAT or SSH key"
    echo "2. Repository doesn't exist - Create it on GitHub first"
    echo "3. Already has commits - Use force push (be careful!)"
    echo ""
    echo "Manual push commands:"
    echo "  git push -u origin main"
    echo "  git push -u origin main --force  (if needed)"
    echo ""
fi

