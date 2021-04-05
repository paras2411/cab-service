package com.example.cab.repository;

import com.example.cab.entity.Cab;
import com.example.cab.utility.MajorState;
import com.example.cab.utility.MinorState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface CabRepository extends JpaRepository<Cab, Integer> {


    Cab findByCabId(int cabId);

    @Query("select c from Cab c where c.majorState=0 order by abs(?1 - c.location)")
    Cab[] getCabs(int location);

    @Query("select c from Cab c where c.majorState=0 and c.minorState=2")
    Cab[] getCabsGivingRide();

    @Query("select c from Cab c where c.majorState=0")
    Cab[] getAllCabsSignedIn();

    @Modifying
    @Query("update Cab c set c.interested = ?2 where c.cabId = ?1")
    @Transactional
    void updateInterested(int cabId, boolean b);

    @Modifying
    @Query("update Cab c set c.minorState = ?2 where c.cabId = ?1")
    @Transactional
    void updateMinorState(int cabId, MinorState committed);

    @Modifying
    @Query("update Cab c set c.location = ?2 where c.cabId = ?1")
    @Transactional
    void updateLocation(int cabId, int destinationLoc);

    @Modifying
    @Query("update Cab c set c.majorState = ?2 where c.cabId = ?1")
    @Transactional
    void updateMajorState(int cabId, MajorState signedIn);
}
