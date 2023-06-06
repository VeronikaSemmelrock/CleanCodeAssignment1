import websiteService.crawledDocument.CrawledDocument;
import websiteService.httpConnector.HttpConnector;
import websiteService.httpConnector.HttpConnectorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import websiteService.WebsiteService;

import static org.junit.jupiter.api.Assertions.*;

class WebsiteServiceTest {

    private WebsiteService websiteService;
    private HttpConnector httpConnectorMock;

    private CrawledDocument crawledDocumentMock;

    @BeforeEach
    void init() {
        httpConnectorMock = Mockito.mock(HttpConnector.class);
        websiteService = new WebsiteService(httpConnectorMock);
        crawledDocumentMock = Mockito.mock(CrawledDocument.class);
    }

    @Test
    void nullTest() throws HttpConnectorException {
        Mockito.when(httpConnectorMock.getDocument(Mockito.anyString())).thenReturn(null);

        assertNull(websiteService.getWebsite("url"));
    }

    @Test
    void successTest() throws HttpConnectorException {
        Mockito.when(httpConnectorMock.getDocument(Mockito.anyString())).thenReturn(crawledDocumentMock);

        assertEquals(crawledDocumentMock, websiteService.getWebsite("url"));
    }
}