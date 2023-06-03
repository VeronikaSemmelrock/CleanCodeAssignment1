package websiteService;

import websiteService.crawledDocument.CrawledDocument;
import websiteService.httpConnector.HttpConnector;
import websiteService.httpConnector.HttpConnectorException;

public class WebsiteService {

    private final HttpConnector httpConnector;

    public WebsiteService(HttpConnector httpConnector) {
        this.httpConnector = httpConnector;
    }

    public CrawledDocument getWebsite(String url) throws HttpConnectorException {
        return httpConnector.getDocument(url);
    }
}
