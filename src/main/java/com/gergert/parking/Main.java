package com.gergert.parking;

import com.gergert.parking.entity.Car;
import com.gergert.parking.parser.CarParser;
import com.gergert.parking.parser.impl.CarParserImpl;
import com.gergert.parking.reader.ReadDataFromFile;
import com.gergert.parking.reader.impl.ReadDataFromFileImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger();
    private static final String FILE_PATH = "data/cars.txt";

    public static void main(String[] args) {
        logger.info("Starting application");

        ReadDataFromFile readDataFromFile = new ReadDataFromFileImpl();
        CarParser carParser = new CarParserImpl();

        List<String> lines = readDataFromFile.readFromFile(FILE_PATH);

        if (lines.isEmpty()){
            logger.error("No data found.");
            return;
        }

        List<Car> cars = carParser.parse(lines);

        ExecutorService executorService = Executors.newCachedThreadPool();

        for (Car car : cars){
            String status = car.isVip() ? "VIP" : "DEFAULT";
            logger.info("Car id = {}, Status = {}", car.getId(), car.isVip());
            executorService.submit(car);
        }

        executorService.shutdown();

        try {
            if (executorService.awaitTermination(5, TimeUnit.MINUTES)){
                logger.info("Application finished successfully.");
            } else {
                logger.warn("Time limit exceeded! Stopping forcefully.");
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error("Main thread interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}