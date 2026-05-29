#!/usr/bin/env bash

set -euo pipefail

wslg_pulse_socket="/mnt/wslg/PulseServer"

if [[ -S "$wslg_pulse_socket" ]]; then
  export PULSE_SERVER="unix:${wslg_pulse_socket}"
  export ALSOFT_DRIVERS="pulse"
  echo "Using WSLg PulseAudio socket: $PULSE_SERVER"
else
  echo "WSLg PulseAudio socket not found at $wslg_pulse_socket"
  echo "Falling back to current environment"
fi

exec ./gradlew lwjgl3:run