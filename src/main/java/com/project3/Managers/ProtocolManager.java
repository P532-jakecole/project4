package com.project3.Managers;

import com.project3.DataTypes.*;
import com.project3.OrderAccess;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProtocolManager {
    private final OrderAccess orderAccess;

    public ProtocolManager(OrderAccess orderAccess) {
        this.orderAccess = orderAccess;
    }

    public ArrayList<Protocol> listProtocols(){
        return orderAccess.findAllProtocols();
    }

    // inputs: [ String name, String description, AccuracyRating ]
    public void createProtocol(Object[] inputs){
        Protocol protocol = new Protocol();
        protocol.setName((String) inputs[0]);
        protocol.setDescription((String) inputs[1]);
        protocol.setAccuracyRating((AccuracyRating) inputs[2]);
        orderAccess.addProtocol(protocol);
    }
}
