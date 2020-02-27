package org.smartregister.dhis.tools.penknife;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class IOUtils {

    public static void saveToDisk(String path, String fileName, byte[] bytes){
        try {
            Files.write(Paths.get(path + fileName), bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileContent(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }


}
