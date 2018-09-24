package com.pp.toptal.soccermanager.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.pp.toptal.soccermanager.entity.Country;
import com.pp.toptal.soccermanager.entity.PlayerEntity;
import com.pp.toptal.soccermanager.utils.UnicodeReader;

@Component
final class PlayerGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerGenerator.class);
    
    private static final int AGE_MIN = 18;
    private static final int AGE_MAX = 40;
    
    private static final String SAMPLE_PATH = "csv/playernames_sample.csv";
    
    private static final String ID_HEADER = "ID";
    private static final String FIRSTNAME_HEADER = "FIRSTNAME";
    private static final String LASTNAME_HEADER = "LASTNAME";
    
    @Autowired
    private ApplicationContext ctxt;
    
    private List<String> playerFirstames;
    private List<String> playerLastnames;
    
    private boolean isDataLoaded;
    
    public PlayerEntity generate() {
        if (! isDataLoaded) {
            loadData();
            isDataLoaded = true;
        }
        
        final String playerName = playerFirstames.get((int) Math.round(Math.random() * (playerFirstames.size() - 1)));
        final String playerLastname = playerLastnames.get((int) Math.round(Math.random() * (playerLastnames.size() - 1)));
        final int age = (int) (AGE_MIN + Math.round(Math.random() * (AGE_MAX - AGE_MIN)));
        final Country country = Optional.of(Country.values())
                .map((vs) -> vs[(int) Math.round(Math.random() * (vs.length - 1))]).get();
        
        return new PlayerEntity(playerName, playerLastname, age, country);
    }

    private void loadData() {

        playerFirstames = new ArrayList<>();
        playerLastnames = new ArrayList<>();
        
        CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(',').withHeader();
        Charset charset = Charset.forName("UTF-8");
        
        try (CSVParser parser = new CSVParser(new UnicodeReader(getSampleInputStream(), charset), csvFormat)) {
            
            Map<String, ?> headers = parser.getHeaderMap();
            if (! (headers.containsKey(ID_HEADER) &&
                   headers.containsKey(FIRSTNAME_HEADER) &&
                   headers.containsKey(LASTNAME_HEADER))) {
                throw new IOException(String.format("Playernames sample doesn't contain one or more columns: %s",
                                                    Arrays.asList(ID_HEADER, FIRSTNAME_HEADER, LASTNAME_HEADER)));
            }
            
            for (CSVRecord record : parser.getRecords()) {
                playerFirstames.add(record.get(FIRSTNAME_HEADER));
                playerLastnames.add(Optional.of(record.get(LASTNAME_HEADER))
                        .filter((ln) -> (! ln.isEmpty())).orElse(null));
            }
        } catch (IOException ex) {
            LOGGER.error("Error on CSV-parsing from {} !", SAMPLE_PATH);
        }
    }
    
    private InputStream getSampleInputStream() throws IOException {
        return (ctxt != null) ? ctxt.getResource("classpath:" + SAMPLE_PATH).getInputStream() :
                                getClass().getClassLoader().getResourceAsStream(SAMPLE_PATH);
    }

}
