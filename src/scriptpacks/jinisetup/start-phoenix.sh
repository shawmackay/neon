JINI_HOME=$[jinihome]

START_CONFIG=config/start-phoenix.config
$[javaloc] -DjiniLib=$JINI_HOME/lib -Djava.security.policy=policy.all -jar $JINI_HOME/lib/start.jar $START_CONFIG

