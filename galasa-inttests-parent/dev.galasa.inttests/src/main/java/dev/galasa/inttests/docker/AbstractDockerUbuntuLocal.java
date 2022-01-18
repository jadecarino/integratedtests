/*
 * Copyright contributors to the Galasa project
 */
package dev.galasa.inttests.docker;

import dev.galasa.BeforeClass;

public abstract class AbstractDockerUbuntuLocal extends AbstractDockerLocal {
	
	private String dockerPort = "2376";

	@BeforeClass
	public void setProps() throws Exception {
		logger.info("Setting the CPS props for shadow environment");
		String dockerHostname = getLinuxImage().getIpHost().getIpv4Hostname();
		getEcosystem().setCpsProperty("docker.default.engines", "DKRENGINE01");
		getEcosystem().setCpsProperty("docker.engine.DKRENGINE01.hostname", dockerHostname);
		getEcosystem().setCpsProperty("docker.engine.DKRENGINE01.port", dockerPort);
		getEcosystem().setCpsProperty("docker.engine.DKRENGINE01.max.slots", "3");
	}
	
	@BeforeClass
	public void ensureRequirementsAreInstalled() throws Exception {
		shell = getLinuxImage().getCommandShell();
		updatePackagetManager("sudo apt update");
		installDocker("sudo apt -y install docker-ce docker-ce-cli containerd.io");
		startDocker("sudo systemctl start docker");
		String res = shell.issueCommand("sudo systemctl show --property ActiveState docker");
		logger.info("Waiting for Docker service to start...");
		while(res.contains("ActiveState=activating")) {
			res = shell.issueCommand("sudo systemctl show --property ActiveState docker");
		}
		exposeDocker();
	}
	
	@Override
	public void exposeDocker() throws Exception {
		logger.info("Exposing Docker engine");
		String fileToSed = "/lib/systemd/system/docker.service";
		String exposeString = "ExecStart=/usr/bin/dockerd -H fd:// -H tcp://0.0.0.0:" + dockerPort + " --containerd=/run/containerd/containerd.sock";
		shell.issueCommand("sudo sed -i \"s|ExecStart=.*|" + exposeString + "|g\" " + fileToSed);
		logger.info("Restarting daemon and docker service");
		shell.issueCommand("sudo systemctl daemon-reload");
		shell.issueCommand("sudo systemctl restart docker");
	}
}
