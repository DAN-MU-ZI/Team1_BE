package com.example.team1_be.utils.security;

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
        http.httpBasic()
                .disable();

        http.csrf()
                .disable();

        http.cors()
                .configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.addExposedHeader("Authorization");
                    return corsConfiguration;
                });

        http.headers()
                .frameOptions()
                .disable();

        http.headers()
                .xssProtection();

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        if (isLocalMode()) {
            authorizeH2Console(http);
            authorizeApiAndDocs(http);

        } else {
            http.headers()
                    .contentSecurityPolicy("script-src 'self'");
        }

        http.authorizeHttpRequests()
                        .antMatchers("/**").permitAll();

//        authorizeLogin(http);
//
//        authorizeGroup(http);
//
//        authorizeSchedule(http);
//
//        authorizeError(http);
//
//        http.authorizeHttpRequests()
//                .anyRequest().denyAll();

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint(om));

        http.exceptionHandling()
                .accessDeniedHandler(new CustomAccessDeniedHandler(om));

        http.addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(new XSSProtectFilter(om),
                ChannelProcessingFilter.class);

        return http.build();
    }

		http.addFilterBefore(new CombinedFilter(jwtProvider, om),
			UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

    private void authorizeSchedule(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .antMatchers(HttpMethod.GET, "/schedule/application/**")
                .hasRole(RoleType.ROLE_MEMBER.getAuthority())
                .antMatchers(HttpMethod.PUT, "/schedule/application")
                .hasRole(RoleType.ROLE_MEMBER.getAuthority())
                .antMatchers(HttpMethod.GET, "/schedule/fix/month/**")
                .hasAnyRole(RoleType.ROLE_ADMIN.getAuthority(), RoleType.ROLE_MEMBER.getAuthority())
                .antMatchers(HttpMethod.GET, "/schedule/fix/day/**")
                .hasAnyRole(RoleType.ROLE_ADMIN.getAuthority(), RoleType.ROLE_MEMBER.getAuthority())
                .antMatchers(HttpMethod.GET, "/schedule/remain/week/**")
                .hasAnyRole(RoleType.ROLE_ADMIN.getAuthority())
                .antMatchers(HttpMethod.GET, "/schedule/recommend/**")
                .hasRole(RoleType.ROLE_ADMIN.getAuthority())
                .antMatchers(HttpMethod.POST, "/schedule/fix/**")
                .hasRole(RoleType.ROLE_ADMIN.getAuthority())
                .antMatchers(HttpMethod.GET, "/schedule/status/**")
                .hasAnyRole(RoleType.ROLE_ADMIN.getAuthority(), RoleType.ROLE_MEMBER.getAuthority())
                .antMatchers(HttpMethod.POST, "/schedule/worktime")
                .hasAnyRole(RoleType.ROLE_ADMIN.getAuthority())
                .antMatchers(HttpMethod.GET, "/schedule/worktime/**")
                .hasAnyRole(RoleType.ROLE_ADMIN.getAuthority());
    }

    private void authorizeGroup(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .antMatchers(HttpMethod.POST, "/group")
                .hasRole(RoleType.ROLE_ADMIN.getAuthority())
                .antMatchers(HttpMethod.GET, "/group")
                .hasAnyRole(RoleType.ROLE_ADMIN.getAuthority(), RoleType.ROLE_MEMBER.getAuthority())
                .antMatchers(HttpMethod.GET, "/group/invitation")
                .hasRole(RoleType.ROLE_ADMIN.getAuthority())
                .antMatchers(HttpMethod.POST, "/group/invitation")
                .hasRole(RoleType.ROLE_MEMBER.getAuthority())
                .antMatchers(HttpMethod.GET, "/group/invitation/information/**")
                .hasRole(RoleType.ROLE_MEMBER.getAuthority());
    }

    private void authorizeLogin(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .antMatchers("/login/kakao").permitAll()
                .antMatchers("/auth/**").permitAll();
    }

    private void authorizeApiAndDocs(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .antMatchers("/api/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll();
    }

    private void authorizeH2Console(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
                .antMatchers("/h2-console/**").permitAll();
    }
}