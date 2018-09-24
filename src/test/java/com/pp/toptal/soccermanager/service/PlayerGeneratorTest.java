package com.pp.toptal.soccermanager.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pp.toptal.soccermanager.entity.PlayerEntity;

public class PlayerGeneratorTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerGeneratorTest.class);
    
    private PlayerGenerator generator = new PlayerGenerator();
    
    @Test
    public void testGenerate() {
        PlayerEntity player = generator.generate();
        
        assertNotNull(player);
        
        assertNotNull(player.getFirstname());
        assertFalse(player.getFirstname().isEmpty());
        
        assertTrue((player.getLastname() == null) || (! player.getLastname().isEmpty()));
        
        assertNotNull(player.getCountry());
        
        LOGGER.info(player.toString());
    }


}
