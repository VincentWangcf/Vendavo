package com.avnet.emasia.webquote.web.stm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.avnet.emasia.webquote.exception.CommonConstants;


public class FTPClientUtil {
	
	private static final Logger LOGGER =Logger.getLogger(FTPClientUtil.class.getName());

    private String         host;
    private int            port;
    private String         username;
    private String         password;

    private boolean        binaryTransfer = true;
    private boolean        passiveMode    = true;
    private String         encoding       = "UTF-8";
    private int            clientTimeout  = 3000*10;

    public FTPClientUtil(String host,int port,String username,String password){
    	this.setHost(host);
    	this.setPort(port);
    	this.setUsername(username);
    	this.setPassword(password);
    	this.setBinaryTransfer(true);
    	this.setPassiveMode(true);
    }
    
    /**
     * ftpConfig  from select c.value from SystemCodeMaintenance c where c.category='STM_EDI_FTP_CONFIG'
     * such as :'Anaheim1|21|ahu|integer1234'
     * @param ftpConfig
     */
    public FTPClientUtil(String ftpConfig) {
		if(ftpConfig!=null){
			String[] ftp =ftpConfig.split("\\|");
			
			if(ftp.length==4){
				this.setHost(ftp[0]);
	    		this.setPort(Integer.valueOf(ftp[1]));
	    		this.setUsername(ftp[2]);
	    		this.setPassword(ftp[3]);
	    		this.setBinaryTransfer(true);
	    		this.setPassiveMode(true);
			}
		}
	}

	private FTPClient getFTPClient() throws FTPClientException {
        FTPClient ftpClient = new FTPClient(); 
        ftpClient.setControlEncoding(encoding);

        connect(ftpClient);

        if (passiveMode) {
            ftpClient.enterLocalPassiveMode();
        }
        setFileType(ftpClient);
        
        try {
            ftpClient.setSoTimeout(clientTimeout);
        } catch (SocketException e) {
            throw new FTPClientException(CommonConstants.WQ_EJB_MASTER_DATA_SET_TIMEOUT_ERROR, e);
        }

        return ftpClient;
    }

    private void setFileType(FTPClient ftpClient) throws FTPClientException {
        try {
            if (binaryTransfer) {
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            } else {
                ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
            }
        } catch (IOException e) {
            throw new FTPClientException(CommonConstants.WQ_EJB_MASTER_DATA_COULD_NOT_TO_SET_FILE_TYPE, e);
        }
    }

