import org.jsoup.select.Elements;

public class WebCrawlerResult {

    private final WebCrawlerConfiguration webCrawlerConfiguration;
    private final Elements headings;

    public WebCrawlerResult(WebCrawlerConfiguration configuration, Elements headings) {
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
