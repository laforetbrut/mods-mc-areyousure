# Are You Sure?

[![Build](https://github.com/laforetbrut/mods-mc-areyousure/actions/workflows/build.yml/badge.svg)](https://github.com/laforetbrut/mods-mc-areyousure/actions/workflows/build.yml)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1%20%7C%2026.1.2-green)
![Loaders](https://img.shields.io/badge/Loaders-NeoForge%20%7C%20Forge-orange)

A Minecraft troll mod. Every time you try to do anything, the game asks if you are
**really** sure, then makes you solve a captcha to prove you are not a robot.

Break a block? Are you sure? Open a chest? Captcha. Hit a zombie while it is eating you? Captcha.

## Features

- **Confirmation screen** on every interaction: breaking, attacking, using items, opening
  containers, placing blocks, interacting with entities, pick-block.
- **Text captcha** with a random 6 character code, scattered colored letters and noise dots.
- **Wrong answer** generates a brand new code. Of course.
- **One captcha per kind of action**: solve it once to attack pigs, then keep hitting pigs as
  long as you want. Switch to chopping a tree and you are asked again. Building works the same
  way, one captcha per block type you place.
- **Context aware**: the screen tells you exactly what you are about to do
  ("Do you really want to break Oak Log?").
- The game is **not paused** while you type. Good luck in the Nether.
- English and French translations.
- **Admin command** `/areyousure` to turn the mod on or off for everyone or for chosen players,
  tune the captcha and send a captcha on demand.
- **Optional on the server**: the client works alone on any server. Install it on the server too
  to get the admin command.

## Supported versions

| Minecraft | Loader   | Folder on `main`     | Branch            | Java |
|-----------|----------|----------------------|-------------------|------|
| 1.21.1    | NeoForge | `neoforge-1.21.1/`   | `neoforge-1.21.1` | 21   |
| 1.21.1    | Forge    | `forge-1.21.1/`      | `forge-1.21.1`    | 21   |
| 26.1.2    | NeoForge | `neoforge-26.1.2/`   | `neoforge-26.1.2` | 25   |
| 26.1.2    | Forge    | `forge-26.1.2/`      | `forge-26.1.2`    | 25   |

## Installation

1. Install [NeoForge](https://neoforged.net/) or [Forge](https://files.minecraftforge.net/)
   for your Minecraft version.
2. Download the jar matching your version and loader from the
   [Releases](https://github.com/laforetbrut/mods-mc-areyousure/releases) page.
3. Drop it into your `mods` folder.
4. Launch the game and regret it.

## Admin command

Install the mod on the server too to manage it with `/areyousure` (permission level 2).
Settings are saved per world in `<world>/areyousure.json`.

| Command | Effect |
|---------|--------|
| `/areyousure status [player]` | Show the settings, or the state of one player |
| `/areyousure enable` / `disable` | Turn the mod on or off for everyone without an override |
| `/areyousure player <targets> enable\|disable\|reset` | Force it on or off for some players, or follow the global setting again |
| `/areyousure only <targets>` | Disable it for everyone except these players |
| `/areyousure resetplayers` | Clear every player override |
| `/areyousure codelength <3-10>` | Captcha length |
| `/areyousure timeout <seconds>` | Idle time after which a pass expires (0 = never) |
| `/areyousure challenge <targets>` | Send a captcha to these players right now |

## Building from source

See [docs/BUILDING.md](docs/BUILDING.md). Short version:

```bash
cd neoforge-1.21.1
./gradlew build
```

The jar lands in `build/libs/`.

## Documentation

- [How it works](docs/HOW_IT_WORKS.md)
- [Building](docs/BUILDING.md)
- [FAQ](docs/FAQ.md)
- [Contributing](CONTRIBUTING.md)
- [Changelog](CHANGELOG.md)
- [Security policy](SECURITY.md)
- [Code of conduct](CODE_OF_CONDUCT.md)

## License

[Apache License 2.0](LICENSE)

## Credits

Author: vyrriox

---

# Are You Sure? (Version Française)

Un mod Minecraft pour troller. À chaque fois que vous essayez de faire quoi que ce soit, le jeu
vous demande si vous êtes **vraiment** sûr, puis vous fait remplir un captcha pour prouver que
vous n'êtes pas un robot.

Casser un bloc ? Vous êtes sûr ? Ouvrir un coffre ? Captcha. Frapper un zombie qui vous dévore ? Captcha.

## Caractéristiques

- **Écran de confirmation** à chaque interaction : casser, attaquer, utiliser un objet, ouvrir
  un conteneur, poser un bloc, interagir avec une entité, pick-block.
- **Captcha texte** avec un code aléatoire de 6 caractères, lettres colorées décalées et bruit visuel.
- **Mauvaise réponse** : un tout nouveau code est généré. Évidemment.
- **Un captcha par type d'action** : résolvez-le une fois pour attaquer des cochons, puis
  enchaînez les cochons autant que vous voulez. Passez à couper un arbre et la question revient.
  La construction fonctionne pareil, un captcha par type de bloc posé.
- **Contextuel** : l'écran indique précisément l'action visée
  (« Voulez-vous vraiment casser : Bûche de chêne ? »).
- Le jeu **n'est pas mis en pause** pendant la saisie. Bon courage dans le Nether.
- Traductions anglaise et française.
- **Commande admin** `/areyousure` pour activer ou désactiver le mod pour tous ou pour certains
  joueurs, régler le captcha et envoyer un captcha à la demande.
- **Optionnel côté serveur** : le client fonctionne seul sur n'importe quel serveur. Installez-le
  aussi sur le serveur pour avoir la commande admin.

## Versions supportées

| Minecraft | Loader   | Dossier sur `main`   | Branche           | Java |
|-----------|----------|----------------------|-------------------|------|
| 1.21.1    | NeoForge | `neoforge-1.21.1/`   | `neoforge-1.21.1` | 21   |
| 1.21.1    | Forge    | `forge-1.21.1/`      | `forge-1.21.1`    | 21   |
| 26.1.2    | NeoForge | `neoforge-26.1.2/`   | `neoforge-26.1.2` | 25   |
| 26.1.2    | Forge    | `forge-26.1.2/`      | `forge-26.1.2`    | 25   |

## Installation

1. Installez [NeoForge](https://neoforged.net/) ou [Forge](https://files.minecraftforge.net/)
   pour votre version de Minecraft.
2. Téléchargez le jar correspondant à votre version et votre loader depuis la page
   [Releases](https://github.com/laforetbrut/mods-mc-areyousure/releases).
3. Placez-le dans le dossier `mods`.
4. Lancez le jeu et regrettez.

## Commande admin

Installez aussi le mod sur le serveur pour le gérer avec `/areyousure` (niveau de permission 2).
Les réglages sont sauvegardés par monde dans `<monde>/areyousure.json`.

| Commande | Effet |
|----------|-------|
| `/areyousure status [joueur]` | Affiche les réglages, ou l'état d'un joueur |
| `/areyousure enable` / `disable` | Active ou désactive le mod pour tous les joueurs sans réglage individuel |
| `/areyousure player <cibles> enable\|disable\|reset` | Force l'activation ou la désactivation pour certains joueurs, ou revient au réglage global |
| `/areyousure only <cibles>` | Désactive pour tout le monde sauf ces joueurs |
| `/areyousure resetplayers` | Supprime tous les réglages individuels |
| `/areyousure codelength <3-10>` | Longueur du captcha |
| `/areyousure timeout <secondes>` | Inactivité après laquelle un passe expire (0 = jamais) |
| `/areyousure challenge <cibles>` | Envoie immédiatement un captcha à ces joueurs |

## Compiler depuis les sources

Voir [docs/BUILDING.md](docs/BUILDING.md). En bref : `cd neoforge-1.21.1` puis `./gradlew build`.
Le jar se trouve dans `build/libs/`.

## Licence

[Apache License 2.0](LICENSE)

## Credits

Author: vyrriox
