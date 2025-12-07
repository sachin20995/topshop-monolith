package learn;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import learn.auth.User;
import learn.auth.UserRepository;

@SpringBootApplication(scanBasePackages = {"learn", "auth", "cart", "order", "product"})
public class TopShopMonolithApplication {

    public static void main(String[] args) {
        SpringApplication.run(TopShopMonolithApplication.class, args);
    }

    // ⬇ REQUIRED BEAN #1
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ⬇ REQUIRED BEAN #2
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security Filter Chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/cart/**").authenticated()
                .requestMatchers("/api/payment/**").authenticated()
                .requestMatchers("/api/orders/**").authenticated()
                .requestMatchers("/api/auth/registerAdmin").hasRole("ADMIN")
                .requestMatchers("/api/product/**").authenticated()
                .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/registerAsAdmin").permitAll()
                .requestMatchers("/api/address/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepo) {
        return phone -> {
            User user = userRepo.findByPhoneNumber(phone);
            if (user != null) return user;
            throw new UsernameNotFoundException("User with phone '" + phone + "' not found");
        };
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtService, username -> 
            org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("")
                .roles("USER")
                .build()
        );
    }
}