import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static int choiceValue;
    public static int secondChoiceValue;
    public static int counterValue;
    public static int daysValue;
    public static String userUUID;
    public static String longUrl;
    public static String shortUrl;
    public static Boolean isBoolean = true;
    public static Boolean isBoolean2 = true;

    public static void main(String[] args) {

        System.out.println("Hello! Are you already our user or do you need to register?");

        while (true) {
            isBoolean2 = true;

            System.out.println("Select answer options");
            System.out.println("1 - I am a user - I will enter UUID");
            System.out.println("2 - I need to register");
            System.out.println("3 - Terminate the program");

            Scanner scanner = new Scanner(System.in);

            choiceValue = scanner.nextInt();

            while (isBoolean2) {
                switch (choiceValue) {
                    case (1): {
                        System.out.println("Enter the UUID previously issued to you");
                        userUUID = scanner.next();

                        try {
                            if (UserDatabase.checkUserExistence(userUUID)) {
                                isBoolean = true;

                                System.out.println("Welcome");

                                while (isBoolean) {
                                    System.out.println("What do you want to do?");
                                    System.out.println("Select answer options");
                                    System.out.println("1 - I want to follow the previously added link");
                                    System.out.println("2 - I need to generate a link");
                                    System.out.println("3 - I want to know all my links");
                                    System.out.println("4  - Delete previously generated link");
                                    System.out.println("5 - Back");
                                    System.out.println("6 - Terminate the program");

                                    secondChoiceValue = scanner.nextInt();

                                    switch (secondChoiceValue) {
                                        case (1): {
                                            System.out.println("Enter the previously issued short link");

                                            shortUrl = scanner.next();
                                            longUrl = UserDatabase.getLongUrl(userUUID, shortUrl);
                                            boolean result = UserDatabase.deleteBasedOnTimestamp(shortUrl);

                                            if (longUrl == null || !result || UserDatabase.getCounter(shortUrl) == 0) {
                                                System.out.println("The user does not have such a short link or it was removed due to exceeding the deadline");

                                                break;
                                            }
                                            UserURLs.visitShortUrl(longUrl);

                                            break;
                                        }
                                        case (2): {
                                            System.out.println("Enter the link you want to shorten");

                                            longUrl = scanner.next();
                                            shortUrl = UserURLs.createShortUrl();

                                            System.out.println("Do you want to set the number of clicks?");
                                            System.out.println("Select answer options");
                                            System.out.println("1 - Yes");
                                            System.out.println("2 - No");

                                            int counterNumberValue = scanner.nextInt();

                                            switch (counterNumberValue) {
                                                case (1): {
                                                    System.out.println("Enter the desired number of transitions");

                                                    counterValue = scanner.nextInt();
                                                    UserURLs.getCounter(counterValue);
                                                    break;
                                                }
                                                case (2): {
                                                    System.out.println("Set the default number of transitions");

                                                    ConfigLoader.loadConfig();
                                                    counterValue = ConfigLoader.getIntValue("DEFAULT_COUNTER");
                                                    UserURLs.getCounter(counterValue);

                                                    break;
                                                }
                                                default:
                                            }

                                            System.out.println("Do you want to set the number of days to store the link?");
                                            System.out.println("Select answer options");
                                            System.out.println("1 - Yes");
                                            System.out.println("2 - No");

                                            int daysCounterValuer = scanner.nextInt();
                                            switch (daysCounterValuer) {

                                                case (1): {
                                                    System.out.println("Enter the desired number of days");
                                                    daysValue = scanner.nextInt();

                                                    break;
                                                }
                                                case (2): {
                                                    System.out.println("Set the default number of transitions");
                                                    ConfigLoader.loadConfig();
                                                    daysValue = ConfigLoader.getIntValue("DEFAULT_INTERVAL_VALUE");

                                                    break;
                                                }
                                                default:
                                            }

                                            UserDatabase.addingUserInfoToTheTable(userUUID, longUrl, shortUrl, counterValue, daysValue);

                                            System.out.println("Link generated successfully");

                                            break;
                                        }
                                        case (3): {
                                            UserDatabase.selectAllUserUrls(userUUID);
                                            break;
                                        }
                                        case (4): {
                                            System.out.println("Enter the link you want to delete");
                                            shortUrl = scanner.next();
                                            UserDatabase.deleteSelectedUrl(userUUID, shortUrl);
                                            break;
                                        }
                                        case (5): {
                                            isBoolean = false;
                                            break;
                                        }
                                        case (6): {
                                            System.exit(0);
                                        }
                                    }
                                }
                            } else {
                                System.out.println("No users found, try registering");
                            }

                        } catch (SQLException | IOException e) {
                            throw new RuntimeException(e);
                        }

                        isBoolean2 = false;

                        break;
                    }

                    case (2): {
                        String userUUID = User.createUserUuid();
                        try {
                            UserDatabase.addUserToTheTable(userUUID);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                        isBoolean2 = false;
                        break;
                    }

                    case (3): {
                        System.exit(0);
                    }
                    default:
                }

            }
        }
    }
}