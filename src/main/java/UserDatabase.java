import java.sql.*;

public class UserDatabase {

    public static final String url = "jdbc:mysql://localhost:3306/shorturlsdb";
    public static final String user = "user";
    public static final String password = "user";

    public static void databaseAndTableCreation() throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, user, password);

            String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS shorturlsdb;";
            Statement statement = connection.createStatement();
            statement.executeUpdate(createDatabaseQuery);

            System.out.println("The database has been successfully created.");

            connection.setCatalog("shorturlsdb");

            String createTableQuery = "CREATE TABLE IF NOT EXISTS userTable (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "UUID VARCHAR(255)," +
                    "LONGURL VARCHAR(255)," +
                    "SHORTURL VARCHAR(255)," +
                    "COUNTER INT," +
                    "CREATIONDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    "DAYSTOEXPIRE INT," +
                    ");";

            statement.executeUpdate(createTableQuery);

            System.out.println("The table was created successfully.");

            statement.close();
            connection.close();

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error creating database or table.");
            e.printStackTrace();
        }
    }


    public static void addUserToTheTable(String uuid) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        PreparedStatement pstmt = connection.prepareStatement("INSERT INTO userTable (UUID) VALUES (?)");
        pstmt.setString(1, uuid);
        pstmt.executeUpdate();
        PreparedStatement pstmt2 = connection.prepareStatement("UPDATE userTable SET CREATIONDATE = NULL WHERE UUID = ?");
        pstmt2.setString(1, uuid);
        pstmt2.executeUpdate();
        connection.close();
    }

    public static boolean checkUserExistence(String uuid) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        String query = "SELECT UUID FROM userTable WHERE UUID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String foundUuid = rs.getString("UUID");
            } else {
                return false;
            }
        } catch (SQLException | RuntimeException e) {
            e.printStackTrace();
        }
        return true;
    }


    public static void addingUserInfoToTheTable (String uuid, String longUrl, String shortUrl, int counter, int daysValue) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        PreparedStatement pstmt = connection.prepareStatement("INSERT INTO userTable (UUID, LONGURL, SHORTURL, COUNTER, DAYSTOEXPIRE) VALUES (?, ?, ?, ?, ?)");
        pstmt.setString(1, uuid);
        pstmt.setString(2, longUrl);
        pstmt.setString(3, shortUrl);
        pstmt.setString(4, String.valueOf(counter));
        pstmt.setString(5, String.valueOf(daysValue));
        pstmt.executeUpdate();
        connection.close();
    }

    public static void selectAllUserUrls(String userUUID) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        PreparedStatement pstmt = connection.prepareStatement("SELECT id, SHORTURL FROM userTable WHERE UUID = ? and SHORTURL is not null");
        pstmt.setString(1, userUUID);

        ResultSet rs = pstmt.executeQuery();
        System.out.println("Доступные вам короткие ссылки:");
        int i = 1;
        while (rs.next()) {
            String  shortUrl = rs.getString("SHORTURL");
            System.out.println(i + "." + "  " + shortUrl);
            i++;
        }
    }

    public static void deleteSelectedUrl(String userUUID, String shortUrl) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        PreparedStatement pstmt = connection.prepareStatement("DELETE FROM userTable WHERE UUID = ? AND SHORTURL = ?");
        pstmt.setString(1, userUUID);
        pstmt.setString(2, shortUrl);
        pstmt.executeUpdate();
        System.out.println("The selected link has been removed.");
        connection.close();
    }

    public static String getLongUrl(String userUUID, String shortUrl) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        String longUrl1 = null;
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT LONGURL FROM userTable WHERE SHORTURL = ? AND UUID = ?")) {
            pstmt.setString(1, shortUrl);
            pstmt.setString(2, userUUID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                longUrl1 = rs.getString("LONGURL");
            }
        }

        catch (SQLException | RuntimeException _) {

        }
        return longUrl1;
    }


    public static int getCounter(String shortUrl) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        PreparedStatement pstmt = connection.prepareStatement("UPDATE userTable SET COUNTER = COUNTER - 1 WHERE SHORTURL = ?");
        pstmt.setString(1, shortUrl);
        pstmt.executeUpdate();
        PreparedStatement pstmt2 = connection.prepareStatement("SELECT COUNTER FROM userTable WHERE SHORTURL = ?");
        pstmt2.setString(1, shortUrl);

        ResultSet rs = pstmt2.executeQuery();
        int counterVal = 0;
        while (rs.next()) {
            int counterValuer = rs.getInt("COUNTER");
            System.out.println("Количество переходов равняется " + counterValuer);
            counterVal = counterValuer;
            if (counterVal == 0) {
                deleteMaxCounterRows();
            }
        }

        return counterVal;
    }

    public static boolean deleteBasedOnTimestamp(String shortUrl) throws SQLException {

        Connection connection = DriverManager.getConnection(url, user, password);
        PreparedStatement pstm1 = connection.prepareStatement("SELECT DAYSTOEXPIRE FROM userTable WHERE SHORTURL = ?");
        pstm1.setString(1, shortUrl);
        ResultSet rs = pstm1.executeQuery();
        while (rs.next()) {
            int number = rs.getInt("DAYSTOEXPIRE");
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM userTable WHERE CREATIONDATE < NOW() - INTERVAL ? DAY");
            pstmt.setString(1, String.valueOf(number));
            pstmt.executeUpdate();
        }

        return true;
    }

    public static void deleteMaxCounterRows() throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        PreparedStatement pstmt = connection.prepareStatement("DELETE FROM userTable WHERE COUNTER = 0");
        pstmt.executeUpdate();
        connection.close();
    }


    public static void main(String[] args) {
        try {
            databaseAndTableCreation();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}