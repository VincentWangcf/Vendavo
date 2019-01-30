package com.avnet.emasia.webquote.utilities.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpUtil {
	private String username;
	private String host;
	private int port;
	private String privateKeyFile;
	private String knownHost;
	private Session session;
	private Channel channel;
	
	private static Logger logger = Logger.getLogger(SftpUtil.class.getName());
	
	public SftpUtil(String username, String host, int port){
		this.username = username;
		this.host = host;
		this.port = port;
	}
	
	private Session openSession() throws JSchException{
		JSch ssh = new JSch();

		//ssh.setKnownHosts("~/.ssh/known_hosts");
		//ssh.addIdentity("~/.ssh/id_rsa");

		if (this.privateKeyFile != null) 
		{ 
			ssh.addIdentity(this.privateKeyFile); 
		} 

		if (this.knownHost != null) 
		{ 
			//log.info("Using known hosts: " + getKnownHosts()); 
			ssh.setKnownHosts(this.knownHost); 
		} 


		Session session = ssh.getSession(this.username, this.host, this.port);

		session.connect();
		logger.info("Session Connected successfully");
		
		return session;
	}
	
	private Channel openSftpChannel() throws JSchException{
		if(this.channel == null){
			this.channel = openSession().openChannel("sftp");
			channel.connect();
			logger.info("SFTP Connected successfully");

			//ChannelSftp c =(ChannelSftp)channel;
		}
		
		return channel;
	}
	
	public void sendFileToRemote(File localFile, String remotePath) throws JSchException, FileNotFoundException, SftpException{
		ChannelSftp sftChannel = (ChannelSftp)openSftpChannel();
		
		logger.info("Transfering file...");
		//sftChannel.put(new FileInputStream(new File("/tmp/testsftp/test.dat")), "/data1/asia/sd/sc/testtosap.dat", ChannelSftp.OVERWRITE);
		sftChannel.put(new FileInputStream(localFile), remotePath, ChannelSftp.OVERWRITE);
		logger.info("Completed transfer file");
		
	}
	
	public void disconnec(){
		
		if(this.channel != null){
			this.channel.disconnect();
		}
		
		if(this.session != null){
			this.session.disconnect();
		}

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPrivateKeyFile() {
		return privateKeyFile;
	}

	public void setPrivateKeyFile(String privateKeyFile) {
		this.privateKeyFile = privateKeyFile;
	}

	public String getKnownHost() {
		return knownHost;
	}

	public void setKnownHost(String knownHost) {
		this.knownHost = knownHost;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
}
