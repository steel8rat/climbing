package com.mokhov.climbing.controllers;

import com.mokhov.climbing.config.AppConfig;
import com.mokhov.climbing.config.UserConfig;
import com.mokhov.climbing.enumerators.BusinessProviderEnum;
import com.mokhov.climbing.exceptions.UserNotFoundException;
import com.mokhov.climbing.models.*;
import com.mokhov.climbing.repository.GymRepository;
import com.mokhov.climbing.repository.UserRepository;
import com.mokhov.climbing.services.*;
import com.mokhov.climbing.utils.Utils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = UserController.PATH)
public class UserController {
    public final static String PATH = AppConfig.API_ROOT_PATH_WITH_V_1 + "/user";
    private final AppleSignInServiceImpl appleSignInService;
    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final S3Service s3Service;
    private final JwtService jwtService;
    private final UserConfig userConfig;
    private final UserService userService;
    private final UuidService uuidService;

    @GetMapping("/logout")
    boolean logout(@NonNull @AuthenticationPrincipal JwtAuthenticatedUser jwtUser) throws UserNotFoundException {
        User user = userService.getMongoUser(jwtUser.getId());
        userRepository.save(user);
        return true;
    }

    @PostMapping("/gyms")
    String addHomGym(@AuthenticationPrincipal JwtAuthenticatedUser jwtUser, @RequestParam BusinessProviderEnum provider, @RequestParam String id) throws UserNotFoundException {
        User user = userService.getMongoUser(jwtUser.getId());
        Gym gym = null;
        Optional<Gym> optionalGym;
        switch (provider) {
            case INTERNAL:
                optionalGym = gymRepository.findById(id);
                if (!optionalGym.isPresent())
                    throw new RuntimeException(String.format("Gym isn't found by yelp id {%s}", id));
                gym = optionalGym.get();
                break;
            case YELP:
                optionalGym = gymRepository.findByYelpId(id);
                if (!optionalGym.isPresent()) {
                    Gym newGym = new Gym();
                    newGym.setYelpId(id);
                    gymRepository.save(newGym);
                    Optional<Gym> optionalNewGym = gymRepository.findByYelpId(id);
                    if (!optionalNewGym.isPresent())
                        throw new RuntimeException(String.format("Gym isn't found by yelp id {%s}", id));
                    gym = optionalNewGym.get();
                } else {
                    gym = optionalGym.get();
                }
                break;
            case GOOGLE:
                throw new RuntimeException("Google api isn't supported yet");
        }
        if (user.getHomeGymIds() == null) user.setHomeGymIds(new HashSet<>());
        user.getHomeGymIds().add(gym.getId());
        userRepository.save(user);
        return gym.getId();
    }

    @DeleteMapping("/gyms")
    void removeHomeGym(@AuthenticationPrincipal JwtAuthenticatedUser jwtUser, @RequestParam String gymId) throws UserNotFoundException {
        User user = userService.getMongoUser(jwtUser.getId());
        Optional<Gym> optionalGym = gymRepository.findById(gymId);
        if (!optionalGym.isPresent()) throw new RuntimeException(String.format("Gym {%s} isn't found", gymId));
        String homeGymId = optionalGym.get().getId();
        Set<String> homeGymIds = user.getHomeGymIds();
        if (homeGymIds != null) {
            if (homeGymIds.remove(homeGymId)) {
                userRepository.save(user);
            } else {
                //TODO add proper logging
                System.out.println(String.format("Gym {%s} isn't found in user {%s} home gyms list", gymId, jwtUser.getId()));
            }
        }
    }

    @GetMapping
    User getUser(@AuthenticationPrincipal JwtAuthenticatedUser jwtUser) throws UserNotFoundException {
        return userService.getMongoUser(jwtUser.getId());
    }

    /**
     * @param appleIdCredential Apple credentials received on the client after user signs in
     * @return uuid
     * @throws BadCredentialsException in case of failed sign in
     */
    @PostMapping("/login/apple")
    public SignInWithProviderResponse signInWithApple(@RequestBody AppleIdCredential appleIdCredential) throws Exception {
        if (!appleSignInService.validateIdentityToken(appleIdCredential.getIdentityToken()))
            throw new BadCredentialsException("Identity token is invalid");
        AppleTokenResponse appleTokenResponse = appleSignInService.obtainRefreshToken(appleIdCredential.getAuthorizationCode());
        if (appleTokenResponse == null)
            throw new BadCredentialsException("Error obtaining refresh token");
        User user = userRepository.findByAppleIdCredentialUser(appleIdCredential.getUser());
        boolean newUserFlag = false;
        if (user == null) {
            // NEW USER
            user = new User(appleIdCredential);
            String email = user.getEmail();
            if (email != null && email.length() > 0) {
                String nickname = Utils.getNicknameFromEmail(email);
                if (!userRepository.existsByNickname(nickname)) user.setNickname(nickname);
            }
            user.setPhotoPath(String.format("/avatar/av%s.jpg", (int) (3.0 * Math.random())));
            user = userRepository.save(user);
            newUserFlag = true;
        }
        //TODO think about a proper way of obtaining admin role
        if (user.getAppleIdCredentialUser() != null && user.getAppleIdCredentialUser().equals("001575.1a1645591f184e58a5a7cf1d9e59d8e4.2258")) {
            user.addRole("ADMIN");
        }
        user.setAppleRefreshToken(appleTokenResponse.getRefreshToken());
        user.setToken(jwtService.generateToken(user));
        userRepository.save(user);
        return new SignInWithProviderResponse(user, newUserFlag);
    }

