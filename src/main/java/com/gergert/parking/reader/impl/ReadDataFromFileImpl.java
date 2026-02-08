package com.gergert.parking.reader.impl;

import com.gergert.parking.reader.ReadDataFromFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ReadDataFromFileImpl implements ReadDataFromFile {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public List<String> readFromFile(String filePath) {
        logger.info("Reading file: {}", filePath);

        Path path = Paths.get(filePath);

        try {
            List<String> lines = Files.readAllLines(path);
            logger.info("Success on reading file, Read {} lines from file {}", lines.size(), path);
            return lines;
        } catch (IOException e) {
            logger.error("Error reading file {}", path, e);
            return List.of();
        }
    }
}
