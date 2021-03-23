package com.example.cab.service;

import com.example.cab.entity.Cab;
import com.example.cab.repository.CabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CabService {

    @Autowired
    private CabRepository cabRepository;

    public Cab saveCab(Cab cab) {

        return cabRepository.save(cab);
    }
}
