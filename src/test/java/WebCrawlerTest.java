import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WebCrawlerTest {

    @Mock
    private WebsiteService websiteService;

    @InjectMocks
    private WebCrawler webCrawler;

    private final String[] args = new String[]{"https://www.aau.at", "0", "english"};
    private final WebCrawlerConfiguration configuration = new WebCrawlerConfiguration(args);

    private final static String TEST_FILES_PATH = "testfiles";

    private final static String OUTPUT_FILE_NAME = "output.md";

    @BeforeEach
    void setup() throws IOException {
        webCrawler = new WebCrawler(configuration);
        MockitoAnnotations.openMocks(this);
        Files.deleteIfExists(Path.of(OUTPUT_FILE_NAME));
    }

    @Test
    void crawlRootDeadLinkTest() throws IOException {
        Mockito.when(websiteService.getWebsite(Mockito.any())).thenReturn(null);

        webCrawler.run();

        String outputFileContent = Files.readString(Path.of(OUTPUT_FILE_NAME));
        String expectedFileContent = """
                                
                \sbroken link <a>https://www.aau.at</a>
                """;
        assertEquals(expectedFileContent, outputFileContent);
    }

    @Test
    void crawlWith0DepthTest() throws IOException {
        Website website = getWebsite("rootWebsiteForCrawlTest.html");
        Mockito.when(websiteService.getWebsite(Mockito.any())).thenReturn(website);

        webCrawler.run();

        String outputFileContent = Files.readString(Path.of(OUTPUT_FILE_NAME));
        String expectedFileContent = """
                input: <a>https://www.aau.at</a>
                <br>depth: 0
                <br>source language: de
                <br>target language: english
                <br>summary:
                # h1 Heading
                ## h2 Heading
                ### h3 Heading
                #### h4 Heading
                ##### h5 Heading
                ###### h6 Heading
                """;
        assertEquals(expectedFileContent, outputFileContent);
    }

    @Test
    void crawlWith1DepthTest() throws IOException {
        String[] args = new String[]{"https://www.aau.at", "1", "english"};
        WebCrawlerConfiguration configuration = new WebCrawlerConfiguration(args);
        webCrawler = new WebCrawler(configuration);
        websiteService = Mockito.mock(WebsiteService.class);
        webCrawler.setWebsiteService(websiteService);

        Website rootwebsite = getWebsite("rootWebsiteForCrawlTest.html");
        Website nestedWebsite = getWebsite("nestedWebsiteForCrawlTest.html");

        Mockito.when(websiteService.getWebsite(Mockito.any()))
                .thenReturn(rootwebsite)
                .thenReturn(nestedWebsite)
                .thenReturn(null);

        webCrawler.run();

        String outputFileContent = Files.readString(Path.of(OUTPUT_FILE_NAME));
        String expectedFileContent = """
                input: <a>https://www.aau.at</a>
                <br>depth: 1
                <br>source language: de
                <br>target language: english
                <br>summary:
                # h1 Heading
                ## h2 Heading
                ### h3 Heading
                #### h4 Heading
                ##### h5 Heading
                ###### h6 Heading
                                
                <br>--> link to <a>https://campus.aau.at/</a>
                ####--> h4 Heading in nested Link
                #--> h1 Heading in nested Link
                ##--> h2 Heading in nested Link
                                
                --> broken link <a>https://www.google.at/</a>
                """;
        assertEquals(expectedFileContent, outputFileContent);
    }

    private static Website getWebsite(String filename) throws IOException {
        String html = Files.readString(Path.of(TEST_FILES_PATH, filename));
        Document document = Jsoup.parse(html);
        return new Website(document);
    }

    @AfterEach
    void teardown() throws IOException {
        Files.deleteIfExists(Path.of(OUTPUT_FILE_NAME));
    }
}