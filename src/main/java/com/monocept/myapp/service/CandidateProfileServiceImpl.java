package com.monocept.myapp.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.monocept.myapp.entity.Admin;
import com.monocept.myapp.entity.CandidateProfile;
import com.monocept.myapp.entity.UploadAudit;
import com.monocept.myapp.entity.User;
import com.monocept.myapp.exception.InvalidFileFormatException;
import com.monocept.myapp.exception.NoRecordFoundException;
import com.monocept.myapp.repository.AdminRepository;
import com.monocept.myapp.repository.CandidateProfileRepository;
import com.monocept.myapp.repository.UploadAuditRepository;
import com.monocept.myapp.repository.UserRepository;
import com.monocept.myapp.util.CandidateProfileSpecification;
import com.monocept.myapp.util.EmailService;

import jakarta.transaction.Transactional;

@Service
public class CandidateProfileServiceImpl implements CandidateProfileService{

	@Autowired
	private CandidateProfileRepository candidateProfileRepository;

	@Autowired
	private UploadAuditRepository uploadAuditRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AdminRepository adminRepository;
	
	@Autowired
	private EmailService emailService;

	@Transactional
	@Override
	public List<String> processExcelFiles(MultipartFile[] files) throws Exception {
		List<String> duplicatesSkipped = new ArrayList<>();
		Set<String> uniqueRecords = new HashSet<>();

		int insertedRows = 0;
		int skippedRows = 0;

		for (MultipartFile file : files) {
			if (!isExcelFile(file)) {
				throw new InvalidFileFormatException("Only Excel files are allowed.");
			}

			try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {

				Sheet sheet = workbook.getSheetAt(0);
				for (Row row : sheet) {
					if (row.getRowNum() == 0)
						continue; // Skip header row

					// Extract values from the row based on your specified columns
					LocalDate date = row.getCell(0).getLocalDateTimeCellValue().toLocalDate(); // Ensure date format is
																								// correct
					String skill = getStringCellValue(row.getCell(1));
					String subSkill = getStringCellValue(row.getCell(2));
					String name = getStringCellValue(row.getCell(3));
					String contact = getStringCellValue(row.getCell(4)).replaceAll("\\s+", ""); // Removes spaces around
																								// commas
					String mailId = getStringCellValue(row.getCell(5));
					Integer totalExperience = getIntegerCellValue(row.getCell(6));
					Integer relevantExperience = getIntegerCellValue(row.getCell(7));
					String location = getStringCellValue(row.getCell(8));
					String noticePeriod = getStringCellValue(row.getCell(9));
					// Create a CandidateProfile object and set its properties
					
					CandidateProfile profile = new CandidateProfile();
					profile.setId(generateCustomId());
					profile.setDate(date);
					profile.setSkill(skill);
					profile.setSubSkill(subSkill);
					profile.setName(name);
					profile.setContact(contact);
					profile.setMailId(mailId);
					profile.setTotalExperience(totalExperience);
					profile.setRelevantExperience(relevantExperience);
					profile.setLocation(location);
					profile.setNoticePeriod(noticePeriod);
					System.out.println(profile);

					// Check for duplicates
					String uniqueKey = "(Name : " + name + " , Mail ID : " + mailId + " , Skill : " + skill + ")\n";

					if (uniqueRecords.contains(uniqueKey)) {
						duplicatesSkipped.add("Duplicate in upload batch: " + uniqueKey);
						skippedRows++;
						continue;
					}

					if (candidateProfileRepository.existsByContactAndMailId(contact, mailId)) {
						duplicatesSkipped.add("Duplicate in database: " + uniqueKey);
						skippedRows++;
						continue;
					}

					uniqueRecords.add(uniqueKey);
					candidateProfileRepository.save(profile);
					insertedRows++;
				}
			}
		}

		// Audit logging
		UploadAudit audit = new UploadAudit();
		String email = getUsernameFromSecurityContext();
		User user = userRepository.findByEmail(email).orElseThrow();
		if (user.getEmployee() != null) {
			audit.setUploaderName(user.getEmployee().getFirstName() + " " + user.getEmployee().getLastName());
			audit.setUploaderId(user.getEmployee().getId());
		} else {
			audit.setUploaderName(user.getAdmin().getFirstName() + " " + user.getAdmin().getLastName());
			audit.setUploaderId(user.getAdmin().getId());
		}
		audit.setUploadDateTime(LocalDateTime.now());
		audit.setInsertedRows(insertedRows);
		audit.setSkippedRows(skippedRows);
		uploadAuditRepository.save(audit);
		//sendUploadNotificationToAdmins(insertedRows, skippedRows, files.length);
		return duplicatesSkipped;
	}
	
//	private void sendUploadNotificationToAdmins(int insertedRows, int skippedRows, int totalFiles) {
//	    // Fetch all admin users
//	    List<Admin> adminUsers = adminRepository.findAll();
//	    
//	    // Map to User to extract emails (assuming Admin has a User reference)
//	    List<String> adminEmails = adminUsers.stream()
//	                                         .map(Admin::getUser) // Assuming Admin has a getUser() method
//	                                         .map(User::getEmail) // Extract emails from User objects
//	                                         .filter(Objects::nonNull) // Filter out any null emails
//	                                         .toList(); // Convert to a list
//
//	    // Fetch the current user from security context
//	    String uploaderEmail = getUsernameFromSecurityContext();
//	    User user = userRepository.findByEmail(uploaderEmail).orElseThrow(()-> new NoRecordFoundException("No USer found"));
//	    String fullName;
//	    if(user.getAdmin()!=null) {
//	    	fullName=user.getAdmin().getFirstName()+" "+user.getAdmin().getLastName();
//	    }
//	    else {
//	    	fullName=user.getEmployee().getFirstName()+" "+user.getEmployee().getLastName();
//	    }
//	    // Get the current date and time
//	    LocalDateTime now = LocalDateTime.now();
//	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
//	    String formattedDateTime = now.format(formatter);
//
//	    // Prepare email content
//	    String subject = "Notification: New Candidate Profiles Uploaded";
//	    String body = String.format(
//	            "Dear Admins,\n\n" +
//	            "We are pleased to inform you that a new batch of candidate profiles has been successfully uploaded to the Trysol system.\n\n" +
//	            "Please find the details of the upload below:\n" +
//	            "----------------------------------\n" +
//	            "Company Name: Trysol Global Services\n" +
//	            "Uploader Name: %s\n" +
//	            "Upload Date and Time: %s\n" +
//	            "Total Files Uploaded: %d\n" +
//	            "Inserted Rows: %d\n" +
//	            "Skipped Rows: %d\n" +
//	            "----------------------------------\n\n" +
//	            "We kindly request that you log in to the system to review the newly uploaded profiles at your earliest convenience.\n\n" +
//	            "Thank you for your attention to this matter.\n\n" +
//	            "Best regards,\n" +
//	            "The Trysol Team",
//	            fullName, formattedDateTime, totalFiles, insertedRows, skippedRows
//	        );
//
//	    // Send email if there are admin emails
//	    if (!adminEmails.isEmpty()) {
//	        emailService.sendEmail(subject, body, adminEmails);
//	    }
//	}

	


