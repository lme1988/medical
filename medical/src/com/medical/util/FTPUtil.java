package com.medical.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
/**
 * 
 * @author bianshuai
 */
public class FTPUtil {
	
	private String ipaddress;
	private int port;
	private String username;
    private String password;
    private FTPClient ftpClient;
    private String path;
    
    /**
     * 
     * @param ipaddress  ftp������ip��ַ
     * @param port  ftp�������˿�
     * @param username �˻���
     * @param password  ����
     * @param ftpClient
     * @param path  ftpָ��·��
     */
	public FTPUtil(String ipaddress, int port, String username, String password, String path) {
		super();
		this.ipaddress = ipaddress;
		this.port = port;
		this.username = username;
		this.password = password;
		this.path = path;
	}

	/**
	 * @
	 * @return
	 * @throws Exception
	 */
	public boolean openConnect() throws Exception{
        boolean result = false;      
        ftpClient = new FTPClient();          
        ftpClient.connect(ipaddress,port);      
        ftpClient.login(username,password);      
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);      
        int reply = ftpClient.getReplyCode();      
        if (!FTPReply.isPositiveCompletion(reply)) {      
        	ftpClient.disconnect();      
            return result;      
        }
		ftpClient.changeWorkingDirectory(path);
        result = true;      
        return result; 
	}
	
    /**  
     *   
     * @param file �ϴ����ļ����ļ���  
     * @throws Exception  
     */    
	public void upload(File file) throws Exception{
        if(file.isDirectory()){           
        	ftpClient.makeDirectory(file.getName());                
        	ftpClient.changeWorkingDirectory(file.getName());      
            String[] files = file.list();             
            for (int i = 0; i < files.length; i++) {      
                File file1 = new File(file.getPath()+"\\"+files[i] );      
                if(file1.isDirectory()){      
                    upload(file1);      
                    ftpClient.changeToParentDirectory();      
                }else{                    
                    File file2 = new File(file.getPath()+"\\"+files[i]);      
                    FileInputStream input = new FileInputStream(file2);      
                    ftpClient.storeFile(file2.getName(), input);      
                    input.close();                            
                }                 
            }      
        }else{      
            File file2 = new File(file.getPath());      
            FileInputStream input = new FileInputStream(file2);      
            ftpClient.storeFile(file2.getName(), input);      
            input.close();
        }      
    }
	
	/**
	 * 
	 * @param ftpPath ftp·��
	 * @param localPath ����·��
	 * @param fileName �ļ���
	 * @throws Exception
	 */
	public void download(String ftpPath, String localPath, String fileName) throws Exception{
		
		if (!"".equals(ftpPath)){
			// ת�Ƶ�FTP������Ŀ¼
			ftpClient.changeWorkingDirectory(ftpPath);
		}
        File localFile = new File(localPath + File.separatorChar + fileName);
        OutputStream os = new FileOutputStream(localFile); 
        // ע��˴�retrieveFile�ĵ�һ��������GBKתΪISO-8859-1���롣�������غ���ļ�����Ϊ�ա�
        // ԭ�����������aixϵͳĬ�ϵı���ΪISO-8859-1
        ftpClient.retrieveFile(new String(fileName.getBytes("GBK"),"ISO-8859-1"), os);  
        os.flush();
        os.close();  
        ftpClient.logout();  
	}
	
	/**
	 * 
	 * @param ftpPath  ftp·��
	 * @param localPath  ����·��
	 * @throws Exception  �ļ���
	 */
	public void downloadFile(String ftpPath, String localPath) throws Exception{
		
        // �������д������Ҫ�����Ҳ��ܸı�����ʽ����������ȷ���������ļ�
		ftpClient.setControlEncoding("GBK");
        FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
        conf.setServerLanguageCode("zh");
		// ת�Ƶ�FTP������Ŀ¼
		ftpClient.changeWorkingDirectory(ftpPath);
        FTPFile[] fs = ftpClient.listFiles();
        for (int i = 0; i < fs.length; i++) {
            FTPFile ff = fs[i];
            if (!ff.getName().equals(".") && !ff.getName().equals("..")) {
                File localFile = new File(localPath + File.separator + ff.getName());
                if(!localFile.exists())//����ļ��Ѵ������ظ�����
                {
                    OutputStream os = new FileOutputStream(localFile);

                    // ע��˴�retrieveFile�ĵ�һ��������GBKתΪISO-8859-1���롣�������غ���ļ�����Ϊ�ա�
                    // ԭ�����������aixϵͳĬ�ϵı���ΪISO-8859-1
                    ftpClient.retrieveFile(new String(ff.getName().getBytes("GBK"),"ISO-8859-1"), os);
                    os.flush();
                    os.close();
                }
            }
        }
        ftpClient.logout();
	}
}
