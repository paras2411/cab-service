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
@RequestMapping("")
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

    @GetMapping("/feedInitialData")
    public void addCab(){
        cabService.feedInitialData();
    }

    @GetMapping("/getCabs")
    public Cab[] getCabs(@RequestParam int location) {

        return cabService.getCabs(location);
    }

    @GetMapping("/cabDetails")
    public Cab findCab(@RequestParam int cabId) {

        return cabService.findByCabId(cabId);
    }

    @GetMapping("/rideId")
    public int findRideId(@RequestParam int cabId) {

        return cabRideService.findRideIdByCahId(cabId);
    }

    @GetMapping("/cabDelete")
    public void deleteCab(@RequestParam int cabId) {

        cabService.deleteByCabId(cabId);
    }

    @GetMapping("cabRideDelete")
    public void deleteCabRide(@RequestParam int cabId) {

        cabRideService.deleteByCabId(cabId);
    }

    @GetMapping("/rides")
    public int ridesCab(@RequestParam int cabId) {
        return cabRideService.findNoOfRides(cabId);
    }

    @GetMapping("/cabSignedOut")
    public boolean cabSignedOut(@RequestParam int cabId) {
        return cabService.isSignedOut(cabId);
    }

    @GetMapping("/cabSignedInAndAvailable")
    public boolean cabSignedIn(@RequestParam int cabId) {
        return cabService.isSignedInAndAvailable(cabId);
    }

    @GetMapping("/cabsGivingRide")
    public Cab[] cabsGivingRide() {
        return cabService.getCabsGivingRide();
    }

    @GetMapping("allCabsSignedIn")
    public Cab[] allCabsSignedIn() {
        return cabService.getAllCabsSignedIn();
    }

    @GetMapping("/requestRide")
    public boolean requestRide(@RequestParam int cabId,
                               @RequestParam int rideId,
                               @RequestParam int sourceLoc,
                               @RequestParam int destinationLoc) {

        if(sourceLoc < 0 || destinationLoc <= sourceLoc) return false;

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

    @GetMapping("/rideStarted")
    public boolean rideStarted(@RequestParam int cabId,
                               @RequestParam int rideId) {


        log.info("Inside rideStarted method of CabController " + cabId + " " + rideId);

        Cab cab = cabService.findByCabId(cabId);

        if(cab != null) {

            CabRide cabRide = cabRideService.findByRideId(rideId);

            if(cab.getMinorState() == MinorState.Committed && cabRide != null && cabRide.isActive()) {
                cabService.updateLocation(cabId, cabRide.getSourceLoc());
                cabService.updateMinorState(cab.getCabId(), MinorState.GivingRide);
                cab.setMinorState(MinorState.GivingRide);
                cab.setLocation(cabRide.getSourceLoc());
                return true;
            }
        }

        return false;
    }

    @GetMapping("/rideCancelled")
    public boolean rideCancelled(@RequestParam int cabId,
                                 @RequestParam int rideId) {


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

    @GetMapping("/rideEnded")
    public boolean rideEnded(@RequestParam int cabId,
                             @RequestParam int rideId) {

        log.info("Inside rideEnded method of CabController " + cabId + " " + rideId);

        Cab cab = cabService.findByCabId(cabId);
        if(cab != null) {
            if(cab.getMinorState() == MinorState.GivingRide) {

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

    @GetMapping("/signIn")
    public  boolean signIn(@RequestParam int cabId,
                           @RequestParam int initialPos) {


        log.info("Inside signIn method of CabController " + cabId + " " + initialPos);
        boolean cabSignedOut = cabService.isSignedOut(cabId);
        if(cabSignedOut) {

            boolean canCabSignIn = cabService.canCabSignIn(cabId, initialPos);
            if(canCabSignIn) {

                Cab cab = cabService.findByCabId(cabId);

                cabService.updateLocation(cabId, initialPos);
                cabService.updateMinorState(cabId, MinorState.Available);
                cabService.updateMajorState(cabId, MajorState.SignedIn);
                cabService.updateInterested(cabId, true);

                cab.setMajorState(MajorState.SignedIn);
                cab.setMinorState(MinorState.Available);
                cab.setLocation(initialPos);
                cab.setInterested(true);

                return true;
            }
        }

        return false;
    }

    @GetMapping("/signOut")
    public boolean signOut(@RequestParam int cabId) {

        log.info("Inside signOut method of CabController " + cabId);
        Cab cab = cabService.findByCabId(cabId);
        if(cab != null) {
            if(cab.getMajorState() == MajorState.SignedIn) {

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

    @GetMapping("/numRides")
    public int numRides(@RequestParam int cabId) {

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
