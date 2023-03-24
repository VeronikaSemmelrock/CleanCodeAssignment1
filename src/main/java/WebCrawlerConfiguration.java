import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawlerConfiguration {
    private String url;
    private int depth;
    private String language;


    public WebCrawlerConfiguration(String[] configuration) throws IllegalArgumentException{
        //is it unclean if someone can call the constructor without executing the check first
        this.url = configuration[0];
        this.depth = Integer.parseInt(configuration[1]);
        this.language = configuration[2];
    }

    public static boolean isValidConfiguration(String[] configuration) {
        if(configuration.length == 3){
            String url = configuration[0];
            String depth = configuration[1];
            String language = configuration[2];

            if(isValidURL(url) && isValidDepth(depth) && isValidLanguage(language)){
                return true;
            };
        }
        return false;
    }

    private static boolean isValidLanguage(String language) {
        //???? - via API?
        return true;
    }

    private static boolean isValidDepth(String depth) {
        try{
            Integer.parseInt(depth);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }

    private static boolean isValidURL(String url) {
        Pattern pattern = Pattern.compile("^(http:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$");
        Matcher matcher = pattern.matcher(url);
        if(matcher.matches()){
            return true;
        }else{
            return false;
        }
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    public String getLanguage() {
        return language;
    }
}
