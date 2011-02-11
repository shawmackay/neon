JINI_HOME=$[jinihome]

$[javaloc] -Djava.protocol.handler.pkgs=net.jini.url -jar $JINI_HOME/lib/tools.jar -dir $JINI_HOME/lib-dl -port 8085 -verbose

