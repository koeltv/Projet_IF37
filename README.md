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

Voici la description complète de la config:
```yaml
{
  "JOYSTICK": { paramètres liés au Joystick
    "MAIN_AXIS": { paramètres liés à l'axe principal (manche du joystick)
      "CONTROL_MOUSE": true, si l'axe principal à le contrôle de la souris
      "CONTROLS": { actions si l'axe principal ne contrôle pas la souris
        "UP": "Z", vers le haut
        "LEFT": "Q", vers la gauche
        "DOWN": "S", vers le bas
        "RIGHT": "D" vers la droite
      },
      "ON_CLICK": "F", sur clique (pression vertical) de l'axe principal
      "CLICK_REVERSED": false, si le bouton est inversé
      "DEFAULT_POSITION": { position par défaut, à renseigner pour éviter le drift
        "X": 497,
        "Y": 512,
        "MARGIN": 10 indique une "zone morte" au tour de la position par défaut
      }
    },
    "SECONDARY_AXIS": { paramètres liés à l'axe secondaire (joystick sur le manche)
      "CONTROL_MOUSE": false, si l'axe secondaire à le contrôle de la souris
      "CONTROLS": { actions si l'axe secondaire ne contrôle pas la souris
        "UP": "Z", vers le haut
        "LEFT": "Q", vers la gauche
        "DOWN": "S", vers le bas
        "RIGHT": "D" vers la droite
      },
      "ON_CLICK": "K", sur clique (pression vertical) de l'axe principal
      "CLICK_REVERSED": false, si le bouton est inversé
      "DEFAULT_POSITION": { position par défaut, à renseigner pour éviter le drift
        "X": 500,
        "Y": 509,
        "MARGIN": 10 indique une "zone morte" au tour de la position par défaut
      }
    },
    "BUTTONS": [ action des boutons, l'ordre est important (ordre de réception)
      "X", "Y", "A", "B"
    ]
  },
  "VOICE": { paramètres liés à la reconnaissance vocale
    "ENABLED": true, active ou désactive la reconnaissance vocale
    "CONFIDENCE": 0.70, degré de confiance nécessaire pour valider une entrée
    "ACTIONS": { commandes vocales reconnues sous le format suivant
      "COMMANDE VOCALE": "ACTION"
    }
  },
  "EYE_TRACKING": { paramètres liés au suivi du regard (WIP)
    "ENABLED": false, active ou désactive le suivi du regard
  }
}
```

### Actions possibles

Les actions possibles incluent les actions souris (BUTTON1, BUTTON2, BUTTON3) et clavier mis à disposition dans la classe Java `java.awt.event.KeyEvent` disponible [ici](https://docs.oracle.com/javase/7/docs/api/java/awt/event/KeyEvent.html) (retirez les "VK_").  
Vous pouvez également retrouver la liste complète [dans ce fichier](https://github.com/koeltv/Projet_IF37/blob/master/Actions.md).