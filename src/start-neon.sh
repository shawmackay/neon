export JINI_HOME=/Volumes/Data/Development/Java/Jini2.1
java -Djava.util.logging.config.file=logging.properties -Djava.security.policy=conf/policy.all -Djini.dir=$JINI_HOME -jar $JINI_HOME/lib/start.jar conf/start-activatable-neon.config
