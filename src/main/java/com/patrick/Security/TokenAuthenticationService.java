package com.patrick.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TokenAuthenticationService {
    private static final long EXPIRATIONTIME = 864_000_000; // 10 days
    private static final String SECRET = "DongCena";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String HEADER_STRING = "Authorization";

    /**
     * Adds a singed JWT to the specified response object with the specified username
     *
     * @param res      HttpServletResponse object
     * @param username Username of authenticated principal
     */
    public static void addAuthentication(HttpServletResponse res,
                                         String username,
                                         Collection<? extends GrantedAuthority> grantedAuthorities) {
        String JWT = createToken(username, grantedAuthorities);
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
    }

    /**
     * Returns an Authentication if the request has a valid Bearer token in it's
     * Authorization header.
     *
     * @param request HttpServletRequest object
     * @return Authentication object populated with the valid user
     */
    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {

            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, "")).getBody();

            List<String> roleList = (List<String>) claims.get("role");

            for (String r : roleList) System.out.println(r);
            Collection<? extends GrantedAuthority> grantedAuthorities = roleList.stream()
                    .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            return claims.getSubject() != null ?
                    new UsernamePasswordAuthenticationToken(claims.getSubject(), null, grantedAuthorities) :
                    null;
        }
        return null;
    }

    /**
     * Creates a JWT token from the with the specified username and granted authorities.
     *
     * @param username           Username of sub
     * @param grantedAuthorities Collection of GrantedAuthorities
     * @return JWT token
     */
    public static String createToken(String username, Collection<? extends GrantedAuthority> grantedAuthorities) {
        Map<String, Object> claims = new HashMap<String, Object>() {{
            put("sub", username);
            put("role", grantedAuthorities.stream().map(GrantedAuthority::toString).collect(Collectors.toList()));
        }};

        return Jwts.builder()
                .setSubject(username)
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }
}