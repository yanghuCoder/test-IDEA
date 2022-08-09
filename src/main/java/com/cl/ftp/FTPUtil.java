package com.cl.ftp;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.*;

/**

 * FTP 工具类
 */
public class FTPUtil {
	static List<FTPClient> ftpClientList=new ArrayList<>();
	/**
	 * 连接 FTP 服务器
	 *
	 * @param addr     FTP 服务器 IP 地址
	 * @param port     FTP 服务器端口号
	 * @param username 登录用户名
	 * @param password 登录密码
	 * @return
	 * @throws Exception
	 */
	public static FTPClient connectFtpServer(String addr, int port, String username, String password, String controlEncoding) {
		FTPClient ftpClient = new FTPClient();
		try {
			/**设置文件传输的编码*/
			ftpClient.setControlEncoding(controlEncoding);

			/**连接 FTP 服务器
			 * 如果连接失败，则此时抛出异常，如ftp服务器服务关闭时，抛出异常：
			 * java.net.ConnectException: Connection refused: connect*/
			ftpClient.connect(addr, port);
			/**登录 FTP 服务器
			 * 1）如果传入的账号为空，则使用匿名登录，此时账号使用 "Anonymous"，密码为空即可*/
			if (StringUtils.isBlank(username)) {
				ftpClient.login("Anonymous", "");
			} else {
				ftpClient.login(username, password);
			}

			/** 设置传输的文件类型
			 * BINARY_FILE_TYPE：二进制文件类型
			 * ASCII_FILE_TYPE：ASCII传输方式，这是默认的方式
			 * ....
			 */
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

			/**
			 * 确认应答状态码是否正确完成响应
			 * 凡是 2开头的 isPositiveCompletion 都会返回 true，因为它底层判断是：
			 * return (reply >= 200 && reply < 300);
			 */
			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				/**
				 * 如果 FTP 服务器响应错误 中断传输、断开连接
				 * abort：中断文件正在进行的文件传输，成功时返回 true,否则返回 false
				 * disconnect：断开与服务器的连接，并恢复默认参数值
				 */
				ftpClient.abort();
				ftpClient.disconnect();
			} else {
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(">>>>>FTP服务器连接登录失败，请检查连接参数是否正确，或者网络是否通畅*********");
		}
		return ftpClient;
	}

