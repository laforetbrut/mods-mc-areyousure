# Are You Sure?

[![Build](https://github.com/laforetbrut/mods-mc-areyousure/actions/workflows/build.yml/badge.svg)](https://github.com/laforetbrut/mods-mc-areyousure/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
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
- **One captcha, one action**: the pass is consumed as soon as you release the key (or after
  10 seconds if unused).
- **Context aware**: the screen tells you exactly what you are about to do
  ("Do you really want to break Oak Log?").
- The game is **not paused** while you type. Good luck in the Nether.
- English and French translations.
- **Client-side only**: works on any server, the server does not need the mod.

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

[MIT](LICENSE)

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
- **Un captcha, une action** : le passe est consommé dès que la touche est relâchée (ou après
  10 secondes s'il n'est pas utilisé).
- **Contextuel** : l'écran indique précisément l'action visée
  (« Voulez-vous vraiment casser : Bûche de chêne ? »).
- Le jeu **n'est pas mis en pause** pendant la saisie. Bon courage dans le Nether.
- Traductions anglaise et française.
- **100 % client** : fonctionne sur n'importe quel serveur, le serveur n'a pas besoin du mod.

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

## Compiler depuis les sources

Voir [docs/BUILDING.md](docs/BUILDING.md). En bref : `cd neoforge-1.21.1` puis `./gradlew build`.
Le jar se trouve dans `build/libs/`.

## Licence

[MIT](LICENSE)

## Credits

Author: vyrriox
