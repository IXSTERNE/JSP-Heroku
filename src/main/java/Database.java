import java.net.URI;
import java.net.URISyntaxException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.mindrot.jbcrypt.BCrypt;

public class Database {

    private static final String DATABASE_URL = System.getenv("DATABASE_URL"); // Comment to just to test this
    private static final String RETRIEVE_PASSWORD = "SELECT password FROM users WHERE username = ?";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS users";

    public static Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI(DATABASE_URL);

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl, username, password);
    }

    public static void createUsersTable() throws URISyntaxException, SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL
            )
            """;
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(DROP_TABLE);
            stmt.execute(createTableSQL);
            System.out.println("Users table created.");
        }
    }
    
    public static void insertRecord(String username, String password) throws URISyntaxException, SQLException {
        String insertSQL = "INSERT INTO users (username, password) VALUES (?, ?)";
        String plainPassword = password;
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());


        try (Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(insertSQL)) {

            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.executeUpdate();
            System.out.println("Inserted user: " + username);
        } 
        catch (SQLException e) {
            System.err.println("Error inserting record: " + e.getMessage());
        }
    }

    public static String getHashedPassword(String username) throws URISyntaxException, SQLException{
        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(RETRIEVE_PASSWORD)){

                ps.setString(1, username);
                var rs = ps.executeQuery();
                if(rs.next()){
                    return rs.getString("password");
                }
            }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args){
        try{
            createUsersTable();
            insertRecord("Joules", "joules");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}


