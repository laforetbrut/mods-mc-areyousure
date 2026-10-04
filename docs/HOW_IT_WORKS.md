# How it works

## Layout

Gameplay code is shared per Minecraft version in `common/<mc>/`. Each loader project only keeps a
small entry point that wires loader events and networking to that shared code.

| Class                        | Side   | Role                                                          |
|------------------------------|--------|---------------------------------------------------------------|
| `client.ActionGuard`         | client | Decides whether an input goes through, manages the pass.      |
| `client.SureScreen`          | client | The two-step screen: confirmation, then captcha.              |
| `client.ClientState`         | client | Settings received from the server, defaults otherwise.        |
| `server.ServerSettings`      | server | Per-world settings, saved to `<world>/areyousure.json`.       |
| `server.AdminCommand`        | server | The `/areyousure` command tree.                               |
| `network.SettingsPayload`    | both   | Server to client sync, optional on both sides.                |
| `<loader>.AreYouSure`        | both   | Loader entry point (events, channel, command registration).   |

## Input interception

Both loaders fire `InputEvent.InteractionKeyMappingTriggered` whenever Minecraft is about to run
an attack (left click, including every tick of block breaking), a use (right click, once per hand)
or a pick-block (middle click). The loader glue asks `ActionGuard.intercept`; when it returns true
the event is cancelled and the hand swing is suppressed.

## The pass

Every input is turned into a context string describing the kind of action:

| Situation                                   | Context               |
|---------------------------------------------|-----------------------|
| Left click on a block                       | `attack:block:<id>`   |
| Left click on an entity                     | `attack:entity:<type>`|
| Right click on a block with a block in hand | `place:<item>`        |
| Right click on a block (otherwise)          | `use:block:<id>`      |
| Right click on an entity                    | `use:entity:<type>`   |
| Right click in the air with an item         | `use:item:<id>`       |
| Middle click                                | `pick:...`            |

Solving the captcha stores that context. Every later input with the same context passes; any
other context opens the screen again. Inputs without a target or an item (swinging at the air,
right clicking with an empty hand) never touch the pass. The pass also expires after an idle
period (30 seconds by default, `0` disables it).

## Server sync

When the mod is on the server, `SettingsPayload` is sent on login and after every change made with
the command: effective on/off state for that player, captcha length, idle timeout, and an optional
"challenge" flag that opens a captcha immediately. The channel is optional on both sides, so a
modded client still works on a server without the mod (defaults apply) and the server never sends
the payload to a client that does not have it.

## Version differences

Minecraft 26.1 renamed the GUI API (`GuiGraphicsExtractor`, `extractRenderState`, `text`,
`textWithWordWrap`, `item`, `KeyEvent`) and `ResourceLocation` became `Identifier`. Forge 26.1 uses
EventBus 7, where a cancellable listener returns `true` to cancel. Commands use
`Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)` in 26.1 and `hasPermission(2)` in 1.21.1.

---

# Fonctionnement

Le code est partagé par version de Minecraft dans `common/<mc>/` ; chaque projet loader ne garde
qu'un point d'entrée qui branche les événements et le réseau.

Chaque action est traduite en un contexte (attaquer un type d'entité, casser un type de bloc, poser
un type de bloc, utiliser un objet...). Résoudre le captcha mémorise ce contexte : toutes les actions
identiques passent ensuite, toute autre action rouvre l'écran. Frapper dans le vide ou cliquer main
vide ne change rien. Le passe expire après une période d'inactivité (30 secondes par défaut).

Quand le mod est aussi sur le serveur, les réglages de `/areyousure` sont envoyés au client à la
connexion et à chaque modification. Le canal est optionnel des deux côtés.
