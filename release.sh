#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Define paths
DOWNLOAD_DIR="docs/download"
RELEASE_DIR="app/release"
TARGET_NAME="BIG.apk"

# Step 1: Delete all files from download folder
echo "Cleaning download folder..."
rm -rf "$DOWNLOAD_DIR"/*

if [ $? -eq 0 ]; then
    echo -e "${GREEN}Download folder cleaned${NC}"
else
    echo -e "${RED}Failed to clean download folder${NC}"
    exit 1
fi

# Step 2: Find and copy APK from release to download
echo "Copying APK from release..."
APK_FILE=$(find "$RELEASE_DIR" -name "*.apk" -type f | head -n 1)

if [ -z "$APK_FILE" ]; then
    echo -e "${RED}No APK found in release folder${NC}"
    exit 1
fi

cp "$APK_FILE" "$DOWNLOAD_DIR/$TARGET_NAME"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}APK copied and renamed to: $TARGET_NAME${NC}"
    echo "Location: $DOWNLOAD_DIR/$TARGET_NAME"
else
    echo -e "${RED}Failed to copy APK${NC}"
    exit 1
fi

# Step 3: Add APK to git
echo "Adding APK to git..."
git add "$DOWNLOAD_DIR/$TARGET_NAME"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}APK staged for commit${NC}"
else
    echo -e "${RED}Failed to stage APK${NC}"
    exit 1
fi

echo "Done!"