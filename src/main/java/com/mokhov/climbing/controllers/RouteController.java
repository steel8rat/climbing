package com.mokhov.climbing.controllers;


import com.mokhov.climbing.config.AppConfig;
import com.mokhov.climbing.exceptions.GymNotFoundException;
import com.mokhov.climbing.models.Gym;
import com.mokhov.climbing.models.RequestPhotoUploadUrlResponse;
import com.mokhov.climbing.repository.GymRepository;
import com.mokhov.climbing.services.S3Service;
import com.mokhov.climbing.services.UuidService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = RouteController.PATH)
public class RouteController {
    public final static String PATH = AppConfig.API_ROOT_PATH_WITH_V_1 + "/routes";
    private final S3Service s3Service;
    private final GymRepository gymRepository;
    private final UuidService uuidService;

    @GetMapping("/generateUploadUrl")
    public RequestPhotoUploadUrlResponse generateUploadUrl(@RequestParam String gymId, @RequestParam String fileExtension) throws GymNotFoundException {
        // TODO validate file extension
        Optional<Gym> optionalGym = gymRepository.findById(gymId);
        if(!optionalGym.isPresent()) throw new GymNotFoundException(String.format("Gym with id %s isn't found", gymId));
        String fileId = uuidService.generateUuid();
        String key = s3Service.getRoutePhotoKey(optionalGym.get(), fileId + fileExtension);
        return new RequestPhotoUploadUrlResponse(fileId ,s3Service.generatePresignedUploadUrl(key));
    }
}
