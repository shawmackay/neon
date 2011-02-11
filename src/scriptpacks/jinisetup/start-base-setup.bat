@echo off

title Lookup Service [activatable]

call env.bat

set START_CONFIG=config/start-base-setup.config

set JVM_ARGS=-Djava.security.policy=policy.all

$[javaloc] %JVM_ARGS% -DjiniLib=%JINI_HOME%\lib -jar %JINI_HOME%\lib\start.jar %START_CONFIG%
