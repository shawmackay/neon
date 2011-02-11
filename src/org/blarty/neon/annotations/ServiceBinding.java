package org.jini.projects.neon.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Determines that an agent is available to be exported.<br>
 * Allowable values for <code>type</code> are 'jini' and 'ws'
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceBinding {
   public String type() default "jini";
}
