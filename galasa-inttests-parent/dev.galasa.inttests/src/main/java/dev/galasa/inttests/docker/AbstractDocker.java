/*
 * Copyright contributors to the Galasa project
 */
package dev.galasa.inttests.docker;

import org.apache.commons.logging.Log;

import dev.galasa.core.manager.Logger;
import dev.galasa.ipnetwork.ICommandShell;

public abstract class AbstractDocker {
	
	@Logger
	public Log logger;
	protected ICommandShell shell;
	
	protected void updatePackagetManager(String command) throws Exception {
		logger.info("Updating the package manager");
		shell.issueCommand(command);
	}
	
	protected void installDocker(String command) throws Exception {
		logger.info("Checking if Docker is installed");
		String res = shell.issueCommand("docker -v");
		if(res.contains("no such file or directory") || res.contains("not found")) {
			logger.info("No Docker found. Installing Docker...");
			shell.issueCommand(command);
		} 
	}
	
	protected void startDocker(String command) throws Exception {
		logger.info("Starting Docker...");
		shell.issueCommand(command);
	}
	
	abstract protected void exposeDocker() throws Exception;
	
	protected void installMaven(String command) throws Exception {
		String res = shell.issueCommand("mvn -v");	
		if(res.contains("no such file or directory") || res.contains("not found")) {
			shell.issueCommand(command);
		}
	}
}
