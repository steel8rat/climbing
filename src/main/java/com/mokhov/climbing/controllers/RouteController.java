package com.mokhov.climbing.controllers;

import com.mokhov.climbing.config.AppConfig;
import com.mokhov.climbing.config.DoConfig;
import com.mokhov.climbing.exceptions.FileExtensionNotSupported;
import com.mokhov.climbing.exceptions.GymNotFoundException;
import com.mokhov.climbing.exceptions.S3ObjectNotFound;
import com.mokhov.climbing.exceptions.UserNotFoundException;
import com.mokhov.climbing.models.*;
import com.mokhov.climbing.repository.GymRouteRepository;
import com.mokhov.climbing.services.GymService;
import com.mokhov.climbing.services.S3Service;
import com.mokhov.climbing.services.UserService;
import com.mokhov.climbing.services.UuidService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = RouteController.PATH)
public class RouteController {
    public final static String PATH = AppConfig.API_ROOT_PATH_WITH_V_1 + "/routes";
    private final S3Service s3Service;
    private final UuidService uuidService;
    private final UserService userService;
    private final GymService gymService;
    private final GymRouteRepository gymRouteRepository;
    private final DoConfig doConfig;

    @GetMapping("/generateUploadUrl")
    public RequestPhotoUploadUrlResponse generateUploadUrl(@RequestParam String gymId, @RequestParam String fileExtension) throws GymNotFoundException, FileExtensionNotSupported {
        if(!doConfig.getUploadFormats().contains(fileExtension.toLowerCase())) throw new FileExtensionNotSupported(String.format("Extension %s is not supported", fileExtension));
        String fileId = uuidService.generateUuid();
        String key = s3Service.getRoutePhotoKey(gymService.getGym(gymId), fileId + fileExtension);
        return new RequestPhotoUploadUrlResponse(fileId ,s3Service.generatePresignedUploadUrl(key));
    }

    @PostMapping
    public void postRoute(@AuthenticationPrincipal JwtAuthenticatedUser jwtUser, @RequestParam String gymId, @RequestParam String photoFileName) throws UserNotFoundException, GymNotFoundException, S3ObjectNotFound {
        User user = userService.getMongoUser(jwtUser.getId());
        Gym gym = gymService.getGym(gymId);
        String objectKey = s3Service.getRoutePhotoKey(gym, photoFileName);
        if (!s3Service.doesObjectExist(objectKey))
            throw new S3ObjectNotFound(String.format("Uploaded file %s not found", objectKey));
        s3Service.setPublicAccess(objectKey);
        GymRoute route = new GymRoute();
        route.setAuthor(user);
        route.addPhoto(photoFileName);
        gymRouteRepository.save(route);
    }

}
