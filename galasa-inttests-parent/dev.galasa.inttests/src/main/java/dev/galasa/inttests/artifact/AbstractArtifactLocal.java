/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2021.
 */
package dev.galasa.inttests.artifact;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.JsonObject;

import dev.galasa.Test;
import dev.galasa.galasaecosystem.IGenericEcosystem;

public abstract class AbstractArtifactLocal {
    
    @Test
    public void testArtifactIvtTest() throws Exception {

        String runName = getEcosystem().submitRun(null, 
                null, 
                null, 
                "dev.galasa.artifact.manager.ivt", 
                "dev.galasa.artifact.manager.ivt.ArtifactManagerIVT", 
                null, 
                null, 
                null, 
                null);
        
        JsonObject run = getEcosystem().waitForRun(runName);
        
        String result = run.get("result").getAsString();
        
        assertThat(result).as("The test indicates the test passes").isEqualTo("Passed");
    }

    abstract protected IGenericEcosystem getEcosystem();

}
