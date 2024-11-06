package com.monocept.myapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "candidate_profiles")
public class CandidateProfile {

	@Id
	@Column(name = "id")
	private String id;


    @NotBlank(message = "Skill name cannot be blank")
    private String skill;

    private String subSkill;

    @NotBlank(message = "Name cannot be blank")
    private String name; // Added back the name field


    @NotBlank(message="Contact cannot be blank")
    private String contact;


    @NotBlank(message = "Mail ID cannot be blank")
    @Email(message = "Invalid email format")
    private String mailId;

    @Min(value = 0, message = "Total experience cannot be negative")
    private int totalExperience; // Assuming experience is in years

    @Min(value = 0, message = "Relevant experience cannot be negative")
    private int relevantExperience;

    @NotBlank(message = "Location cannot be blank")
    private String location; 

    @NotNull(message = "Date is required")
    private LocalDate date; // New field for date from the Excel sheet
    
    @NotBlank(message = " Notice period is required")
    private String noticePeriod;
}
