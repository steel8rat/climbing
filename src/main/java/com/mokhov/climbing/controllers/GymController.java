package com.mokhov.climbing.controllers;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mokhov.climbing.config.AppConfig;
import com.mokhov.climbing.enumerators.BusinessProviderEnum;
import com.mokhov.climbing.enumerators.DistanceUnitsEnum;
import com.mokhov.climbing.models.*;
import com.mokhov.climbing.repository.GymRepository;
import com.mokhov.climbing.repository.UserRepository;
import com.mokhov.climbing.repository.YelpBusinessRepository;
import com.mokhov.climbing.repository.YelpCacheRepository;
import com.mokhov.climbing.services.YelpService;
import com.mokhov.climbing.utils.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = GymController.PATH)
public class GymController {
    public final static String PATH = AppConfig.API_ROOT_PATH_WITH_V_1 + "/gyms";
    private final UserRepository userRepository;
    private final YelpService yelpService;
    private final YelpCacheRepository yelpCacheRepository;
    private final YelpBusinessRepository yelpBusinessRepository;
    private final GymRepository gymRepository;


    @GetMapping
    public GymsResponse getGyms(@RequestParam double latitude, @RequestParam double longitude) throws UnirestException {
        // Normalize coordinates to one decimal place
        String latitudeNormalized = normalizeCoordinate(latitude);
        String longitudeNormalized = normalizeCoordinate(longitude);
        // Create string key like "43.7,-79.4"
        String latLonId = generateLatLonId(latitudeNormalized, longitudeNormalized);
        // Check if mongo has cache for the given coordinates
        Optional<YelpCache> cache = yelpCacheRepository.findById(latLonId);
        GymsResponse response;
        if (cache.isPresent() && cache.get().getBusinesses() != null && !cache.get().getBusinesses().isEmpty()) { // Cache is found
            response = new GymsResponse(BusinessProviderEnum.YELP, true, cache.get().getBusinesses());
        } else { // Cache isn't found, call Yelp
            YelpSearchResponse yelpSearchResponse = yelpService.search(latitudeNormalized, longitudeNormalized);
            // Create new Yelp cache
            YelpCache yelpCache = new YelpCache(latLonId, yelpSearchResponse.getBusinesses());
            yelpCacheRepository.save(yelpCache);
            yelpBusinessRepository.saveAll(yelpCache.getBusinesses());
            response = new GymsResponse(BusinessProviderEnum.YELP, false, yelpSearchResponse.getBusinesses());
        }
        // Apply hidden flags
        extendWithInternalGyms(response.getBusinesses());
        // For better accuracy sort results by distance from not rounded coordinates
        sortByDistance(response.getBusinesses(), latitude, longitude);
        return response;
    }

    @DeleteMapping("/cache/yelp")
    public void purgeYelpCache(@RequestParam(required = false) Double latitude, @RequestParam(required = false) Double longitude) {
        if (latitude == null || longitude == null) {
            yelpCacheRepository.deleteAll();
            yelpBusinessRepository.deleteAll();
        } else {
            yelpCacheRepository.deleteById(generateLatLonId(latitude, longitude));
        }
    }


    /**
     * Change visibility of existed gym or, if such gym does not exist in gym repo, create new gym with provided
     * visibility property. This method should only be accessed by admins. The purpose of this method is to provide
     * ability to hide businesses that are not climbing gyms.
     *
     * @param jwtAuthenticatedUser injected authentication principal
     * @param id         of the business
     * @param provider   provider of the business (INTERNAL, YELP, GOOGLE)
     * @param visibility to set
     */
    @PatchMapping("/{id}")
    public void setVisibility(@AuthenticationPrincipal JwtAuthenticatedUser jwtAuthenticatedUser,
                              @PathVariable("id") String id, @RequestParam BusinessProviderEnum provider,
                              @RequestParam boolean visibility) {
        Optional<User> optionalOfUser = userRepository.findById(jwtAuthenticatedUser.getId());
        if(!optionalOfUser.isPresent()) throw new RuntimeException("jwtUser isn't found");
        User user = optionalOfUser.get();
        //TODO: add id validation
        Optional<Gym> optionalGym;
        switch (provider) {
            case INTERNAL:
                optionalGym = gymRepository.findById(id);
                if (!optionalGym.isPresent()) throw new RuntimeException("Gym not found");
                Gym foundGym = optionalGym.get();
                foundGym.setVisible(visibility);
                foundGym.setVisibilityChangedBy(user);
                gymRepository.save(foundGym);
                return;
            case YELP:
                Gym gym;
                optionalGym = gymRepository.findByYelpId(id);
                if (!optionalGym.isPresent()) {
                    gym = new Gym();
                    gym.setYelpId(id);
                } else {
                    gym = optionalGym.get();
                }
                gym.setVisible(visibility);
                gym.setVisibilityChangedBy(user);
                gymRepository.save(gym);
                break;
            case GOOGLE:
                throw new RuntimeException("GOOGLE provider is not supported yet");
        }
    }

    private void extendWithInternalGyms(List<YelpBusiness> yelpBusinessList) {
        List<String> ids = new ArrayList<>();
        yelpBusinessList.forEach(business -> ids.add(business.getId()));
        List<Gym> gyms = gymRepository.findAllByYelpIds(ids);
        if (!gyms.isEmpty()) {
            for(Gym gym: gyms){
                for(YelpBusiness yelpBusiness: yelpBusinessList){
                    if(gym.getYelpId().equals(yelpBusiness.getId())){
                        yelpBusiness.setHidden(gym.isVisible());
                        yelpBusiness.setInternalGymId(gym.getId());
                        break;
                    }
                }
            }
        }
    }

    private void sortByDistance(List<YelpBusiness> businesses, double latitude, double longitude) {
        businesses.forEach((business) -> business.setDistance(DistanceCalculator.distance(latitude, longitude, business.getCoordinates().getLatitude(), business.getCoordinates().getLongitude(), DistanceUnitsEnum.degrees)));
        businesses.sort(Comparator.comparingDouble(YelpBusiness::getDistance));
    }

    private String generateLatLonId(String latitude, String longitude) {
        // Creat string key like "43.7,-79.4"
        return String.format("%s,%s", latitude, longitude);
    }

    /**
     * Round coordinate to one decimal point and generate Id for yelp cache
     *
     * @param latitude  coordinate
     * @param longitude coordinate
     * @return string like "43.7,-79.4"
     */
    private String generateLatLonId(double latitude, double longitude) {
        String latitudeNormalized = normalizeCoordinate(latitude);
        String longitudeNormalized = normalizeCoordinate(longitude);
        return generateLatLonId(latitudeNormalized, longitudeNormalized);
    }

    private String normalizeCoordinate(double coordinate) {
        // Round coordinate to one decimal place
        DecimalFormat decimalFormatter = new DecimalFormat(".#");
        return decimalFormatter.format(coordinate);
    }

}
