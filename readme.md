# Projet SIT213

Étape 1 - 12/09/2024

BODIN Noé

COLIN Guillaume

DOUANT Antoine

LE COQ Justine

## Organisation du projet

- `doc/` : documentation du projet
- `src/` : code source du projet
- `bin/` : exécutables du projet
- `lib/` : bibliothèques du projet
- cleanAll : script de nettoyage
- genDeliverable : script de génsudération de l'archive
- genDoc : script de génération de la documentation
- compile : script de compilation
- runTests : script de lancement des tests
- readme.md : ce fichier
- simulateur : script du simulateur

## Compilation

Pour compiler le projet, il suffit de lancer la commande `./compile` dans le répertoire racine du projet.

## Lancement des tests

Pour lancer les tests, il suffit de lancer la commande `./runTests` dans le répertoire racine du projet.

## Lanceur de simulation

Pour lancer la simulation, il suffit de lancer la commande `./simulateur <args>` dans le répertoire racine du projet.

Par exemple `./simulateur -s -mess 150 -seed 1`

## Génération de la documentation

Pour générer la documentation, il suffit de lancer la commande `./genDoc` dans le répertoire racine du projet.

## Nettoyage

Pour nettoyer le projet, il suffit de lancer la commande `./cleanAll` dans le répertoire racine du projet.

## Génération du livrable

Pour générer le livrable, il suffit de lancer la commande `./genDeliverable` dans le répertoire racine du projet.

## Fonctionnalités

- [X] Génération de sources aléatoires (avec ou sans seed)
- [X] Génération de sources à partir d'un String
- [X] Transmetteur parfait
- [X] Destination finale
- [X] Simulateur