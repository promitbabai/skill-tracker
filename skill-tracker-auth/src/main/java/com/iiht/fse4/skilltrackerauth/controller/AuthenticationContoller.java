package com.iiht.fse4.skilltrackerauth.controller;


import com.iiht.fse4.skilltrackerauth.model.AuthenticationRequest;
import com.iiht.fse4.skilltrackerauth.model.Response;
import com.iiht.fse4.skilltrackerauth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200/")
//@RequestMapping("/skill-tracker/api/v1/auth")
public class AuthenticationContoller {



    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/")
    public String welcome() {
        return "Welcome to javatechie !!";
    }


    @PostMapping("/authenticate")
    public ResponseEntity authenticate(@RequestBody AuthenticationRequest authRequest) throws Exception {
        System.out.println("---------------- \n\n START FLOW Authenticate()");
        System.out.println("Inside Controller - authenticate()");
        System.out.println("Username - Pass = " + authRequest.getUsername() + " - " + authRequest.getPassword());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException ex) {
            System.out.println(" ############## inavalid password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Inavalid Password");
        } catch (UsernameNotFoundException ex){
            System.out.println(" ############## inavalid Username");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Inavalid Username");
        }
        String generatedToken = jwtUtil.generateToken(authRequest.getUsername());
        Response response = new Response();
        response.setStatus(200);
        response.setMessage(generatedToken);
        return ResponseEntity.status(response.getStatus()).body(response.getMessage());
    }


}
