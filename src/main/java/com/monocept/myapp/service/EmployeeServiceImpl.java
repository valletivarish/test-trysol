package com.monocept.myapp.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.monocept.myapp.dto.EmployeeRequestDto;
import com.monocept.myapp.entity.Employee;
import com.monocept.myapp.entity.Role;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.exception.BankApiException;
import com.monocept.myapp.exception.NoRecordFoundException;
import com.monocept.myapp.repository.EmployeeRespository;
import com.monocept.myapp.repository.RoleRepository;
import com.monocept.myapp.repository.UserRepository;

@Service
public class EmployeeServiceImpl implements EmployeeService{
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EmployeeRespository employeeRespository;

	@Override
	public String createEmployee(EmployeeRequestDto employeeRequestDto) {
		Employee employee= new Employee();
		User user=new User();
		user.setEmail(employeeRequestDto.getEmail());
		user.setPassword(passwordEncoder.encode(employeeRequestDto.getPassword()));
		Role userRole = getRole("ROLE_USER");
        user.setRoles(new HashSet<>(Set.of(userRole))); 
        employee.setFirstName(employeeRequestDto.getFirstName());
        employee.setLastName(employeeRequestDto.getLastName());
        employee.setActive(true);
        user.setEmployee(employee);
        userRepository.save(user);
		return "User Created Successfully with Name : "+employeeRequestDto.getFirstName()+" "+employeeRequestDto.getLastName() ;
	}

	private Role getRole(String roleName) {
        return roleRepository.findByName(roleName).orElseThrow(
            () -> new BankApiException(HttpStatus.BAD_REQUEST, roleName + " role not set up in the database")
        );
    }

	@Override
	public Page<Employee> getAllEmployees(Pageable pageable) {
		System.out.println(employeeRespository.findAll(pageable));
		return employeeRespository.findAll(pageable);
	}

	@Override
	public String deactivateEmployee(long id) {
		Employee employee = employeeRespository.findById(id).orElseThrow(()->new NoRecordFoundException("Employee Not Found With ID with "+id));
		employee.setActive(false);
		employeeRespository.save(employee);
		return "Employee with ID: " + id + " has been deactivated.";
	}

	@Override
	public String activateEmployee(long id) {
		Employee employee = employeeRespository.findById(id).orElseThrow(()->new NoRecordFoundException("Employee Not Found With ID with "+id));
		employee.setActive(true);
		employeeRespository.save(employee);
		return "Employee with ID: " + id + " has been activated.";
	}

}
