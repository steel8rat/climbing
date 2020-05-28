package com.mokhov.climbing.services;

import com.mokhov.climbing.exceptions.GymNotFoundException;
import com.mokhov.climbing.models.Gym;
import com.mokhov.climbing.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class GymService {

    private final GymRepository gymRepository;

    public Gym getGym(String gymId) throws GymNotFoundException {
        Optional<Gym> optionalGym = gymRepository.findById(gymId);
        if(!optionalGym.isPresent()) throw new GymNotFoundException(String.format("Gym with id %s isn't found", gymId));
        return  optionalGym.get();
    }
}
