import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsoupCrawledDocument implements CrawledDocument {

    private final Document document;

    public JsoupCrawledDocument(Document document) {
        this.document = document;
    }

    @Override
    public List<Heading> getHeadings() {
        Elements jsoupHeadings = filterBlankHeadings(document.select("h1,h2,h3,h4,h5,h6"));
        return jsoupHeadings.stream().map(element -> new Heading(element.ownText(), Integer.parseInt(element.tagName().substring(1))))
                .collect(Collectors.toList());
    }

    private org.jsoup.select.Elements filterBlankHeadings(org.jsoup.select.Elements headings) {
        return headings.stream().filter(heading -> !heading.ownText().isBlank()).collect(Collectors.toCollection(org.jsoup.select.Elements::new));
    }

    @Override
    public Set<String> getLinks() {
        Elements linkElements = document.select("a");
        Set<String> links = linkElements.stream().map(element -> element.attr("href")).collect(Collectors.toSet());
        return links;
    }

    @Override
    public String getSourceLanguage() {
        String sourceLanguage = document.select("html").attr("lang");
        if (sourceLanguage == null || sourceLanguage.equals("")) {
            sourceLanguage = "undetectable";
        }
        return sourceLanguage;
    }
}