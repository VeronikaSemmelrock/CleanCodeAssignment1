package translatorService;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextTranslator2TranslatorService implements TranslatorService {

    private final static Map<String, String> languagesWithAbbreviations = createLanguagesWithAbbreviations();
    private final static String KEY = "e8fef7c6e0mshfc66f325458218cp1d656cjsn56f6f32be12c";
    private OkHttpClient client = new OkHttpClient();

    private static Map<String, String> createLanguagesWithAbbreviations() {
        Map<String, String> languagesWithAbbreviations = new HashMap<>();
        languagesWithAbbreviations.put("afrikaans", "af");
        languagesWithAbbreviations.put("albanian", "sq");
        languagesWithAbbreviations.put("amharic", "am");
        languagesWithAbbreviations.put("arabic", "ar");
        languagesWithAbbreviations.put("armenian", "hy");
        languagesWithAbbreviations.put("azerbaijani", "az");
        languagesWithAbbreviations.put("basque", "eu");
        languagesWithAbbreviations.put("belarusian", "be");
        languagesWithAbbreviations.put("bengali", "bn");
        languagesWithAbbreviations.put("bulgarian", "bg");
        languagesWithAbbreviations.put("croatian", "hr");
        languagesWithAbbreviations.put("czech", "cs");
        languagesWithAbbreviations.put("danish", "da");
        languagesWithAbbreviations.put("dutch", "nl");
        languagesWithAbbreviations.put("english", "en");
        languagesWithAbbreviations.put("estonian", "et");
        languagesWithAbbreviations.put("filipino", "tl");
        languagesWithAbbreviations.put("finnish", "fi");
        languagesWithAbbreviations.put("french", "fr");
        languagesWithAbbreviations.put("georgian", "ka");
        languagesWithAbbreviations.put("german", "de");
        languagesWithAbbreviations.put("greek", "el");
        languagesWithAbbreviations.put("hawaiian", "haw");
        languagesWithAbbreviations.put("hungarian", "hu");
        languagesWithAbbreviations.put("icelandic", "is");
        languagesWithAbbreviations.put("indonesian", "id");
        languagesWithAbbreviations.put("italian", "it");
        languagesWithAbbreviations.put("japanese", "ja");
        languagesWithAbbreviations.put("korean", "ko");
        languagesWithAbbreviations.put("norwegian", "no");
        languagesWithAbbreviations.put("polish", "pl");
        languagesWithAbbreviations.put("portuguese", "pt");
        languagesWithAbbreviations.put("romanian", "ro");
        languagesWithAbbreviations.put("russian", "ru");
        languagesWithAbbreviations.put("serbian", "sr");
        languagesWithAbbreviations.put("slovak", "sk");
        languagesWithAbbreviations.put("slovenian", "sl");
        languagesWithAbbreviations.put("spanish", "es");
        languagesWithAbbreviations.put("swedish", "sv");
        languagesWithAbbreviations.put("thai", "th");
        languagesWithAbbreviations.put("turkish", "tr");
        languagesWithAbbreviations.put("ukrainian", "uk");
        languagesWithAbbreviations.put("vietnamese", "vi");
        languagesWithAbbreviations.put("chinese", "zh");
        return languagesWithAbbreviations;
    }

    @Override
    public boolean isValidLanguage(String language) {
        return languagesWithAbbreviations.containsKey(language.toLowerCase());
    }

    public static String getAbbreviationOfLanguage(String language) {
        String abbreviation = languagesWithAbbreviations.get(language.toLowerCase());
        if (abbreviation == null) {
            throw new IllegalArgumentException();
        }
        return abbreviation;
    }

    @Override
    public String translate(String text, String targetLanguage) throws TranslatorServiceException {
        try {
            RequestBody requestBody = createTranslationRequestBody(text, targetLanguage);
            Request request = createTranslationRequest(requestBody);
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                return getTranslatedTextFromResponse(response);
            } else {
                throw new TranslatorServiceException("Translation did not work!");
            }
        } catch (Exception e) {
            throw new TranslatorServiceException("Translation did not work!");
        }
    }

    private String getTranslatedTextFromResponse(Response response) throws IOException {
        JSONObject responseBody = new JSONObject(response.body().string());
        return responseBody.getJSONObject("data").getString("translatedText");
    }

    private Request createTranslationRequest(RequestBody requestBody) {
        return new Request.Builder()
                .url("https://text-translator2.p.rapidapi.com/translate")
                .post(requestBody)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("X-RapidAPI-Key", KEY)
                .addHeader("X-RapidAPI-Host", "text-translator2.p.rapidapi.com")
                .build();
    }

    private RequestBody createTranslationRequestBody(String text, String targetLanguage) throws IllegalArgumentException {
        if (text == null) {
            throw new IllegalArgumentException();
        }
        String abbreviation = getAbbreviationOfLanguage(targetLanguage);
        return new FormBody.Builder()
                .add("source_language", "auto")
                .add("target_language", abbreviation)
                .add("text", text)
                .build();
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }
}
