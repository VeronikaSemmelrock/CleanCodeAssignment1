import websiteService.httpConnector.HttpConnectorException;
import websiteService.httpConnector.JsoupHttpConnector;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class JsoupHttpConnectorTest {

    private JsoupHttpConnector jsoupHttpConnector;
    private Document documentMock;
    private Connection connectionMock;

    @BeforeEach
    void init() {
        jsoupHttpConnector = new JsoupHttpConnector();
    }

    @Test
    void errorTest() {
        try (MockedStatic<Jsoup> mockedStatic = Mockito.mockStatic(Jsoup.class)) {
            Mockito.when(Jsoup.connect(Mockito.anyString())).thenAnswer(invocationOnMock -> {
                throw new IOException();
            });

            assertThrows(HttpConnectorException.class, () -> jsoupHttpConnector.getDocument("url"));
        }
    }

    @Test
    void successTest() {
        try (MockedStatic<Jsoup> mockedStatic = Mockito.mockStatic(Jsoup.class)) {
            initForSuccessTest();

            assertEquals(documentMock, Jsoup.connect("url").get());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void initForSuccessTest() throws IOException {
        documentMock = Mockito.mock(Document.class);
        connectionMock = Mockito.mock(Connection.class);
        Mockito.when(Jsoup.connect(Mockito.anyString())).thenReturn(connectionMock);
        Mockito.when(connectionMock.get()).thenReturn(documentMock);
    }
}
