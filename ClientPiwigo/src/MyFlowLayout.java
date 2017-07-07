import java.awt.Button;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class MyFlowLayout extends JFrame {

	JTable table;
	String[] columnNames = { "ID", "File", "Attrs", "Key", "Epoch" };
	private static final long serialVersionUID = 1L;
	private File chosenFile;
	private File chosenKey = new File("/home/hieu/Downloads/piwigo_client/key_file1.txt");
	private File reKeyToken = new File("/home/hieu/Sieve_user_client/Key_hom/token.txt");
	String stt = "No key selected!!!";
	DefaultTableModel model;
	Statement stmt;
	
	static Connection con;
	private static final String url = "jdbc:mysql://localhost";

	private static final String user = "root";

	private static final String password = "123";
	
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

	public MyFlowLayout(String title)

	{

		connectDb();
		setTitle(title);

		JPanel pnFlow = new JPanel();

		pnFlow.setLayout(new FlowLayout());

		model = new DefaultTableModel();
		model.setColumnIdentifiers(columnNames);
		// DefaultTableModel model = new DefaultTableModel(tm.getData1(),
		// tm.getColumnNames());
		// table = new JTable(model);
		table = new JTable();
		table.setModel(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setFillsViewportHeight(true);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		String roll = "";
		String name = "";
		String cl = "";
		String sec = "";

		pnFlow.add(scroll);
		loadTable();
		
		JButton btn1 = new JButton("Choose file");
		JButton btn2 = new JButton("Choose key");
		JButton btn3 = new JButton("Re-Encrypt");
		JButton btnSendKey = new JButton("Send Key");

		JTextArea txtType = new JTextArea("Type");
		JTextField type = new JTextField();
		JTextArea txtLoc = new JTextArea("Location");
		JTextField location = new JTextField();
		JTextArea txtYear = new JTextArea("Year");
		JTextField year = new JTextField();
		JTextArea txtStt = new JTextArea(stt);
		type.setColumns(5);
		location.setColumns(5);
		year.setColumns(5);

		pnFlow.add(btn2);
		pnFlow.add(btn1);

		pnFlow.add(txtType);
		pnFlow.add(type);
		pnFlow.add(txtLoc);
		pnFlow.add(location);
		pnFlow.add(txtYear);
		pnFlow.add(year);
		pnFlow.add(txtStt);
		pnFlow.add(btn3);
		pnFlow.add(btnSendKey);
		Container con = getContentPane();

		con.add(pnFlow);
		btn1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser chooser = new JFileChooser();
				int choice = chooser.showOpenDialog(pnFlow);
				if (choice != JFileChooser.APPROVE_OPTION)
					return;
				chosenFile = chooser.getSelectedFile();
				System.out.println(chosenFile.getName());
				String attrs = "TYPE" + type.getText() + " and LOCATION" + location.getText() + " and YEAR"
						+ year.getText() + " and EPOCH0";
				System.out.println(attrs);
				if (attrs.equals("")) {
					System.out.println("No attribute is specified");
					return;
				}
				
				// Insert table
				if(chosenKey == null){
					System.out.println("No key selected");
					return;
				}
				
				try {
					InsertTable(chosenFile.getAbsolutePath(), attrs, chosenKey.getName(), 0);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				deleteTable();
				loadTable();
				// Client fc = new Client("localhost", 1988,
				// chosenFile.getPath(),attrs,chosenKey.getPath());
			}
		});

		btn2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser chooser = new JFileChooser();
				int choice = chooser.showOpenDialog(pnFlow);
				if (choice != JFileChooser.APPROVE_OPTION)
					return;
				chosenKey = chooser.getSelectedFile();
				txtStt.setText("Key file is " + chosenKey.getPath());
			}
		});
		
		btn3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				JFileChooser chooser = new JFileChooser();
				int choice = chooser.showOpenDialog(pnFlow);
				if (choice != JFileChooser.APPROVE_OPTION)
					return;
				chosenKey = chooser.getSelectedFile();
				txtStt.setText("Key file is " + chosenKey.getPath());
				int row = table.getSelectedRow();
				int ID = (int)table.getModel().getValueAt(row, 0);
				//int ID = Integer.parseInt(IDstr);
				reEncryption(chosenKey, ID);
				deleteTable();
				loadTable();
			}

		});
		
		btnSendKey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser chooser = new JFileChooser();
				int choice = chooser.showOpenDialog(pnFlow);
				if (choice != JFileChooser.APPROVE_OPTION)
					return;
				File chosenKey = chooser.getSelectedFile();
				sendKey(chosenKey);
			}

			
		});
	}
	
	private void sendKey(File chosenKey) {
		// TODO Auto-generated method stub
		try {
			Socket s = new Socket("localhost", 1993);
			DataOutputStream os = new DataOutputStream(s.getOutputStream());
			
			byte[] buffer = new byte[65536];
			// Send file length
			os.writeLong(chosenKey.length());
			
			FileInputStream fis = new FileInputStream(chosenKey);
			
			while(fis.read(buffer, 0, buffer.length) > 0){
				os.write(buffer);
			}
			fis.close();
			os.flush();
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void reEncryption(File newKey,int ID) {
		// TODO Auto-generated method stub
		// - sendBlob();
		// - increase epoch
		// regenerate metadata and send
		String sql = "select * from client_files where ID = '"+ID+"'";
		System.out.println(System.currentTimeMillis());
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			String attrs = rs.getString("Attrs");
			String fileName = rs.getString("File");
			System.out.println("origin "+attrs);
			int epoch = rs.getInt("Epoch");
			String[] AttrArr = attrs.split("and");
			
			StringBuilder modifiedAttrs = new StringBuilder("");
			modifiedAttrs.append(AttrArr[0]);
			for(int i = 1; i < AttrArr.length -1; i++){
				modifiedAttrs.append("and"+AttrArr[i]);
			}
			modifiedAttrs.append("and EPOCH"+(epoch + 1));
			System.out.println("Modify attrs"+modifiedAttrs.toString());
			String updateAttrs = "update client_files set Attrs = '"+modifiedAttrs+"', Epoch = '"+(epoch+1)+"' where ID = '"+ID+"'";
			stmt.executeUpdate(updateAttrs);
			
//			ClientPiwigo.reKey(fileName, reKeyToken, newKey, attrs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void loadTable() {

		
		try {
			String sql = "select * from client_files";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int i = 0;
			while (rs.next()) {
				int ID = rs.getInt("ID");
				String File = rs.getString("File");
				String attrs = rs.getString("Attrs");
				String key = rs.getString("Key_file");
				int epoch = rs.getInt("Epoch");
				model.addRow(new Object[] { ID, File, attrs, key, epoch });
				i++;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void InsertTable(String file, String attrs, String key, int epoch) throws SQLException{
		String sql = "insert into client_files (File, Attrs, Key_file, Epoch) values ('"+file+"','"+attrs+"','"+key+"','"+epoch+"');";
		Statement stmt = con.createStatement();
		stmt.executeUpdate(sql);
	}
	
	private void deleteTable(){
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		int rowCount = model.getRowCount();
		for(int i = rowCount - 1; i >=0; i--){
			model.removeRow(i);
		}
	}

	public static void main(String[] args)

	{

		MyFlowLayout myUI = new MyFlowLayout("Demo Sieve Client");

		myUI.setSize(2000, 2000);

		myUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		myUI.setLocationRelativeTo(null);

		myUI.setVisible(true);

	}

}