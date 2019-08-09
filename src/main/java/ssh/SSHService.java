package ssh;

/**
 * @author yinkailun
 * @description:
 * @date 2019-07-24 11:11 AM
 */

import com.jcraft.jsch.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * SSH端口转发
 */
public class SSHService {
	static Integer lport = 40022;//本地端口
	static String rhost = "rm-m5eapjk1157l7uc4b.mysql.rds.aliyuncs.com";//远程数据库服务器
	static int rport = 3306;//远程数据库服务端口

	static String user = "robot_read";//SSH连接用户名
	static String password = "TpCChHa2P8CF4YGj";//SSH连接密码
	static String host;//SSH服务器
	static int port;//SSH访问端口

//	static {
//		//读取配置文件
//		try {
//			// 获取hive.properties文件的路径
////			InputStream is = SSHService.class.getClassLoader().getResourceAsStream("SSH.properties");
////			Properties prop = new Properties();
////			prop.load(is);
//			// 读取配置文件的值
//			lport =  ;
//			rhost = ;
//			rport = ;
//			user = ;
//			password = ;
//			host = ;
//			port = ;
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}


	public static void sshRun() {
		JSch jsch = new JSch();
		Session session = null;
		try {
			session = jsch.getSession(user, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			// step1：建立ssh连接
			session.connect();
			System.out.println(session.getServerVersion());//这里打印SSH服务器版本信息
			//step2： 设置SSH本地端口转发，本地转发到远程
			int assinged_port = session.setPortForwardingL(lport, rhost, rport);
			System.out.println("localhost:" + assinged_port + " -> " + rhost + ":" + rport);
		} catch (Exception e) {
			if (null != session) {
				//关闭ssh连接
				session.disconnect();
			}
			e.printStackTrace();
		}
	}

	public static void main(String[] arg) {

		String keyFile = "/private/var/root/.ssh/id_rsa_mysql";
		String user = "robot_read";
		String host = "rm-m5eapjk1157l7uc4b.mysql.rds.aliyuncs.com";
		String passphrase = "TpCChHa2P8CF4YGj";
		int port = 3306;
		try {
			JSch jsch = new JSch();
			jsch.addIdentity(keyFile);

			Session session = jsch.getSession(user, host, port);

			// username and passphrase will be given via UserInfo interface.
			UserInfo ui = new MyUserInfo(passphrase);
			session.setUserInfo(ui);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftp = (ChannelSftp) channel;
//
			System.out.println(sftp.pwd());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}

	}
	public static class MyUserInfo implements UserInfo {
		private String passphrase = null;

		public MyUserInfo(String passphrase) {
			this.passphrase = passphrase;
		}

		public String getPassphrase() {
			return passphrase;
		}

		public String getPassword() {
			return null;
		}

		public boolean promptPassphrase(String s) {
			return true;
		}

		public boolean promptPassword(String s) {
			return true;
		}

		public boolean promptYesNo(String s) {
			return true;
		}

		public void showMessage(String s) {
			System.out.println(s);
		}
	}
}
