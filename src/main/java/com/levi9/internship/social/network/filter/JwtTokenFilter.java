package com.levi9.internship.social.network.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.levi9.internship.social.network.dao.UserDao;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@AllArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserDao userDao;

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("Checking authorization header");
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isEmpty(header) || !header.startsWith("Bearer")) {
            log.debug("No Bearer token provided for uri {}. Clearing Security Context", request.getRequestURI());
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }
        final String token = header.replace("Bearer", "").trim();
        final DecodedJWT jwt = JWT.decode(token);

        log.debug("Checking token expiration date");
        final LocalDateTime expiryDate = jwt.getExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (expiryDate.isBefore(LocalDateTime.now())) {
            log.debug("Token provided for uri {} is expired. Clearing Security Context", request.getRequestURI());
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        log.debug("Checking token subject");
        userDao.findById(jwt.getSubject())
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist"));

        filterChain.doFilter(request, response);
    }
}
