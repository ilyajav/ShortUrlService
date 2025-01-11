import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

public class UserURLs {

    public static final String fixURL = "https://clck.ru/";
    public static String shortUrl;

    public static String createShortUrl() {
        Random rand = new Random();

        String str = rand.ints(48, 123)
                .filter(num -> (num<58 || num>64) && (num<91 || num>96))
                .limit(5)
                .mapToObj(c -> (char)c).collect(StringBuffer::new, StringBuffer::append, StringBuffer::append)
                .toString();

        shortUrl = fixURL + str;
        System.out.println("Your shortened link is equal to - " + shortUrl);
        return shortUrl;
    }

    public static void getCounter(int counter) {
        System.out.println("Your conversion counter is: " + counter);
    }

    public static void visitShortUrl(String longUrl) {
        try {
            Desktop.getDesktop().browse(new URI(longUrl));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}