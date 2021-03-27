package com.example.cab.service;

import com.example.cab.entity.Cab;
import com.example.cab.entity.CabRide;
import com.example.cab.repository.CabRepository;
import com.example.cab.utility.MajorState;
import com.example.cab.utility.MinorState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CabService {

    @Autowired
    private CabRepository cabRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Cab saveCab(Cab cab) {

        return cabRepository.save(cab);
    }

    public Cab findByCabId(int cabId) {

        return cabRepository.findByCabId(cabId);
    }

    public void deleteByCabId(int cabId) {

        Cab cab = cabRepository.findByCabId(cabId);
        cabRepository.delete(cab);
    }

    public Cab[] getCabs(int location) {
        return cabRepository.getCabs(location);
    }

    public boolean isSignedOut(int cabId) {
        Cab cab = cabRepository.findByCabId(cabId);
        return cab != null && cab.getMajorState() == MajorState.SignedOut;
    }

    public boolean isSignedIn(int cabId) {
        Cab cab = cabRepository.findByCabId(cabId);
        return cab != null && cab.getMajorState() == MajorState.SignedIn
                && cab.getMinorState() == MinorState.Available;
    }

    public Cab[] getCabsGivingRide() {
        return cabRepository.getCabsGivingRide();
    }

    public Cab[] getAllCabsSignedIn() {
        return cabRepository.getAllCabsSignedIn();
    }

    public void updateInterested(int cabId, boolean b) {
        cabRepository.updateInterested(cabId, b);
    }

    public void updateMinorState(int cabId, MinorState committed) {
        cabRepository.updateMinorState(cabId, committed);
    }

    public void updateLocation(int cabId, int destinationLoc) {
        cabRepository.updateLocation(cabId, destinationLoc);
    }

    public Boolean canRideEnd(int rideId) {

        return restTemplate.getForObject(
                "http://localhost:8081/ride/ride-ended?" +
                        "rideId=" + rideId,
                Boolean.class
        );
    }

    public Boolean canCabSignIn(int cabId, int initialPos) {
        return restTemplate.getForObject(
                "http://localhost:8081/ride/cab-signs-in?" +
                        "cabId=" + cabId +
                        "&initialPos=" + initialPos,
                Boolean.class
        );
    }

    public void updateMajorState(int cabId, MajorState majorState) {
        cabRepository.updateMajorState(cabId, majorState);
    }

    public Boolean canCabSignOut(int cabId) {
        return restTemplate.getForObject(
                "http://localhost:8081/ride/cab-signs-out?" +
                        "cabId=" + cabId,
                Boolean.class
        );
    }
}
