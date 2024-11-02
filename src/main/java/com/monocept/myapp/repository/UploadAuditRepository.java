package com.monocept.myapp.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monocept.myapp.entity.UploadAudit;

@Repository
public interface UploadAuditRepository extends JpaRepository<UploadAudit, Long> {
}