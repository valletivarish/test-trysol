package com.monocept.myapp.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "upload_audits")
public class UploadAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message="Uploader name cannot be blank")
    private String uploaderName;

    @NotNull(message="Uploader ID cannot be null")
    private Long uploaderId;

    private LocalDateTime uploadDateTime; // Date and time of the upload

    private int insertedRows; // Number of rows successfully inserted

    private int skippedRows; // Number of rows skipped due to errors or duplicates

}
