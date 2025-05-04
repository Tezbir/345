import java.sql.*;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AuthService {
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    public static String hashPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean validatePassword(String password, String stored) throws Exception {
        String[] parts = stored.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        String hashOfInput = hashPassword(password, salt).split(":")[1];
        return hashOfInput.equals(parts[1]);
    }

    public static void register(String username, String password) {
        try (Connection conn = Database.connect()) {
            String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
            byte[] salt = generateSalt();
            String hashedPassword = hashPassword(password, salt);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, hashedPassword);
                pstmt.executeUpdate();
                System.out.println("User registered successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Hashing error: " + e.getMessage());
        }
    }

    public static boolean login(String username, String password) {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT password_hash FROM users WHERE username = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    return validatePassword(password, storedHash);
                } else {
                    System.out.println("User not found.");
                }
            }
        } catch (SQLException | Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
        return false;
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
