#!/bin/zsh
# Regenerates the README screenshots: debug build on the running Android emulator, English, clean status bar.
set -e
cd "${0:A:h}/../.."
APP=com.gondroid.subtrack
./gradlew :androidApp:installDebug --quiet
adb shell cmd locale set-app-locales $APP --locales en-US
adb shell settings put global sysui_demo_allowed 1
demo() { adb shell am broadcast -a com.android.systemui.demo -e command "$@" >/dev/null; }
demo enter
demo clock -e hhmm 1200
demo battery -e level 100 -e plugged false
demo notifications -e visible false
demo network -e wifi show -e level 4
demo network -e mobile show -e level 4 -e datatype false
trap 'demo exit' EXIT
maestro test docs/screenshots/flow.yml
