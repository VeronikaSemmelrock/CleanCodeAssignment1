import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import websiteService.crawledDocument.Heading;
import websiteService.crawledDocument.JsoupCrawledDocument;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class JsoupCrawledDocumentTest {

    private static JsoupCrawledDocument websiteWithLanguage;
    private static JsoupCrawledDocument websiteWithoutLanguage;

    private final static String FILE_PATH = "testfiles";

    @BeforeAll
    static void init() throws IOException {
        websiteWithLanguage = getWebsite("websiteWithLang.html");
        websiteWithoutLanguage = getWebsite("websiteWithoutLang.html");
    }

    private static JsoupCrawledDocument getWebsite(String filename) throws IOException {
        String html = Files.readString(Path.of(FILE_PATH, filename));
        Document document = Jsoup.parse(html);
        return new JsoupCrawledDocument(document);
    }

    @Test
    void getSourceLanguageTest() {
        assertEquals("de", websiteWithLanguage.getSourceLanguage());
    }

    @Test
    void getSourceLanguageUnknownTest() {
        assertEquals("undetectable", websiteWithoutLanguage.getSourceLanguage());
    }

    @Test
    void getLinksTest() {
        Set<String> expectedLinks = getExpectedLinks();

        assertIterableEquals(expectedLinks, websiteWithLanguage.getLinks());
    }

    @Test
    void getHeadingsTest() {
        List<String> expectedHtmlHeadings = IntStream.range(1, 7).mapToObj(this::getHtmlHeadingForTest).collect(Collectors.toList());
        List<String> actualHtmlHeadings = websiteWithLanguage.getHeadings().stream().map(this::headingToHtmlHeadingString).collect(Collectors.toList());

        assertIterableEquals(expectedHtmlHeadings, actualHtmlHeadings);
    }

    private String getHtmlHeadingForTest(int level) {
        Element element = new Element("h" + level);
        element.html(level + "");
        return element.toString();
    }

    private String headingToHtmlHeadingString(Heading heading) {
        return "<h" + heading.getIndent() + ">" + heading.getText() + "</h" + heading.getIndent() + ">";
    }
    private Set<String> getExpectedLinks() {
        Set<String> expectedLinks = new HashSet<>();
        expectedLinks.add("https://www.aau.at/");
        expectedLinks.add("https://campus.aau.at/");
        return expectedLinks;
    }
}