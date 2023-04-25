import okhttp3.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TranslatorTest {
    private static OkHttpClient mockedClient;
    private static Translator translator;

    private static Response mockedResponse;
    private static Call mockedCall;
    private static ResponseBody mockedResponseBody;

    @BeforeAll
    static void setUp() throws IOException {
        mockedClient = Mockito.mock(OkHttpClient.class);
        translator = new Translator();
        translator.setClient(mockedClient);
        mockedResponse = Mockito.mock(Response.class);
        mockedCall = Mockito.mock(Call.class);
        mockedResponseBody = Mockito.mock(ResponseBody.class);

        Mockito.when(mockedClient.newCall(Mockito.any())).thenReturn(mockedCall);
        Mockito.when(mockedCall.execute()).thenReturn(mockedResponse);
        Mockito.when(mockedResponse.body()).thenReturn(mockedResponseBody);
    }

    @AfterAll
    static void tearDown() {
        mockedClient = null;
        translator = null;
        mockedResponse = null;
        mockedCall = null;
        mockedResponseBody = null;
    }


    @Test
    void isValidLanguageTest_validInput() {
        assertTrue(Translator.isValidLanguage("german"));
        assertTrue(Translator.isValidLanguage("hawaiian"));
    }

    @Test
    void isValidLanguageTest_invalidInput() {
        assertFalse(Translator.isValidLanguage("deutsch"));
        assertFalse(Translator.isValidLanguage("notALanguage"));
    }

    @Test
    void getAbbreviationOfLanguageTest_validInput() {
        assertEquals("de", Translator.getAbbreviationOfLanguage("german"));
    }

    @Test
    void getAbbreviationOfLanguageTest_invalidInput() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Translator.getAbbreviationOfLanguage("notALanguage")
        );
    }

    @Test
    void translateTest_validInput() throws IOException, TranslatorAPINetworkException {
        Mockito.when(mockedResponse.isSuccessful()).thenReturn(true);
        Mockito.when(mockedResponseBody.string()).thenReturn("{ \"data\": { \"translatedText\": \"Good morning\" }}");

        assertEquals("Good morning", translator.translate("Guten Morgen", "english"));
    }

    @Test
    void translateTest_invalidInput() {

        assertThrows(
                IllegalArgumentException.class,
                () -> translator.translate(null, "german")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> translator.translate("Hello", "notALanguage")
        );
    }

    @Test
    void translateTest_APIException() {
        Mockito.when(mockedResponse.isSuccessful()).thenReturn(false);
        assertThrows(
                TranslatorAPINetworkException.class,
                () -> translator.translate("Hallo", "german")
        );
    }


}
