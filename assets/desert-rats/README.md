# Desert Rats – ZX Spectrum Game Assets

## Overview

**Desert Rats** is a turn-based tactical wargame for the ZX Spectrum computer,
published in **1985** by **CCS (Computer Conflict Simulations)**. The game
simulates key armoured battles of the **North African campaign (1941–1942)**,
pitting the British 8th Army (the legendary "Desert Rats" – 7th Armoured
Division) against **Rommel's Afrika Korps**.

| Attribute     | Details                                         |
|---------------|-------------------------------------------------|
| Publisher     | CCS (Computer Conflict Simulations)             |
| Year          | 1985                                            |
| Platform      | ZX Spectrum 48K / 128K                          |
| Genre         | Turn-based Tactical Wargame                     |
| Players       | 1–2                                             |
| Perspective   | Top-down 2-D map                                |
| Setting       | North Africa, WWII (1941–1942)                  |

---

## Contents of This Directory

| File / Directory                 | Description                                        |
|----------------------------------|----------------------------------------------------|
| `README.md`                      | This file – game overview and asset index          |
| `game-rules.md`                  | Full game rules in English                         |
| `screenshots/README.md`          | Annotated description of every game screen         |
| `screenshots/01-title.svg`       | Title screen placeholder                           |
| `screenshots/02-setup.svg`       | Game setup / options screen placeholder            |
| `screenshots/03-map.svg`         | Main strategic map screen placeholder              |
| `screenshots/04-unit-info.svg`   | Unit information panel placeholder                 |
| `screenshots/05-combat.svg`      | Combat resolution screen placeholder               |
| `screenshots/06-end-game.svg`    | End-of-game / victory screen placeholder           |

> **Note on SVG placeholders** — The `.svg` files are schematic representations
> of the original ZX Spectrum screens drawn from documented game descriptions.
> To obtain pixel-accurate screenshots, load the original `.tzx` / `.tap` image
> in a ZX Spectrum emulator (e.g. **Fuse**, **ZEsarUX** or **Spectaculator**)
> and capture frames at the key points described in `screenshots/README.md`.

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
