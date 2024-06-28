/*
 * Copyright contributors to the Galasa project
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package dev.galasa.inttests.sdv;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.JsonObject;

import dev.galasa.BeforeClass;
import dev.galasa.ProductVersion;
import dev.galasa.Test;
import dev.galasa.cicsts.CicsRegion;
import dev.galasa.cicsts.CicstsManagerException;
import dev.galasa.cicsts.ICicsRegion;
import dev.galasa.galasaecosystem.GalasaEcosystemManagerException;
import dev.galasa.galasaecosystem.IGenericEcosystem;

public abstract class AbstractSDVLocal {

  @CicsRegion(cicsTag = "A")
  public ICicsRegion cics;
  
  @BeforeClass
    public void setProperties() throws GalasaEcosystemManagerException, CicstsManagerException {
    // Setting these properties in the shadow ecosystem
    getEcosystem().setCpsProperty("cicsts.provision.type", "DSE");
    getEcosystem().setCpsProperty("cicsts.dse.tag.A.applid", cics.getApplid());
    getEcosystem().setCpsProperty("cicsts.dse.tag.A.version", cics.getVersion().toString());
    getEcosystem().setCpsProperty("cicsts.default.logon.initial.text", "HIT ENTER FOR LATEST STATUS");
    getEcosystem().setCpsProperty("cicsts.default.logon.gm.text", "******\\(R)");

  }
        
    @Test
    public void testSDVIvtTest() throws Exception {

        // Only run test if running on CICS 6.2+
        if (!cics.getVersion().isEarlierThan(ProductVersion.v(750))) {

            String runName = getEcosystem().submitRun(null, 
                    null, 
                    null, 
                    "dev.galasa.sdv.manager.ivt",
                    "dev.galasa.sdv.manager.ivt.SdvManagerIVT", 
                    null, 
                    null, 
                    null, 
                    null);
            
            JsonObject run = getEcosystem().waitForRun(runName);
            
            String result = run.get("result").getAsString();
            
            assertThat(result).as("The test indicates the test passes").isEqualTo("Passed");
        } else {
            // Just pass the test if running on earlier CICS versions
            assertThat(true).isTrue();
        }

    }

    abstract protected IGenericEcosystem getEcosystem();
    
}