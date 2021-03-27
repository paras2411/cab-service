package com.example.cab.controller;

import com.example.cab.entity.Cab;
import com.example.cab.entity.CabRide;
import com.example.cab.service.CabRideService;
import com.example.cab.service.CabService;
import com.example.cab.utility.MajorState;
import com.example.cab.utility.MinorState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cab")
@Slf4j
public class CabController {

    @Autowired
    private CabService cabService;

    @Autowired
    private CabRideService cabRideService;

    @PostMapping("/")
    public Cab saveCab(@RequestBody Cab cab) {

        log.info("Inside saveCab method of CabController saving Cab Id " + cab.getCabId());
        return cabService.saveCab(cab);
    }

    @GetMapping("/get-cabs")
    public Cab[] getCabs(@RequestParam int location) {

        return cabService.getCabs(location);
    }


    @GetMapping("/cab")
    public Cab findCab(@RequestParam int cabId) {

        return cabService.findByCabId(cabId);
    }

    @GetMapping("/cab-delete")
    public void deleteCab(@RequestParam int cabId) {

        cabService.deleteByCabId(cabId);
    }

    @GetMapping("cab-ride-delete")
    public void deleteCabRide(@RequestParam int cabId) {

        cabRideService.deleteByCabId(cabId);
    }

    @GetMapping("/rides")
    public int ridesCab(@RequestParam int cabId) {
        return cabRideService.findNoOfRides(cabId);
    }

    @GetMapping("/cab-signed-out")
    public boolean cabSignedOut(@RequestParam int cabId) {
        return cabService.isSignedOut(cabId);
    }

    @GetMapping("/cab-signed-in")
    public boolean cabSignedIn(@RequestParam int cabId) {
        return cabService.isSignedIn(cabId);
    }

    @GetMapping("/cabs-giving-ride")
    public Cab[] cabsGivingRide(@RequestParam int cabId) {
        return cabService.getCabsGivingRide();
    }

    @GetMapping("all-cabs-signed-in")
    public Cab[] allCabsSignedIn(@RequestParam int cabId) {
        return cabService.getAllCabsSignedIn();
    }

    @GetMapping("/request-ride")
    public boolean requestRide(@RequestParam int cabId,
                               @RequestParam int rideId,
                               @RequestParam int sourceLoc,
                               @RequestParam int destinationLoc) {

        // If cab accepting the request, then send true. It should accept if cabId is a valid ID and cabId is available and interested to accept.
        // If response is true then cabId enters committed else remains available.

        // Implement interested part such that cab accept alternate requests.

        // For Part 2, this request should be isolated such that the request coming should wait in queue till the previous request is completed.
        log.info("Inside requestRide method of CabController " + cabId + " " + rideId + " " + sourceLoc + " " + destinationLoc);

        Cab cab = cabService.findByCabId(cabId);
        if(cab != null) {
            if(cab.getMinorState() == MinorState.Available) {
                if(cab.isInterested()) {
                    cabService.updateMinorState(cab.getCabId(), MinorState.Committed);
                    CabRide cabRide = new CabRide(cabId, rideId, sourceLoc, destinationLoc);
                    cabRideService.saveCabRide(cabRide);
                    cabService.updateInterested(cab.getCabId(), false);
                    return true;
                }
                else {
                    cabService.updateInterested(cab.getCabId(), true);
                }
            }
        }

        return false;
    }

    @GetMapping("/ride-started")
    public boolean rideStarted(@RequestParam int cabId,
                               @RequestParam int rideId) {

        // If cabId is valid and the cab is in committed state for previous request of same rideId then move it into
        // giving-ride state and return true otherwise false
        log.info("Inside rideStarted method of CabController " + cabId + " " + rideId);

        Cab cab = cabService.findByCabId(cabId);

        if(cab != null) {

            CabRide cabRide = cabRideService.findByRideId(rideId);

            if(cab.getMinorState() == MinorState.Committed && cabRide != null && cabRide.isActive()) {
                cabService.updateMinorState(cab.getCabId(), MinorState.GivingRide);
                cab.setMinorState(MinorState.GivingRide);
                return true;
            }
        }

        return false;
    }

