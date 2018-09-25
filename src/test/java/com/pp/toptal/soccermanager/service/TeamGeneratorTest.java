package com.pp.toptal.soccermanager.service;

import static org.junit.Assert.*;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pp.toptal.soccermanager.entity.TeamEntity;

public class TeamGeneratorTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamGeneratorTest.class);
            
    private TeamGenerator generator = new TeamGenerator();
    
    @Test
    public void testGenerate() {
        TeamEntity team = generator.generate();
        
        assertNotNull(team);
        
        assertNotNull(team.getTeamName());
        assertFalse(team.getTeamName().isEmpty());
        
        assertNotNull(team.getCountry());
        
        LOGGER.info(team.toString());
    }

}
