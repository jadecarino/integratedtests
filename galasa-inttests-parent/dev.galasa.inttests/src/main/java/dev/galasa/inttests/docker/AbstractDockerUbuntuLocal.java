/*
 * Copyright contributors to the Galasa project
 */
package dev.galasa.inttests.docker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import dev.galasa.BeforeClass;
import dev.galasa.ResultArchiveStoreContentType;
import dev.galasa.SetContentType;
import dev.galasa.core.manager.StoredArtifactRoot;

public abstract class AbstractDockerUbuntuLocal extends AbstractDocker {
	
	@StoredArtifactRoot
    public Path storedArtifactRoot;
	
	@BeforeClass
	public void setupEnvironment() throws Exception {
		shell = getLinuxImage().getCommandShell();
		logger.info("Updating package manager docker installation");
		String res = shell.issueCommand("sudo apt update");
		storeOutput("ecosystem", "dockerSetup.txt", "Command: sudo apt update \nOutput:\n" + res);
		if(res.contains("E:")) {
			logger.error("Updating package manager failed.");
			throw new Exception("Package manager could not be updated: " + res);
		}
		logger.info("Installing Docker...");
		res = shell.issueCommand("sudo apt -y install docker.io");
		storeOutput("ecosystem", "dockerSetup.txt", "Command: sudo apt -y install docker.io \nOutput:\n" + res);
		if(res.contains("E:")) {
			logger.error("Installing docker.io failed.");
			throw new Exception("Could not install docker.io: " + res);
		}
		logger.info("Starting Docker service...");
		res = shell.issueCommand("sudo systemctl start docker");
		storeOutput("ecosystem", "dockerSetup.txt", "Command: sudo systemctl start docker \nOutput:\n" + res);
		res = shell.issueCommand("sudo systemctl show --property ActiveState docker");
		storeOutput("ecosystem", "dockerSetup.txt", "Command: sudo systemctl show --property ActiveState docker \nOutput:\n" + res);
		if(res.contains("AcriveState=failed")) {
			logger.error("Docker service could not be started.");
			throw new Exception("Docker could not be restarted: " + res);
		} else {
			while(res.contains("ActiveState=activating")) {
				res = shell.issueCommand("sudo systemctl show --property ActiveState docker");
			}
		}
		
		// Exposing docker engine
		logger.info("Exposing Docker engine");
		String fileToSed = "/lib/systemd/system/docker.service";
		String exposeString = "ExecStart=/usr/bin/dockerd -H fd:// -H tcp://0.0.0.0:" + DOCKER_PORT + " --containerd=/run/containerd/containerd.sock";
		res = shell.issueCommand("sudo sed -i \"s|ExecStart=.*|" + exposeString + "|g\" " + fileToSed + " && echo $?");
		storeOutput("ecosystem", "dockerSetup.txt", "Command: sudo sed -i \"s|ExecStart=.*|" + exposeString + "|g\\\" " + fileToSed + "\nOutput:\n" + res);
		if(!res.contains("0")) {
			logger.error("Exposing Docker service failed.");
			throw new Exception("Exposing Docker service failed with return code: " + res);
		}
		logger.info("Restarting daemon and docker services...");
		res = shell.issueCommand("sudo systemctl daemon-reload && echo $?");
		storeOutput("ecosystem", "dockerSetup.txt", "Command: sudo systemctl daemon-reload \nOutput:\n" + res);
		if(!res.contains("0")) {
			logger.error("Restarting daemon failed.");
			throw new Exception("Restarting daemon failed with return code: " + res);
		}
		res = shell.issueCommand("sudo systemctl restart docker && echo $?");
		storeOutput("ecosystem", "dockerSetup.txt", "Command: sudo systemctl restart docker \nOutput:\n" + res);
		if(!res.contains("0")) {
			logger.error("Restarting docker service failed.");
			throw new Exception("Restarting docker service failed with return code: " + res);
		}
	}
	
	private void storeOutput(String folder, String file, String content) throws IOException {	
		Path requestPath = storedArtifactRoot.resolve(folder).resolve(file);
		byte[] newContent = content.getBytes();
		if(Files.exists(requestPath)) {
			byte[] currentContent = Files.readAllBytes(requestPath);
			newContent = ByteBuffer.allocate(newContent.length + currentContent.length).put(currentContent).put(newContent).array();
		}
        Files.write(requestPath, newContent, new SetContentType(ResultArchiveStoreContentType.TEXT),
                StandardOpenOption.CREATE);
    }
}
