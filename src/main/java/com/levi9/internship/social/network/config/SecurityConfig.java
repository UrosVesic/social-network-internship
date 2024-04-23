package com.levi9.internship.social.network.config;

import com.levi9.internship.social.network.dao.UserDao;
import com.levi9.internship.social.network.filter.JwtTokenFilter;
import io.awspring.cloud.autoconfigure.core.RegionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String URL_SIGN_UP = "/api/auth/sign-up";
    private static final String URL_SIGN_IN = "/api/auth/sign-in";
    private static final String URLS_SWAGGER_UI = "/swagger-ui/**";

    private static final String URL_SWAGGER_UI = "/swagger-ui.html";
    private static final String URL_API_DOCS = "/v3/api-docs/**";
    private static final String UIR_ACTUATOR = "/actuator/**";
    private static final String URL_FORGOT_PASSWORD = "/api/auth/forgot-password";
    private static final String URL_RESET_PASSWORD = "/api/auth/reset-password";


    private final UserDao userDao;

    private final RegionProperties regionProperties;

    public SecurityConfig(final UserDao userDao, RegionProperties regionProperties) {
        this.userDao = userDao;
        this.regionProperties = regionProperties;
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        final String[] permitAllEndpointList =
                {URL_SIGN_UP, URL_SIGN_IN, URLS_SWAGGER_UI, URL_SWAGGER_UI, URL_API_DOCS,
                        UIR_ACTUATOR, URL_FORGOT_PASSWORD, URL_RESET_PASSWORD};

        return http.csrf(AbstractHttpConfigurer::disable)
                .addFilterAfter(new JwtTokenFilter(userDao), BasicAuthenticationFilter.class)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(permitAllEndpointList).permitAll()
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
                .build();

    }

    @Bean
    CognitoIdentityProviderClient cognitoIdentityProviderClient() {
        return CognitoIdentityProviderClient.builder()
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .region(Region.of(regionProperties.getStatic()))
                .build();
    }

}
