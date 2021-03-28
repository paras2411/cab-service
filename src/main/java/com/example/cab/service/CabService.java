package com.example.cab.service;

import com.example.cab.entity.Cab;
import com.example.cab.entity.CabRide;
import com.example.cab.repository.CabRepository;
import com.example.cab.utility.MajorState;
import com.example.cab.utility.MinorState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Slf4j
@Service
public class CabService {

    @Autowired
    private CabRepository cabRepository;

    @Autowired
    private RestTemplate restTemplate;

    private final String rideUrl = "http://localhost:8081/";

    public Cab saveCab(Cab cab) {

        return cabRepository.save(cab);
    }

    public void feedInitialData() {

        File file = new File("/Users/paraslohani/Documents/IISc/PoDS/IDs.txt");
        try {
            Scanner scan = new Scanner(file);
            int counter = 0;
            while(scan.hasNextLine()) {
                String cur = scan.nextLine();
                if(cur.equals("****")) counter++;
                else if(counter == 1) {
                    Cab c = new Cab(Integer.parseInt(cur), MajorState.SignedOut, MinorState.NotAvailable, false, 0);
                    cabRepository.save(c);
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            log.info("File IDs.txt not found");
        }
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
                rideUrl + "rideEnded?" +
                        "rideId=" + rideId,
                Boolean.class
        );
    }

    public Boolean canCabSignIn(int cabId, int initialPos) {
        return restTemplate.getForObject(
                rideUrl + "cabSignsIn?" +
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
                rideUrl + "cabSignsOut?" +
                        "cabId=" + cabId,
                Boolean.class
        );
    }

}
