JINI_HOME=$[jinihome]

START_CONFIG=config/phoenix.config

$[javaloc] -DjiniLib=$JINI_HOME/lib -Djava.security.policy=policy.all -jar $JINI_HOME/lib/phoenix.jar -stop $START_CONFIG
