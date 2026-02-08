package com.gergert.parking.parser.impl;

import com.gergert.parking.entity.Car;
import com.gergert.parking.parser.CarParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CarParserImpl implements CarParser {
    private static final Logger logger = LogManager.getLogger();
    private static final String DELIMITER_REGEX = ",";


    @Override
    public List<Car> parse(List<String> lines) {
        List<Car> cars = new ArrayList<>();

        for (String line : lines){
            String[] parts = line.split(DELIMITER_REGEX);

            int id = Integer.parseInt(parts[0].trim());
            boolean isVip = Boolean.parseBoolean(parts[1].trim());
            cars.add(new Car(id, isVip));
        }

        logger.info("Parsed {} cars.", cars.size());
        return cars;
    }
}
