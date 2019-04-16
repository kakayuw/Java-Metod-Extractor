package JavaParser;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodParser {

    private static final String FILE_PATH = "src/main/resources/extractMethodProcessor.java";

    public static void main(String[] args) throws Exception {
        MethodParser parser = new MethodParser();
        parser.getMethodCallSeqs(FILE_PATH);
    }

    public void getMethodCallSeqs(String filepath) throws IOException {
        TypeSolver typeSolver = new ReflectionTypeSolver();
        JavaParser javaParser = new JavaParser();
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        javaParser.getParserConfiguration().setSymbolResolver(symbolSolver);

        String name = "C:\\Users\\kakay\\Desktop\\self_blog\\deep-code-search\\server_connection\\javaSplits\\javaSplits\\RunnerPoolTest_502.txt";
        FileReader fr = new FileReader(name);
        BufferedReader bf = new BufferedReader(fr);
        String str;
        while ((str = bf.readLine()) != null) {
            str = "public " + str;
            ParseResult<CompilationUnit> cuResults = javaParser.parse(str);
            if (cuResults.getResult().isPresent()) {
                CompilationUnit cu = cuResults.getResult().get();
                cu.findAll(MethodCallExpr.class).forEach(mce -> {
                    try {
                        System.out.println(mce.resolve().getQualifiedSignature());
                    } catch (UnsolvedSymbolException e) {
                        String returnValue = parseException(e);
                        if (returnValue.length() > 0) System.out.println(returnValue);
                    }
                });
            }
            break;
        }



    }

    public String parseException(UnsolvedSymbolException e) {
//        System.out.println(e.getMessage() + " | " + e.getCause());
        if (e.getCause() == null) return "";
        String rootName = e.getName();
        Matcher contextMatch = Pattern.compile("Unsolved symbol in (.*?) :").matcher(e.getMessage());
        String rootContext = "";
        if (contextMatch.find()) {
            rootContext = contextMatch.group(1);
        }
        Throwable cause = e.getCause();
        String replacement = e.getName();
        while (cause != null) {
            Matcher mch = Pattern.compile("name='(.*?)'").matcher(cause.toString());
            replacement = mch.find() ? mch.group(1) : "";
            cause = cause.getCause();
        }
        if (replacement.indexOf(" ") > 0) replacement = replacement.substring(replacement.indexOf(" ") + 1);    // remove 'Solving'
//        System.out.println("rootContext:" + rootContext);
//        System.out.println("rootName:" + rootName);
//        System.out.println("replacement:" + replacement);
        return rootContext.replace(rootName, replacement).replaceAll("\\(.*?\\)", "()");
    }

}
