package com.example.demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    // Lista de formatos de imagen válidos
    private final List<String> allowedContentTypes = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/bmp", "image/tiff", "image/webp", "image/heic"
    );


//    public String uploadImage(MultipartFile file) throws IOException {
//        @SuppressWarnings("rawtypes")
//        Map uploadResult = cloudinary.uploader().upload(
//                file.getBytes(),
//                ObjectUtils.asMap(
//                        "transformation", new com.cloudinary.Transformation()
//                                .width(500)
//                                .height(500)
//                                .crop("fill")
//                                .gravity("auto")));
//        return uploadResult.get("secure_url").toString();
//    }

    public String uploadImage(MultipartFile file) throws IOException {
        // Validar que el archivo sea una imagen permitida
        if (!allowedContentTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException("El archivo no es un formato de imagen permitido");
        }

        @SuppressWarnings("rawtypes")
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "transformation", new com.cloudinary.Transformation()
                                .width(500)
                                .height(500)
                                .crop("fill")
                                .gravity("auto")
                )
        );

        return uploadResult.get("secure_url").toString();
    }



}
