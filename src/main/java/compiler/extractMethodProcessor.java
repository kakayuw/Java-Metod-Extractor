package compiler;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

//@SupportedSourceVersion( SourceVersion.RELEASE_7 )
@SupportedAnnotationTypes( "*" )
public class extractMethodProcessor extends AbstractProcessor {

    private static final String outputDir = "/mnt/sdb/yh/luceneSeries/data/javaSplits/";

    private final MethodScanner scanner;

    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        //super.init(processingEnv);
        trees = Trees.instance(processingEnv);
    }

    public extractMethodProcessor( final MethodScanner scanner ) {
        this.scanner = scanner;
    }

    public boolean process( final Set< ? extends TypeElement > types, final RoundEnvironment environment ) {
//        System.out.println("entering process");
        if( environment.processingOver() ) {
//            System.out.println("not process over");
            return false;
        }
        String segName = "NULL";
        List<String> lines = new ArrayList<String>();
        for( final Element element: environment.getRootElements() ) {
            if (segName.equals("NULL")) {
                segName = element.getSimpleName().toString() + "_" + environment.getRootElements().size() + ".txt";
                System.out.println("catch file:" + environment.getRootElements().size() + " starting from from:" + segName);
            }
            // get full method body through method scanner
            TreePath tp = this.trees.getPath(element);
            if (tp == null) continue;
            scanner.scan(  tp, this.trees );
            MethodScanner methodScanner = new MethodScanner();
            List<MethodTree> methodTrees = methodScanner.scan(tp, this.trees);
            // store in line of string
            for(MethodTree mt: methodTrees) {
                if (mt.getReturnType() == null) continue; // ignore all init function
                StringBuffer methbodyBf = new StringBuffer();
                methbodyBf.append("" + mt.getReturnType() +  " " + mt.getName() + "(" + mt.getParameters() + ")");
                methbodyBf.append("" + mt.getBody());
                String methbody = methbodyBf.toString().replace("\r\n", "\t").replace("\n", "\t");
                lines.add(methbody);
            }

        }
//        System.out.println("size:"+lines.size());
        // save file
        FileWriter fw = null;
//        String outputDir = "C:\\Users\\kakay\\Downloads\\advanced-java-part-13\\advanced-java-part-13\\advanced-java-part-13-java7\\src\\main\\resources\\";

        try {
            fw = new FileWriter(outputDir + segName);
            for (String s : lines) {
                fw.write(s + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.gc();
        return true;
    }
}
