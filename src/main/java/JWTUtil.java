import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import java.security.Key;
import javax.crypto.SecretKey;
import java.util.Date;


public class JWTUtil{

    private static SecretKey key = Jwts.SIG.HS256.key().build();

    public static String generateToken(String username){
        long expirationTime = 1000 * 30;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
        
        String jws = Jwts.builder()
            .subject(username)
            .expiration(expirationDate)
            .signWith(key)
            .compact();

        return jws;
    }

    public static boolean validateToken(String token){
        try{
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        }
        catch(JwtException ex){
            return false;
        }
    }
}
