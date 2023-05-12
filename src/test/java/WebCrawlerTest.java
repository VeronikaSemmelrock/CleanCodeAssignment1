import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WebCrawlerTest {

    private WebsiteService mockedWebsiteService;
    private WebCrawler webCrawler;

    private final String[] args = new String[]{"https://www.aau.at", "0", "english"};
    private final WebCrawlerConfiguration configuration = new WebCrawlerConfiguration(args);

    private final static String TEST_FILES_PATH = "testfiles";
    private final static String OUTPUT_FILE_NAME = "output.md";

    @BeforeEach
    void setup() throws IOException, TranslatorAPINetworkException {
        init(configuration);
    }

    private void init(WebCrawlerConfiguration configuration) throws IOException, TranslatorAPINetworkException {
        Files.deleteIfExists(Path.of(OUTPUT_FILE_NAME));

        webCrawler = new WebCrawler(configuration);
        mockedWebsiteService = Mockito.mock(WebsiteService.class);
        webCrawler.setWebsiteService(mockedWebsiteService);

        Translator mockedTranslator = Mockito.mock(Translator.class);
        Mockito.when(mockedTranslator.translate(Mockito.anyString(), Mockito.anyString()))
                .thenAnswer(i -> i.getArguments()[0]);  // When Translator#translate is invoked, just return the first parameter
        webCrawler.setTranslator(mockedTranslator);
    }

    @Test
    void crawlRootIsDeadLinkTest() throws IOException {
        initForRootIsDeadLinkTest();

        webCrawler.run();

        String outputFileContent = Files.readString(Path.of(OUTPUT_FILE_NAME));
        String expectedFileContent = """
                                
                \sbroken link <a>https://www.aau.at</a>
                """;
        assertEquals(expectedFileContent, outputFileContent);
    }

    @Test
    void crawlWith0DepthTest() throws IOException {
        initFor0DepthTest();

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
    void crawlWith1DepthTest() throws IOException, TranslatorAPINetworkException {
        initFor1DepthTest();

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

    private void initFor1DepthTest() throws IOException, TranslatorAPINetworkException {
        String[] argsFor1Depth = new String[]{"https://www.aau.at", "1", "english"};
        WebCrawlerConfiguration configurationFor1Depth = new WebCrawlerConfiguration(argsFor1Depth);
        init(configurationFor1Depth);

        Website rootWebsite = getWebsite("rootWebsiteForCrawlTest.html");
        Website nestedWebsite = getWebsite("nestedWebsiteForCrawlTest.html");

        Mockito.when(mockedWebsiteService.getWebsite(Mockito.any(WebCrawlerConfiguration.class)))
                .thenReturn(rootWebsite)    // Return the root website at the first invocation of WebsiteService#getWebsite
                .thenReturn(nestedWebsite)  // Return the nested website at the second invocation of WebsiteService#getWebsite
                .thenReturn(null);          // Return the null at the third invocation of WebsiteService#getWebsite
    }

    private static Website getWebsite(String filename) throws IOException {
        String html = Files.readString(Path.of(TEST_FILES_PATH, filename));
        Document document = Jsoup.parse(html);
        return new Website(document);
    }

    private void initFor0DepthTest() throws IOException {
        Website website = getWebsite("rootWebsiteForCrawlTest.html");
        Mockito.when(mockedWebsiteService.getWebsite(Mockito.any(WebCrawlerConfiguration.class))).thenReturn(website);
    }

    private void initForRootIsDeadLinkTest() {
        Mockito.when(mockedWebsiteService.getWebsite(Mockito.any(WebCrawlerConfiguration.class))).thenReturn(null);
    }

    @AfterEach
    void teardown() throws IOException {
        Files.deleteIfExists(Path.of(OUTPUT_FILE_NAME));
    }
}