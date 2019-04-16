package fileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileIO {
    /**
     * Get filename list in one directory
     * @param dirPath
     * @return
     */
    public static List<String> getFilenameList(String dirPath) {
        // scan target directory and get file list
        File[] allFiles = new File(dirPath).listFiles();
        List<String> filenameList = new ArrayList<String>();
        for (int i = 0; i < allFiles.length; i++) {
            File file = allFiles[i];
            if (file.isFile())  filenameList.add(file.getName());
        }
        return filenameList;
    }

    /**
     * Get ONLY Java filename list in one directory
     * @param dirPath
     * @return
     */
    public static List<String> getJavaFilenameList(String dirPath) {
        List<String> allfiles = getFilenameList(dirPath);
        List<String> allJava = new ArrayList<String >();
        for (String name : allfiles) {
            if (name.substring(name.length() - 5).equals(".java")) {
                allJava.add(name);
            }
        }
        return allJava;
    }
}
