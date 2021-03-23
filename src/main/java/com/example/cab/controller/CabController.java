package com.example.cab.controller;

import com.example.cab.entity.Cab;
import com.example.cab.service.CabService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cab")
@Slf4j
public class CabController {

    @Autowired
    private CabService cabService;

    @PostMapping("/")
    public Cab saveCab(@RequestBody Cab cab) {

        log.info("Inside saveCab method of CabController saving Cab Id " + cab.getCabId());
        return cabService.saveCab(cab);
    }

    @GetMapping("/request-ride/")
    public boolean requestRide(@RequestParam int cabId, @RequestParam int rideId, @RequestParam int sourceLoc, @RequestParam int destinationLoc) {

        // If cab accepting the request, then send true. It should accept if cabId is a valid ID and cabId is available and interested to accept.
        // If response is true then cabId enters committed else remains available.

        // Implement interested part such that cab accept alternate requests.

        // For Part 2, this request should be isolated such that the request coming should wait in queue till the previous request is completed.
        log.info("Inside requestRide method of CabController " + cabId + " " + rideId + " " + sourceLoc + " " + destinationLoc);
        return true;
    }

    @GetMapping("ride-started/")
    public boolean rideStarted(@RequestParam int cabId, @RequestParam int rideId) {

        // If cabId is valid and the cab is in committed state for previous request of same rideId then move it into giving-ride state and return true otherwise false

        return true;
    }

    @GetMapping("ride-cancelled")
    public boolean rideCancelled(@RequestParam int cabId, @RequestParam int rideId) {

        // If cabId is valid and in committed state of previous rideId, then return true with changing state to available state
        return true;
    }

    @GetMapping("ride-ended")
    public boolean rideEnded(@RequestParam int cabId, @RequestParam int rideId) {

        // triggered by driver. If cabId is valid and if cab is currently in giving-ride state, then enter available
        // and send request to RideService.rideEnded and return true. Assume to end in specified distance



        return true;
    }

    @GetMapping("sign-in")
    public  boolean signIn(@RequestParam int cabId, @RequestParam int initialPos) {

        // If cabId is valid and cab in signed out state then send request to rideService.cabSignsIn. If response is true then transition to signed in. else respond false

        return true;
    }

    @GetMapping("sign-out")
    public boolean signOut(@RequestParam int cabId) {

        // cab driver send the request to sign out. If valid ID and currently signed in, then send request to  RideService.cabSignsOut. If response true, then transit else respond -1

        return true;
    }

    @GetMapping("num-rides")
    public int numRides(@RequestParam int cabId) {

        // testing purpose. If cabId invalid, return -1.
        // If cab is signed in then return no. of rides given after the last sign-in(include ongoing ride). else return 0

        return 1;
    }
}
