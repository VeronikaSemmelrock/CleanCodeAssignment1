import java.io.IOException;

public interface HttpConnector {
    CrawledDocument get(String url) throws IOException;
}
