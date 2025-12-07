package learn.auth;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/address")
//@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AddressController {
	private final UserRepository userRepo;

    // Get cart for a user
    @GetMapping("/{phoneNumber}")
    public List<Address> getAddress(@PathVariable String phoneNumber) {
        User user = userRepo.findByPhoneNumber(phoneNumber);
        return user.getAddresses();
    }
    
    @PostMapping("/{phoneNumber}/add")
    public User addToUser(@PathVariable String phoneNumber, @RequestBody Address address) {
    	User user = userRepo.findByPhoneNumber(phoneNumber);
    	user.getAddresses().forEach(a -> a.setSelected(false));
    	address.setSelected(true);
    	address.setUser(user);
    	user.getAddresses().add(address);
        return userRepo.save(user);
    }
    
    @DeleteMapping("/{phoneNumber}/remove/{id}")
    public User removeFromAddresses(@PathVariable String phoneNumber, @PathVariable String id) {
        User user = userRepo.findByPhoneNumber(phoneNumber);
        user.getAddresses().removeIf(address -> address.getId().toString().equals(id));
        return userRepo.save(user);
    }
    
    @PutMapping("/{phoneNumber}/update/{id}")
    public User updateSelectAddress(@PathVariable String phoneNumber, @PathVariable String id) {
        User user = userRepo.findByPhoneNumber(phoneNumber);
        user.getAddresses().forEach(address -> {
        	if(address.getId().toString().equals(id)) {
        		address.setSelected(true);
        	} else {
        		address.setSelected(false);
        	};
        });
        return userRepo.save(user);
    }
    
    @PutMapping("/{phoneNumber}/update")
    public User updateAddress(@PathVariable String phoneNumber, @RequestBody Address addr) {
        User user = userRepo.findByPhoneNumber(phoneNumber);
        user.getAddresses().forEach(address -> {
        	if(address.getId().toString().equals(addr.getId().toString())) {
        		address.setSelected(true);
        		address.setName(addr.getName());
        		address.setCity(addr.getCity());
        		address.setStreet(addr.getStreet());
        		address.setZip(addr.getZip());
        	}
        });
        return userRepo.save(user);
    }
}
