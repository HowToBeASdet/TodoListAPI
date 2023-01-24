package com.howtobeasdet.todolistapi.controller;

import com.howtobeasdet.todolistapi.model.*;
import com.howtobeasdet.todolistapi.payload.request.LoginRequest;
import com.howtobeasdet.todolistapi.payload.request.SignupRequest;
import com.howtobeasdet.todolistapi.payload.request.UpdateRequest;
import com.howtobeasdet.todolistapi.payload.response.MessageResponse;
import com.howtobeasdet.todolistapi.payload.response.ResponseMessage;
import com.howtobeasdet.todolistapi.payload.response.SignInResponse;
import com.howtobeasdet.todolistapi.payload.response.UpdateResponse;
import com.howtobeasdet.todolistapi.repository.RoleRepository;
import com.howtobeasdet.todolistapi.repository.UserRepository;
import com.howtobeasdet.todolistapi.security.jwt.JwtUtils;
import com.howtobeasdet.todolistapi.security.services.UserDetailsImpl;
import com.howtobeasdet.todolistapi.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(path = "/user", produces = {"application/json", "plain/text", MediaType.IMAGE_PNG_VALUE}, consumes = {"application/json", "multipart/form-data"})
public class UserController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    FilesStorageService storageService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid
            @RequestBody SignupRequest signUpRequest
    ) {
        if (userRepository.existsByUsername(signUpRequest.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        LocalDateTime createdAt = LocalDateTime.now();

        User user = new User(UUID.randomUUID().toString(),
                signUpRequest.getName(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getAge(),
                createdAt.toString(),
                createdAt.toString(),
                1
        );

        Set<String> strRoles = null;
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);

        String jwt = jwtUtils.generateJwtToken(user.getName());
        userRepository.save(user);

        user = userRepository.findByUsername(user.getName()).get();
        user.setToken(null);
        user.setRoles(null);
        user.setId(null);

        SignInResponse signInResponse = new SignInResponse(user, jwt);

        return ResponseEntity.ok(signInResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @Valid
            @RequestBody LoginRequest loginRequest
    ) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User u = userRepository.findByUsername(userDetails.getUsername()).get();
        u.setLogIn(true);
        userRepository.save(u);
        u.setRoles(null);
        u.setId(null);
        u.set__v(2);
        u.setLogIn(null);

        return ResponseEntity.ok(new SignInResponse(u, jwt));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(
            @RequestHeader("Authorization") String auth
    ) {

        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            auth = auth.substring(7, auth.length());
        }

        String userName = jwtUtils.getUserNameFromJwtToken(auth);
        User u = userRepository.findByUsername(userName).get();
        u.setLogIn(false);
        u = userRepository.save(u);
        return ResponseEntity.ok("{\"success\":true}");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getLoginViaToken(
            @RequestHeader("Authorization") String auth
    ) {

        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            auth = auth.substring(7, auth.length());
        }

        String userName = jwtUtils.getUserNameFromJwtToken(auth);
        User u = userRepository.findByUsername(userName).get();
        u.setLogIn(true);
        u = userRepository.save(u);
        u.set__v(3);
        u.setId(null);
        u.setId(null);
        u.setToken(null);
        u.setRoles(null);
        u.setLogIn(null);

        return ResponseEntity.ok(u);
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUserProfile(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody UpdateRequest signUpRequest
    ) {

        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            auth = auth.substring(7, auth.length());
        }

        String userName = jwtUtils.getUserNameFromJwtToken(auth);
        User u = userRepository.findByUsername(userName).get();
        if (signUpRequest.getEmail() != null) {
            u.setEmail(signUpRequest.getEmail());
        }
        if (signUpRequest.getAge() != null) {
            u.setAge(signUpRequest.getAge());
        }
        if (signUpRequest.getPassword() != null) {
            u.setPassword(encoder.encode((signUpRequest.getPassword())));
        }

        u = userRepository.save(u);
        u.set__v(5);
        u.setId(null);
        u.setToken(null);
        u.setRoles(null);
        u.setLogIn(null);

        return ResponseEntity.ok(new UpdateResponse(u, true));
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<?> uploadFile(
            @RequestHeader("Authorization") String auth,
            @RequestParam("avatar") MultipartFile file
    ) {
        String message = "";
        try {
            if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
                auth = auth.substring(7, auth.length());
            }

            String userName = jwtUtils.getUserNameFromJwtToken(auth);
            User user = userRepository.findByUsername(userName).get();
            String userId = user.get_id();
            storageService.init();
            storageService.save(userId, file);

            return ResponseEntity.status(HttpStatus.OK).body(new Success(true));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping(path = "/{userId}/avatar", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Resource> getUserImage(@PathVariable String userId) {

        Resource file = storageService.load(userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @DeleteMapping(path = "/me/avatar")
    public ResponseEntity<?> deleteImage(@RequestHeader("Authorization") String auth) {

        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            auth = auth.substring(7, auth.length());
        }

        String userName = jwtUtils.getUserNameFromJwtToken(auth);
        User u = userRepository.findByUsername(userName).get();

        storageService.delete(u.get_id());

        return ResponseEntity.ok()
                .body(new Success(true));
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(
            @RequestHeader("Authorization") String auth
    ) {

        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            auth = auth.substring(7, auth.length());
        }

        String userName = jwtUtils.getUserNameFromJwtToken(auth);
        User u = userRepository.findByUsername(userName).get();
        userRepository.delete(u);

        return ResponseEntity.ok(new Success(true));
    }
}
