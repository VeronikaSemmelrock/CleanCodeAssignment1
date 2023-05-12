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
    }

    @Test
    void isValidLanguageTest_invalidInput() {
        assertFalse(Translator.isValidLanguage("deutsch"));
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
        initForTranslateTest_validInput();

        assertEquals("Good morning", translator.translate("Guten Morgen", "english"));
    }

    @Test
    void translateTest_invalidInputNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> translator.translate(null, "german")
        );
    }

    @Test
    void translateTest_invalidInputNotALanguage() {
        assertThrows(
                IllegalArgumentException.class,
                () -> translator.translate("Hello", "notALanguage")
        );
    }

    @Test
    void translateTest_APIException() {
        initForTranslateTest_APIException();
        assertThrows(
                TranslatorAPINetworkException.class,
                () -> translator.translate("Hallo", "german")
        );
    }

    private void initForTranslateTest_APIException() {
        Mockito.when(mockedResponse.isSuccessful()).thenReturn(false);
    }

    private void initForTranslateTest_validInput() throws IOException {
        Mockito.when(mockedResponse.isSuccessful()).thenReturn(true);
        Mockito.when(mockedResponseBody.string()).thenReturn("{ \"data\": { \"translatedText\": \"Good morning\" }}");
    }

}
