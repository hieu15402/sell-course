package com.example.sellcourse.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.sellcourse.entities.User;
import com.example.sellcourse.exception.AppException;
import com.example.sellcourse.exception.ErrorCode;
import com.example.sellcourse.repository.user.UserRepository;
import com.example.sellcourse.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CloudinaryService {

    Cloudinary cloudinary;
    UserRepository userRepository;

    @PreAuthorize("isAuthenticated()")
    public String getAvatar(){
        String currentLogin = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(currentLogin)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return (user.getAvatar() != null) ? user.getAvatar() : "";
    }

    @PreAuthorize("isAuthenticated()")
    public String uploadImage(MultipartFile file){
        try{
            var result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "/upload",
                    "use_filename", true,
                    "unique_filename", true,
                    "resource_type","auto"
            ));
            return  result.get("secure_url").toString();
        } catch (IOException io){
            throw new RuntimeException("Image upload fail");
        }
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void updateAvatar(String url, String email) {
        String currentUserEmail = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setAvatar(url);
        userRepository.save(user);

        log.info("Avatar updated successfully for user with email: {}", email);
    }

    @Transactional
    public void deleteAvatar() {
        String currentUserEmail = SecurityUtils.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_INVALID));

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setAvatar(null);
        userRepository.save(user);

        log.info("Avatar deleted successfully for user with email: {}", currentUserEmail);
    }

    public Map<String, Object> uploadVideoChunked(MultipartFile file, String folderName) throws IOException {
        File tempFile = convertMultipartFileToFile(file);
        Map<String, Object> uploadResult = cloudinary.uploader().uploadLarge(tempFile, ObjectUtils.asMap(
                "resource_type", "video",
                "folder", folderName,
                "chunk_size", 6000000
        ));
        if (!tempFile.delete()) {
            System.err.println("Cant not delete this file: " + tempFile.getAbsolutePath());
        }
        return uploadResult;
    }
    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload", file.getOriginalFilename());
        file.transferTo(tempFile);
        return tempFile;
    }

}