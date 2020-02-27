package org.smartregister.dhis.tools;

import org.smartregister.dhis.tools.tools.FileSplitter;

public class Application {

    public static void main(String args[]) {

        String rootLocation = "/home/locations/";
        String sourceFile = "metadata.json";
        String destinationFilePrefix = "analyzed_";
        int fileCount = 5;

        FileSplitter splitter = new FileSplitter();
        splitter.splitDHIS2File(rootLocation, sourceFile, destinationFilePrefix, fileCount);
    }
}
