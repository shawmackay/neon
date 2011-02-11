SET JINI_HOME=d:\development\jini2_0
java -Djava.util.logging.config.file=logging.properties -Djava.security.policy=\policy.all -Djini.dir=%JINI_HOME% -jar %JINI_HOME%\lib\start.jar conf\start-activatable-neon.config
