package com.example.cab.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import com.example.cab.utility.MajorState;
import com.example.cab.utility.MinorState;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cab {

    @Id
    private int cabId;
    private MajorState majorState;      // Signed-In or Signed-Out
    private MinorState minorState;      // Available / Committed / Giving Ride
    private boolean interested;
    private int location;

}
