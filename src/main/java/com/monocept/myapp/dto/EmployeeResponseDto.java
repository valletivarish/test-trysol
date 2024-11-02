package com.monocept.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDto {
	private long employeeId;
    private String firstName;
    private String lastName;
    private boolean active;
    private String email;
}
