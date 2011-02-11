package org.jini.projects.neon.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Indicates what methods are invoked in reaction to a call from the Render agent.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Render {
        String action() default "index";
        Class[] populate() default {};    
        String contentType() default "text/html";
        boolean cacheable() default true;
}
