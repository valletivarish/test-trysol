package com.monocept.myapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.monocept.myapp.entity.CandidateProfile;
import com.monocept.myapp.entity.UploadAudit;
import com.monocept.myapp.service.CandidateProfileService;
import com.monocept.myapp.service.UploadAuditService;

@RestController
@RequestMapping("/trysol/candidates")
public class CandidateProfileController {

    @Autowired
    private CandidateProfileService candidateProfileService;
    
    @Autowired
    private UploadAuditService uploadAuditService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadExcelFiles(@RequestParam("files") MultipartFile[] files) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<String> duplicatesSkipped = candidateProfileService.processExcelFiles(files);
            response.put("status", "success");
            response.put("message", "Files processed successfully.");
            response.put("duplicatesSkipped", duplicatesSkipped);
            response.put("totalProcessed", files.length); // Or whatever logic you have for processed files
            response.put("totalDuplicatesSkipped", duplicatesSkipped.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error processing files: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    
    @GetMapping
    public ResponseEntity<Page<CandidateProfile>> getAllCandidateProfiles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String subSkill,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer minRelExp,
            @RequestParam(required = false) Integer maxRelExp,
            @RequestParam(required = false) Integer minTotalExp,
            @RequestParam(required = false) Integer maxTotalExp,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<CandidateProfile> profiles = candidateProfileService.getCandidateProfiles(
                name, skill, subSkill, location, minRelExp, maxRelExp, minTotalExp, maxTotalExp, pageable);

        return ResponseEntity.ok(profiles);
    }

    
    @GetMapping("/audits")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<UploadAudit>> getAllUploadAudits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "uploadDateTime") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<UploadAudit> audits = uploadAuditService.getAllUploadAudits(pageable);
        return ResponseEntity.ok(audits);
    }
    
    @GetMapping("/count")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Long> getCount(){
    	return new ResponseEntity<Long>(candidateProfileService.getCount(),HttpStatus.OK);
    }
}
