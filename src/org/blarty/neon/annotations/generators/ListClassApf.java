package org.jini.projects.neon.annotations.generators;

import com.sun.mirror.apt.*;
import com.sun.mirror.declaration.*;
import com.sun.mirror.type.*;
import com.sun.mirror.util.*;

import java.util.Collection;
import java.util.Set;
import java.util.Arrays;

import static java.util.Collections.*;
import static com.sun.mirror.util.DeclarationVisitors.*;
import org.jini.projects.neon.annotations.Async;

/*
 * This class is used to run an annotation processor that lists class
 * names.  The functionality of the processor is analogous to the
 * ListClass doclet in the Doclet Overview.
 */
public class ListClassApf implements AnnotationProcessorFactory {
    // Process any set of annotations
    private static final Collection<String> supportedAnnotations = unmodifiableCollection(Arrays.asList("org.jini.projects.neon.annotations.*"));
    
    // No supported options
    private static final Collection<String> supportedOptions = emptySet();
    
    public Collection<String> supportedAnnotationTypes() {
        return supportedAnnotations;
    }
    
    public Collection<String> supportedOptions() {
        return supportedOptions;
    }
    
    public AnnotationProcessor getProcessorFor(
            
            Set<AnnotationTypeDeclaration> atds, AnnotationProcessorEnvironment env) {
        System.out.println("Getting Processor");
        
        for(AnnotationTypeDeclaration atdecl: atds){
            
           System.out.println("Decl: " + atdecl.toString());
           System.out.println("Decl Dclass: " + atdecl.getQualifiedName());
            if(atdecl.getQualifiedName().equals(Async.class.getName())){
                System.out.println("Getting AsyncProcessor");
                return new AsyncProcessor(env);
            }
            
            
        }
        return AnnotationProcessors.NO_OP;
    }
    
    
}