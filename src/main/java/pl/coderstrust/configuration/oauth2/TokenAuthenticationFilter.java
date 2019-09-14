package pl.coderstrust.configuration.oauth2;

import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenProvider tokenProvider;

    private Logger log = LoggerFactory.getLogger(TokenProvider.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        try {
            if (!requestUri.equals("/auth/login")) {
                String jwt = getJwtFromRequest(request);
                if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                    UserPrincipal userPrincipal = new UserPrincipal(tokenProvider.getUserNameFromToken(jwt), "", List.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("API_PRIVILEGE")));
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, ((UserDetails) userPrincipal).getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null) {
                        if (authentication.getPrincipal() instanceof UserDetails && !isRequestFromBrowserUserAgent(request)) {
                            SecurityContextHolder.clearContext();
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        }
                        if (isApiRequest(authentication) && isRequestUri(request, "/")) {
                            SecurityContextHolder.clearContext();
                        }
                    }
                    if (isRequestWithTokenAuthorization(request)) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                    if (isRequestFromReferer(request, "http://localhost:8080/swagger-ui.html")) {
                        SecurityContextHolder.clearContext();
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }
        if (response.getStatus() != 401) {
            filterChain.doFilter(request, response);
        }
    }

    private boolean isRequestUri(HttpServletRequest request, String uri) {
        return request.getRequestURI().equals(uri);
    }

    private boolean isApiRequest(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority("API_PRIVILEGE"));
    }

    private boolean isRequestFromReferer(HttpServletRequest request, String referer) {
        if (request.getHeader("referer") == null) {
            return false;
        }
        return request.getHeader("referer").equals(referer);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean isRequestWithTokenAuthorization(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return StringUtils.hasText(request.getHeader("Authorization")) && bearerToken.startsWith("Bearer");
    }

    private boolean isRequestFromBrowserUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent").contains("Mozilla");
    }
}
