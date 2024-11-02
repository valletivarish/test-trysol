package com.monocept.myapp.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monocept.myapp.entity.CandidateProfile;

@Repository
public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, String> {
    boolean existsByContactAndMailId(String contact, String mailId);

	Page<CandidateProfile> findAll(Specification<CandidateProfile> spec, Pageable pageable);
}