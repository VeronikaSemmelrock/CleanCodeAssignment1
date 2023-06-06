import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import webcrawler.WebCrawlerReportBuilder;
import webcrawler.WebCrawlerResult;
import websiteService.crawledDocument.Heading;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebCrawlerReportBuilderTest {

    private WebCrawlerResult webCrawlerResult;

    @BeforeEach
    void init() {
        List<Heading> headings = new ArrayList<>();
        headings.add(new Heading("firstHeading", 3));
        headings.add(new Heading("secondHeading", 2));
        webCrawlerResult = new WebCrawlerResult(2, "anyUrl", headings);
        WebCrawlerReportBuilder.sourceLanguage = "de";
    }

    @Test
    void getCrawlerResultAsBaseReportTest() {
        String expected = """
                input: <a>anyUrl</a>
                <br>depth: 0
                <br>source language: de
                <br>target language: english
                <br>summary:
                ### firstHeading
                ## secondHeading
                """;
        assertEquals(expected, WebCrawlerReportBuilder.getCrawlerResultAsBaseReport(webCrawlerResult, 0, "english"));
    }

    @Test
    void getCrawlerResultAsNestedReportTest() {
        String expected = """
                
                <br>----> link to <a>anyUrl</a>
                ###----> firstHeading
                ##----> secondHeading
                """;
        assertEquals(expected, WebCrawlerReportBuilder.getCrawlerResultAsNestedReport(webCrawlerResult));
    }

    @Test
    void getCrawlerResultAsBrokenLinkAtDepthTest() {
        String expected = """
                
                --> broken link <a>anyUrl</a>
                """;
        assertEquals(expected, WebCrawlerReportBuilder.getCrawlerResultAsBrokenLinkAtDepth("anyUrl", 1));
    }

}