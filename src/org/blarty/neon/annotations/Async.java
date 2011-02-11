package org.jini.projects.neon.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Determines that an Asynchronous version of an interface should be generated
 * 
 * @author Calum
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Async {
    String packagename() default "[unassigned]";
    String classname() default "[unassigned]";
}