    /**
     * Connect to the FTP server
     * 
     * @param ftpClient
     * @return The connection is successful return true, otherwise it returns false
     * @throws FTPClientException
     */
    private boolean connect(FTPClient ftpClient) throws FTPClientException {
        try {
            ftpClient.connect(host, port);
            int reply = ftpClient.getReplyCode();

            if (FTPReply.isPositiveCompletion(reply)) {
                if (ftpClient.login(username, password)) {
                    setFileType(ftpClient);
                    return true;
                }
            } else {
                ftpClient.disconnect();
                throw new FTPClientException(CommonConstants.WQ_WEB_FTP_SERVER_REFUSED_CONNECTION);
            }
        } catch (IOException e) {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect(); 
                } catch (IOException e1) {
                    throw new FTPClientException(CommonConstants.COMMON_COULD_NT_DISCONNECT_SERVER, e);
                }

            }
            throw new FTPClientException(CommonConstants.WQ_EJB_MASTER_DATA_COULD_NOT_CONNECT_TO_SERVER, e);
        }
        return false;
    }

    private void disconnect(FTPClient ftpClient) throws FTPClientException {
        try {
            ftpClient.logout();
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            throw new FTPClientException(CommonConstants.COMMON_COULD_NT_DISCONNECT_SERVER, e);
        }
    }

    /**
     * Upload a local file to remote 
     * 
     * @param serverFile 
     * @param localFile 
     * @return Returns true on success, or false on failure,
     * @throws FTPClientException
     */
    public boolean put(String serverFile, String localFile) throws FTPClientException {
        return put(serverFile, localFile, false);
    }

    public boolean put(String serverFile, String localFile, boolean delFile) throws FTPClientException {
        FTPClient ftpClient = null;
        InputStream input = null;
        try {
            ftpClient = getFTPClient();
            input = new FileInputStream(localFile);
            LOGGER.info("strat put " + localFile  +" to " + serverFile);
            boolean isStoreSuccess = ftpClient.storeFile(serverFile, input);
            input.close();
            if(isStoreSuccess){
            	 LOGGER.info("put " + localFile +" success ");
                 if (delFile) {
                     (new File(localFile)).delete();
                     LOGGER.info("delete " + localFile);
                 }

                 return true;
            }else{
            	 LOGGER.info("put " + localFile +" fail ");
                 return false;
            }
        } catch (FileNotFoundException e) {
            throw new FTPClientException(CommonConstants.WQ_EJB_MASTER_DATA_LOCAL_FILE_NOT_FOUND, e);
        } catch (IOException e) {
            throw new FTPClientException(CommonConstants.WQ_WEB_COULD_NT_PUT_FILE_SERVER, e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
                throw new FTPClientException(CommonConstants.WQ_EJB_MASTER_DATA_COULDNT_CLOSE_FILEINPUTSTREAM, e);
            }
            if (ftpClient != null) {
                disconnect(ftpClient);
            }
        }
    }
    
    /**
     * Download a remote file to the specified local file
     * 
     * @param serverFile
     * @param localFile 
     * @return Returns true on success, or false on failure,
     * @throws FTPClientException
     */
    public boolean get(String serverFile, String localFile) throws FTPClientException {
        return get(serverFile, localFile, false);
    }

    public boolean get(String serverFile, String localFile, boolean delFile) throws FTPClientException {
        OutputStream output = null;
        try {
            output = new FileOutputStream(localFile);
            return get(serverFile, output, delFile);
        } catch (FileNotFoundException e) {
            throw new FTPClientException(CommonConstants.WQ_EJB_MASTER_DATA_LOCAL_FILE_NOT_FOUND, e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                throw new FTPClientException(CommonConstants.WQ_EJB_MASTER_DATA_COULDNT_CLOSE_FILEOUTPUTSTREAM, e);
            }
        }
    }
    
    public boolean get(String serverFile, OutputStream output) throws FTPClientException {
        return get(serverFile, output, false);
    }
    
    private boolean get(String serverFile, OutputStream output, boolean delFile) throws FTPClientException {
        FTPClient ftpClient = null;
        try {
            ftpClient = getFTPClient();
     
            ftpClient.retrieveFile(serverFile, output);
            if (delFile) { 
                ftpClient.deleteFile(serverFile);
            }
            return true;
        } catch (IOException e) {
            throw new FTPClientException(CommonConstants.WQ_EJB_MASTER_DATA_COULDNT_GET_FILE_FROM_SERVER, e);
        } finally {
            if (ftpClient != null) {
                disconnect(ftpClient);
            }
        }
    }
    
    public boolean delete(String delFile) throws FTPClientException {
        FTPClient ftpClient = null;
        try {
            ftpClient = getFTPClient();
            ftpClient.deleteFile(delFile);
            return true;
        } catch (IOException e) {
            throw new FTPClientException(CommonConstants.WQ_EJB_MASTER_DATA_COULDNT_DELETE_FILE_FROM_SERVER, e);
        } finally {
            if (ftpClient != null) {
                disconnect(ftpClient);
            }
        }
    }
    
    /**
     * batch delete files
     * 
     * @param delFiles
     * @return
     * @throws FTPClientException
     */
    public boolean delete(String[] delFiles) throws FTPClientException {
        FTPClient ftpClient = null;
        try {
            ftpClient = getFTPClient();
            for (String s : delFiles) {
                ftpClient.deleteFile(s);
            }
            return true;
        } catch (IOException e) {
            throw new FTPClientException(CommonConstants.WQ_EJB_MASTER_DATA_COULDNT_DELETE_FILE_FROM_SERVER, e);
        } finally {
            if (ftpClient != null) {
                disconnect(ftpClient); //�Ͽ�����
            }
        }
    }

    public String[] listNames() throws FTPClientException {
        return listNames(null);
    }

    public String[] listNames(String remotePath) throws FTPClientException {
        FTPClient ftpClient = null;
        try {
            ftpClient = getFTPClient();
            String[] listNames = ftpClient.listNames(remotePath);
            return listNames;
        } catch (IOException e) {
            throw new FTPClientException(CommonConstants.WQ_EJB_MASTER_DATA_LISTED_ALL_THE_REMOTE_DIRECTORY_FILE_IS_ABNORMAL, e);
        } finally {
            if (ftpClient != null) {
                disconnect(ftpClient);
            }
        }
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isBinaryTransfer() {
        return binaryTransfer;
    }

    public void setBinaryTransfer(boolean binaryTransfer) {
        this.binaryTransfer = binaryTransfer;
    }

    public boolean isPassiveMode() {
        return passiveMode;
    }

    public void setPassiveMode(boolean passiveMode) {
        this.passiveMode = passiveMode;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public int getClientTimeout() {
        return clientTimeout;
    }

    public void setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
    }

}
