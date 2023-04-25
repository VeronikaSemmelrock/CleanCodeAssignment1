import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebCrawlerConfigurationTest {

    @Test
    void isValidConfigurationTest_validInput() {
        assertTrue(WebCrawlerConfiguration.isValidConfiguration(("https://www.neromylos.com;2;german").split(";")));
        assertTrue(WebCrawlerConfiguration.isValidConfiguration(("https://www.aau.at;15;english").split(";")));
    }

    @Test
    void isValidConfigurationTest_invalidInput() {
        assertFalse(WebCrawlerConfiguration.isValidConfiguration(("neromylos.com;2;german").split(";")));
        assertFalse(WebCrawlerConfiguration.isValidConfiguration(("https://www.neromylos.com;-1;english").split(";")));
        assertFalse(WebCrawlerConfiguration.isValidConfiguration(("https://www.neromylos.com;2;notALanguage").split(";")));
        assertFalse(WebCrawlerConfiguration.isValidConfiguration(("www.aau.at;-1;notALanguage").split(";")));

    }
}
