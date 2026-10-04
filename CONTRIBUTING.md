# Contributing

Thanks for wanting to make Minecraft even more annoying.

## Repository layout

- `main` holds every loader/version in its own folder, plus the shared documentation.
- One branch per target (`neoforge-1.21.1`, `forge-1.21.1`, `neoforge-26.1.2`, `forge-26.1.2`)
  holds that single project at the repository root.

Gameplay code lives in `common/1.21.1/` and `common/26.1.2/`; each loader folder only holds its
entry point. A gameplay change goes into both `common/` folders (they differ only by the 26.1 GUI
API and a few renames). Never edit the per-target branches by hand: run
`scripts/split-branches.sh` from `main` to regenerate them.

## Workflow

1. Fork the repository and create a branch from `main`: `feat/short-name` or `fix/short-name`.
2. Make your change in every affected folder.
3. Build each project you touched: `./gradlew build`.
4. Test in game (`./gradlew runClient`) at least on one 1.21.1 and one 26.1.2 target.
5. Update `CHANGELOG.md` (English and French sections).
6. Open a pull request using the template.

## Commit messages

`type: descriptive message` in English. Types: `feat`, `fix`, `docs`, `chore`, `refactor`, `ci`.

## Translations

Add a file in `src/main/resources/assets/areyousure/lang/` of every folder, using the keys from
`en_us.json`.

---

# Contribuer

Merci de vouloir rendre Minecraft encore plus pénible.

## Organisation du dépôt

- `main` contient chaque loader/version dans son propre dossier, plus la documentation commune.
- Une branche par cible (`neoforge-1.21.1`, `forge-1.21.1`, `neoforge-26.1.2`, `forge-26.1.2`)
  contient ce seul projet à la racine.

Le code de jeu vit dans `common/1.21.1/` et `common/26.1.2/` ; chaque dossier loader ne contient que
son point d'entrée. Une modification de gameplay va dans les deux dossiers `common/`. Les branches
par cible ne se modifient jamais à la main : lancez `scripts/split-branches.sh` depuis `main`.

## Déroulement

1. Forkez le dépôt et créez une branche depuis `main` : `feat/nom-court` ou `fix/nom-court`.
2. Appliquez la modification dans chaque dossier concerné.
3. Compilez chaque projet touché : `./gradlew build`.
4. Testez en jeu (`./gradlew runClient`) sur au moins une cible 1.21.1 et une 26.1.2.
5. Mettez à jour `CHANGELOG.md` (sections anglaise et française).
6. Ouvrez une pull request avec le modèle fourni.

## Messages de commit

`type: message descriptif` en anglais.
