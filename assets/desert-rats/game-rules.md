# Desert Rats – Game Rules (ZX Spectrum, CCS 1985)

> **Full title:** Desert Rats: The North Africa Campaign  
> **Authors:** Robert T. Smith (code), John Berry (art)  
> **ZXDB entry:** #1357 at spectrumcomputing.co.uk

## 1. Introduction

Desert Rats is a two-player (or one-player vs computer) **turn-based tactical
wargame** set in the North African desert during World War II.  One player
commands the **British 8th Army** (Allied side); the other commands the **German
Afrika Korps** (Axis side).  The objective is to capture and hold key objectives
on the map, or to destroy enough enemy forces to achieve a decisive victory
before the scenario time limit expires.

---

## 2. Components

### 2.1 The Map

The playing area is a **scrollable top-down desert map** divided into a regular
grid of squares (each square = approximately 2 km across).  Terrain types
displayed on the map:

| Symbol / Colour | Terrain Type      | Effect on Movement             | Effect on Defence |
|-----------------|-------------------|--------------------------------|-------------------|
| Sandy yellow    | Open desert       | Full movement                  | No bonus          |
| Brown/grey      | Rough/rocky ground| ½ movement                     | +1 defence shift  |
| Dark brown      | Escarpment/ridge  | Blocks or costs 2 MP to cross  | +2 defence shifts |
| Grey patches    | Minefield         | Cannot enter unless cleared    | Attacker –1 shift |
| White outlines  | Road / track      | +1 extra movement point        | No bonus          |
| Green/white     | Town / settlement | ½ movement (urban)             | +1 defence shift  |
| Blue line       | Wadi / dried river| Costs 2 MP to cross            | +1 defence shift  |

### 2.2 Units

Each side controls a number of **counters** (represented as coloured squares
with a symbol and two numbers):

```
┌───────┐
│  ╠═╣  │  ← Unit type symbol (e.g. ╠═╣ = armour, ╪ = infantry)
│ 3 / 2 │  ← Attack factor / Defence factor
└───────┘
```

**British (Allied) units** are displayed in **green**; **German (Axis) units**
are displayed in **grey/black**.

#### Unit Types

| Type              | Symbol | Move | Attack | Defence | Notes                              |
|-------------------|--------|------|--------|---------|------------------------------------|
| Armoured (Tank)   | ╠═╣    | 4    | 4      | 3       | Best offensive unit                |
| Motorised Infantry| ╪      | 3    | 2      | 2       | Can dig in for +1 defence          |
| Infantry          | ╫      | 2    | 2      | 3       | Cheaper; best in rough terrain     |
| Artillery         | ✦      | 2    | 3      | 1       | Can support from adjacent square   |
| Anti-Tank Gun     | ⊕      | 1    | 3*     | 2       | *Only vs armour; weak vs infantry  |
| Reconnaissance    | ◇      | 5    | 1      | 1       | Reveals hidden enemy units         |
| Supply Truck      | ▭      | 3    | 0      | 0       | Required to keep units in supply   |

### 2.3 Scenarios

The game ships with **three historical scenarios**:

| # | Scenario              | Turns | Map Size       | Forces Each Side |
|---|-----------------------|-------|----------------|------------------|
| 1 | Operation Battleaxe   | 10    | 20 × 15 squares| 8 units          |
| 2 | Operation Crusader    | 15    | 24 × 18 squares| 12 units         |
| 3 | Rommel's Breakthrough | 12    | 22 × 16 squares| 14 units         |

---

## 3. Setup

1. **Select a scenario** from the opening menu (keys **1–3**).
2. **Choose sides** – Player 1 selects Allied or Axis; Player 2 (or computer)
   takes the other.
3. **Difficulty** (single-player only) – Easy / Normal / Hard adjusts the
   computer's reaction time and planning depth.
4. Units are placed on the map in their **historical start positions** as shown
   in the scenario briefing screen.  Players may not change start positions.

---

## 4. Turn Structure

Each complete game round consists of the following phases executed in order:

```
┌─────────────────────────────────────┐
│  1. Supply Check Phase               │
│  2. Allied Movement Phase            │
│  3. Allied Combat Phase              │
│  4. Axis Movement Phase              │
│  5. Axis Combat Phase                │
│  6. Victory Check                    │
└─────────────────────────────────────┘
```

