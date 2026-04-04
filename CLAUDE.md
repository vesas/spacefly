# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the game (recommended during development)
gradlew :desktop:run

# Run tests
gradlew :core:test

# Run a single test class
gradlew :core:test --tests "com.vesas.spacefly.visibility.VisibilityTest"

# Build fat JAR + launch (run.bat wraps this)
gradlew :desktop:dist && java -jar desktop\build\libs\desktop-1.0.jar

# Debug with JVM agent attached
gradlew :desktop:debug

# JaCoCo coverage report (auto-runs after :core:test)
gradlew :core:jacocoTestReport
```

Working dir for the running game is `core/assets` (set in `desktop/build.gradle`).

## Architecture

**Modules:** `core` (all gameplay), `desktop` (LWJGL3 launcher), `graphics` (assets only, no Java).

**Game lifecycle:** `DesktopLauncher` → `SpaceflyGame.create()` loads textures via `G`, initialises `Box2DWorld`, `Player.INSTANCE`, `AbstractGameWorld.INSTANCE`, then shows `MainMenuScreen` → `GameScreen`.

**Screen flow:** `MainMenuScreen` → `GameScreen` (gameplay) → `EscScreen` (pause menu, ESC key) → `DeathScreen` (on player death). `DeathScreen` calls `SpaceflyGame.restartGame()` to reset to floor 1.

**Level generation — two-pass pipeline:**
1. **Meta pass** (`world/procedural/generator/`): `MetaRegionBuilder` builds an abstract graph of `MetaFeature` objects (rooms + corridors) using recursive portal-expansion. No physics at this stage.
2. **Real pass** (`world/procedural/`): `WorldGen` dispatches each `MetaFeature` to a `FeatureBuilder` via a `Map<Class, FeatureBuilder>` to construct Box2D bodies, register visibility segments, and generate floor `Pixmap`s.
3. **Monster pass:** `WorldGen.addMonstersPass()` places monsters after all features are built.

**Visibility system** (`visibility/`): 2D visibility polygon implementation based on Bungiu et al. Used for lighting/fog-of-war. Has its own unit tests with approval-test snapshots (`.approved.txt` files alongside test sources).

**Weapon system:** `Weapon` is a class (not enum) with an inner `Archetype` enum (BLASTER, SCATTER_GUN, BEAM_REPEATER, RAIL_LAUNCHER). `Weapon.forArchetype()` creates base instances; `Weapon.randomDrop(Random)` produces droppable weapons (non-Blaster). Players pick up weapon drops by walking near them and pressing E. The comparison HUD panel appears automatically when a `WeaponDrop` is within 1.5 world units.

**Bullet visuals:** `Bullet` carries a `Weapon.Archetype archetype` field set at fire time via `PlayerBullets.fireBullet(...)`. Each archetype has distinct visuals in `Bullet.draw()`: Blaster = warm orange `0.15×0.40`, Scatter Gun = wide yellow pellets `0.22×0.18` (normal blend), Beam Repeater = thin cyan needle `0.07×0.58` (additive blend), Rail Launcher = fat white-yellow bolt `0.26×0.72` (additive blend). Fire sounds use `G.shot.play(volume, pitch, pan)` — Scatter Gun pitch 0.62 (bassy), Beam Repeater pitch 1.70 (snappy), Rail Launcher pitch 0.38 (boomy). Muzzle flash scale also varies dramatically per archetype (0.14–0.55); Beam/Rail use additive blend on the flash sprite.

**Monster types:** `ShellMonster` (standard), `SlurgMonster` (slow, tough), `ZipperMonster` (fast swarms managed by `ZipperCloud`/`ZipperCloudManager`). `ZipperCloudManager.reset()` must be called in `destroyWorld()` to prevent stale references across floor transitions.

**Key singletons:** `Player.INSTANCE`, `AbstractGameWorld.INSTANCE`, `GenSeed.random` (global `Random`), `IDGenerator` (global non-resetting static ID counter). Be aware that `IDGenerator` never resets between runs — the `getCurrentId() < 16` threshold in `MetaRegionBuilder` depends on it starting at 0.

**Floor transitions:** `SpaceflyGame.startNewFloor()` tears down Box2D world, resets bullets, reinitialises `ProceduralGameWorld` with a new seed. `Player.resetForNewFloor()` restores health/ammo but keeps the current weapon. `SpaceflyGame.restartGame()` does the same but resets to floor 1 and calls `Player.resetFull()` (resets health, ammo, and weapon back to defaults).

**HUD** (`game/Hud.java`): All layout uses `G.wFont.getCapHeight()` queried at runtime after setting scale — never hardcoded pixel values. Health/ammo at top-left, floor number at top-centre, minimap at top-right. Weapon block (name + stat bars) at bottom-right. Weapon comparison panel shown when near a drop. `DebugHelper` FPS counter at bottom-left.

**Testing:** JUnit 5 + ApprovalTests (for visibility polygon output) + Mockito. Approval test `.approved.txt` files must be updated when visibility output changes.

**`ExperimentLauncher`** runs numbered experiments (1=quadtree, 2=noise, 3=procedural rooms) via `TestGame` — useful for isolated feature work without loading the full game.
