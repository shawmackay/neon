/**
 * 
 */
package org.jini.projects.neon.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *Defines that a class should be treated as an Agent even though it doesn't implement the agent methods
 * @author Calum
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Agent {  
    String name() default "";
    String namespace() default "";    
    String init() default "";
}