### 4.1 Supply Check Phase

A unit is **in supply** if there is an unbroken chain of friendly squares (no
enemy Zone of Control, no enemy units) leading from the unit back to a **supply
source** (edge of the map for the side that entered from that edge, or a town
held by that side).

* **In supply** – Unit operates normally.
* **Out of supply** – Unit's movement allowance is halved (round down); attack
  factor is halved (round down).
* A unit that remains out of supply for **two consecutive turns** is **eliminated**.

### 4.2 Movement Phase

The active player moves any or all of their units, one at a time.

**Movement rules:**

1. Each unit has a **movement allowance (MA)** in Movement Points (MP).
2. Entering each terrain square costs MP as listed in the terrain table (§2.1).
3. A unit **may not move through** a square occupied by an enemy unit.
4. A unit **may not end its movement** in a square occupied by any other unit.
5. **Zone of Control (ZoC):** Each unit projects a ZoC into all four
   orthogonally adjacent squares.  A unit moving into an enemy ZoC square
   **must stop** (no further movement that turn) unless it is attacking from
   that square.
6. Moving through a friendly unit's ZoC square costs +1 MP.

### 4.3 Combat Phase

After all movement is complete, the active player may declare **attacks**.

**Attack rules:**

1. An attacking unit must be **adjacent** (orthogonally) to the target unit.
2. Multiple friendly units may **support** a single attack by being adjacent to
   the target — each supporting unit adds half its attack factor (rounded down)
   to the combined attack strength.
3. Multiple enemy units in the same or adjacent squares may **defend together**
   — sum their defence factors.
4. Calculate the **combat odds ratio**: total attack ÷ total defence, rounded
   to the nearest listed odds column.

#### 4.3.1 Combat Results Table (CRT)

Roll a single six-sided die and cross-reference with the odds column:

| Odds Ratio | Die 1 | Die 2 | Die 3 | Die 4 | Die 5 | Die 6 |
|------------|-------|-------|-------|-------|-------|-------|
| 1:3        | EX    | DE    | DE    | DR    | DR    | NE    |
| 1:2        | EX    | DE    | DR    | DR    | NE    | NE    |
| 1:1        | DE    | DR    | DR    | NE    | AE    | AE    |
| 2:1        | DR    | NE    | AE    | AE    | AR    | AR    |
| 3:1        | NE    | AE    | AE    | AR    | AR    | AR    |
| 4:1        | AE    | AE    | AR    | AR    | AR    | AR    |
| 5:1+       | AE    | AR    | AR    | AR    | AR    | AR    |

**Result codes:**

| Code | Meaning                                                        |
|------|----------------------------------------------------------------|
| DE   | **Defender Eliminated** – defending unit(s) removed from map   |
| DR   | **Defender Retreat** – defender moves 1–2 squares away         |
| NE   | **No Effect** – neither side affected                          |
| AE   | **Attacker Eliminated** – attacking unit removed from map      |
| AR   | **Attacker Retreat** – attacker moves 1–2 squares back         |
| EX   | **Exchange** – both attacker and defender are eliminated       |

**Terrain modifiers:**

* Defender in rough/rocky ground: shift CRT one column in defender's favour
* Defender in escarpment/ridge: shift CRT two columns in defender's favour
* Defender in town: shift CRT one column in defender's favour
* Attacker crosses wadi in attack: shift CRT one column in defender's favour
* Defender in minefield: shift CRT one column in attacker's favour (minefield
  disrupts defenders too)

### 4.4 Victory Check

After both sides have completed their combat phases, the game checks for
**victory conditions**:

* Count **Victory Points (VP)** for each side:
  * Holding a **town** square: +2 VP per town per turn
  * Eliminating an enemy **armoured unit**: +3 VP
  * Eliminating any other enemy unit: +1 VP
* If one side reaches the **VP threshold** for the scenario, they win
  immediately (decisive victory).
* At the **final turn**, the side with more VP wins.  A difference of less than
  10% of total possible VP is a **draw**.

---

## 5. Special Rules

### 5.1 Minefields

