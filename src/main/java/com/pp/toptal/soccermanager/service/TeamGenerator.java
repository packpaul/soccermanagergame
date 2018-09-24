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
import com.pp.toptal.soccermanager.entity.TeamEntity;
import com.pp.toptal.soccermanager.utils.UnicodeReader;

@Component
class TeamGenerator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamGenerator.class);
    
    private static final String SAMPLE_PATH = "csv/teamnames_sample.csv";
    
    private static final String ID_HEADER = "ID";
    private static final String TEAMNAME_HEADER = "TEAMNAME";
    
    @Autowired
    private ApplicationContext ctxt;
    
    private List<String> teamNames;
    
    private boolean isDataLoaded;
    
    public TeamEntity generate() {
        if (! isDataLoaded) {
            loadData();
            isDataLoaded = true;
        }

        final Country country = Optional.of(Country.values())
                .map((vs) -> vs[(int) Math.round(Math.random() * (vs.length - 1))]).get();
        String teamName = teamNames.get((int) Math.round(Math.random() * (teamNames.size() - 1)));
        teamName = String.format("%s (%s)", teamName, country); 
        
        return new TeamEntity(teamName, country);
    }

    private void loadData() {

        teamNames = new ArrayList<>();
        
        CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(',').withHeader();
        Charset charset = Charset.forName("UTF-8");
        
        try (CSVParser parser = new CSVParser(new UnicodeReader(getSampleInputStream(), charset), csvFormat)) {
            
            Map<String, ?> headers = parser.getHeaderMap();
            if (! (headers.containsKey(ID_HEADER) && headers.containsKey(TEAMNAME_HEADER))) {
                throw new IOException(String.format("Teamnames sample doesn't contain one or more columns: %s",
                                                    Arrays.asList(ID_HEADER, TEAMNAME_HEADER)));
            }
            
            for (CSVRecord record : parser.getRecords()) {
                teamNames.add(record.get(TEAMNAME_HEADER));                
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
