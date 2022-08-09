package com.cl.ftp;


import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

public class test {
	public static void main(String[] args) throws IOException {
		FTPClient ftpClient=new FTPClient();
		ftpClient.setControlEncoding("UTF-8");
		ftpClient.connect("192.168.192.164",21);

		ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
		if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
			if(ftpClient.login("user1","123456")) {
				System.out.println(ftpClient.printWorkingDirectory());
				String[] strings = ftpClient.listNames();
				for (String string : strings) {
					//System.out.println(string);
					System.out.println("当前目录："+string);
				}

//				for (FTPFile ftpFile : ftpFiles) {
//					System.out.println(ftpFile);
//				}
				//下级目录
				String[] dirs = ftpClient.listNames();
				System.out.println(dirs.length);
				for (String dir : dirs) {
					System.out.println(dir);
				}
				System.out.println("连接成功");
			}
			System.out.println("连接失败");
		}
	}
}
