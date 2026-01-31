# Cursed Combat Plugin

A production-ready, generic combat system plugin inspired by high-energy melee combat mechanics. Designed for Paper/Purpur 1.21.x.

## Features
- Cursed Energy (CE) resource with regeneration.
- Ability registry with cooldowns and targeting pipeline.
- Lightweight status effects stored in PersistentDataContainer (PDC).
- Sample abilities: Cursed Dash, Impact Strike, Barrier Zone, Energy Burst, Binding Snap.
- Commands for stats, casting, technique assignment, and reload.
- Configurable VFX presets for abilities.

## How to use

### Install
1. Build the plugin: `./gradlew build`
2. Copy `build/libs/cursed-combat-1.0.0.jar` to your server's `plugins/` folder.
3. Start the server to generate the default `config.yml`.
4. If the Gradle wrapper JAR is missing, regenerate it locally with `gradle wrapper`.

### Commands
- `/curse stats` — shows CE, technique, and cooldowns summary.
- `/curse cast <abilityId>` — dev/test cast (permission: `cursedcombat.cast`).
- `/curse settechnique <player> <techniqueId>` — admin command.
- `/curse reload` — reloads configuration safely.

### Permissions
- `cursedcombat.use` — base command permission (default: true).
- `cursedcombat.cast` — allows `/curse cast` (default: op).
- `cursedcombat.admin` — admin commands (default: op).

### Example config
```yml
cursed-energy:
  max: 100.0
  regen-per-second: 5.0
  starting-technique: initiate
  particle-throttle-ms: 150

targeting:
  entity-range: 5.0
  location-range: 12.0

abilities:
  cursed_dash:
    name: "Cursed Dash"
    cost: 15.0
    cooldown-ms: 3500
    distance: 6.0
    velocity-cap: 1.5
  impact_strike:
    name: "Impact Strike"
    cost: 20.0
    cooldown-ms: 5000
    range: 3.5
    bonus-damage: 4.0
    knockback: 0.8
    debuff-duration-seconds: 3
  energy_burst:
    name: "Energy Burst"
    cost: 30.0
    cooldown-ms: 8000
    radius: 4.5
    damage: 3.0
    knockback: 0.6
    debuff-duration-seconds: 2
  binding_snap:
    name: "Binding Snap"
    cost: 25.0
    cooldown-ms: 9000
    range: 5.0
    pull-strength: 0.3
    stun-duration-seconds: 2
  barrier_zone:
    name: "Barrier Zone"
    cost: 35.0
    cooldown-ms: 12000
    radius: 5.0
    duration-seconds: 6
    tick-interval: 10
    debuff-duration-seconds: 3

techniques:
  initiate:
    name: "Initiate Technique"
    abilities:
      - cursed_dash
      - impact_strike
  sentinel:
    name: "Sentinel Technique"
    abilities:
      - barrier_zone
      - binding_snap
  disruptor:
    name: "Disruptor Technique"
    abilities:
      - energy_burst
      - impact_strike
      - cursed_dash

vfx:
  abilities:
    cursed_dash:
      cast:
        enabled: true
        particle:
          type: "CLOUD"
          count: 14
          offset:
            x: 0.2
            y: 0.1
            z: 0.2
          extra: 0.02
        sound:
          type: "ENTITY_ENDERMAN_TELEPORT"
          volume: 0.6
          pitch: 1.2
```

### Local testing checklist
1. Start a Paper or Purpur 1.21.x server with the plugin installed.
2. Run `/curse stats` to confirm CE and technique loading.
3. Use `/curse cast cursed_dash` and `/curse cast impact_strike` to verify cooldowns and CE drain.
4. Use `/curse cast barrier_zone` and watch debuffs apply inside the zone.
5. Use `/curse reload` after adjusting config values.
