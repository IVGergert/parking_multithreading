package com.gergert.parking.parser;

import com.gergert.parking.entity.Car;

import java.util.List;

public interface CarParser {
    List<Car> parse(List<String> lines);
}
