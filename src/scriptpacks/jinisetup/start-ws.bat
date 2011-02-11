@echo off

title httpd port 8085


call env.bat

$[javaloc] -Djava.protocol.handler.pkgs=net.jini.url -jar $[jinihome]\lib\tools.jar -dir $[jinihome]\lib-dl -port 8085 -verbose
