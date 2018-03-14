package com.patrick.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security
        .authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;

class TokenAuthenticationService {
    static final long EXPIRATIONTIME = 864_000_000; // 10 days
    static final String SECRET = "DongCena";
    static final String TOKEN_PREFIX = "Bearer";
    static final String HEADER_STRING = "Authorization";

    /**
     * Adds a singed JWT to the specified response object with the specified username
     *
     * @param res      HttpServletResponse object
     * @param username Username of authenticated principal
     */
    static void addAuthentication(HttpServletResponse res, String username, Collection<? extends GrantedAuthority> grantedAuthorities) {
        Map claims = new HashMap<String, Collection>(){{
            put("role", grantedAuthorities);
        }};
        String JWT = Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
    }

    /**
     * Returns an Authentication if the request has a valid Bearer token in it's
     * Authorization header.
     *
     * @param request HttpServletRequest object
     * @return Authentication object populated with the valid user
     */
    static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();

            return user != null ?
                    new UsernamePasswordAuthenticationToken(user, null, emptyList()) :
                    null;
        }
        return null;
    }
}