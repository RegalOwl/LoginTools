package regalowl.logintools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class SQL {
	private LoginTools lt;
	private String username;
	private String password;
	private String host;
	private int port;
	private String database;
	
	SQL() {
		lt = LoginTools.lt;
		FileConfiguration config = lt.getYaml().getConfig();
		username = config.getString("sql-connection.username");
		password = config.getString("sql-connection.password");
		port = config.getInt("sql-connection.port");
		host = config.getString("sql-connection.host");
		database = config.getString("sql-connection.database");
	}
	
	
	
	
	public long getIntValue(String statement) {
		int data = 0;
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery(statement);
			if (result.next()) {
				data = result.getInt(1);
			}
			result.close();
			state.close();
			connect.close();
			return data;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return data;
		}
	}
	
	
	public long getLongValue(String statement) {
		long data = 0;
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery(statement);
			if (result.next()) {
				data = result.getLong(1);
			}
			result.close();
			state.close();
			connect.close();
			return data;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return data;
		}
	}
	
	
	public String getStringValue(String statement) {
		String data = "";
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery(statement);
			if (result.next()) {
				data = result.getString(1);
			}
			result.close();
			state.close();
			connect.close();
			return data;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return data;
		}
	}

	
	
	public ArrayList<String> getStringArray(String statement) {
		ArrayList<String> data = new ArrayList<String>();
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery(statement);
			while (result.next()) {
				data.add(result.getString(1));
			}
			result.close();
			state.close();
			connect.close();
			return data;
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "hyperconomy.error");
			e.printStackTrace();
			return data;
		}
	}

	
	
	public boolean inDatabase(String player) {
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			ResultSet result = state.executeQuery("SELECT * FROM logintools_firstlogin WHERE PLAYER = '" + player + "'");
			if (result.next()) {
				result.close();
				state.close();
				connect.close();
				return true;
			} else {
				result.close();
				state.close();
				connect.close();
				return false;
			}
		} catch (SQLException e) {
			Bukkit.broadcast(ChatColor.RED + "SQL connection failed.  Check your config settings.", "logintools.error");
			e.printStackTrace();
			return false;
		}
	}
	
	
	public void checkPlayer(String player) {
		if (!inDatabase(player)) {
			String statement = "INSERT INTO logintools_firstlogin (PLAYER, TIME, PLAYTIME) VALUES ('" + player + "', NOW(), '0')";
			lt.getSQLWrite().writeData(statement);
		}
	}
	
	public void addPlaytime(String player, long seconds) {
		String statement = "UPDATE logintools_firstlogin SET PLAYTIME = PLAYTIME + '" + seconds + "' WHERE PLAYER = '" + player + "'";
		lt.getSQLWrite().writeData(statement);		
	}
	
	public void addLogin(String player, String ip) {
		String statement = "INSERT INTO logintools_logins (PLAYER, LOGIN_TIME, IP) VALUES ('" + player + "', NOW(), '" + ip + "')";
		lt.getSQLWrite().writeData(statement);
	}
	
	

	
	
	
	public boolean checkTables() {
		try {
			Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
			Statement state = connect.createStatement();
			state.execute("CREATE TABLE IF NOT EXISTS logintools_firstlogin (ID INT NOT NULL AUTO_INCREMENT, PLAYER TINYTEXT, TIME DATETIME, PLAYTIME BIGINT(64), PRIMARY KEY (ID))");
			state.execute("CREATE TABLE IF NOT EXISTS logintools_logins (ID INT NOT NULL AUTO_INCREMENT, PLAYER TINYTEXT, LOGIN_TIME DATETIME, IP TEXT, PRIMARY KEY (ID))");
			state.close();
			connect.close();
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	
	
}
