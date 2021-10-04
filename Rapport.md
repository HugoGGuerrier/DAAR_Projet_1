## Réflexions d'implémentation

### Le parser
* Réécriture du parser pour s'approprier le code
* RegExParser : constantes > 1 octet pr éviter conflits avec table ASCII

### La création du NDFA
* Représentation de l'automate avec une Map
* Cases INIT et ACCEPT de la map : pour le true / false -> LinkedList vide / null. Permet d'éviter d'avoir un tableau supplémentaire à gérer.
* NodeId : représente l'identifiant d'un noeud. Permet de manipuler plus facilement l'automate quand on le construit. C'est un Set de numéros de noeuds.
* Algorithme de création de l'automate qui est un simple parcours de l'arbre donc algo O(n)
* Utilisation d'une fonction de la forme "processNDFA(tree, final) -> NodeId" qui permet de construire l'automate de manière récursive

### La création du DFA
* L'étape de détermination de l'algorithme utilise une version modifié de l'algo vu en cours
* On créé un nouvel automate pour éviter de dégrader l'ancien et de perdre des données
* Tous les états d'un automates fini sont accessibles à partir de l'état initial (axiome)
* Utilisation d'une structure "(ensemble, NodeId)" pour associer à chaque ensemble une instance précise de NodeId
* Simple liste de travail qui se vide et se rempli de manière dynamique
* Algorithme parcourant tous les noeud

## Tests
* Tests unitaires sur le parser pour vérifier la véracité du nouveau parser
* Tests unitaires que la création du NDFA pour s'assurrer de son fonctionnement
* Tests unitaires sur la détermination
* Tests unitaires sur la recherche à l'aide d'une regex

## Benchmarking
* Utilisation du texte "History of Babylonia"
* Implémentation de plusieurs algorithmes de recherche pour tester et conclure sur leur efficacité
* Création de multiples fichiers à partir de ce texte avec des tailles différentes pour pouvoir tester la monté en charge des algorithme de recherche
* Création d'un mode d'exécution qui effectue les tests de benchmarking

## Idées d'améliorations
* Faire hériter Automaton de la classe Map pour une implémentation plus élégante
* Rajouter les parties manquante des e-regex
