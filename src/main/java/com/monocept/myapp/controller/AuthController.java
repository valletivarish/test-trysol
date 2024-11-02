package com.monocept.myapp.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.LoginDto;
import com.monocept.myapp.dto.LoginResponseDto;
import com.monocept.myapp.dto.RegisterDto;
import com.monocept.myapp.security.JwtTokenProvider;
import com.monocept.myapp.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/trysol/auth")
@CrossOrigin(origins = "http://localhost:3000", exposedHeaders = HttpHeaders.AUTHORIZATION)
public class AuthController {

	private AuthService authService;
	private JwtTokenProvider jwtTokenProvider;
	//private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	public AuthController(AuthService authService,JwtTokenProvider jwtTokenProvider) {
		this.authService = authService;
		this.jwtTokenProvider=jwtTokenProvider;
	}

	@PostMapping(value = { "/login"})
	public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
	    LoginResponseDto loginResponseDto = authService.login(loginDto);

	    String token = jwtTokenProvider.generateToken(SecurityContextHolder.getContext().getAuthentication());

	    return ResponseEntity.ok()
	            .header(HttpHeaders.AUTHORIZATION, token)
	            .body(loginResponseDto);
	}

	@PostMapping(value = { "/register"})
	public ResponseEntity<String> register(@Valid @RequestBody RegisterDto registerDto) {
		String response = authService.register(registerDto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@GetMapping("/verifyAdmin")
    public ResponseEntity<Boolean> verifyAdmin(@RequestParam(name = "auth") String token) {
        // Remove "Bearer " prefix if present
		if(token=="") {
			return ResponseEntity.ok(false);
		}
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            if (authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.ok(true);
            }
        }
        return ResponseEntity.ok(false);
    }
	
	@GetMapping("/verifyUser")
    public ResponseEntity<Boolean> verifyUser(@RequestParam(name = "auth") String token) {
        // Remove "Bearer " prefix if present
		if(token=="") {
			return ResponseEntity.ok(false);
		}
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            if (authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"))) {
                return ResponseEntity.ok(true);
            }
        }
        return ResponseEntity.ok(false);
    }
	
//	@GetMapping("getCurrentUser")
//	public Map<String,Object> getUserByEmail(@RequestParam(name = "email")String email) {
//		return authService.getUserByEmail(email);
//	}
	
}
