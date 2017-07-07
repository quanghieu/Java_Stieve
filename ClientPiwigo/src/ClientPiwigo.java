import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JFrame;

public class ClientPiwigo {

	private static final String url = "jdbc:mysql://localhost";

	private static final String user = "root";

	private static final String password = "123";
	
	private static final String Encrypt_location = "/home/hieu/Sieve_user_client/Key_hom/";

	static Connection con;

	static ServerSocket ss;

	public static void connectDb() {
		try {
			con = DriverManager.getConnection(url, user, password);
			System.out.println("Success");
			String query1 = "use piwigo_client;";
			Statement stmt = con.createStatement();
			stmt.executeQuery(query1);
		} catch (Exception e) {
			System.out.println("Fail");
			e.printStackTrace();
		}
	}

	private Socket s;

	public ClientPiwigo(String host, int port, File file, String attrs, String key) {
		try {
			s = new Socket(host, port);
			// Upload request
//			sendRequest(1);
			System.out.println("Call");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String execCmd(String cmd) throws java.io.IOException {
		java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream())
				.useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	public static void reKey(String fileName, File reKeyToken, File nKey, String attrs) throws IOException {
		Socket s = new Socket("localhost", 1988);
		InputStream is = s.getInputStream();
		OutputStream os = s.getOutputStream();
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		sendRequest(s, 2);
		
		dos.writeUTF(fileName);
		
		// - send Metadata
		// - send rekey token
		try {
			sendMetadata(s, attrs, nKey.getName());
			sendToken(s, reKeyToken);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendMetadata(Socket s, String attrs, String keyFile) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		// input
		DataOutputStream os = new DataOutputStream(s.getOutputStream());
		InputStream is = s.getInputStream();
		int guid = is.read();
		System.out.println("GUID is " + guid);
		System.out.println("Key file is: "+keyFile);
		String outMetadata = "/home/hieu/Downloads/piwigo_client/metadata.cpabe";
		System.out.println("./metadata_encrypt.sh " + Encrypt_location+""+keyFile + " " + guid + " " + attrs);
		Process p = Runtime.getRuntime().exec("./metadata_encrypt.sh " + Encrypt_location+""+keyFile + " " + guid + " " + attrs);
		p.waitFor();
//		System.out.println(execCmd("./metadata_encrypt.sh " + Encrypt_location+""+keyFile + " " + guid + " " + attrs));
		File f = new File(outMetadata);
		
		PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
		pw.println("I am Hieu");
		pw.println((int)f.length());
		
		byte[] buffer = new byte[(int)f.length()];
		
		FileInputStream fis = new FileInputStream(f);
		
		while(fis.read(buffer) > 0){
			os.write(buffer);
		}
		
		fis.close();
		os.close();
	//	f.delete();
	}
	


	public static void sendToken(Socket s, File f) throws IOException {
		DataOutputStream os = new DataOutputStream(s.getOutputStream());
		
//		os.writeUTF(f.getName());
//		System.out.println("File path "+f.getAbsolutePath());
		
		byte[] buffer = new byte[65536];
		// Send file length
//		os.writeUTF("Hey guys");
		os.writeLong(f.length());
		
		System.out.println("Token name is "+f.getName());
		System.out.println("Token size "+f.length());
		
		FileInputStream fis = new FileInputStream(f);
		int length = 0;
		if(f.length() > 65536)
			length = 65536;
		else
			length = (int)f.length();
		while(fis.read(buffer, 0, length) > 0){
			os.write(buffer);
		}
		
		fis.close();
		os.flush();
		os.close();
//		os.close();
//		bis.read(buffer, 0, buffer.length);
//		os.write(buffer, 0, buffer.length);
//		os.flush();
//		os.close();
//		bis.close();
//		System.out.println("Finish write: " + count + " bytes");
	}
	
	private int uploadClient(String file) throws IOException {
		// TODO Auto-generated method stub
		InputStream dis = s.getInputStream();
		OutputStream os = s.getOutputStream();
		while (dis.available() == 0) {

		}
		int ack = dis.read();
		if (ack == 0) { // start upload
			System.out.println("Acknowledgement");
			// send filename first
	//		sendFile(new File(file));
			// send ecrypted file
		}

		// wait a guid response
		// InputStream guidis = s.getInputStream();
		while (dis.read() != 0) {

		}
		int guid = dis.read();
		// Encrypt metadata
		return guid;
	}

	private static void sendRequest(Socket s, int i) throws IOException {
		// TODO Auto-generated method stub
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		dos.write(i);
	}

	public static void sendFile(Socket s, File f) throws IOException {
		DataOutputStream os = new DataOutputStream(s.getOutputStream());
		
		os.writeUTF(f.getName());
		System.out.println("File path "+f.getAbsolutePath());
		
		byte[] buffer = new byte[65536];
		// Send file length
		os.writeLong(f.length());
		
		FileInputStream fis = new FileInputStream(f);
		int length = 0;
		if(f.length() > 65536)
			length = 65536;
		else
			length = (int)f.length();
		while(fis.read(buffer, 0, length) > 0){
			os.write(buffer);
		}
		
		fis.close();
		os.flush();
//		os.close();
//		bis.read(buffer, 0, buffer.length);
//		os.write(buffer, 0, buffer.length);
//		os.flush();
//		os.close();
//		bis.close();
//		System.out.println("Finish write: " + count + " bytes");
	}
	
	private static void copyFile(String File, String To){
		InputStream inStream = null;
		OutputStream outStream = null;
		String[] str = File.split("/");
		String fileName = str[str.length - 1];

	    	try{

	    	    File afile =new File(File);
	    	    File bfile =new File(To+"/"+fileName);

	    	    inStream = new FileInputStream(afile);
	    	    outStream = new FileOutputStream(bfile);

	    	    byte[] buffer = new byte[1024];

	    	    int length;
	    	    //copy the file content in bytes
	    	    while ((length = inStream.read(buffer)) > 0){

	    	    	outStream.write(buffer, 0, length);

	    	    }

	    	    inStream.close();
	    	    outStream.close();
	    	} catch (Exception e) {
				// TODO: handle exception
			}
	    	
	}
	
	private static String[] checkValid(String file){
		String sql = "select * from client_files where File = '"+file+"'";
		String[] retArr = null;
		String keyFile = null;
		String attrs = null;
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if(!rs.next())
				return null;
			keyFile = rs.getString("Key_file");
			attrs = rs.getString("Attrs");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		retArr = new String[2];
		retArr[0] = keyFile;
		retArr[1] = attrs;
		return retArr;
	}
	
	private static void EncryptAndSendFile(Socket client, ArrayList<String> listFile) throws IOException, InterruptedException{
		for(String s : listFile){
			String[] info = checkValid(s);
			if(info == null){
				System.out.println("File not specified yet");
				continue;
			}
			else
				System.out.println("File "+s+" listed");
			String keyFile = info[0];
			String attrs = info[1];
			copyFile(s, Encrypt_location);
//			MyJniFunc.Encrypt(s, Encrypt_location+"/"+keyFile, s+"_enc");
			homo_encrypt(s, Encrypt_location+""+keyFile, s+"_enc");
			sendRequest(client, 1);
			sendFile(client, new File(s+"_enc"));
			sendMetadata(client, attrs, keyFile);
		}
	}

	private static void homo_encrypt(String inputFile, String keyFile, String outputFile) {
		// TODO Auto-generated method stub
		Process p;
		try {
//			System.out.println(execCmd("./homo_encrypt.sh " +Encrypt_location+" "+inputFile+" "+keyFile+ " "+outputFile));
			p = Runtime.getRuntime().exec("./homo_encrypt.sh " +Encrypt_location+" "+inputFile+" "+keyFile+ " "+outputFile);
			System.out.println("./homo_encrypt.sh " +Encrypt_location+" "+inputFile+" "+keyFile+ " "+outputFile);
			p.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		// Client fc = new Client("localhost", 1988,
		// "/home/hieu/Downloads/photo.jpg");
		connectDb();
		MyFlowLayout myUI = new MyFlowLayout("Demo Sieve Client");
		myUI.setSize(600, 500);
		myUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myUI.setLocationRelativeTo(null);
		myUI.setVisible(true);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Socket client;
				Socket toServer;
		
				try {
					ss = new ServerSocket(1992);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while (true) {
					ArrayList<String> fileList = new ArrayList<String>();
					try {
						client = ss.accept();
						toServer = new Socket("localhost", 1988);
						BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
						while(true){
							String text = br.readLine();
							System.out.println(text);
							if(text.equals("**Finish**"))
								break;
							fileList.add(text);
						}
						EncryptAndSendFile(toServer, fileList);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("List file:");
					for(String s : fileList){
						System.out.println(s);
					}
					
					
					
				}
			}
		});
		t.start();
	}

}