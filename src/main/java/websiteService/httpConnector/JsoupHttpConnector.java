package websiteService.httpConnector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import websiteService.crawledDocument.CrawledDocument;
import websiteService.crawledDocument.JsoupCrawledDocument;

public class JsoupHttpConnector implements HttpConnector {

    @Override
    public CrawledDocument getDocument(String url) throws HttpConnectorException {
        try {
            Document document = Jsoup.connect(url).get();
            return new JsoupCrawledDocument(document);
        } catch (Exception e) {
            throw new HttpConnectorException("An error occurred trying to connect.");
        }
    }
}
