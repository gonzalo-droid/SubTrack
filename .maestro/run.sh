#!/bin/zsh
# Runs every Maestro flow from a clean install state, in Spanish.
# Usage: .maestro/run.sh [flow ...]   (default: all flows in .maestro/)
# Each flow starts with app data cleared (Room + preferences) and the per-app locale set to es-PE,
# so results don't depend on the emulator's language or on what a previous flow changed.
APP=com.gondroid.subtrack
cd "${0:A:h}"
flows=(${@:-*.yaml})
failed=0
for flow in $flows; do
  adb shell pm clear $APP >/dev/null
  adb shell cmd locale set-app-locales $APP --locales es-PE
  if maestro test "$flow" >/tmp/maestro_${flow:r}.log 2>&1; then
    echo "PASS  $flow"
  else
    echo "FAIL  $flow  (log: /tmp/maestro_${flow:r}.log)"
    failed=1
  fi
done
exit $failed
