import okhttp3.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import translatorService.TextTranslator2TranslatorService;
import translatorService.TranslatorServiceException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TextTranslator2TranslatorServiceTest {
    private static OkHttpClient mockedClient;
    private static TextTranslator2TranslatorService textTranslator2Translator;

    private static Response mockedResponse;
    private static Call mockedCall;
    private static ResponseBody mockedResponseBody;

    @BeforeAll
    static void setUp() throws IOException {
        mockedClient = Mockito.mock(OkHttpClient.class);
        textTranslator2Translator = new TextTranslator2TranslatorService();
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


    @Test
    void isValidLanguageTest_validInput() {
        assertTrue(textTranslator2Translator.isValidLanguage("german"));
    }

    @Test
    void isValidLanguageTest_invalidInput() {
        assertFalse(textTranslator2Translator.isValidLanguage("deutsch"));
    }

    @Test
    void getAbbreviationOfLanguageTest_validInput() {
        assertEquals("de", TextTranslator2TranslatorService.getAbbreviationOfLanguage("german"));
    }

    @Test
    void getAbbreviationOfLanguageTest_invalidInput() {
        assertThrows(
                IllegalArgumentException.class,
                () -> TextTranslator2TranslatorService.getAbbreviationOfLanguage("notALanguage")
        );
    }

    @Test
    void translateTest_validInput() throws IOException, TranslatorServiceException {
        initForTranslateTest_validInput();

        assertEquals("Good morning", textTranslator2Translator.translate("Guten Morgen", "english"));
    }

    @Test
    void translateTest_invalidInputNull() {
        assertThrows(
                TranslatorServiceException.class,
                () -> textTranslator2Translator.translate(null, "german")
        );
    }

    @Test
    void translateTest_invalidInputNotALanguage() {
        assertThrows(
                TranslatorServiceException.class,
                () -> textTranslator2Translator.translate("Hello", "notALanguage")
        );
    }

    @Test
    void translateTest_TranslatorServiceException() {
        initForTranslateTest_TranslatorServiceException();
        assertThrows(
                TranslatorServiceException.class,
                () -> textTranslator2Translator.translate("Hallo", "german")
        );
    }

    private void initForTranslateTest_TranslatorServiceException() {
        Mockito.when(mockedResponse.isSuccessful()).thenReturn(false);
    }

    private void initForTranslateTest_validInput() throws IOException {
        Mockito.when(mockedResponse.isSuccessful()).thenReturn(true);
        Mockito.when(mockedResponseBody.string()).thenReturn("{ \"data\": { \"translatedText\": \"Good morning\" }}");
    }

}
