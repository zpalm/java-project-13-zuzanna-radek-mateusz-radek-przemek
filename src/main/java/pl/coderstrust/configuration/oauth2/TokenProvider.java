package pl.coderstrust.configuration.oauth2;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class TokenProvider {
    private Logger log = LoggerFactory.getLogger(TokenProvider.class);
    private AppProperties appProperties;

    public TokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String createToken(Authentication authentication) {
        String principalName = getPrincipalName(authentication.getPrincipal());
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());
        return Jwts.builder()
            .setSubject(principalName)
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret())
            .compact();
    }

    private String getPrincipalName(Object principal) {
        if (principal instanceof OAuth2User) {
            return ((OAuth2User) principal).getName();
        }
        return ((UserDetails) principal).getUsername();
    }

    public String getUserNameFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(appProperties.getAuth().getTokenSecret())
            .parseClaimsJws(token)
            .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(appProperties.getAuth().getTokenSecret()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
            SecurityContextHolder.clearContext();
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
            SecurityContextHolder.clearContext();
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            SecurityContextHolder.clearContext();
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            SecurityContextHolder.clearContext();
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
            SecurityContextHolder.clearContext();
        }
        return false;
    }
}
