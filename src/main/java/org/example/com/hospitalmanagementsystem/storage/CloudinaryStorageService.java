package org.example.com.hospitalmanagementsystem.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryStorageService {

    private final Cloudinary cloudinary;

    public String uploadPrescription(MultipartFile file, Long appointmentId) throws IOException {
        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "resource_type", "raw",
                        "folder", "prescriptions",
                        "public_id", "appointment_" + appointmentId + "_" + System.currentTimeMillis(),
                        "format", "pdf"
                )
        );
        String url = (String) result.get("secure_url");
        log.info("Prescription uploaded to Cloudinary for appointment {}: {}", appointmentId, url);
        return url;
    }

    public void deletePrescription(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "raw"));
            log.info("Prescription deleted from Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.error("Failed to delete prescription from Cloudinary: {}", publicId, e);
        }
    }
}
