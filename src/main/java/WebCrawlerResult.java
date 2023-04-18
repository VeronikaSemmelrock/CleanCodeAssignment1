import org.jsoup.select.Elements;

import java.util.Set;

public class WebCrawlerResult {

    private final WebCrawlerConfiguration webCrawlerConfiguration;

    private final Elements headings;

    public WebCrawlerResult(WebCrawlerConfiguration configuration, Elements headings, Set<String> links) {
        this.webCrawlerConfiguration = configuration;
        this.headings = headings;
    }

    public WebCrawlerConfiguration getWebCrawlerConfiguration() {
        return webCrawlerConfiguration;
    }

    public Elements getHeadings() {
        return headings;
    }

}
