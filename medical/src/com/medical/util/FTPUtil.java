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
 * @ClassName: FTPUtil 
 * @Description: ftp服务器 上传下载
 * @author bianshuai 
 * @date 2017年4月23日 下午1:42:32 
 *
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
     * @param ipaddress  ftp服务器ip地址
     * @param port  ftp服务器端口
     * @param username 账户名
     * @param password  密码
     * @param ftpClient
     * @param path  ftp指定路径
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
	 * 
	 * @Title: openConnect 
	 * @Description: 连接ftp服务器，并且换到指定目录 
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return boolean    返回类型 
	 * @throws
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
     * @Title: upload 
     * @Description: 上传文件或文件夹
     * @param @param file  上传的文件或文件夹对象
     * @param @throws Exception    设定文件 
     * @return void    返回类型 
     * @throws
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
	 * @Title: download 
	 * @Description: TODO(这里用一句话描述这个方法的作用) 
	 * @param @param ftpPath ftp路径
	 * @param @param localPath 本地文件路径
	 * @param @param fileName  文件名
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public void download(String ftpPath, String localPath, String fileName) throws Exception{
		
		if (!"".equals(ftpPath)){
			// 转移到FTP服务器目录
			ftpClient.changeWorkingDirectory(ftpPath);
		}
        File localFile = new File(localPath + File.separatorChar + fileName);
        OutputStream os = new FileOutputStream(localFile); 
        // 注意此处retrieveFile的第一个参数由GBK转为ISO-8859-1编码。否则下载后的文件内容为空。
        // 原因可能是由于aix系统默认的编码为ISO-8859-1
        ftpClient.retrieveFile(new String(fileName.getBytes("GBK"),"ISO-8859-1"), os);  
        os.flush();
        os.close();  
        ftpClient.logout();  
	}
	
	/**
	 * 
	 * @param ftpPath  ftp路径
	 * @param localPath  本地路径
	 * @throws Exception  文件名
	 */
	public void downloadFile(String ftpPath, String localPath) throws Exception{
		
        // 下面三行代码必须要，而且不能改变编码格式，否则不能正确下载中文文件
		ftpClient.setControlEncoding("GBK");
        FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
        conf.setServerLanguageCode("zh");
		// 转移到FTP服务器目录
		ftpClient.changeWorkingDirectory(ftpPath);
        FTPFile[] fs = ftpClient.listFiles();
        for (int i = 0; i < fs.length; i++) {
            FTPFile ff = fs[i];
            if (!ff.getName().equals(".") && !ff.getName().equals("..")) {
                File localFile = new File(localPath + File.separator + ff.getName());
                if(!localFile.exists())//如果文件已存在则不重复下载
                {
                    OutputStream os = new FileOutputStream(localFile);

                    // 注意此处retrieveFile的第一个参数由GBK转为ISO-8859-1编码。否则下载后的文件内容为空。
                    // 原因可能是由于aix系统默认的编码为ISO-8859-1
                    ftpClient.retrieveFile(new String(ff.getName().getBytes("GBK"),"ISO-8859-1"), os);
                    os.flush();
                    os.close();
                }
            }
        }
        ftpClient.logout();
	}
}
