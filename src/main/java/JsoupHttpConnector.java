import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupHttpConnector implements HttpConnector {

    @Override
    public CrawledDocument get(String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        return new JsoupCrawledDocument(document);
    }
}
