import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import translatorService.TranslatorService;
import translatorService.TranslatorServiceException;
import webcrawler.WebCrawler;
import webcrawler.WebCrawlerScheduler;
import websiteService.WebsiteService;
import websiteService.crawledDocument.CrawledDocument;
import websiteService.crawledDocument.JsoupCrawledDocument;
import websiteService.httpConnector.HttpConnectorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebCrawlerTest {

    private WebsiteService mockedWebsiteService;
    private WebCrawler webCrawler;

    private final static String TEST_FILES_PATH = "testfiles";
    private final static String OUTPUT_FILE_NAME = "output.md";

    @BeforeEach
    void setup() throws IOException, TranslatorServiceException {
        WebCrawlerScheduler.initializeThreadPoolWithThreadCount(2);
        init(0, "english");
    }

    private void init(int maxDepth, String targetLanguage) throws IOException, TranslatorServiceException {
        Files.deleteIfExists(Path.of(OUTPUT_FILE_NAME));

        webCrawler = new WebCrawler(maxDepth, targetLanguage);
        mockedWebsiteService = Mockito.mock(websiteService.WebsiteService.class);
        webCrawler.setWebsiteService(mockedWebsiteService);

        TranslatorService mockedTranslator = Mockito.mock(TranslatorService.class);
        Mockito.when(mockedTranslator.translate(Mockito.anyString(), Mockito.anyString()))
                .thenAnswer(i -> i.getArguments()[0]);  // When Translator#translate is invoked, just return the first parameter
        webCrawler.setTranslatorService(mockedTranslator);
    }

    @Test
    void runRootIsDeadLinkTest() throws IOException, HttpConnectorException {
        initForRootIsDeadLinkTest();

        webCrawler.run(List.of("https://www.aau.at"));

        String outputFileContent = Files.readString(Path.of(OUTPUT_FILE_NAME));
        String expectedFileContent = """

                \sbroken link <a>https://www.aau.at</a>
                """;
        assertEquals(expectedFileContent, outputFileContent);
    }

    @Test
    void runWith0DepthTest() throws IOException, HttpConnectorException {
        initFor0DepthTest();

        webCrawler.run(List.of("https://www.aau.at"));

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
    void runWith1DepthSingleThreadedTest() throws IOException, TranslatorServiceException, HttpConnectorException {
        WebCrawlerScheduler.timeoutForChildThreadsInMinutes = 1;
        WebCrawlerScheduler.initializeThreadPoolWithThreadCount(1);
        initFor1DepthTest();

        webCrawler.run(List.of("https://www.aau.at"));

        String outputFileContent = Files.readString(Path.of(OUTPUT_FILE_NAME));
        String expectedFileContent = """
                https://www.aau.at
                TimeoutException when waiting on child crawling url""";

        // Should throw TimeoutException because single thread tries to start child threads but there are no threads available because only one thread is in the pool.
        // Parent starts child and waits for result (which will never return) thus timeout is reached.
        assertEquals(expectedFileContent, outputFileContent);
    }

    @Test
    void runWith1DepthLargeThreadPoolTest() throws IOException, TranslatorServiceException, HttpConnectorException {
        WebCrawlerScheduler.initializeThreadPoolWithThreadCount(10000);
        initFor1DepthTest();

        webCrawler.run(List.of("https://www.aau.at"));

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

    @Test
    void runWith3DepthMultipleUrlsTest() throws IOException, TranslatorServiceException, HttpConnectorException {
        WebCrawlerScheduler.initializeThreadPoolWithThreadCount(10000);
        initFor3DepthMultipleUrlsTest();

        webCrawler.run(List.of("https://www.aau.at", "https://campusplan.aau.at/de/", "https://www.neromylos.com"));

        String outputFileContent = Files.readString(Path.of(OUTPUT_FILE_NAME));

        assertEquals(getExpectedResultFor3DepthMultipleUrlsTest(), outputFileContent);
    }

    @Test
    void isValidUrl_invalidInput() {
        assertFalse(WebCrawler.isValidURL("neromylos.com"));    // Without https://
    }

    @Test
    void isValidUrl_validInput() {
        assertTrue(WebCrawler.isValidURL("https://www.neromylos.com"));
    }

    @Test
    void areValidUrls_invalidInput() {
        assertFalse(WebCrawler.areValidURLs(List.of("https://www.neromylos.com", "neromylos.com")));    // Without https://
    }

    @Test
    void areValidUrls_validInput() {
        assertTrue(WebCrawler.areValidURLs(List.of("https://www.neromylos.com", "https://www.aau.at")));
    }

    @Test
    void isValidDepth_invalidInput() {
        assertFalse(WebCrawler.isValidDepth(-1));
    }

    @Test
    void isValidDepth_validInput() {
        assertTrue(WebCrawler.isValidDepth(1));
    }

    @AfterEach
    void teardown() throws IOException {
        Files.deleteIfExists(Path.of(OUTPUT_FILE_NAME));
    }

    private void initFor0DepthTest() throws IOException, HttpConnectorException {
        CrawledDocument website = getCrawledDocument("rootWebsiteForCrawlTest.html");
        Mockito.when(mockedWebsiteService.getWebsite(Mockito.anyString())).thenReturn(website);
    }

    private void initFor1DepthTest() throws IOException, TranslatorServiceException, HttpConnectorException {
        init(1, "english");

        CrawledDocument rootWebsite = getCrawledDocument("rootWebsiteForCrawlTest.html");
        CrawledDocument nestedWebsite = getCrawledDocument("nestedWebsiteForCrawlTest.html");

        Mockito.when(mockedWebsiteService.getWebsite("https://www.aau.at")).thenReturn(rootWebsite);
        Mockito.when(mockedWebsiteService.getWebsite("https://campus.aau.at/")).thenReturn(nestedWebsite);
        Mockito.when(mockedWebsiteService.getWebsite("https://www.google.at/")).thenThrow(HttpConnectorException.class);
    }

    private void initFor3DepthMultipleUrlsTest() throws IOException, TranslatorServiceException, HttpConnectorException {
        init(3, "english");

        CrawledDocument rootWebsite = getCrawledDocument("rootWebsiteForCrawlTest.html");
        Mockito.when(mockedWebsiteService.getWebsite(Mockito.anyString())).thenReturn(rootWebsite);
    }

    private static CrawledDocument getCrawledDocument(String filename) throws IOException {
        String html = Files.readString(Path.of(TEST_FILES_PATH, filename));
        Document document = Jsoup.parse(html);
        return new JsoupCrawledDocument(document);
    }

    private void initForRootIsDeadLinkTest() throws HttpConnectorException {
        Mockito.when(mockedWebsiteService.getWebsite(Mockito.anyString())).thenThrow(HttpConnectorException.class);
    }


    private String getExpectedResultFor3DepthMultipleUrlsTest() {
        return """
                input: <a>https://www.aau.at</a>
                <br>depth: 3
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
                #--> h1 Heading
                ##--> h2 Heading
                ###--> h3 Heading
                ####--> h4 Heading
                #####--> h5 Heading
                ######--> h6 Heading

                <br>----> link to <a>https://campus.aau.at/</a>
                #----> h1 Heading
                ##----> h2 Heading
                ###----> h3 Heading
                ####----> h4 Heading
                #####----> h5 Heading
                ######----> h6 Heading

                <br>------> link to <a>https://campus.aau.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>------> link to <a>https://www.google.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>----> link to <a>https://www.google.at/</a>
                #----> h1 Heading
                ##----> h2 Heading
                ###----> h3 Heading
                ####----> h4 Heading
                #####----> h5 Heading
                ######----> h6 Heading

                <br>------> link to <a>https://campus.aau.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>------> link to <a>https://www.google.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>--> link to <a>https://www.google.at/</a>
                #--> h1 Heading
                ##--> h2 Heading
                ###--> h3 Heading
                ####--> h4 Heading
                #####--> h5 Heading
                ######--> h6 Heading

                <br>----> link to <a>https://campus.aau.at/</a>
                #----> h1 Heading
                ##----> h2 Heading
                ###----> h3 Heading
                ####----> h4 Heading
                #####----> h5 Heading
                ######----> h6 Heading

                <br>------> link to <a>https://campus.aau.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>------> link to <a>https://www.google.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>----> link to <a>https://www.google.at/</a>
                #----> h1 Heading
                ##----> h2 Heading
                ###----> h3 Heading
                ####----> h4 Heading
                #####----> h5 Heading
                ######----> h6 Heading

                <br>------> link to <a>https://campus.aau.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>------> link to <a>https://www.google.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading
                input: <a>https://campusplan.aau.at/de/</a>
                <br>depth: 3
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
                #--> h1 Heading
                ##--> h2 Heading
                ###--> h3 Heading
                ####--> h4 Heading
                #####--> h5 Heading
                ######--> h6 Heading

                <br>----> link to <a>https://campus.aau.at/</a>
                #----> h1 Heading
                ##----> h2 Heading
                ###----> h3 Heading
                ####----> h4 Heading
                #####----> h5 Heading
                ######----> h6 Heading

                <br>------> link to <a>https://campus.aau.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>------> link to <a>https://www.google.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>----> link to <a>https://www.google.at/</a>
                #----> h1 Heading
                ##----> h2 Heading
                ###----> h3 Heading
                ####----> h4 Heading
                #####----> h5 Heading
                ######----> h6 Heading

                <br>------> link to <a>https://campus.aau.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>------> link to <a>https://www.google.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>--> link to <a>https://www.google.at/</a>
                #--> h1 Heading
                ##--> h2 Heading
                ###--> h3 Heading
                ####--> h4 Heading
                #####--> h5 Heading
                ######--> h6 Heading

                <br>----> link to <a>https://campus.aau.at/</a>
                #----> h1 Heading
                ##----> h2 Heading
                ###----> h3 Heading
                ####----> h4 Heading
                #####----> h5 Heading
                ######----> h6 Heading

                <br>------> link to <a>https://campus.aau.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>------> link to <a>https://www.google.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>----> link to <a>https://www.google.at/</a>
                #----> h1 Heading
                ##----> h2 Heading
                ###----> h3 Heading
                ####----> h4 Heading
                #####----> h5 Heading
                ######----> h6 Heading

                <br>------> link to <a>https://campus.aau.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>------> link to <a>https://www.google.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading
                input: <a>https://www.neromylos.com</a>
                <br>depth: 3
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
                #--> h1 Heading
                ##--> h2 Heading
                ###--> h3 Heading
                ####--> h4 Heading
                #####--> h5 Heading
                ######--> h6 Heading

                <br>----> link to <a>https://campus.aau.at/</a>
                #----> h1 Heading
                ##----> h2 Heading
                ###----> h3 Heading
                ####----> h4 Heading
                #####----> h5 Heading
                ######----> h6 Heading

                <br>------> link to <a>https://campus.aau.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>------> link to <a>https://www.google.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>----> link to <a>https://www.google.at/</a>
                #----> h1 Heading
                ##----> h2 Heading
                ###----> h3 Heading
                ####----> h4 Heading
                #####----> h5 Heading
                ######----> h6 Heading

                <br>------> link to <a>https://campus.aau.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>------> link to <a>https://www.google.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>--> link to <a>https://www.google.at/</a>
                #--> h1 Heading
                ##--> h2 Heading
                ###--> h3 Heading
                ####--> h4 Heading
                #####--> h5 Heading
                ######--> h6 Heading

                <br>----> link to <a>https://campus.aau.at/</a>
                #----> h1 Heading
                ##----> h2 Heading
                ###----> h3 Heading
                ####----> h4 Heading
                #####----> h5 Heading
                ######----> h6 Heading

                <br>------> link to <a>https://campus.aau.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>------> link to <a>https://www.google.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>----> link to <a>https://www.google.at/</a>
                #----> h1 Heading
                ##----> h2 Heading
                ###----> h3 Heading
                ####----> h4 Heading
                #####----> h5 Heading
                ######----> h6 Heading

                <br>------> link to <a>https://campus.aau.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading

                <br>------> link to <a>https://www.google.at/</a>
                #------> h1 Heading
                ##------> h2 Heading
                ###------> h3 Heading
                ####------> h4 Heading
                #####------> h5 Heading
                ######------> h6 Heading
                """;
    }
}