# Projet SIT213

Étape 2 - 15/09/2024 - Groupe C1

BODIN Noé

COLIN Guillaume

DOUANT Antoine

LE COQ Justine

## Organisation du projet

- `doc/` : documentation du projet
- `src/` : code source du projet
- `bin/` : exécutables du projet
- `lib/` : bibliothèques du projet
- `tests/` : tests du projet
- `rapports/` : rapports du projet par étape
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

Par exemple `./simulateur -s -mess 150 -seed 1` pour lancer en mode logique avec 150 messages et une seed de 1.
Ou, `./simulateur -s -mess 30 -form NRZT -seed 1 -nbEch 30 -ampl -1.0 1.0` pour lancer en mode logique avec 30 messages, une seed de 1, une forme NRZT, 30 échantillons par bit et une amplitude de -1.0 à 1.0.

## Génération de la documentation

Pour générer la documentation, il suffit de lancer la commande `./genDoc` dans le répertoire racine du projet.

## Nettoyage

Pour nettoyer le projet (suprimmer binaires, docs, livrables), il suffit de lancer la commande `./cleanAll` dans le répertoire racine du projet.

## Génération du livrable

Pour générer le livrable, il suffit de lancer la commande `./genDeliverable` dans le répertoire racine du projet.

## Fonctionnalités

- [X] Génération de sources aléatoires (avec ou sans seed) (booléen)
- [X] Génération de sources à partir d'un String (booléen)
- [X] Transmetteur Parfait (booléen -> booléen)
- [X] Destination Finale (booléen)
- [X] Emetteur (booléen -> analogique(float))
- [X] Transmetteur Analogique Parfait (analogue(float) -> analogue(float))
- [X] Recepteur (analogue(float) -> booléen)
- [X] Simulateur (Main du projet)
