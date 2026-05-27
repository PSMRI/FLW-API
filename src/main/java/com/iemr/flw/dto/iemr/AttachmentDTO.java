package com.iemr.flw.dto.iemr;

public class AttachmentDTO {

    private String fileName;   // e.g. "meeting_photo1.jpg"
    private String fileType;   // e.g. "image/jpeg", "image/png", "application/pdf"
    private Long fileSize;     // size in bytes

    public AttachmentDTO() {
    }

    public AttachmentDTO(String fileName, String fileType, Long fileSize) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
