# Release Process

This document describes the steps to release a new version of the Talkable Android SDK.

## Prerequisites

- Write access to the repository
- All changes merged to `master`
- CI checks passing

## Steps

### 1. Bump Version Numbers

Update `gradle.properties` in the project root:

```properties
sdk_version_code=<previous + 1>
sdk_version_name=<new version, e.g. 0.5.15>
```

If the App demo module was also changed, bump its version too:

```properties
app_version_code=<previous + 1>
app_version_name=<new version>
```

### 2. Update README

Update the dependency version in `README.md` to match the new `sdk_version_name`:

```gradle
implementation 'com.github.talkable:android-sdk:<new version>'
```

### 3. Commit and Merge

Create a commit with the version bump:

```bash
git add gradle.properties README.md
git commit -m "Bump SDK version to <new version>"
```

Push and merge to `master` via a pull request. Ensure CI passes.

### 4. Tag the Release

After merging to `master`, create and push a lightweight tag matching the version:

```bash
git checkout master
git pull
git tag <new version>    # e.g. git tag 0.5.15
git push origin <new version>
```

Tags must match the `sdk_version_name` exactly (e.g. `0.5.15`, not `v0.5.15`).

### 5. Verify JitPack Build

JitPack automatically detects new tags and builds the artifact. Verify the build at:

```
https://jitpack.io/#talkable/android-sdk/<new version>
```

A green status indicates a successful build. If it fails, check the build log on JitPack and fix any issues.

## Version Scheme

- **sdk_version_name**: Semantic versioning (`MAJOR.MINOR.PATCH`). Increment PATCH for fixes and minor updates, MINOR for new features, MAJOR for breaking changes.
- **sdk_version_code**: Integer that increments by 1 with every release.
- Tags use the bare version number (e.g. `0.5.15`), without a `v` prefix.

## Configuration Reference

All version and build configuration lives in `gradle.properties`:

| Property | Description | Example |
|---|---|---|
| `android_build_tools` | Build tools version (shared) | `36.0.0` |
| `sdk_compile_sdk` | SDK compile SDK version | `35` |
| `sdk_target_sdk` | SDK target SDK version | `35` |
| `sdk_min_sdk` | SDK module minimum SDK | `16` |
| `sdk_version_code` | SDK version code (integer) | `43` |
| `sdk_version_name` | SDK version name (semver) | `0.5.14` |
| `app_compile_sdk` | App compile SDK version | `35` |
| `app_target_sdk` | App target SDK version | `35` |
| `app_min_sdk` | App module minimum SDK | `21` |
| `app_version_code` | App module version code | `3` |
| `app_version_name` | App module version name | `1.0.0` |

Both `sdk/build.gradle` and `app/build.gradle` read from these properties. Do not hardcode build config values in the module build files.
