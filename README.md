# Projet IF37 - Manette à une main

Ceci est le dépôt GitHub de notre projet sur une manette (joystick) utilisable avec une seule main dans le cadre de l'UE [IF37](https://moodle.utt.fr/course/view.php?id=2184).  
Retrouvez la page principale du projet [ici](https://mahara.utt.fr/view/view.php?id=4858).

## Objectif

L'objectif de cette manette est de proposer une alternative à une manette traditionnelle à 2 mains.

## Solution mise en place

Notre manette est une manette à 1 main composé d'un manche (axe principal) et un joystick par-dessus, en plus de 4 boutons sur le côté.  
Les boutons peuvent être déplacés de gauche à droite pour droitier ou gaucher.  
Afin de palier à l'absence de certains boutons, la manette est multimodale grâce à l'intégration de la voix et du suivi du regard.  
Cette solution se veut low-tech, en se basant sur l'environnement Arduino et hautement configurable.  

## Modalité Vocal

Grâce à l'algorithme [SRA-5](https://github.com/truillet/ivy/blob/master/agents/sra5.zip) et la librairie [Ivy](https://gitlab.com/ivybus/ivy-java), le logiciel est capable de récupérer des commandes vocales simples configurées au préalable dans le fichier `actions.json`.
Chaque action permet de déclencher une combinaison de touches clavier et/ou cliques souris ou des actions préexistantes du logiciel.

## Modalité Visuel (WIP)

Ce logiciel est également capable d'effectuer du suivi du regard à travers la librairie [OpenCV](https://opencv.org/). Cette modalité est néanmoins plus difficile à utiliser avec une simple webcam et reste en développement pour l'instant, mais vous pouvez l'utiliser en l'activant dans le fichier `actions.json`.

## Configuration

Toute la configuration du logiciel se fait à travers le fichier `actions.json`, cela inclut :
 - Les actions du Joystick
 - Les déclencheurs vocaux
 - Le suivi du regard
 - Tout autre paramètre du logiciel