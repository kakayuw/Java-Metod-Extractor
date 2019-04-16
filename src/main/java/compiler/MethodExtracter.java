package compiler;

import fileUtil.FileIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class MethodExtracter {

    private static final int batch_size = 10;
    private static final int batch_count = 20;

    private static final String dirPath = "/mnt/sdb/heyq/origin1010_java/";


    public static void  runExtracter()throws IOException {
        // get file to precess
//        String dirPath = "C:\\Users\\kakay\\Downloads\\advanced-java-part-13\\advanced-java-part-13\\advanced-java-part-13-java7\\src\\main\\resources\\";
        List<String> fnList = FileIO.getJavaFilenameList(dirPath);
        System.out.println(String.format("Total %s java files",  String.valueOf(fnList.size())));
        // prepare compiler work
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final DiagnosticCollector< JavaFileObject > diagnostics = new DiagnosticCollector<>();
        final MethodScanner scanner = new MethodScanner();
        final extractMethodProcessor processor = new extractMethodProcessor( scanner );

        try( final StandardJavaFileManager manager =
                     compiler.getStandardFileManager( diagnostics, null, null ) ) {
            List<File> file2process = new ArrayList<File>();
            for (int i = 0; i <= fnList.size(); i++) {

                if((i % batch_size == 0 && i > 0) || (i == fnList.size() && file2process.size() > 0)) {   // update batch list
                   final Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles( file2process );
                   final CompilationTask task = compiler.getTask( null, manager, diagnostics, null, null, sources );
                   task.setProcessors( Arrays.asList( processor ) );
                   task.call();
//                   System.out.println(file2process);
                   file2process = new ArrayList<File>();
                   System.gc();
                }
                if (i < fnList.size()) {
                   String fileName = dirPath + fnList.get(i);
                   final File file = new File( fileName);
                    file2process.add(file);
                }
                System.gc();
            }
        }
    }

    public static void main( String[] args ) throws IOException {
        System.out.println("Begin parsing...");
        runExtracter();
    }
}
