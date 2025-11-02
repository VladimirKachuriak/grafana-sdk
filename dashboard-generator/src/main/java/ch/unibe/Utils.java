package ch.unibe;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;

public class Utils {
    /**
     * Saves dashboard json in provided folder
     *
     */
    public static void storeToFile(Path fileName, String content) {
        if (!fileName.toFile().exists()) {
            fileName.getParent().toFile().mkdirs();
        }
        try (PrintWriter out = new PrintWriter(fileName.toString());){
            out.println(content);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
