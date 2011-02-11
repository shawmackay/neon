JINI_HOME=$[jinihome]
START_CONFIG=config/start-base-setup.config
JVM_ARGS=-Djava.security.policy=policy.all
$[javaloc] $JVM_ARGS -DjiniLib=$JINI_HOME/lib -jar $JINI_HOME/lib/start.jar $START_CONFIG
