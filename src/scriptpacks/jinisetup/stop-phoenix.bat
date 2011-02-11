@echo off

call env.bat

set CONFIG=config/phoenix.config

$[javaloc] -Djava.security.policy=policy.all -jar %JINI_HOME%\lib\phoenix.jar -stop %CONFIG%
