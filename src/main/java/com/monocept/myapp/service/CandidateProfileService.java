package com.monocept.myapp.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.monocept.myapp.entity.CandidateProfile;

public interface CandidateProfileService {

	List<String> processExcelFiles(MultipartFile[] files) throws Exception;

	Page<CandidateProfile> getCandidateProfiles(String name, String skill, String subSkill, String location,
			Integer minRelExp, Integer maxRelExp, Integer minTotalExp, Integer maxTotalExp, Pageable pageable);

	Long getCount();

}
