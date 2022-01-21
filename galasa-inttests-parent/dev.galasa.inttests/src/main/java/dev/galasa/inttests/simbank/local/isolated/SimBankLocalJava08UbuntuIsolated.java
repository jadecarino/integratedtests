/*
* Copyright contributors to the Galasa project 
*/
package dev.galasa.inttests.simbank.local.isolated;

import dev.galasa.Test;
import dev.galasa.TestAreas;
import dev.galasa.galasaecosystem.ILocalEcosystem;
import dev.galasa.galasaecosystem.IsolationInstallation;
import dev.galasa.galasaecosystem.LocalEcosystem;
import dev.galasa.inttests.simbank.AbstractSimBankLocal;
import dev.galasa.java.JavaVersion;
import dev.galasa.java.ubuntu.IJavaUbuntuInstallation;
import dev.galasa.java.ubuntu.JavaUbuntuInstallation;
import dev.galasa.linux.ILinuxImage;
import dev.galasa.linux.LinuxImage;
import dev.galasa.linux.OperatingSystem;

@Test
@TestAreas({"simplatform","localecosystem","java08","ubuntu","isolated"})
public class SimBankLocalJava08UbuntuIsolated extends AbstractSimBankLocal {

    @LocalEcosystem(linuxImageTag = "PRIMARY", startSimPlatform = true, isolationInstallation = IsolationInstallation.Full)
    public ILocalEcosystem ecosystem;
    
    @LinuxImage(operatingSystem = OperatingSystem.ubuntu)
    public ILinuxImage linuxImage;
    
    @JavaUbuntuInstallation(javaVersion = JavaVersion.v8)
    public IJavaUbuntuInstallation java;

    @Override
    protected ILocalEcosystem getEcosystem() {
        return this.ecosystem;
    }

}
