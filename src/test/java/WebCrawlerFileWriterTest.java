import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebCrawlerFileWriterTest {

    private WebCrawlerFileWriter webCrawlerFileWriter;
    private static final String fileName = "output.md";
    private static final File targetFile = new File(fileName);
    private WebCrawlerConfiguration configuration;

    @BeforeEach
    void setUp() {
        String[] args = new String[]{"url", "2", "en"};
        configuration = new WebCrawlerConfiguration(args);
        webCrawlerFileWriter = new WebCrawlerFileWriter(targetFile);
    }

    @Test
    void writeBaseReportTest() throws IOException {
        WebCrawlerResult result = new WebCrawlerResult(configuration, getHeadings());

        webCrawlerFileWriter.writeCrawlerResultToFileAsBaseReport(result, "de");
        webCrawlerFileWriter.flush();

        String expected = """
                input: <a>url</a>
                <br>depth: 2
                <br>source language: de
                <br>target language: en
                <br>summary:
                ## h2Heading
                # h1Heading
                """;

        String actual = Files.readString(Path.of(fileName));
        assertEquals(expected, actual);
    }

    @Test
    void writeNestedReportTest() throws IOException {
        WebCrawlerResult result = new WebCrawlerResult(configuration, getHeadings());

        webCrawlerFileWriter.writeCrawlerResultToFileAsNestedReport(result, 2);
        webCrawlerFileWriter.flush();

        String expected = """
                                
                <br>----> link to <a>url</a>
                ##----> h2Heading
                #----> h1Heading                                
                """;

        String actual = Files.readString(Path.of(fileName));
        assertEquals(expected, actual);
    }

    @Test
    void writeBrokenLinkReportTest() throws IOException {
        webCrawlerFileWriter.writeCrawlerResultBrokenLinkToFileAtDepth(configuration, 3);
        webCrawlerFileWriter.flush();

        String expected = """
                                
                ------> broken link <a>url</a>
                """;

        String actual = Files.readString(Path.of(fileName));
        assertEquals(expected, actual);
    }

    @Test
    void emptyFlushTest() throws IOException {
        webCrawlerFileWriter.flush();
        String actual = Files.readString(Path.of(fileName));
        assertEquals("", actual);
    }

    private Elements getHeadings() {
        Elements headings = new Elements();
        Element element1 = new Element("h2");
        element1.html("<h2>h2Heading</h2>");
        headings.add(element1);
        Element element2 = new Element("h1");
        element2.html("<h1>h1Heading</h1>");
        headings.add(element2);
        return headings;
    }

    @AfterAll
    static void teardown() throws IOException {
        Files.deleteIfExists(targetFile.toPath());
    }
}