package com.monocept.myapp.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.LoginDto;
import com.monocept.myapp.dto.LoginResponseDto;
import com.monocept.myapp.dto.RegisterDto;
import com.monocept.myapp.entity.Admin;
import com.monocept.myapp.entity.Employee;
import com.monocept.myapp.entity.Role;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.exception.BankApiException;
import com.monocept.myapp.repository.AdminRepository;
import com.monocept.myapp.repository.EmployeeRespository;
import com.monocept.myapp.repository.RoleRepository;
import com.monocept.myapp.repository.UserRepository;
import com.monocept.myapp.security.JwtTokenProvider;

@Service
public class AuthServiceImpl implements AuthService {

    Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmployeeRespository employeeRespository;

    public AuthServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository,
                           RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider,
                           AdminRepository adminRepository, EmployeeRespository employeeRespository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.adminRepository = adminRepository;
        this.employeeRespository = employeeRespository;
    }

    @Override
    public LoginResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(
            () -> new BankApiException(HttpStatus.UNAUTHORIZED, "User not found")
        );

        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        if (user.getAdmin() != null) {
            roles.add("ROLE_ADMIN");
        }

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setEmail(loginDto.getEmail());
        loginResponseDto.setRoles(roles);

        return loginResponseDto;
    }

    @Override
    public String register(RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new BankApiException(HttpStatus.BAD_REQUEST, "Email already exists!");
        }

        User user = setupNewUser(registerDto);
        if (registerDto.isAdmin()) {
            registerAdmin(registerDto, user);
            return "Admin registered successfully!";
        } else {
            Employee employee = new Employee();
            employee.setFirstName(registerDto.getFirstName());
            employee.setLastName(registerDto.getLastName());
            employee.setActive(true);
            
            // Associate employee with user
            user.setEmployee(employee);
            employee.setUser(user);
            
            // Assign role and save user (cascades to employee)
            assignUserRole(user);
            userRepository.save(user); // This saves both user and employee due to cascading
            
            return "User registered successfully!";
        }
    }

    private User setupNewUser(RegisterDto registerDto) {
        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        return user;
    }

    private void registerAdmin(RegisterDto registerDto, User user) {
        Admin admin = new Admin();
        admin.setFirstName(registerDto.getFirstName());
        admin.setLastName(registerDto.getLastName());
        user.setAdmin(admin);
        
        // Assign ROLE_ADMIN 
        Role adminRole = getRole("ROLE_ADMIN");
        user.setRoles(new HashSet<>(Set.of(adminRole)));

        adminRepository.save(admin);
        userRepository.save(user);
    }

    private void assignUserRole(User user) {
        Role userRole = getRole("ROLE_USER");
        user.setRoles(new HashSet<>(Set.of(userRole))); 
        userRepository.save(user);
    }

    private Role getRole(String roleName) {
        return roleRepository.findByName(roleName).orElseThrow(
            () -> new BankApiException(HttpStatus.BAD_REQUEST, roleName + " role not set up in the database")
        );
    }
}
