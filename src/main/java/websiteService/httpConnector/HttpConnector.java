package websiteService.httpConnector;

import websiteService.crawledDocument.CrawledDocument;

public interface HttpConnector {
    CrawledDocument getDocument(String url) throws HttpConnectorException;
}
