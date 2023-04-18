import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Set;
import java.util.stream.Collectors;

public class Website {

    private final Document document;

    public Website(Document document) {
        this.document = document;
    }

    public Elements getHeadings() {
        return document.select("h1,h2,h3,h4,h5,h6");
    }

    public Set<String> getLinks() {
        Elements linkElements = document.select("a");
        Set<String> links = linkElements.stream().map(element -> element.attr("href")).collect(Collectors.toSet());
        return links;
    }

    public String getSourceLanguage() {
        String sourceLanguage = document.select("html").attr("lang");
        if (sourceLanguage.equals("") || sourceLanguage == null) {
            sourceLanguage = "undetectable";
        }
        return sourceLanguage;
    }
}
