import java.io.IOException;

public interface Translator {
    boolean isValidLanguage(String language);
    String translate(String text, String targetLanguage) throws IOException, TranslatorAPINetworkException;
}
