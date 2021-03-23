package com.example.cab.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cab {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cabId;
    private String majorState;      // Signed-In or Signed-Out
    private String minorState;      // Available / Committed / Giving Ride

}
