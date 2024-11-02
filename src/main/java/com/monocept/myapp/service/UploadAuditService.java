package com.monocept.myapp.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.monocept.myapp.entity.UploadAudit;

public interface UploadAuditService{

	Page<UploadAudit> getAllUploadAudits(Pageable pageable);

}
