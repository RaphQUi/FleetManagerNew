# FleetManagerNew# 🚗 FleetManager

Application Java de gestion et d'analyse d'une flotte automobile, développée dans le cadre du module **Outils technologiques pour la digitalisation** 

## Contexte métier

Pilotage du **TCO** (Total Cost of Ownership) d'une flotte mixte de 8 véhicules (électrique, essence, diesel) sur la période 2022-2024. Le cas est inspiré des problématiques d'une grande compagnie d'assurance française gérant un parc automobile pour ses collaborateurs.

L'application calcule pour chaque véhicule et chaque année : coût énergie, coût entretien, amortissement, TCO total, et coût au kilomètre. Le notebook d'analyse fournit ensuite une vision agrégée pour le pilotage.

## Architecture du projet

| Entité | Rôle |
|---|---|
| `Vehicule` | véhicule de la flotte (marque, modèle, prix, consommation, etc.) |
| `TypeEnergie` | type d'énergie utilisé (ELEC, ESS, DIES) avec prix unitaire |
| `TypeEntretien` | catégorie d'entretien (pneus, freins, révision, vidange) |
| `Entretien` | opération d'entretien sur un véhicule (coût + fréquence en km) |
| `UtilisationAnnuelle` | km parcourus par un véhicule sur une année donnée |
| `Main` | point d'entrée : chargement des CSV, résolution des relations |

## Structure du repo

