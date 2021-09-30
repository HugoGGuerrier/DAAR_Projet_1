## Réflexions d'implémentation
Représentation de l'automate avec une Map
RegExParser : constantes > 1 octet pr éviter conflits avec table ASCII
Cases INIT et ACCEPT de la map : pour le true / false -> LinkedList vide / null. Permet d'éviter d'avoir un tableau supplémentaire à gérer.
NodeId : représente l'identifiant d'un noeud. Permet de manipuler plus facilement l'automate quand on le construit. C'est un Set de numéros de noeuds.

## Idées d'améliorations
Faire hériter Automaton de la classe Map
