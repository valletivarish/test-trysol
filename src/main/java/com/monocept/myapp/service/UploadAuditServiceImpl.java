package com.monocept.myapp.service;

import com.monocept.myapp.entity.UploadAudit;
import com.monocept.myapp.repository.UploadAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UploadAuditServiceImpl implements UploadAuditService{

    @Autowired
    private UploadAuditRepository uploadAuditRepository;

    public Page<UploadAudit> getAllUploadAudits(Pageable pageable) {
        return uploadAuditRepository.findAll(pageable);
    }
}
