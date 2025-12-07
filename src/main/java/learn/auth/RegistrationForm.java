package learn.auth;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Data;
 
@Data
public class RegistrationForm {
 
  private String username;
  private String password;
  private String fullname;
  private String email;
  private String phoneNumber;
//  private long id;
  
  public User toUser(PasswordEncoder passwordEncoder, String role) {
	    User user = new User(
	        username,
	        passwordEncoder.encode(password),
	        fullname,
	        email,
	        phoneNumber,
	        role
	    );
	    return user;
	  }
  
}