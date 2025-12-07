package learn.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import learn.JwtService;
import lombok.Data;

@RestController
@RequestMapping("/api/auth")
@Data
public class AuthController {
	private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
        	System.out.println("🔑 Login API hit with phoneNumber: " + request.getPhoneNumber());
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword())
            );

            String token = jwtService.generateToken(authentication); // build JWT with user details
            User user = userRepo.findByPhoneNumber(request.getPhoneNumber());
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }
            LoginResponse response = new LoginResponse(
                    token,
                    user.getUsername(),
                    user.getFullname(),
                    user.getPhoneNumber(),
                    user.getRole()
                );

                return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
        	throw new BadCredentialsException("Invalid username or password");
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegistrationForm request) {
    	System.out.println("🔑 Login API hit with phoneNumber: ");
    	userRepo.save(request.toUser(this.passwordEncoder, "ROLE_USER"));
    	return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }
    
    @PostMapping("/registerAsAdmin")
    public ResponseEntity<Map<String, String>> registerAdmin(@RequestBody RegistrationForm form) {
        User admin = form.toUser(passwordEncoder, "ROLE_ADMIN");
        userRepo.save(admin);
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("message", "Not authenticated"));
        }

        String phoneNumber = authentication.getName(); // subject from JWT
        System.out.println(authentication.getCredentials() + "  " + authentication.getName());

        User user = userRepo.findByPhoneNumber(phoneNumber);
        if(user != null) {
        	return ResponseEntity.ok(user);
        } else {
        	return ResponseEntity.status(404).body(Map.of("message", "User not found"));
        }
//        return userRepo.findByPhoneNumber(phoneNumber)
//                .map(user -> ResponseEntity.ok(user)) // full user JSON with addresses
//                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("message", "User not found")));
    }
}