* Minefields appear as grey patches on the map.
* A unit moving into a minefield square must stop.
* The first time a unit enters a minefield, roll 1d6:
  * 1–2: Unit is **eliminated**
  * 3–4: Unit **retreats** 1 square
  * 5–6: Unit passes through safely
* **Engineering units** (if present in a scenario) may **clear** a minefield by
  spending their entire movement allowance in an adjacent square.

### 5.2 Dug-In Status

* An **infantry** or **anti-tank gun** unit may choose to **dig in** instead of
  moving.  Mark it as dug-in.
* A dug-in unit gains +1 defence shift on the CRT.
* The dug-in status is lost if the unit moves at any point.

### 5.3 Hidden Units (Fog of War)

* In **single-player** mode, Axis units (computer-controlled) are **hidden**
  until they enter the ZoC of a British unit or are spotted by a Recon unit.
* Hidden units appear as question-mark tiles.
* A **Reconnaissance unit** that moves adjacent to a hidden enemy unit reveals
  it permanently.

### 5.4 Air Support (Scenario 3 only)

* Each side receives **1 air support token** per 5 turns.
* Spend a token during your Combat Phase before rolling: shift the CRT two
  columns in the attacker's favour.
* Air support cannot be used against units in a town.

---

## 6. Controls (Keyboard)

| Key(s)           | Action                                         |
|------------------|------------------------------------------------|
| **Q / A**        | Scroll map up / down                           |
| **O / P**        | Scroll map left / right                        |
| **5–8** (cursor) | Move cursor on map                             |
| **ENTER / 0**    | Select unit under cursor / confirm action      |
| **SPACE**        | Deselect unit / cancel action                  |
| **F**            | Show unit information panel                    |
| **T**            | End movement phase, start combat phase         |
| **E**            | End turn (pass to other player / computer)     |
| **S**            | Save game                                      |
| **L**            | Load game                                      |
| **H**            | Display help / rules summary                   |

---

## 7. Victory Conditions (Summary per Scenario)

### Scenario 1 – Operation Battleaxe

* **Allied goal:** Relieve **Halfaya Pass** and **Fort Capuzzo** (both must be
  held at end of turn 10) **and** have ≥ 6 units remaining.
* **Axis goal:** Prevent the above by holding at least one of the objectives
  or reducing Allied forces to < 6 units.

### Scenario 2 – Operation Crusader

* **Allied goal:** Control **Tobruk** and the **Sidi Rezegh** airfield by turn
  15, with supply lines intact.
* **Axis goal:** Keep Tobruk besieged (no Allied unit in Tobruk hex) and
  accumulate ≥ 25 VP.

### Scenario 3 – Rommel's Breakthrough

* **Allied goal:** Maintain a continuous defensive line from the coast to the
  Qattara Depression (no gap of more than 3 squares) at end of turn 12.
* **Axis goal:** Break through the Allied line and reach **Alexandria** (eastern
  edge of the map) with at least 3 armoured units.

---

## 8. Glossary

| Term              | Definition                                                   |
|-------------------|--------------------------------------------------------------|
| **CRT**           | Combat Results Table                                         |
| **MP**            | Movement Points                                             |
| **VP**            | Victory Points                                              |
| **ZoC**           | Zone of Control – the four orthogonal squares around a unit  |
| **Dug-in**        | Infantry/AT gun that chose to fortify instead of moving      |
| **Supply source** | A map edge or friendly-held town that provides supply        |
| **Afrika Korps**  | German/Italian forces in North Africa (Axis side)            |
| **Desert Rats**   | Nickname of the British 7th Armoured Division (Allied side)  |

---

## 9. Notes for the Java / libGDX Re-implementation

The following are design notes for translating the original rules into code:

* **Grid representation** – use a 2-D array of `TileType` enum values; store
  units in a separate `Map<GridPosition, Unit>`.
* **CRT** – implement as a 2-D lookup table indexed by `OddsRatio` and `DieRoll`.
* **ZoC** – calculated dynamically; cache per turn to avoid repeated scans.
* **Hidden units** – store server-side; send client only a "hidden" marker until
  revealed.
* **AI** – start with a simple minimax depth-2 tree evaluated by VP difference;
  upgrades possible once the core engine is working.
* **Scenarios** – define as JSON files loaded at startup; keep game logic
  scenario-agnostic.