    @GetMapping("/ride-cancelled")
    public boolean rideCancelled(@RequestParam int cabId,
                                 @RequestParam int rideId) {

        // If cabId is valid and in committed state of previous rideId, then return true with changing state to available state
        log.info("Inside rideCancelled method of CabController " + cabId + " " + rideId);

        Cab cab = cabService.findByCabId(cabId);
        if(cab != null) {
            if(cab.getMinorState() == MinorState.Committed) {
                cabService.updateMinorState(cab.getCabId(), MinorState.Available);
                cab.setMinorState(MinorState.Available);
                cabRideService.deleteByRideId(rideId);
                return true;
            }
        }

        return false;
    }

    @GetMapping("/ride-ended")
    public boolean rideEnded(@RequestParam int cabId,
                             @RequestParam int rideId) {

        // triggered by driver. If cabId is valid and if cab is currently in giving-ride state, then enter available
        // and send request to RideService.rideEnded and return true. Assume to end in specified distance

        log.info("Inside rideEnded method of CabController " + cabId + " " + rideId);

        Cab cab = cabService.findByCabId(cabId);
        if(cab != null) {
            if(cab.getMinorState() == MinorState.GivingRide) {

                // send request to rideservice
                boolean canRideEnd = cabService.canRideEnd(rideId);

                if(canRideEnd) {

                    cabService.updateMinorState(cab.getCabId(), MinorState.Available);
                    cab.setMinorState(MinorState.Available);

                    CabRide cabRide = cabRideService.findByRideId(rideId);
                    cabRideService.updateActive(cabRide.getRideId(), false);
                    cabRide.setActive(false);

                    cabService.updateLocation(cab.getCabId(), cabRide.getDestinationLoc());
                    cab.setLocation(cabRide.getDestinationLoc());

                    return true;
                }
            }
        }


        return false;
    }

    @GetMapping("/sign-in")
    public  boolean signIn(@RequestParam int cabId,
                           @RequestParam int initialPos) {

        // If cabId is valid and cab in signed out state then send request to rideService.cabSignsIn. If response is true then transition to signed in. else respond false

        log.info("Inside signIn method of CabController " + cabId + " " + initialPos);
        boolean cabSignedOut = cabService.isSignedOut(cabId);
        if(cabSignedOut) {
                // send request to ride service
            boolean canCabSignIn = cabService.canCabSignIn(cabId, initialPos);
            if(canCabSignIn) {
                Cab cab = cabService.findByCabId(cabId);
                cabService.updateLocation(cabId, initialPos);
                cabService.updateMinorState(cabId, MinorState.Available);
                cabService.updateMajorState(cabId, MajorState.SignedIn);
                cab.setMajorState(MajorState.SignedIn);
                cab.setMinorState(MinorState.Available);
                cab.setLocation(initialPos);
                return true;
            }
        }

        return false;
    }

    @GetMapping("/sign-out")
    public boolean signOut(@RequestParam int cabId) {

        // cab driver send the request to sign out. If valid ID and currently signed in, then send request to  RideService.cabSignsOut. If response true, then transit else respond -1

        log.info("Inside signOut method of CabController " + cabId);
        Cab cab = cabService.findByCabId(cabId);
        if(cab != null) {
            if(cab.getMajorState() == MajorState.SignedIn) {
                // send request to ride service

                boolean canCabSignOut = cabService.canCabSignOut(cabId);

                if(canCabSignOut) {
                    cabService.updateMajorState(cabId, MajorState.SignedOut);
                    cabService.updateMinorState(cabId, MinorState.NotAvailable);
                    cab.setMajorState(MajorState.SignedOut);
                    cab.setMinorState(MinorState.NotAvailable);
                    cabRideService.deleteByCabId(cabId);
                    return true;
                }
            }
        }
        return false;
    }

    @GetMapping("/num-rides")
    public int numRides(@RequestParam int cabId) {

        // testing purpose. If cabId invalid, return -1.
        // If cab is signed in then return no. of rides given after the last sign-in(include ongoing ride). else return 0

        log.info("Inside numRides method of CabController " + cabId);
        Cab cab = cabService.findByCabId(cabId);
        if(cab != null) {
            if(cab.getMajorState() == MajorState.SignedIn) {
                return cabRideService.findNoOfRides(cabId);
            }
            else {
                return 0;
            }
        }
        return -1;
    }
}
