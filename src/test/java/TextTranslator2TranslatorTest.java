import okhttp3.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TextTranslator2TranslatorTest {
    private static OkHttpClient mockedClient;
    private static TextTranslator2Translator textTranslator2Translator;

    private static Response mockedResponse;
    private static Call mockedCall;
    private static ResponseBody mockedResponseBody;

    @BeforeAll
    static void setUp() throws IOException {
        mockedClient = Mockito.mock(OkHttpClient.class);
        textTranslator2Translator = new TextTranslator2Translator();
        textTranslator2Translator.setClient(mockedClient);
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
        textTranslator2Translator = null;
        mockedResponse = null;
        mockedCall = null;
        mockedResponseBody = null;
    }


//    @Test
//    void isValidLanguageTest_validInput() {
//        assertTrue(TextTranslator2Translator.isValidLanguage("german"));
//    }
//
//    @Test
//    void isValidLanguageTest_invalidInput() {
//        assertFalse(TextTranslator2Translator.isValidLanguage("deutsch"));
//    }

    @Test
    void getAbbreviationOfLanguageTest_validInput() {
        assertEquals("de", TextTranslator2Translator.getAbbreviationOfLanguage("german"));
    }

    @Test
    void getAbbreviationOfLanguageTest_invalidInput() {
        assertThrows(
                IllegalArgumentException.class,
                () -> TextTranslator2Translator.getAbbreviationOfLanguage("notALanguage")
        );
    }

    @Test
    void translateTest_validInput() throws IOException, TranslatorAPINetworkException {
        initForTranslateTest_validInput();

        assertEquals("Good morning", textTranslator2Translator.translate("Guten Morgen", "english"));
    }

    @Test
    void translateTest_invalidInputNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> textTranslator2Translator.translate(null, "german")
        );
    }

    @Test
    void translateTest_invalidInputNotALanguage() {
        assertThrows(
                IllegalArgumentException.class,
                () -> textTranslator2Translator.translate("Hello", "notALanguage")
        );
    }

    @Test
    void translateTest_APIException() {
        initForTranslateTest_APIException();
        assertThrows(
                TranslatorAPINetworkException.class,
                () -> textTranslator2Translator.translate("Hallo", "german")
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
