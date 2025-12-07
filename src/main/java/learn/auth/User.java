package learn.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class User implements UserDetails {
	private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true)
    @Id
    private String phoneNumber;

    private String email;
    private String username;
    private String password;
    private String fullname;
    private String role;

    // One user can have many addresses
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Address> addresses = new ArrayList<>();

    public User(String username, String password, String fullname, String email,
                String phoneNumber, String role) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
    }

    @Override
    public String getUsername() {
        return this.phoneNumber;  // ✅ phoneNumber is the principal
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(role));
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
