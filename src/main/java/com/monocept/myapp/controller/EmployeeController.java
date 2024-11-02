package com.monocept.myapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monocept.myapp.dto.EmployeeRequestDto;
import com.monocept.myapp.dto.EmployeeResponseDto;
import com.monocept.myapp.entity.Employee;
import com.monocept.myapp.service.EmployeeService;


@RequestMapping("/trysol/employees")
@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	
	@PostMapping
	public String createEmployee(@RequestBody EmployeeRequestDto employeeRequestDto) {
		
		return employeeService.createEmployee(employeeRequestDto);
	}
	@GetMapping
	public ResponseEntity<Page<EmployeeResponseDto>> getAllEmployees(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "id") String sortBy,
	        @RequestParam(defaultValue = "DESC") String direction) {

	    Sort.Direction sortDirection = Sort.Direction.fromString(direction);
	    Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

	    Page<Employee> employeePage = employeeService.getAllEmployees(pageable);
	    
	    // Convert Employee entities to EmployeeResponseDto
	    Page<EmployeeResponseDto> responsePage = employeePage.map(employee -> 
	        new EmployeeResponseDto(
	        	employee.getId(),
	            employee.getFirstName(),
	            employee.getLastName(),
	            employee.isActive(),
	            employee.getUser() != null ? employee.getUser().getEmail() : null // Assuming you have an email field in the User entity
	        )
	    );

	    return ResponseEntity.ok(responsePage);
	}
	
	@DeleteMapping("/{id}/deactivate")
	public ResponseEntity<String> deactivateEmployeeById(@PathVariable(name = "id") long id){
		return new ResponseEntity<String>(employeeService.deactivateEmployee(id),HttpStatus.OK);
	}
	@PutMapping("/{id}/activate")
	public ResponseEntity<String> activateEmployeeById(@PathVariable(name = "id") long id){
		return new ResponseEntity<String>(employeeService.activateEmployee(id),HttpStatus.OK);
	}
	
}
