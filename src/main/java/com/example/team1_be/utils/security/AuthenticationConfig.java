package com.example.team1_be.utils.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.example.team1_be.domain.User.Role.RoleType;
import com.example.team1_be.utils.security.auth.CustomAccessDeniedHandler;
import com.example.team1_be.utils.security.auth.CustomAuthenticationEntryPoint;
import com.example.team1_be.utils.security.auth.jwt.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class AuthenticationConfig {
    private final JwtProvider jwtProvider;
    private final ObjectMapper om;
    private final Environment env;

    @Value("${cors.origin}")
    private String CORS_ORIGIN;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .httpBasic()
                        .disable()
                        .csrf()
                        .disable()
                        .cors()
                        .configurationSource(request -> {
                                CorsConfiguration corsConfiguration = new CorsConfiguration();
                                corsConfiguration.setAllowedOrigins(List.of(CORS_ORIGIN));
                                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                                corsConfiguration.setAllowedHeaders(List.of("*"));
                                corsConfiguration.addExposedHeader("Authorization");
                                return corsConfiguration;
                        })
                        .and()
                        .headers()
                        .frameOptions()
                        .disable()
                        .and()

                        .sessionManagement()
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and()

                        .authorizeRequests()
                        .antMatchers("/api/h2-console/**")
                        .permitAll()
                        .antMatchers("/api/api/**", "/api/swagger-ui/**", "/api/v3/api-docs/**", "/api/swagger-resources/**")
                        .permitAll()
                        .antMatchers("/api/login/kakao")
                        .permitAll()
                        .antMatchers("/api/auth/**")
                        .permitAll()
                        .antMatchers(HttpMethod.POST, "/api/group")
                        .hasRole(RoleType.ROLE_ADMIN.getAuth())
                        .antMatchers(HttpMethod.GET, "/api/group")
                        .hasAnyRole(RoleType.ROLE_ADMIN.getAuth(), RoleType.ROLE_MEMBER.getAuth())
                        .antMatchers(HttpMethod.GET, "/api/group/invitation")
                        .hasRole(RoleType.ROLE_ADMIN.getAuth())
                        .antMatchers(HttpMethod.POST, "/api/group/invitation")
                        .hasRole(RoleType.ROLE_MEMBER.getAuth())
                        .antMatchers(HttpMethod.GET, "/api/group/invitation/information/**")
                        .hasRole(RoleType.ROLE_MEMBER.getAuth())
                        .antMatchers(HttpMethod.GET, "/api/schedule/application/**")
                        .hasRole(RoleType.ROLE_MEMBER.getAuth())
                        .antMatchers(HttpMethod.PUT, "/api/schedule/application")
                        .hasRole(RoleType.ROLE_MEMBER.getAuth())
                        .antMatchers(HttpMethod.GET, "/api/schedule/fix/month/**")
                        .hasAnyRole(RoleType.ROLE_ADMIN.getAuth(), RoleType.ROLE_MEMBER.getAuth())
                        .antMatchers(HttpMethod.GET, "/api/schedule/fix/day/**")
                        .hasAnyRole(RoleType.ROLE_ADMIN.getAuth(), RoleType.ROLE_MEMBER.getAuth())
                        .antMatchers(HttpMethod.GET, "/api/schedule/remain/week/**")
                        .hasAnyRole(RoleType.ROLE_ADMIN.getAuth())
                        .antMatchers(HttpMethod.GET, "/api/schedule/recommend/**")
                        .hasRole(RoleType.ROLE_ADMIN.getAuth())
                        .antMatchers(HttpMethod.POST, "/api/schedule/fix/**")
                        .hasRole(RoleType.ROLE_ADMIN.getAuth())
                        .antMatchers(HttpMethod.GET, "/api/schedule/status/**")
                        .hasAnyRole(RoleType.ROLE_ADMIN.getAuth(), RoleType.ROLE_MEMBER.getAuth())
                        .antMatchers(HttpMethod.POST, "/api/schedule/worktime")
                        .hasAnyRole(RoleType.ROLE_ADMIN.getAuth())
                        .antMatchers(HttpMethod.GET, "/api/schedule/worktime/**")
                        .hasAnyRole(RoleType.ROLE_ADMIN.getAuth())
                        .antMatchers("/error")
                        .permitAll()
                        .anyRequest()
                        .denyAll()
                        .and()
                        .addFilterBefore(new CombinedFilter(jwtProvider, om),
                                UsernamePasswordAuthenticationFilter.class);

                if (!isLocalMode()) {
                        http.headers()
                                .contentSecurityPolicy("script-src 'self'");
                }

        authorizeLogin(http);

        authorizeGroup(http);

                return http.build();
        }

    private void applyCorsPolicy(HttpSecurity http) throws Exception {
        http.cors()
                .configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(Collections.singletonList("*"));
                    corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowCredentials(true);
                    corsConfiguration.setAllowedHeaders(
                            Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
                    corsConfiguration.setExposedHeaders(Arrays.asList("Authorization"));
                    return corsConfiguration;
                });
    }

}