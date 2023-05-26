import java.util.List;
import java.util.Set;

public interface CrawledDocument {
    List<Heading> getHeadings();
    Set<String> getLinks();
    String getSourceLanguage();
}
