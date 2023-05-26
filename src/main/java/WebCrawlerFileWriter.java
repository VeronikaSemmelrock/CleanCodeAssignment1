import java.io.FileWriter;
import java.io.IOException;

public class WebCrawlerFileWriter {

    private static final String fileName = "output.md";

    public static void writeToFile(String content) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


