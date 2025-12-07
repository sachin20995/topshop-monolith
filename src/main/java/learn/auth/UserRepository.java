package learn.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import learn.auth.User;

public interface UserRepository extends JpaRepository<User, String> {  
    // String because phoneNumber is the @Id

    User findByPhoneNumber(String phoneNumber);   // main method for login
}