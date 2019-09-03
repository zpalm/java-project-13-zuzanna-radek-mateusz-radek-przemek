package pl.coderstrust.configuration.oauth2;

import java.io.IOException;
import java.util.Collections;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            if (!requestUri.equals("/auth/login")) {
                String jwt = getJwtFromRequest(request);
                if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                    UserPrincipal userPrincipal = new UserPrincipal(tokenProvider.getUserNameFromToken(jwt), "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, ((UserDetails) userPrincipal).getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    if (authentication != null) {
                        if (authentication.getPrincipal() instanceof UserDetails) {
                            SecurityContextHolder.clearContext();
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        }
                    }
                    if (isRequestWithTokenAuthorization(request)) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
}
