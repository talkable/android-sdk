#!/bin/bash

# Solano CI pre_setup hook

ANDROID_COMPILE_SDK="26"
ANDROID_BUILD_TOOLS="26.0.1"
ANDROID_SDK_TOOLS_REV="3859397" # "26.0.1"

# Only trigger the android SDK download if it is not already installed
if ! test -d $ANDROID_HOME; then
  mkdir $HOME/.android # For sdkmanager configs
  echo 'count=0' > $HOME/.android/repositories.cfg # Avoid warning

  wget --quiet --output-document=android-sdk.zip https://dl.google.com/android/repository/sdk-tools-linux-${ANDROID_SDK_TOOLS_REV}.zip
  mkdir $PWD/android-sdk-linux
  unzip -qq android-sdk.zip -d $PWD/android-sdk-linux

  echo y | $ANDROID_HOME/tools/bin/sdkmanager --update
  echo y | $ANDROID_HOME/tools/bin/sdkmanager 'tools'
  echo y | $ANDROID_HOME/tools/bin/sdkmanager 'platform-tools'
  echo y | $ANDROID_HOME/tools/bin/sdkmanager 'build-tools;'$ANDROID_BUILD_TOOLS
  echo y | $ANDROID_HOME/tools/bin/sdkmanager 'platforms;android-'$ANDROID_COMPILE_SDK
fi

chmod +x ./gradlew
./gradlew clean
