package br.com.senai.controllers;

import br.com.senai.models.Users;
import br.com.senai.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/user") //localhost:8080/user
public class UserController {

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //localhost:8080/user/all
    @GetMapping(value = "/all",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    //localhost:8080/user/createUsers
    @PostMapping(value = "/createUsers",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Users createUsers(@RequestBody Users users) {
        // Create a new Users object
        Users newUsers = new Users();
        // Set user properties
        newUsers.setUsername(users.getUsername());
        newUsers.setEmail(users.getEmail());

        // Encode password before saving
        newUsers.setPassword(passwordEncoder.encode(users.getPassword()));

        // Save the user with encrypted password
        return usersRepository.save(newUsers);
    }

    // Update user (similar password encryption logic)
    @PutMapping(value = "/updateUsers",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Users updateUser(@RequestBody Users users) {
        Users getUser = usersRepository.findById(users.getId()).orElseThrow();
        Users updateUsers = new Users();

        updateUsers.setId(users.getId());
        updateUsers.setUsername(users.getUsername());
        updateUsers.setEmail(users.getEmail());

        // Encode password before saving
        updateUsers.setPassword(passwordEncoder.encode(users.getPassword()));

        return usersRepository.save(updateUsers);
    }

    // Delete user (unchanged)
    @DeleteMapping(value = "/deleteUsers/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Users deleteUsers(@PathVariable Long id) {
        Users getUsers = usersRepository.findById(id).orElseThrow();
        usersRepository.delete(getUsers);
        return getUsers;
    }

    // Login route with username and password in request body
    @PostMapping(value = "/login",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Users> loginUser(@RequestBody LoginRequest loginRequest) {
        // Find user by username
        Optional<Users> userOptional = usersRepository.findByUsername(loginRequest.getUsername());

        // Check if user exists
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // User not found
        }

        Users user = userOptional.get();

        // Validate password using PasswordEncoder
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid credentials
        }

        // Login successful, return user information (optional)
        return ResponseEntity.ok(user); // You can decide what information to return in the response
    }

    // This class can be further extended to handle functionalities like generating JWT tokens for authentication
}


