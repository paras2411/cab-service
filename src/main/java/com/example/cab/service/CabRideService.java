package com.example.cab.service;

import com.example.cab.entity.Cab;
import com.example.cab.entity.CabRide;
import com.example.cab.repository.CabRideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CabRideService {

    @Autowired
    private CabRideRepository cabRideRepository;

    public CabRide saveCabRide(CabRide cabRide) {

        return cabRideRepository.save(cabRide);
    }

    public CabRide[] findByCabId(int cabId) {

        return cabRideRepository.findByCabId(cabId);
    }

    public void deleteByRideId(int rideId) {

        cabRideRepository.deleteByRideId(rideId);
    }

    public CabRide findByRideId(int rideId) {
        return cabRideRepository.findByRideId(rideId);
    }

    public int findNoOfRides(int cabId) {
        return cabRideRepository.numOfCabRides(cabId);
    }

    public void deleteByCabId(int cabId) {
        cabRideRepository.deleteByCabId(cabId);
    }

    public void updateActive(int rideId, boolean b) {
        cabRideRepository.updateActive(rideId, b);
    }
}
