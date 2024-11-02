package com.monocept.myapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRequestDto {
	@NotBlank
	@Size(min = 3,max = 14)
	private String firstName;
	@NotBlank
	@Size(min = 3,max = 14)
	private String lastName;
	@Email
	private String email;
	@Size(min = 8,max = 14)
	private String password;
}
