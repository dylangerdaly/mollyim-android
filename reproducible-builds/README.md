# Reproducible Builds

[![Docker](https://github.com/mollyim/mollyim-android/actions/workflows/docker.yml/badge.svg)](https://github.com/mollyim/mollyim-android/actions/workflows/docker.yml)

Follow these instructions to verify that this source code is exactly the same code that was used to compile the APK distributed on the website.

The [reproducible-builds.org](https://reproducible-builds.org/) project has more information about this general topic.

## Prerequisites

- Docker
- Docker Compose
- Python 3

## Build and Verify

You can compile you own release of Molly inside a Docker container and compare the resulted APK to the APK that is officially distributed. To do so, execute the following:

```shell
# Set the release version you want to check
export VERSION=v4.76.3-1

# Clone the source code repository
git clone https://github.com/mollyim/mollyim-android.git

# Go to this directory
cd mollyim-android/reproducible-builds

# Check out the release tag
git checkout $VERSION

# Build the APK using the Docker environment
docker-compose up --build

# Download the official APK
wget https://github.com/mollyim/mollyim-android/releases/download/$VERSION/Molly-production-$VERSION.apk

# Download the official APK (FOSS)
wget https://github.com/mollyim/mollyim-android/releases/download/$VERSION/Molly-production-$VERSION-FOSS.apk

# Run the diff script to compare the APKs
python apkdiff/apkdiff.py \
  Molly-production-$VERSION.apk \
  outputs/apk/nonFreeProduction/release/Molly-production-release-unsigned-$VERSION.apk

# Run the diff script to compare the APKs (FOSS)
python apkdiff/apkdiff.py \
  Molly-production-$VERSION-FOSS.apk \
  outputs/apk/freeProduction/release/Molly-production-release-unsigned-$VERSION-FOSS.apk

# Clean up the Docker environment
docker-compose down
```

If you get `APKs match`, you have **successfully verified** that the official release matches with your own self-built version of Molly. Congratulations!

If you get `APKs don't match`, please [report the issue](https://github.com/mollyim/mollyim-android/issues).
