import org.jsoup.select.Elements;

public class WebCrawlerResult {
    private final int depth;
    private final String url;
    private final Elements headings;

    public WebCrawlerResult(int depth, String url, Elements headings) {
        this.depth = depth;
        this.url = url;
        this.headings = headings;
    }

    public String getUrl() {
        return url;
    }

    public Elements getHeadings() {
        return headings;
    }

    public int getDepth() {
        return depth;
    }
}
