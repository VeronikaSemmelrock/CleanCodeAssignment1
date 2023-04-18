import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebsiteServiceImpl implements WebsiteService {

    @Override
    public Website getWebsite(WebCrawlerConfiguration configuration) {
        String url = configuration.getUrl();
        try {
            Document document = Jsoup.connect(url).get();
            return new Website(document);
        } catch (Exception e) {
            return null;
        }
    }
}