	/**
	 * 使用完毕，应该及时关闭连接
	 * 终止 ftp 传输
	 * 断开 ftp 连接
	 *
	 * @param ftpClient
	 * @return
	 */
	public static FTPClient closeFTPConnect(FTPClient ftpClient) {
		try {
			if (ftpClient != null && ftpClient.isConnected()) {
				ftpClient.abort();
				ftpClient.disconnect();
				System.out.println("----已退出ftp连接---");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ftpClient;
	}
	public static void loginClient() {
		if(ftpClientList.size()==0) {
			System.out.println("      ------欢迎登录-----   ");
			Scanner scanner=new Scanner(System.in);
			System.out.print("请输入主机地址：");
			String hostname = scanner.nextLine();
			System.out.print("请输入端口号：");
			String port = scanner.nextLine();
			System.out.print("请输入用户名:");
			String username = scanner.nextLine();
			System.out.print("请输入密码：");
			String password = scanner.nextLine();
			FTPClient ftpClient = FTPUtil.connectFtpServer(hostname, Integer.parseInt(port), username, password, "UTF-8");
			if(ftpClient.isConnected()) {

				System.out.println("FTP 连接成功----");
				ftpClientList.add(ftpClient);
			}else{

				System.out.println("连接失败------");
			}
		}

	}

	public static void main(String[] args) throws Exception {
		System.out.println("-----------------------应用启动------------------------");
		while (true) {
			if (ftpClientList.size() == 0) {
				System.out.println("1.登录");
			}
			System.out.println("2.创建目录");
			System.out.println("3.传输文件");
			System.out.println("4.退出系统");
			System.out.print("###请输入你的选择：");
			Scanner scanner = new Scanner(System.in);
			int selector = scanner.nextInt();
			switch (selector) {
				case 1:
					loginClient();
					break;
				case 2:
					createDir();
					break;
				case 3:
					transferFile();
					break;
				case 4:
					closeFTPConnect(ftpClientList.get(0));
					System.out.println("--------应用关闭----------");
					break;
				default:
					System.err.println("请输入1-4之间的整数!");
			}
		}


	}

	private static void transferFile() throws IOException {
		if (ftpClientList.size()!=0&&ftpClientList.get(0) != null && ftpClientList.get(0).isConnected()) {
			FTPClient ftpClient = ftpClientList.get(0);
			System.out.println("---传输文件---");

			System.out.println("请输入要传输的文件路径：");
			String originfilename = new Scanner(System.in).next();
			System.out.println("请输入ftp端的文件夹路径");
			String pathname = new Scanner(System.in).next();
			System.out.print("请输入上传到ftp端的名称：");
			String fileName = new Scanner(System.in).next();

				//ftp端
				FileInputStream input = new FileInputStream(new File(originfilename));
				CreateDirecroty(pathname);


				ftpClient.makeDirectory(pathname);
				boolean changeFlag = ftpClient.changeWorkingDirectory(pathname);
				System.out.println("切换目录：" + changeFlag);
				ftpClient.enterLocalPassiveMode();
				ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
				boolean flag = ftpClient.storeFile(fileName, input);
				input.close();
				ftpClient.logout();
				System.out.println("上传文件结果：" + flag);

				return;

				


		}
		System.out.println("你还未登录,请按1登录");
	}

	private static void createDir() throws IOException {
		if (ftpClientList.size()!=0&&ftpClientList.get(0) != null && ftpClientList.get(0).isConnected()) {
			FTPClient ftpClient = ftpClientList.get(0);
			Scanner scanner = new Scanner(System.in);
			boolean flag = true;
			while (flag) {
				System.out.print(ftpClient.printWorkingDirectory() + "     子目录(或文件)有：" + " | ");
				String[] fileList = ftpClient.listNames();
				for (String s : fileList) {
					System.out.print(s+" ");
				}
				System.out.println();
				System.out.print("是否要改变工作路径(true为改变)：");
				boolean b = scanner.nextBoolean();
				if (b == true) {
					System.out.print("请输入要改变的路径:");
					String change = scanner.next();
					ftpClient.changeWorkingDirectory(change);
				} else {
					System.out.print("请输入要创建的目录名称：");
					String pathname = scanner.next();
					ftpClient.makeDirectory(pathname);
					System.out.println("创建成功");
					flag = false;
					return;
				}

			}

		}else {
			System.out.println("你还未登录,请按1登录");
		}

	}
	//创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
	public static boolean CreateDirecroty(String remote) throws IOException {
		boolean success = true;
		String directory = remote + "/";
		// 如果远程目录不存在，则递归创建远程服务器目录
		if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			String path = "";
			String paths = "";
			while (true) {
				String subDirectory = new String(remote.substring(start, end).getBytes("UTF-8"), "iso-8859-1");
				path = path + "/" + subDirectory;
				if (!existFile(path)) {
					if (makeDirectory(subDirectory)) {
						changeWorkingDirectory(subDirectory);
					} else {
						System.out.println("创建目录[" + subDirectory + "]失败");
						changeWorkingDirectory(subDirectory);
					}
				} else {
					changeWorkingDirectory(subDirectory);
				}

				paths = paths + "/" + subDirectory;
				start = end + 1;
				end = directory.indexOf("/", start);
				// 检查所有目录是否创建完毕
				if (end <= start) {
					break;
				}
			}
		}
		return success;
	}
	//改变目录路径
	public static boolean changeWorkingDirectory(String directory) {
		FTPClient ftpClient = ftpClientList.get(0);
		boolean flag = true;
		try {
			flag = ftpClient.changeWorkingDirectory(directory);
			if (flag) {
				System.out.println("进入文件夹" + directory + " 成功！");

			} else {
				System.out.println("进入文件夹" + directory + " 失败！开始创建文件夹");
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return flag;
	}
	//判断ftp服务器文件是否存在
	public static boolean existFile(String path) throws IOException {
		FTPClient ftpClient = ftpClientList.get(0);
		boolean flag = false;
		FTPFile[] ftpFileArr = ftpClient.listFiles(path);
		if (ftpFileArr.length > 0) {
			flag = true;
		}
		return flag;
	}
	//创建目录
	public static boolean makeDirectory(String dir) {
		FTPClient ftpClient = ftpClientList.get(0);
		boolean flag = true;
		try {
			flag = ftpClient.makeDirectory(dir);
			if (flag) {
				System.out.println("创建文件夹" + dir + " 成功！");

			} else {
				System.out.println("创建文件夹" + dir + " 失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

}
