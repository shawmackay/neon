RUNNING PHOENIX & ACTIVATABLE REGGIE


STEP 1

1. WINDOWS: Edit env.bat & set you values for JAVA_HOME & JINI_HOME
   UNIX: Edit all the .sh scripts and paste in your values for JAVA_HOME & JINI_HOME
2. start-sw - start HTTPD on port 8085
3. start-phoenix
4. start-activatable-reggie - FIRST TIME ONLY

When #4 runs it will return to the command prompt with no messages -- this is the CORRECT behavior.

Run up a Lookup Browser* and you will see that Reggie gets activated


5. stop-phoenix

YOU MUST ALWAYS USE "stop-phoenix" otherwise you will get errors when you try to restart it

STEP 2:

Assuming the HTTPD is still running then ONLY run "start-phoenix" and this will automatically activate reggie.

YOU DO NOT NEED TO RUN "start-activatable-reggie" when you restart phoenix


STOPPING REGGIE
---------------

If you stop reggie from a service browser, you will need to delete reggie's log file before restarting it. The default log file used by these scripts is log/reggie.log


HOW TO CLEAN UP THE ACTIVATION SYSTEM IF THINGS GO WRONG
--------------------------------------------------------

If you have problems because you kill processes etc, you can clean up by deleting the "log" directory, before restarting Phoenix.

If Windows complains about a file sharing violation this is because one of the processes hasn't been shutdown correctly.

BIND ADDRESS ALREADY IN USE
---------------------------

You will get this error message if you are also running the Inca X IDE. If you want to the IDE at the same time, let us know and we'll explain the changes you need to make so you can use both these scripts and the IDE at the same time.


INCA X
http://www.incax.com

*You can get a free Jini 2.0 service browser from 
http://www.incax.com/service-browser.htm

THIS DISTRIBUTION IS PROVIDED FOR EDUCATIONAL PURPOSES ONLY AND IS *NOT* SUPPORTED BY INCA X
