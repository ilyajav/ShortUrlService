import java.util.UUID;

public class User {

    public static String createUserUuid() {
        UUID newUUID = UUID.randomUUID();
        String uuidAsString = newUUID.toString();

        System.out.println("Your generated UUID - " + uuidAsString);

        return uuidAsString;
    }

}