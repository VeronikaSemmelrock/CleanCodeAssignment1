import java.io.IOException;

public interface TranslatorService {
    boolean isValidLanguage(String language);
    String translate(String text, String targetLanguage) throws IOException, TranslatorServiceException;
}
