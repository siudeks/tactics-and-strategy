# Devcontainer Audio on WSL + Windows

This guide covers the common setup where the app runs in a devcontainer (Linux) and displays through WSL/Windows.

## Why graphics can work but sound does not

`DISPLAY` forwards graphics. Audio is separate and usually needs PulseAudio/PipeWire routing.

## 1. Rebuild container with audio tools

After updating the devcontainer image, rebuild the container.

The main devcontainer is configured for this machine and already includes:
- mount of `/mnt/wslg`,
- default `PULSE_SERVER=unix:/mnt/wslg/PulseServer`,
- `ALSOFT_DRIVERS=pulse`,
- GPU device pass-through hint (`/dev/dri`).

The setup installs:
- `pulseaudio-utils` (`pactl`)
- `alsa-utils` (`aplay`)

## 2. Inspect runtime audio context in container

Run:

```bash
echo "DISPLAY=$DISPLAY"
echo "PULSE_SERVER=$PULSE_SERVER"
echo "XDG_RUNTIME_DIR=$XDG_RUNTIME_DIR"
command -v pactl
command -v aplay
pactl info
aplay -l
```

Interpretation:
- If `pactl` is missing, tools are not installed (rebuild container).
- If `pactl info` fails, container cannot reach an audio server.
- If `aplay -l` shows no devices, direct ALSA passthrough is unavailable.

## 3. Set Pulse server in shell for current session

If `PULSE_SERVER` is empty, try one of these:

```bash
export PULSE_SERVER=unix:/mnt/wslg/PulseServer
# or
export PULSE_SERVER=tcp:host.docker.internal
```

Then re-test:

```bash
pactl info
```

If the first value works, persist it in shell profile:

```bash
echo 'export PULSE_SERVER=unix:/mnt/wslg/PulseServer' >> ~/.bashrc
```

## 4. Optional: pass host audio device/socket to container

If your environment does not expose Pulse by default, add explicit mounts/run args in your local devcontainer override.

Example options (environment-specific):
- bind mount host pulse socket path into container,
- set `PULSE_SERVER` to mounted socket,
- pass `/dev/snd` when ALSA passthrough is available.

## 5. Run game and verify target confirmation sound

Start the game and confirm a MOVE target.

If no sound is heard, capture logs and include:
- output of `pactl info`,
- value of `PULSE_SERVER`,
- whether `/mnt/wslg/PulseServer` exists.

## 6. Known Error: ALSA cannot find card 0

If you see logs like:

```text
ALSA lib ... cannot find card '0'
ALSA lib ... Unknown PCM default
```

it means OpenAL selected ALSA backend but no ALSA device exists in container.

The main devcontainer sets:
- `PULSE_SERVER=unix:/mnt/wslg/PulseServer`
- `ALSOFT_DRIVERS=pulse`

If the alternate devcontainer profile is not being applied by VS Code, use the wrapper script instead:

```bash
bash scripts/run-lwjgl3-with-audio.sh
```

That script sets the same audio variables explicitly before launching Gradle.

Then verify:

```bash
echo "$PULSE_SERVER"
echo "$ALSOFT_DRIVERS"
test -S /mnt/wslg/PulseServer && echo "Pulse socket OK" || echo "Pulse socket missing"
pactl info
```

If the socket is missing, WSLg audio is not exposed to the container and Pulse routing must be fixed on host side.
