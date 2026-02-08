package com.gergert.parking.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Parking {
    private static final Logger logger = LogManager.getLogger();
    private static final int SPOTS_COUNT = 5;

    private final Queue<ParkingSpot> freeSpots;
    private final Lock lock = new ReentrantLock(true);

    private final Condition priorityQueue = lock.newCondition();
    private final Condition normalQueue = lock.newCondition();

    private int waitingPriorityCount;

    private Parking() {
        freeSpots = new LinkedList<>();

        for (int i = 1; i <= SPOTS_COUNT; i++){
            freeSpots.add(new ParkingSpot(i));
        }

        logger.info("Parking initialized. Places: {}", SPOTS_COUNT);
    }

    private static class SingletonHolder {
        private static final Parking INSTANCE = new Parking();
    }

    public static Parking getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ParkingSpot getSpot(Car car) {
        lock.lock();
        try {
            boolean isVip = car.isVip();

            if (isVip){
                waitingPriorityCount++;
                logger.debug("Car: {} vip waits for spot.", car.getId());

                while (freeSpots.isEmpty()){
                    priorityQueue.await();
                }

                waitingPriorityCount--;
            } else {
                while (freeSpots.isEmpty() || waitingPriorityCount > 0) {
                    logger.debug("Car {} normal waits.", car.getId());
                    normalQueue.await();
                }
            }

            ParkingSpot parkingSpot = freeSpots.poll();
            logger.info("Car {} took spot {}", car, parkingSpot.getId());

            return parkingSpot;
        } catch (InterruptedException e) {
            if (car.isVip()) {
                waitingPriorityCount--;
            }

            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.unlock();
        }
    }

    public void releaseSpot(ParkingSpot parkingSpot) {
        lock.lock();
        try {
            freeSpots.add(parkingSpot);
            logger.info("{} released. Expected VIPs: {}", parkingSpot.getId(), waitingPriorityCount);

            if (waitingPriorityCount > 0) {
                priorityQueue.signal();
            } else {
                normalQueue.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}
