# Application Bibliothèque

Cette application a été réalisée dans le cadre d'une évaluation Java. 

## Explication des choix techniques 
* Les repository sont des passe-plats avec la base de données JSON. Ce sont des singletons parce qu'on veut pouvoir utiliser le même repo quel que soit l'endroit d'où le repository est appelé. Ils sont rédigés avec les principes de clean architecture en tête : ce sont aujourd'hui des passe-plats, mais si l'on remplace le fichier json par une base de données, le remplacement de ces repo par d'autres repository pourrait se faire sans modification aux services, ou minimes.
* Idéalement, on utiliserait une base de données, le JSON forçant soit la réécriture complète à chaque interaction (coûteux), soit la volatilité des données (le risque est de perdre des données si l'application s'arrête de façon inattendue). Le JSON est le format forcé par le sujet du cours ; cette application n'ayant pas vocation à être utilisée dans le futur, on choisit d'endurer le risque de perte de données.
* On utilise FastJson pour le json, car c'est une librairie rapide et simple à prendre en main

## Faiblesses de l'application :
* On peut avoir deux utilisateurs avec le même login (à retravailler)