    /**
     * @param googleIdCredential Apple credentials received on the client after user signs in
     */
    @PostMapping("/login/google")
    public SignInWithProviderResponse signInWithGoogle(@RequestBody GoogleIdCredentials googleIdCredential) {
        //TODO: add code instead of placeholder below for removing IDE complains
        User user = new User();
        user.setId(googleIdCredential.getId());
        user.setToken(jwtService.generateToken(user));
        return new SignInWithProviderResponse(user, true);
    }

    @PostMapping("/")
    public void updateUser(@AuthenticationPrincipal JwtAuthenticatedUser jwtUser, @RequestBody EditUserRequest editUserRequest) throws UserNotFoundException {
        User user = userService.getMongoUser(jwtUser.getId());
        user.setName(editUserRequest.getName());
        user.setNickname(editUserRequest.getNickname());
        userRepository.save(user);
    }

    @GetMapping("/generateUploadUrl")
    public RequestPhotoUploadUrlResponse requestUploadUrl(@AuthenticationPrincipal JwtAuthenticatedUser jwtUser, @RequestParam String fileExtension) throws UserNotFoundException {
        //TODO validate file extension
        User user = userService.getMongoUser(jwtUser.getId());
        String fileId = uuidService.generateUuid();
        String objectKey = s3Service.getUserPhotoKey(user, fileId + fileExtension);
        return new RequestPhotoUploadUrlResponse(fileId, objectKey);
    }

    @GetMapping("/updatePhotoUrl")
    public String updatePhotoUrl(@AuthenticationPrincipal JwtAuthenticatedUser jwtUser, @RequestParam String fileName) throws UserNotFoundException {
        User user = userService.getMongoUser(jwtUser.getId());
        String objectKey = s3Service.getUserPhotoKey(user, fileName);
        if (!s3Service.doesObjectExist(objectKey))
            throw new RuntimeException("S3 object isn't found");
        s3Service.deleteFile(s3Service.getUserPhotoKey(user, user.getPhotoFileName()));
        s3Service.setPublicAccess(objectKey);
        String photoUrl = "/" + objectKey;
        user.setPhotoPath(photoUrl);
        user.setPhotoFileName(fileName);
        userRepository.save(user);
        return photoUrl;
    }

    /**
     * Validate user value
     *
     * @param jwtUser user that is currently authorized
     * @param value   to validate
     * @return empty string if value is valid; if not, returns message explaining reason
     */
    @GetMapping("/validateNickname")
    public String validateNickname(@AuthenticationPrincipal JwtAuthenticatedUser jwtUser, @RequestParam String value) {
        assert jwtUser.getId() != null;
        value = value.trim();
        if (value.contains(" ")) return "Can't contain spaces";
        if (value.isEmpty())
            return "Please enter nickname";
        if (value.length() > userConfig.getNicknameMaxChar())
            return String.format("Must be less than %s characters", userConfig.getNicknameMaxChar());
        String allowedCars = userConfig.getNicknameAllowedChar();
        for (int i = 0; i < value.length(); i++) {
            if (allowedCars.indexOf(value.charAt(i)) == -1)
                return "Can only use letters, numbers, underscores and periods";
        }
        Optional<User> optionalUser = userRepository.findById(jwtUser.getId());
        if (!optionalUser.isPresent())
            throw new RuntimeException(String.format("User with {%s} isn't found", jwtUser.getId()));
        User foundUser = optionalUser.get();
        // If user with such nickname already exists, return message
        if (foundUser.getNickname() != null && !foundUser.getNickname().equals(value))
            if (userRepository.existsByNickname(value.trim())) return "Already taken";
        // No issues found
        return "";
    }

    /**
     * Validate user value
     *
     * @param value to validate
     * @return empty string if nickname is valid; if not, returns message explaining reason
     */
    @GetMapping("/validateFullName")
    public String validateFullName(@RequestParam String value) {
        value = value.trim();
        return userService.checkFullname(value);
    }

}
