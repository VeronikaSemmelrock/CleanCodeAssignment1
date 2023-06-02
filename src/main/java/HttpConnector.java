public interface HttpConnector {
    CrawledDocument getDocument(String url) throws HttpConnectorException;
}
