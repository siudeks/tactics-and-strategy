# Tactics and Strategy

A project for creating a modern reimplementation of classic ZX Spectrum
wargames using **Java** and **[libGDX](https://libgdx.com/)**.

## Current Focus – Desert Rats (CCS, 1985)

The first game being reimplemented is **Desert Rats**, a turn-based tactical
wargame originally published in 1985 by CCS for the ZX Spectrum.  The game
simulates WWII North African armoured battles between the British 8th Army
("Desert Rats") and Rommel's Afrika Korps.

### Repository Layout

```
assets/
  desert-rats/
    README.md          ← Game overview and asset index
    game-rules.md      ← Complete game rules in English
    screenshots/
      README.md        ← Annotated description of every game screen
      01-title.svg     ← Title screen placeholder
      02-setup.svg     ← Setup / options screen placeholder
      03-map.svg       ← Main strategic map screen placeholder
      04-unit-info.svg ← Unit information panel placeholder
      05-combat.svg    ← Combat resolution screen placeholder
      06-end-game.svg  ← End-of-game / victory screen placeholder
```

See [`assets/desert-rats/README.md`](assets/desert-rats/README.md) for the
full asset index and instructions on capturing real screenshots from a ZX
Spectrum emulator.

See [`assets/desert-rats/game-rules.md`](assets/desert-rats/game-rules.md)
for the complete game rules in English, including design notes for the
Java / libGDX reimplementation.