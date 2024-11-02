package com.monocept.myapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RegisterDto {
	private String firstName;
	
	private String lastName;
	
	
	@Email
	@NotBlank
	private String email;
	@NotBlank
	@Size(min = 8,max = 14)
	private String password;
	private boolean admin;

	

}

