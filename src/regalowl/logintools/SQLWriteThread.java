package regalowl.logintools;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class SQLWriteThread {


	private LoginTools lt;
	private SQLWrite sw;
	private int savecompletetaskid;
	private int writethreadid;
	private String statement;
	private boolean savecomplete;
	private ConnectionPool cp;
	private Connection connection;
		

		
	public void writeThread(String state) {
		lt = LoginTools.lt;
		sw = lt.getSQLWrite();
		cp = lt.getConnectionPool();
		statement = state;

			
		savecomplete = false;

		writethreadid = lt.getServer().getScheduler().scheduleAsyncDelayedTask(lt, new Runnable() {
			public void run() {
				try {
					connection = cp.getConnection();
					if (connection == null || connection.isClosed()) {
						failed();
						return;
					} else {
						Statement state = connection.createStatement();
						state.execute(statement);
					    state.close();
					    cp.returnConnection(connection);
					    savecomplete = true;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					cp.returnConnection(connection);
					sw.writeFailed(statement);
					savecomplete = true;
				}
			}
		}, 0L);
		

			
		savecompletetaskid = lt.getServer().getScheduler().scheduleSyncRepeatingTask(lt, new Runnable() {
			public void run() {
				if (savecomplete) {
					sw.writeSuccess(statement);
					lt.getServer().getScheduler().cancelTask(savecompletetaskid);
					lt.getServer().getScheduler().cancelTask(writethreadid);
				}
			}
		}, 1L, 1L);
			
		
		
	}


	private void failed() {
		sw.writeFailed(statement);
		savecomplete = true;
	}

	
}
