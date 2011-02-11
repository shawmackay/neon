Neon Beta 0.2 release

To build from source see INSTALL

Using Neon under Java 6

If you export an agent as a webservice when running under Sun JDK 6, you may encounter the following error:

Exception in thread "event listener notification" java.lang.LinkageError: JAXB 2.0 API is being loaded from the bootstra
p classloader, but this RI (from jar:file:/...../apache-cxf-2.1.1/lib/jaxb-impl-2.1.6.jar!/com
/sun/xml/bind/v2/model/impl/ModelBuilder.class) needs 2.1 API. Use the endorsed directory mechanism to place jaxb-api.ja
r in the bootstrap classloader. (See http://java.sun.com/j2se/1.5.0/docs/guide/standards/)

If this happens, copy the jaxb-api jar to your JRE's endorsed directory (JAVA_HOME/jre/lib/endorsed) and restart Neon



