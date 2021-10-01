## Réflexions d'implémentation
* Réécriture du parser pour s'approprier le code
* Représentation de l'automate avec une Map
* RegExParser : constantes > 1 octet pr éviter conflits avec table ASCII
* Cases INIT et ACCEPT de la map : pour le true / false -> LinkedList vide / null. Permet d'éviter d'avoir un tableau supplémentaire à gérer.
* NodeId : représente l'identifiant d'un noeud. Permet de manipuler plus facilement l'automate quand on le construit. C'est un Set de numéros de noeuds.
* Algorithme de création de l'automate qui est un simple parcours de l'arbre donc algo O(n)
* Utilisation d'une fonction de la forme "processNDFA(tree, final) -> NodeId" qui permet de construire l'automate de manière récursive

## Tests
* Tests unitaires sur le parser pour vérifier la véracité du nouveau parser

## Idées d'améliorations
* Faire hériter Automaton de la classe Map
