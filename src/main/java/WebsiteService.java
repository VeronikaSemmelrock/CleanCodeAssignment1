public class WebsiteService {

    private HttpConnector httpConnector;

    public WebsiteService(HttpConnector httpConnector) {
        this.httpConnector = httpConnector;
    }

    public CrawledDocument getWebsite(String url) {
        try {
           return httpConnector.get(url);
        } catch (Exception e) {
            return null;
        }
    }
}
