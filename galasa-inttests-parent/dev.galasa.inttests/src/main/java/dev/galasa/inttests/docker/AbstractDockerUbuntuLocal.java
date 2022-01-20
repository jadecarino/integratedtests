/*
 * Copyright contributors to the Galasa project
 */
package dev.galasa.inttests.docker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dev.galasa.BeforeClass;
import dev.galasa.ResultArchiveStoreContentType;
import dev.galasa.SetContentType;
import dev.galasa.core.manager.StoredArtifactRoot;

public abstract class AbstractDockerUbuntuLocal extends AbstractDocker {
	
	@StoredArtifactRoot
    public Path storedArtifactRoot;
	
	@BeforeClass
	public void setupEnvironment() throws Exception {
		shell = getDockerLinuxImage().getCommandShell();
		
		logger.info("Checking if docker already exists");
		String res = shell.issueCommand("sudo docker ps");
		if(res.contains("CONTAINER ID")) {
			logger.info("Docker is already installed. Skipping install step.");
		} else {
			installDocker();
		}
			
		logger.info("Exposing Docker engine");
		Path servicePath = getDockerLinuxImage().getRoot();
		servicePath = servicePath.resolve("lib/systemd/system/docker.service");
		Path homePath = getDockerLinuxImage().getHome();
		homePath = homePath.resolve("docker.service");

		List<String> newLines = new ArrayList<>();
		for (String line : Files.readAllLines(servicePath, StandardCharsets.UTF_8)) {
		    if (line.startsWith("ExecStart=")) {
		       newLines.add(line.replace(line, "ExecStart=/usr/bin/dockerd -H fd:// -H tcp://0.0.0.0:" + DOCKER_PORT + " --containerd=/run/containerd/containerd.sock"));
		    } else {
		       newLines.add(line);
		    }
		}
		Files.write(homePath, newLines, StandardCharsets.UTF_8);
		res = shell.issueCommand("sudo mv " + homePath.toString() + " /etc/systemd/system/docker.service && echo \"Exit code:\" $?");
		storeOutput("ecosystem/dockerSetup", "serviceFileMove.txt", "Command: sudo mv \" + homePath.toString() + \" /etc/systemd/system/docker.service \nOutput:\n" + res);
		if(!res.contains("Exit code: 0")) {
			logger.error("Moving updated service file failed.");
			throw new Exception("Moving updated service file failed: " + res);
		}
		
		logger.info("Restarting daemon and docker services...");
		res = shell.issueCommand("sudo systemctl daemon-reload && echo \"Exit code:\" $?");
		storeOutput("ecosystem/dockerSetup", "daemonReload.txt", "Command: sudo systemctl daemon-reload \nOutput:\n" + res);
		if(!res.contains("Exit code: 0")) {
			logger.error("Restarting daemon failed.");
			throw new Exception("Restarting daemon failed with return code: " + res);
		}
		res = shell.issueCommand("sudo systemctl restart docker && echo \"Exit code:\" $?");
		storeOutput("ecosystem/dockerSetup", "dockerSetup.txt", "Command: sudo systemctl restart docker \nOutput:\n" + res);
		if(!res.contains("Exit code: 0")) {
			logger.error("Restarting docker service failed.");
			throw new Exception("Restarting docker service failed with return code: " + res);
		}
	}
	
	private void installDocker() throws Exception {
		logger.info("Updating package manager docker installation");
		String res = shell.issueCommand("sudo apt update");
		storeOutput("ecosystem/dockerSetup", "aptUpdate.txt", "Command: sudo apt update \nOutput:\n" + res);
		if(!res.contains("packages can be upgraded") && !res.contains("packages are up to date")) {
			logger.error("Updating package manager failed.");
			throw new Exception("Package manager could not be updated: " + res);
		}
		logger.info("Installing Docker...");
		res = shell.issueCommand("sudo apt -y install docker.io");
		storeOutput("ecosystem/dockerSetup", "installDocker.txt", "Command: sudo apt -y install docker.io \nOutput:\n" + res);
		if(!res.contains("Processing triggers for man-db")) {
			logger.error("Installing docker.io failed.");
			throw new Exception("Could not install docker.io: " + res); 
		}
		logger.info("Starting Docker service...");
		res = shell.issueCommand("sudo systemctl start docker");
		storeOutput("ecosystem/dockerSetup", "startDocker.txt", "Command: sudo systemctl start docker \nOutput:\n" + res);
		res = shell.issueCommand("sudo systemctl show --property ActiveState docker");	
		long timeout = Calendar.getInstance().getTimeInMillis() + 180000; // 3 mins
		while(!res.contains("ActiveState=active") && timeout <= Calendar.getInstance().getTimeInMillis()) {
			res = shell.issueCommand("sudo systemctl show --property ActiveState docker");
			if(!res.contains("ActiveState=active") || !res.contains("ActiveState=activating")) {
				logger.error("Unknown active state.");
				throw new Exception("Docker could not be started: " + res);
			}
		}
	}
	
	private void storeOutput(String folder, String file, String content) throws IOException {	
		Path requestPath = storedArtifactRoot.resolve(folder).resolve(file);
        Files.write(requestPath, content.getBytes(), new SetContentType(ResultArchiveStoreContentType.TEXT),
                StandardOpenOption.CREATE);
    }
}
