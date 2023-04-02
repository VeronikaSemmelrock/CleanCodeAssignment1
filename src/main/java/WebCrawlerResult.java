import org.jsoup.select.Elements;

import java.util.Set;

public class WebCrawlerResult {

    private final WebCrawlerConfiguration webCrawlerConfiguration;

    private Elements headings;
    private Set<String> links;

    public WebCrawlerResult(WebCrawlerConfiguration webCrawlerConfiguration) {
        this.webCrawlerConfiguration = webCrawlerConfiguration;
    }

    public void setHeadings(Elements headings) {
        this.headings = headings;
    }

    public WebCrawlerConfiguration getWebCrawlerConfiguration() {
        return webCrawlerConfiguration;
    }

    public Elements getHeadings() {
        return headings;
    }

    public Set<String> getLinks() {
        return links;
    }

    public void setLinks(Set<String> links) {
        this.links = links;
    }
}
