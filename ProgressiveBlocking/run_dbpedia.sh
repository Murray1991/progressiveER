#!/bin/bash

BASE=/home/ubuntu/parallel-entity-resolution-framework/data/cleanCleanErDatasets/
D1=DBPedia1Profiles
D2=DBPedia2Profiles
GT=DBPediaIdDuplicates
FRACTION=1


VMSIZE=-Xmx40G

#mvn clean package
#java -cp target/progressiveER-1.0-jar-with-dependencies.jar Experiments.complete_experiment.JedaiCepExperiments_complete /Users/leonardo/Desktop/data/cleanCleanErDatasets/ imdbProfiles dbpediaProfiles moviesIdDuplicates 1
#java -cp target/progressiveER-1.0-jar-with-dependencies.jar Experiments.complete_experiment.JedaiCepExperiments_complete $BASE imdbProfiles dbpediaProfiles moviesIdDuplicates 1

# This have been done: init time -> 5H 57Min
#java $VMSIZE -cp target/progressiveER-1.0-jar-with-dependencies.jar Experiments.complete_experiment.JedaiCepExperiments_complete $BASE $D1 $D2 $GT 1 1

# This need to be executed
#java $VMSIZE -cp target/progressiveER-1.0-jar-with-dependencies.jar Experiments.complete_experiment.JedaiCepExperiments_complete $BASE $D1 $D2 $GT 4 1

# CepEntities
#java $VMSIZE -cp target/progressiveER-1.0-jar-with-dependencies.jar Experiments.complete_experiment.JedaiCepExperiments_complete $BASE $D1 $D2 $GT 4 2
#java $VMSIZE -cp target/progressiveER-1.0-jar-with-dependencies.jar Experiments.complete_experiment.JedaiCepExperiments_complete $BASE $D1 $D2 $GT 1 2

java $VMSIZE -cp target/progressiveER-1.0-jar-with-dependencies.jar Experiments.complete_experiment.JedaiCepExperiments_complete $BASE $D1 $D2 $GT 1 1
java $VMSIZE -cp target/progressiveER-1.0-jar-with-dependencies.jar Experiments.complete_experiment.JedaiCepExperiments_complete $BASE $D1 $D2 $GT 1 2