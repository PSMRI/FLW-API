package com.iemr.flw.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iemr.flw.dto.crashlogs.CrashLogRequest;
import com.iemr.flw.service.CrashLogService;
import com.iemr.flw.utils.exception.IEMRException;
import org.springframework.beans.factory.annotation.Value;

@Service
public class CrashLogServiceImpl implements CrashLogService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

   // @Value("${crash.logs.base.path}")
    private String crashLogsBasePath;

    @Override
    public String saveCrashLog(CrashLogRequest request, Integer userId, MultipartFile file)
            throws IEMRException {

        try {
            // 🛡️ Validate file
            if (file.isEmpty()) {
                throw new IEMRException("Uploaded file is empty");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new IEMRException("Invalid filename");
            }

            // Prevent path traversal
            String safeOriginalName = Paths.get(originalFilename).getFileName().toString();

            // Only allow .txt
            if (!safeOriginalName.toLowerCase().endsWith(".txt")) {
                throw new IEMRException("Only .txt files are allowed");
            }

            // Validate MIME
            String contentType = file.getContentType();
            if (contentType == null ||
                    (!contentType.equals("text/plain") &&
                            !contentType.equals("application/octet-stream"))) {
                throw new IEMRException("Invalid file type. Only plain text allowed.");
            }

            // Block binary content
            if (isBinaryFile(file.getBytes())) {
                throw new IEMRException("Binary files are not allowed. Upload valid text logs only.");
            }

            // 📁 Create date-based folder
            LocalDate date = LocalDate.now();
            String dateFolder = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

            File dateDir = new File(crashLogsBasePath, dateFolder);
            if (!dateDir.exists() && !dateDir.mkdirs()) {
                throw new IEMRException("Failed to create folder: " + dateDir.getAbsolutePath());
            }

            //  Use timestamp as sent by frontend (NO conversion)
            String rawTimestamp = request.getTimestamp(); // Already "yyyy-MM-dd HH:mm:ss"

            // Make timestamp filename-safe
            String safeTimestamp = rawTimestamp
                    .replace(" ", "_")
                    .replace(":", "-");

            // 📝 Final file name
            String filename = String.format(
                    "%d_%s_%s_%s.txt",
                    userId,
                    sanitizeFilename(request.getAppVersion()),
                    sanitizeFilename(request.getDeviceId()),
                    sanitizeFilename(safeTimestamp));

            File crashLogFile = new File(dateDir, filename);

            // 💾 Save file
            try (FileOutputStream fos = new FileOutputStream(crashLogFile)) {
                fos.write(file.getBytes());
            }

            String relativePath = dateFolder + "/" + filename;
            logger.info("Crash log saved: " + relativePath);

            return relativePath;

        } catch (IOException e) {
            logger.error("Error saving crash log", e);
            throw new IEMRException("Error saving crash log: " + e.getMessage(), e);
        }
    }

    // Detect binary content
    private boolean isBinaryFile(byte[] data) {
        int len = Math.min(data.length, 2048);
        for (int i = 0; i < len; i++) {
            byte b = data[i];
            if (b < 0x09)
                return true;
            if (b > 0x0D && b < 0x20)
                return true;
        }
        return false;
    }

    // Sanitize metadata for filename safety
    private String sanitizeFilename(String input) {
        if (input == null)
            return "unknown";
        return input.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}
