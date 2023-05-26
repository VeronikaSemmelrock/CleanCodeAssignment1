import java.util.List;

public class WebCrawlerResult {
    private final int depth;
    private final String url;
    private final List<Heading> headings;

    public WebCrawlerResult(int depth, String url, List<Heading> headings) {
        this.depth = depth;
        this.url = url;
        this.headings = headings;
    }

    public String getUrl() {
        return url;
    }

    public List<Heading> getHeadings() {
        return headings;
    }

    public int getDepth() {
        return depth;
    }
}
