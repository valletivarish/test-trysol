package com.monocept.myapp.service;

import com.monocept.myapp.dto.LoginDto;
import com.monocept.myapp.dto.LoginResponseDto;
import com.monocept.myapp.dto.RegisterDto;

public interface AuthService {
	LoginResponseDto login(LoginDto loginDto);

    String register(RegisterDto registerDto);
//
//	Map<String,Object> getUserByEmail(String email);
}
