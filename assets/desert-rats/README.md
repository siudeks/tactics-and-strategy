# Desert Rats – ZX Spectrum Game Assets

## Overview

**Desert Rats** (subtitle: *The North Africa Campaign*) is a turn-based
tactical wargame for the ZX Spectrum computer, published in **1985** by
**CCS (Computer Conflict Simulations)**. The game simulates key armoured
battles of the **North African campaign (1941–1942)**, pitting the British
8th Army (the legendary "Desert Rats" – 7th Armoured Division) against
**Rommel's Afrika Korps**.

| Attribute     | Details                                         |
|---------------|-------------------------------------------------|
| ZXDB ID       | 1357 (Spectrum Computing / ZXDB)                |
| Publisher     | CCS (Computer Conflict Simulations)             |
| Year          | 1985                                            |
| Platform      | ZX Spectrum 48K / 128K                          |
| Genre         | Strategy Game: War (Turn based)                 |
| Authors       | Robert T. Smith (code), John Berry (art)        |
| Players       | 1–2                                             |
| Controls      | Cursor keys, Interface 2, Kempston Joystick     |
| Score         | 8.2 / 10 (Spectrum Computing community)         |
| Awards        | Crash Readers Award 1986                        |
| Setting       | North Africa, WWII (1941–1942)                  |

---

## Contents of This Directory

| File / Directory                 | Description                                        |
|----------------------------------|----------------------------------------------------|
| `README.md`                      | This file – game overview and asset index          |
| `game-rules.md`                  | Full game rules in English                         |
| `screenshots/README.md`          | Annotated description of every game screen         |
| `screenshots/01-title.png`       | Title / loading screen (512×384 px, 2× scale)      |
| `screenshots/02-setup.png`       | Scenario selection / game setup screen             |
| `screenshots/03-map.png`         | Main strategic map screen during gameplay          |
| `screenshots/04-unit-info.png`   | Unit information panel                             |
| `screenshots/05-combat.png`      | Combat resolution screen                           |
| `screenshots/06-end-game.png`    | End-of-game / victory screen                       |

---

## Historical Context

The "Desert Rats" nickname was given to the British **7th Armoured Division**
that fought in the Western Desert campaign against the German Afrika Korps led
by Field Marshal **Erwin Rommel**. Major engagements represented in the game
include:

* **Operation Battleaxe** (June 1941) – First British attempt to relieve Tobruk
* **Operation Crusader** (November–December 1941) – Relief of Tobruk
* **Gazala Line** battles (May–June 1942) – Rommel's breakthrough to El Alamein

---

## Intended Use

These assets are collected as the reference material for building a modernised
version of Desert Rats using **Java** and **libGDX**.  The new implementation
will:

1. Recreate the original hex/square-grid tactical gameplay
2. Provide a graphical upgrade while preserving the original rules
3. Support single-player (vs AI) and two-player hot-seat modes
4. Use libGDX for cross-platform deployment (Desktop, Android, HTML5)

---

## Verified Source Files (ZXDB entry 1357)

The following file paths are from the ZXDB / Spectrum Computing archive.
Use `https://spectrumcomputing.co.uk` as the base URL.

| Path (relative to base URL)                                                        | Type                    |
|------------------------------------------------------------------------------------|-------------------------|
| `/pub/sinclair/screens/load/d/scr/DesertRats.scr`                                  | Loading screen (48K)    |
| `/pub/sinclair/screens/load/d/scr/DesertRats128.scr`                               | Loading screen (128K)   |
| `/pub/sinclair/screens/in-game/d/DesertRats.gif`                                   | In-game screenshot      |
| `/zxdb/sinclair/entries/0001357/DesertRats_Front.jpg`                              | Inlay – front           |
| `/zxdb/sinclair/entries/0001357/DesertRats_Front_2.jpg`                            | Inlay – front (alt)     |
| `/zxdb/sinclair/entries/0001357/DesertRats_SideA.jpg`                              | Cassette – side A       |
| `/zxdb/sinclair/entries/0001357/DesertRats_SideB.jpg`                              | Cassette – side B       |
| `/pub/sinclair/games/d/DesertRats.tzx.zip`                                         | Tape image (TZX)        |
| `/pub/sinclair/games/d/DesertRats48.z80.zip`                                       | 48K Z80 snapshot        |
| `/pub/sinclair/games/d/DesertRats128.z80.zip`                                      | 128K Z80 snapshot       |
| `/pub/sinclair/games-info/d/DesertRats.txt`                                        | Instructions (text)     |
| `/pub/sinclair/games-info/d/DesertRats_Front.jpg`                                  | Scanned instructions    |
| `/pub/sinclair/games-maps/d/DesertRats.jpg`                                        | Game map                |

> **Note on screenshots** — The PNG files in `screenshots/` are reconstructed
> representations of the original ZX Spectrum screens, generated using the
> actual ZX Spectrum ROM character set and exact ZX Spectrum palette, based
> on documented game descriptions.  To obtain pixel-accurate captures from the
> real game ROM, download the `.tzx` or `.z80` file above, load it in a ZX
> Spectrum emulator (e.g. **Fuse**, **ZEsarUX** or **Spectaculator**), and
> capture frames at the key points described in `screenshots/README.md`.
> Replace the existing PNGs with the emulator captures.
