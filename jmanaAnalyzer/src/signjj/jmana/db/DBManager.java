package signjj.jmana.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DBManager {
	private String driver = "org.mariadb.jdbc.Driver";
	private String url = "jdbc:mariadb://localhost:3306/jmana";
	private String id = "root";
	private String pass = "dbfldid1";
	
	private Connection con;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	public DBManager() {
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, id, pass);
			
			//if(con != null) System.out.println("DB Access");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insert(String title, String vol) throws Exception {
		String sql = "insert into data(title, vol) values(?, ?)";
		
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, title);
		pstmt.setString(2, vol);
		
		pstmt.executeUpdate();
		
		pstmt.close();
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray select(String title) throws Exception {
		JSONArray resultJSON = new JSONArray();
		
		String sql = "select * from data where title = ?";
		
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, title);
		
		System.out.println("Query >> " + pstmt.toString());
		
		rs = pstmt.executeQuery();
		
		while(rs.next()) {
			JSONObject json = new JSONObject();
			
			json.put("title", rs.getString(1));
			json.put("vol", rs.getString(2));
			
			resultJSON.add(json);
		}
		
		pstmt.close();
		
		return resultJSON;
	}
}
