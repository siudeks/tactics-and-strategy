# Desert Rats – Screenshots

This directory contains annotated placeholders for all key screens in the
original ZX Spectrum version of Desert Rats (CCS, 1985).

## How to Capture Real Screenshots

1. Download the original `.tzx` or `.tap` image from the World of Spectrum
   archive (entry **#0001310**).
2. Open it in a ZX Spectrum emulator:
   * **Fuse** (Linux/macOS/Windows) — `File › Open`
   * **ZEsarUX** (cross-platform) — `File › Open ROM`
   * **Spectaculator** (Windows) — drag-and-drop
3. Play through to each screen listed below.
4. Use the emulator's screenshot function (usually `F10` or
   `Screenshot` in the `File` menu) to save a **256 × 192 px PNG**.
5. Save the PNG alongside the matching `.svg` placeholder, using the same
   base name (e.g. `01-title.png` next to `01-title.svg`). Once real
   screenshots are available, update `assets/desert-rats/README.md` to
   reference the `.png` files and delete the `.svg` placeholders.

---

## Screen Descriptions

### `01-title.svg` — Title Screen

**When shown:** Immediately after loading completes.

**Visual elements:**
* Black background fills the whole screen.
* "DESERT RATS" appears in large yellow block letters centred near the top.
* Below the title: "A CCS WARGAME" in white text.
* A small desert silhouette (horizon with dunes and palm tree) rendered in
  yellow/orange pixels is centred vertically.
* Bottom third: "PRESS ANY KEY TO START" flashes in white (blink attribute).
* Colour border: **yellow** (Spectrum attribute 6).

---

### `02-setup.svg` — Game Setup Screen

**When shown:** After the title screen, before the scenario starts.

**Visual elements:**
* White background panel (attribute paper 7, ink 0).
* Menu title "SELECT SCENARIO" in bold black.
* Three numbered options:
  1. OPERATION BATTLEAXE
  2. OPERATION CRUSADER
  3. ROMMEL'S BREAKTHROUGH
* Cursor arrow `▶` (or `>`) beside currently highlighted item.
* Below scenarios: "NUMBER OF PLAYERS: 1 / 2" toggle line.
* Below that (single-player only): "DIFFICULTY: EASY / NORMAL / HARD".
* Bottom line: "USE 1-3 TO SELECT, ENTER TO CONFIRM".
* Colour border: **white** (attribute 7).

---

### `03-map.svg` — Main Strategic Map

**When shown:** During gameplay (movement and combat phases).

**Visual elements (from top to bottom):**
* **Status bar (top, 2 character rows):**
  * Left: `TURN: 3 / 10` (current / max)
  * Centre: `ALLIED MOVEMENT PHASE`
  * Right: `VP  ALLIED: 8  AXIS: 5`
* **Map area (main area, ~180 px tall):**
  * Sandy yellow (`INK 6, PAPER 6`) for open desert squares.
  * Brown (`INK 2, PAPER 6`) for rocky terrain.
  * Dark brown (`INK 2, PAPER 0`) ridge/escarpment lines.
  * White road lines over yellow/brown terrain.
  * Town squares shown as tiny white block outlines (`INK 7, PAPER 0`).
  * Wadi as a thin blue horizontal stripe (`INK 1`).
  * **British (Allied) unit counters:** Green squares (`PAPER 4, INK 0`) with
    a white symbol and two white digits (attack/defence).
  * **Axis unit counters:** Grey squares (`PAPER 5, INK 0`) with black symbols.
  * **Selected unit:** Flashing border (blink attribute) around the active counter.
  * **Cursor:** Flashing white square outline.
* **Mini-panel (bottom, 3 character rows):**
  * Shows name and stats of unit under cursor.
  * E.g. `UNIT: 4TH ARMD BDE  ATK: 4  DEF: 3  MOV: 4  STATUS: OK`
* Colour border: **cyan** (`attribute 5`) during Allied turn;
  **red** (`attribute 2`) during Axis turn.

---

### `04-unit-info.svg` — Unit Information Panel

**When shown:** Player presses `F` to inspect a unit.

**Visual elements:**
* Overlays the lower half of the map with a semi-transparent black panel
  (achieved by inverting attribute).
* Panel title: `UNIT INFORMATION` in yellow.
* Large unit symbol centred (e.g. tank sprite, 16 × 16 px).
* Stats block:
  ```
  NAME     : 7TH ARMD DIV (TANKS)
  TYPE     : ARMOURED
  ATTACK   : 4
  DEFENCE  : 3
  MOVEMENT : 4 / 4
  SUPPLY   : IN SUPPLY
  STATUS   : ACTIVE
  ```
* Bottom: `PRESS SPACE TO CLOSE`.

---

### `05-combat.svg` — Combat Resolution Screen

**When shown:** When the player confirms an attack.

**Visual elements:**
* Full-screen overlay, black background.
* Header: `COMBAT RESOLUTION` in red (flashing).
* Two opposed counters side by side:
  * Left (attacker): green counter with label `ATK TOTAL: 6`
  * Right (defender): grey counter with label `DEF TOTAL: 3`
* Calculated odds displayed: `ODDS: 2:1`
* Terrain modifiers listed (if any): `TERRAIN: +1 DEFENDER`
* After adjustment: `ADJUSTED ODDS: 1:1`
* `ROLL: 4` (large yellow digit)
* Result line: `RESULT: DEFENDER RETREAT` (or appropriate result, in white).
* `PRESS ANY KEY TO CONTINUE` at bottom (flashing).

---

### `06-end-game.svg` — End of Game / Victory Screen

**When shown:** When victory conditions are met, or the final turn ends.

**Visual elements:**
* Black background.
* Large centred text — one of:
  * `ALLIED VICTORY!` (yellow, flashing)
  * `AXIS VICTORY!`   (red, flashing)
  * `DRAW`            (white)
* Sub-text: `OPERATION CRUSADER – TURN 12 / 15`
* VP summary box:
  ```
  ┌──────────────────────┐
  │  ALLIED VP :  32     │
  │  AXIS   VP :  21     │
  │  RESULT    : ALLIED  │
  └──────────────────────┘
  ```
* Bottom: `PLAY AGAIN? Y / N`
* Colour border matches winner's side (yellow = Allied, red = Axis, white = draw).
