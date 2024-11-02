package com.monocept.myapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monocept.myapp.entity.Employee;

@Repository
public interface EmployeeRespository extends JpaRepository<Employee, Long>{

}
