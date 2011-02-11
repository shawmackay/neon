package org.jini.projects.neon.annotations.generators;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;


import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeElementDeclaration;
import com.sun.mirror.declaration.AnnotationValue;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.type.ArrayType;
import com.sun.mirror.type.DeclaredType; 
import com.sun.mirror.type.TypeMirror;
import com.sun.mirror.util.DeclarationVisitor;
import com.sun.mirror.util.DeclarationVisitors;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import com.sun.mirror.util.SourcePosition;
import com.sun.mirror.util.Types;

public class AsyncProcessor implements AnnotationProcessor {
        private AnnotationProcessorEnvironment environment;

        private TypeDeclaration asyncDeclaration;

        private DeclarationVisitor declarationVisitor;

        private PrintWriter outputFile;
        
        private String packageName;
        
        public AsyncProcessor(AnnotationProcessorEnvironment env) {
                environment = env;
                // get the annotation type declaration for our 'Note'
                // annotation.
                asyncDeclaration = environment.getTypeDeclaration("org.jini.projects.neon.annotations.Async");

                declarationVisitor = new AllDeclarationsVisitor();

        }

        public void process() {
                Collection<TypeDeclaration> declarations = environment.getTypeDeclarations();
                // Note here we use a helper method to create a declaration
                // scanner for our
                // visitor, and a no-op visitor.
                DeclarationVisitor scanner = DeclarationVisitors.getSourceOrderDeclarationScanner(declarationVisitor, DeclarationVisitors.NO_OP);
                for (TypeDeclaration declaration : declarations) {
                        declaration.accept(scanner); // invokes the
                                                        // processing on the
                                                        // scanner.
                }
        }

        private class AllDeclarationsVisitor
                        extends
                        SimpleDeclarationVisitor {
                @Override
                public void visitDeclaration(Declaration arg0) {
                        Collection<AnnotationMirror> annotations = arg0.getAnnotationMirrors();
                        for (AnnotationMirror mirror : annotations) {
                                // if the mirror in this iteration is for our
                                // note declaration...
                                if (mirror.getAnnotationType().getDeclaration().equals(asyncDeclaration)) {
                                        // print out the goodies.
                                        SourcePosition position = mirror.getPosition();
                                        Map<AnnotationTypeElementDeclaration, AnnotationValue> values = mirror.getElementValues();
                                        environment.getMessager().printNotice("Async class being generated: " + asyncDeclaration.toString());
                                        for(Map.Entry<AnnotationTypeElementDeclaration,AnnotationValue> value: values.entrySet()){
                                                if(value.getKey().getSimpleName().equals("packagename"))
                                                   packageName = (String) value.getValue().getValue();
                                        }
                                        viewClassDeclaration(arg0);

                                       
                                        
                                       
                                }
                                

                        }
                }
        }
       

        public void viewClassDeclaration(Declaration decl) {
                // TODO Auto-generated method stub
                //System.out.println("Dec SimpleName: " + decl.getSimpleName());
                //System.out.println("Dec Type: " + decl.getClass().getName());
                Collection<Modifier> c = decl.getModifiers();
//                for(Modifier m :  c){
//                        System.out.println("Modifier: " + m.name());
//                }
//                
                ClassDeclaration cdecl = (ClassDeclaration) decl;
                String className = "Async" +cdecl.getSimpleName();
                if(packageName==null)
                        packageName = cdecl.getPackage().getQualifiedName();
                
                StringBuffer buffer = new StringBuffer();
                buffer.append("package ");
                buffer.append(packageName + ";\n");
                buffer.append("/* Generated via AsyncGen Annotations */\n\n");
                buffer.append("public interface " + className +"{");
                for(MethodDeclaration method : cdecl.getMethods()){
                        viewMethodDeclaration(method,buffer);
                }
                buffer.append("}");
                try {
                        outputFile = environment.getFiler().createSourceFile(packageName +"." + className);
                        outputFile.append(buffer);
                        
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }

       
        public void viewMethodDeclaration(MethodDeclaration decl, StringBuffer buffer){
                Collection<Modifier> mods = decl.getModifiers();
                for(Modifier mod: mods){
                        if(mod.equals(Modifier.STATIC)){
//                                System.out.println("Static Modifier detected....skipping");
                                return;
                        }
                                
                }
                buffer.append("\n/*\n * Asynchronous Version of  " + decl.getSimpleName() +"\n");
                buffer.append( " * @see " + decl.getDeclaringType().getQualifiedName() + "#" + decl.toString() + "\n");
                buffer.append(" */\n");
                for(Modifier mod: mods){
                        buffer.append(mod + " ");
                }
                buffer.append("void " + decl.getSimpleName() + "(");
                

                for(ParameterDeclaration param: decl.getParameters()){
                        TypeMirror type = param.getType();
                        int dimensions = 0;
                        while (type instanceof ArrayType){
                                type = ((ArrayType) type).getComponentType();
                                dimensions++;
                        }
                        if(type instanceof DeclaredType){
                                buffer.append(((DeclaredType) type).getDeclaration().getQualifiedName());
                                for(int i=0 ;i< dimensions;i++){
                                        buffer.append("[]");
                                }
                                buffer.append(" ");
                        }
                       
                        buffer.append(param.getSimpleName() + ", ");


                }
                buffer.append("org.jini.projects.neon.host.AsyncHandler contextHandler);\n\n");
        }
        
}
