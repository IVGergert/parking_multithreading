package com.gergert.parking.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Car implements Callable<String> {
    private static final Logger logger = LogManager.getLogger();

    private final int id;
    private final boolean isVip;

    public Car(int id, boolean isVip) {
        this.id = id;
        this.isVip = isVip;
    }

    public int getId() {
        return id;
    }

    public boolean isVip() {
        return isVip;
    }

    @Override
    public String call() {
        Parking parking = Parking.getInstance();
        ParkingSpot parkingSpot = parking.getSpot(this);

        if (parkingSpot != null) {
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        parking.releaseSpot(parkingSpot);

        return "Car " + id + " finished";
    }
}
