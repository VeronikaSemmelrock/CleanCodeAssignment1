package webcrawler;

import java.io.FileWriter;
import java.io.IOException;

public class WebCrawlerFileWriter {

    public static final String fileName = "output.md";

    public static void writeToFile(String content) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error occurred when trying to write to file. File content is written to console:");
            System.out.println(content);
        }
    }
}


