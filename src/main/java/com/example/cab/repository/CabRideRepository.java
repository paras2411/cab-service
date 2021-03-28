package com.example.cab.repository;

import com.example.cab.entity.Cab;
import com.example.cab.entity.CabRide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface CabRideRepository extends JpaRepository<CabRide, Integer> {

    @Query("select c from CabRide c where c.cabId = ?1")
    CabRide[] findByCabId(int cabId);

    @Query("delete from CabRide c where c.cabId = ?1")
    void deleteByCabId(int cabId);

    CabRide findByRideId(int rideId);

    @Query("SELECT count(c) from CabRide c where c.cabId = ?1")
    int numOfCabRides(int cabId);

    @Query("delete from CabRide c where c.rideId = ?1")
    void deleteByRideId(int rideId);

    @Modifying
    @Query("update CabRide c set c.active = ?2 where c.rideId = ?1")
    @Transactional
    void updateActive(int rideId, boolean b);

    @Query("select c.rideId from CabRide c where c.cabId=?1 and c.active=true")
    int findRideIdByCabId(int cabId);
}
