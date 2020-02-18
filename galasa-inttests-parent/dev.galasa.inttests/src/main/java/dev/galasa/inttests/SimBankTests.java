package dev.galasa.inttests;

import java.util.UUID;

import org.apache.commons.logging.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.galasa.Test;
import dev.galasa.core.manager.Logger;
import dev.galasa.core.manager.RunName;
import dev.galasa.galasaecosystem.GalasaEcosystemManagerException;
import dev.galasa.galasaecosystem.IKubernetesEcosystem;
import dev.galasa.galasaecosystem.KubernetesEcosystem;
import dev.galasa.kubernetes.IKubernetesNamespace;
import dev.galasa.kubernetes.KubernetesNamespace;

@Test
public class SimBankTests {

    @Logger
    public Log logger;
    
    @RunName
    public String runName;
    
    @KubernetesNamespace(kubernetesNamespaceTag = "SHARED")
    public IKubernetesNamespace namespace;
    
    @KubernetesEcosystem(ecosystemNamespaceTag = "SHARED", kubernetesNamespaceTag = "SHARED")
    public IKubernetesEcosystem ecosystem;
    
    
    @Test
    public void testSimbankIvt() throws GalasaEcosystemManagerException, InterruptedException, TestException {

        String groupName = UUID.randomUUID().toString();
        
        ecosystem.submitRun(null, 
                runName,
                groupName, 
                "dev.galasa.simbank.tests", 
                "dev.galasa.simbanks.tests.SimBankIVT", 
                null, 
                null, 
                "simbank", 
                null);
        
        JsonObject finalResponse = ecosystem.waitForGroupNames(groupName, 180);
        finalResponse = ecosystem.getSubmittedRuns(groupName); // TODO ensure the result is written before the test is marked finished
        JsonArray runs = finalResponse.getAsJsonArray("runs");
        if (runs == null || runs.size() == 0) {
            throw new TestException("Lost the SimBankIVT test, last response was:-\n" + finalResponse);
        }
        
        if (runs.size() != 1) {
            throw new TestException("Too many runs returned, last response was:-\n" + finalResponse);
        }
                
        JsonObject run = runs.get(0).getAsJsonObject();
        JsonElement result = run.get("result");
        if (result == null) {
            throw new TestException("Run did return a result, last response was:-\n" + finalResponse);
        }
        
        if (!"Passed".equals(result.getAsString())) {
            throw new TestException("Run did not pass, last response was:-\n" + finalResponse);
        }
        
        logger.info("SimBankIVT test passed");
    }
    
}
