import org.mindrot.jbcrypt.BCrypt;

public class HashGenerator {
    public static void main(String[] args) {
        String plainPassword = "password123";
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
        
        System.out.println("===========================================");
        System.out.println("BCrypt Hash Generator");
        System.out.println("===========================================");
        System.out.println("Plain Password: " + plainPassword);
        System.out.println("Hashed Password: " + hashedPassword);
        System.out.println("===========================================");
        System.out.println("\nSQL to update admin user:");
        System.out.println("UPDATE users SET password = '" + hashedPassword + "' WHERE username = 'admin';");
        System.out.println("===========================================");
        
        // Verify it works
        boolean matches = BCrypt.checkpw(plainPassword, hashedPassword);
        System.out.println("Verification test: " + (matches ? "SUCCESS" : "FAILED"));
    }
}
