package com.javacodegeeks.advanced.compiler;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

@SupportedSourceVersion( SourceVersion.RELEASE_7 )
@SupportedAnnotationTypes( "*" )
public class extractMethodProcessor extends AbstractProcessor {
    private final MethodScanner scanner;

    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
    }

    public extractMethodProcessor( final MethodScanner scanner ) {
        this.scanner = scanner;
    }

    public boolean process( final Set< ? extends TypeElement > types, final RoundEnvironment environment ) {
        System.out.println(trees.getClass().getName());
        if( !environment.processingOver() ) {
            for( final Element element: environment.getRootElements() ) {

                TreePath tp = this.trees.getPath(element);
                scanner.scan(  tp, this.trees );
                MethodScanner methodScanner = new MethodScanner();
                List<MethodTree> methodTrees = methodScanner.scan(tp, this.trees);
                for(MethodTree mt: methodTrees) {
                    if (mt.getReturnType() == null) continue; // ignore all init function
                    System.out.print( "" + mt.getReturnType() +  " "+ mt.getName() + "(" + mt.getParameters() + ")");
                    System.out.println("" + mt.getBody());
                    System.out.println("################################################");
                }

            }
        }
        return true;
    }
}
