echo This script copies the neonadmin web application to your tomcat installation
echo It requires that you have defined the tomcatBase variable in the neon configuration program, that was launched when you first installed Neon
echo If you need to change the value of the tomcatBase variable please press Ctrl-C now, and run
echo     neon0.1a_post_install.sh
echo If you are ready to copy the webapp, please press enter
read
cp lib-war/neonadmin.war "$[tomcatBase]/webapps/."
echo Installation complete