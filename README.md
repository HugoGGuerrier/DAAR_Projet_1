# DAAR | Projet 1 : Clone de "egrep"

Auteurs : Emilie SIAU | Hugo GUERRIER\
Version : 0.5

## Dépendances :

* Java >= 8
* Apache Ant >= 1.10.4

## Comment compiler et exécuter :

* Compiler : `ant build`
* Tester : `ant test` (Si une erreur survient : `ant test -lib lib`)
* Créer un jar : `ant jar`
* Exécuter le programme et lancer les tests Benchmark `java -jar bin/egrep.jar -b`
* Exécuter le programme et afficher l'aide `java -jar bin/egrep.jar -h`
* Nettoyer : `ant clean`
