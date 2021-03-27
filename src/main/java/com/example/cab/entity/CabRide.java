package com.example.cab.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CabRide {

    @Id
    private int rideId;
    private int cabId;
    private int sourceLoc;
    private int destinationLoc;
    private boolean active;

    public CabRide(int cabId, int rideId, int sourceLoc, int destinationLoc) {
        this.cabId = cabId;
        this.rideId = rideId;
        this.sourceLoc = sourceLoc;
        this.destinationLoc = destinationLoc;
        this.active = true;
    }

}
