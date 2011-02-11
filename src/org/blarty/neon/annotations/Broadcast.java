/**
 * 
 */
package org.jini.projects.neon.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines that calls sent to this method should be broadcast to all agents within a certain scope.<br>
 * Scope is one of<ul><li>partition</li><li>host</li><li>domain</li><li>all</li></ul>
 * @author Calum
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Broadcast {  
    String scope() default "local";
    String name();
}
