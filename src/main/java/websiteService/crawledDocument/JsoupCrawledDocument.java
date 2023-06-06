package websiteService.crawledDocument;

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
        Elements filteredHeadings = filterBlankHeadings(document.select("h1,h2,h3,h4,h5,h6"));
        return filteredHeadings.stream()
                .map(element -> new Heading(element.ownText(), Integer.parseInt(element.tagName().substring(1))))
                .collect(Collectors.toList());
    }

    private Elements filterBlankHeadings(Elements headings) {
        return headings.stream().filter(heading -> !heading.ownText().isBlank()).collect(Collectors.toCollection(Elements::new));
    }

    @Override
    public Set<String> getLinks() {
        Elements linkElements = document.select("a");
        return linkElements.stream().map(element -> element.attr("href")).collect(Collectors.toSet());
    }

    @Override
    public String getSourceLanguage() {
        String sourceLanguage = document.select("html").attr("lang");
        if (sourceLanguage.equals("")) {
            sourceLanguage = "undetectable";
        }
        return sourceLanguage;
    }
}