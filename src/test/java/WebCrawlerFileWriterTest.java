import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import webcrawler.WebCrawlerFileWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WebCrawlerFileWriterTest {

    private static final Path pathToFile = Path.of(WebCrawlerFileWriter.fileName);

    @BeforeEach
    void init() throws IOException {
        Files.deleteIfExists(pathToFile);
    }

    @Test
    void writeToFileTest() throws IOException {
        String content = "fileContent";
        WebCrawlerFileWriter.writeToFile(content);
        assertEquals("fileContent", Files.readString(pathToFile));
    }

    @AfterAll
    static void teardown() throws IOException {
        Files.deleteIfExists(pathToFile);
    }
}