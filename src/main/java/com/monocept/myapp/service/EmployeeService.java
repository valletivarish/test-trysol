package com.monocept.myapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.monocept.myapp.dto.EmployeeRequestDto;
import com.monocept.myapp.entity.Employee;

public interface EmployeeService{

	String createEmployee(EmployeeRequestDto employeeRequestDto);

	Page<Employee> getAllEmployees(Pageable pageable);

	String deactivateEmployee(long id);

	String activateEmployee(long id);

}