	private String getStringCellValue(Cell cell) {
		if (cell != null) {
			if (cell.getCellType() == CellType.STRING) {
				return cell.getStringCellValue().trim();
			} else if (cell.getCellType() == CellType.NUMERIC) {
				// Convert numeric value to string and remove any decimal points
				return String.valueOf((long) cell.getNumericCellValue()).trim();
			}
		}
		return "";
	}

	private Integer getIntegerCellValue(Cell cell) {
		if (cell != null) {
			if (cell.getCellType() == CellType.NUMERIC) {
				return (int) cell.getNumericCellValue();
			} else if (cell.getCellType() == CellType.STRING) {
				try {
					return Integer.valueOf(cell.getStringCellValue().trim());
				} catch (NumberFormatException e) {
					return null; // Handle invalid number formats
				}
			}
		}
		return null;
	}

	private String getUsernameFromSecurityContext() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			return userDetails.getUsername();
		}
		return null;
	}

	private boolean isExcelFile(MultipartFile file) {
		String contentType = file.getContentType();
		return "application/octet-stream".equals(contentType) || "application/vnd.ms-excel".equals(contentType)
				|| "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType);
	}

	private String generateCustomId() {
		long count = candidateProfileRepository.count(); // Get the total count of records
		return String.format("trysol_%02d", count + 1); // Format ID as trysol_01, trysol_02, etc.
	}

	public Page<CandidateProfile> getCandidateProfiles(String name, String skill, String subSkill, String location,
			Integer minRelExp, Integer maxRelExp, Integer minTotalExp, Integer maxTotalExp, Pageable pageable) {
		Specification<CandidateProfile> spec = Specification.where(CandidateProfileSpecification.hasName(name))
				.and(CandidateProfileSpecification.hasSkill(skill))
				.and(CandidateProfileSpecification.hasSubSkill(subSkill))
				.and(CandidateProfileSpecification.hasLocation(location))
				.and(CandidateProfileSpecification.hasRelevantExperienceBetween(minRelExp, maxRelExp))
				.and(CandidateProfileSpecification.hasTotalExperienceBetween(minTotalExp, maxTotalExp));

		return candidateProfileRepository.findAll(spec, pageable);
	}

	@Override
	public Long getCount() {
		return candidateProfileRepository.count();
	}
}
