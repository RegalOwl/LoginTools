package regalowl.logintools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;

public class ConnectionPool {
	
	private int maxConnections;
	private SQLWrite sw;
	private LoginTools lt;
	private String username;
	private String password;
	private String host;
	private int port;
	private String database;
	
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private ArrayList<Boolean> inUse = new ArrayList<Boolean>();
	
	
	ConnectionPool() {
		
		lt = LoginTools.lt;
		FileConfiguration config = lt.getYaml().getConfig();
		username = config.getString("sql-connection.username");
		password = config.getString("sql-connection.password");
		port = config.getInt("sql-connection.port");
		host = config.getString("sql-connection.host");
		database = config.getString("sql-connection.database");
		maxConnections = config.getInt("sql-connection.max-sql-threads");
		sw = lt.getSQLWrite();
		openConnections();
		
		lt.getServer().getScheduler().scheduleAsyncRepeatingTask(lt, new Runnable() {
			public void run() {
				refreshConnections();
			}
		}, 12000L, 12000L);
		
	}
	
	
	private void refreshConnections() {
		sw.pauseWrite(1200L);
		lt.getServer().getScheduler().scheduleAsyncDelayedTask(lt, new Runnable() {
			public void run() {
				closeConnections();
				openConnections();
			}
		}, 1000L);
	}
	
	
	
	public Connection getConnection() {
		int i = 0;
		if (inUse != null && connections != null) {
			while (i < maxConnections) {
				if (!inUse.get(i)) {
					inUse.set(i, true);
					return connections.get(i);
				}
				i++;
			}
		}
		return null;
	}
	
	
	public void returnConnection(Connection connection) {
		if (connections.contains(connection)) {
			inUse.set(connections.indexOf(connection), false);
		}
	}
	
	
	
	public void openConnections() {
		
		for (int i = 0; i < maxConnections; i++) {
			try {
				Connection connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
				connections.add(connect);
				inUse.add(false);
			} catch (SQLException e) {
				e.printStackTrace();
				refreshConnections();
				return;
			}
		}
	}

	
	public void closeConnections() {
		for (int i = 0; i < connections.size(); i++) {
			try {
				connections.get(i).close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		connections.clear();
		inUse.clear();
	}
	

}