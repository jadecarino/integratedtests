/*
 * Licensed Materials - Property of IBM
 * 
 * (c) Copyright IBM Corp. 2021.
 */
package dev.galasa.inttests.simbank;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.JsonObject;

import dev.galasa.Test;
import dev.galasa.galasaecosystem.GalasaEcosystemManagerException;
import dev.galasa.galasaecosystem.IGenericEcosystem;
import dev.galasa.inttests.TestException;

public abstract class AbstractSimBankLocal {

   @Test
    public void testSimbankIvt() throws GalasaEcosystemManagerException, InterruptedException, TestException {

       String runName = getEcosystem().submitRun(null, 
               null, 
               null, 
               "dev.galasa.simbank.tests", 
               "dev.galasa.simbank.tests.SimBankIVT", 
               null, 
               null, 
               "simbank", 
               null);
       
       JsonObject run = getEcosystem().waitForRun(runName);
       
       String result = run.get("result").getAsString();
       
       assertThat(result).as("The test indicates the test passes").isEqualTo("Passed");
    }
    
    @Test
    public void testBasicAccountCreditTest() throws GalasaEcosystemManagerException, InterruptedException, TestException {

        String runName = getEcosystem().submitRun(null, 
                null, 
                null, 
                "dev.galasa.simbank.tests", 
                "dev.galasa.simbank.tests.BasicAccountCreditTest", 
                null, 
                null, 
                "simbank", 
                null);
        
        JsonObject run = getEcosystem().waitForRun(runName);
        
        String result = run.get("result").getAsString();
        
        assertThat(result).as("The test indicates the test passes").isEqualTo("Passed");
    }
    
    @Test
    public void testProvisionedAccountCreditTests() throws GalasaEcosystemManagerException, InterruptedException, TestException {

        String runName = getEcosystem().submitRun(null, 
                null, 
                null, 
                "dev.galasa.simbank.tests", 
                "dev.galasa.simbank.tests.ProvisionedAccountCreditTests", 
                null, 
                null, 
                "simbank", 
                null);
        
        JsonObject run = getEcosystem().waitForRun(runName);
        
        String result = run.get("result").getAsString();
        
        assertThat(result).as("The test indicates the test passes").isEqualTo("Passed");
    }
    
    abstract protected IGenericEcosystem getEcosystem();

}
