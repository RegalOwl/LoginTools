package regalowl.logintools;

import java.util.ArrayList;
import java.util.logging.Logger;


public class SQLWrite {

	private LoginTools lt;
	private int activethreads;
	private int threadlimit;
	private int writeTaskID;
	private ArrayList<String> buffer = new ArrayList<String>();
	private ArrayList<String> workingBuffer = new ArrayList<String>();
	private boolean writePaused;
	private boolean writeActive;
	private ConnectionPool cp;
	
	SQLWrite() {
		lt = LoginTools.lt;
		activethreads = 0;
		threadlimit = lt.getYaml().getConfig().getInt("sql-connection.max-sql-threads");
		cp = lt.getConnectionPool();
		writePaused = false;
		writeActive = false;
	}

	public void writeData(ArrayList<String> statements) {
		for (int c = 0; c < statements.size(); c++) {
			workingBuffer.add(statements.get(c));
			buffer.add(statements.get(c));
			startWrite();
		}
	}
	public void writeData(String statement) {
		workingBuffer.add(statement);
		buffer.add(statement);
		startWrite();
	}
	
	
	
	
	
    private void startWrite() {
    	if (!writeActive && !writePaused) {
        	writeActive = true;
    		writeTaskID = lt.getServer().getScheduler().scheduleSyncRepeatingTask(lt, new Runnable() {
    		    public void run() {
        		    Write();
    		    }
    		}, 5L, 5L);
    	}
    }
    private void stopWrite() {
		lt.getServer().getScheduler().cancelTask(writeTaskID);
		writeActive = false;
    }
	
	
    
    
    
	
	
    private void Write() {
    	if (workingBuffer.size() == 0) {
    		stopWrite();
    	}
    	while (activethreads < threadlimit && workingBuffer.size() > 0) {
			activethreads++;
    		int index = workingBuffer.size() - 1;
    		String statement = workingBuffer.get(index);
    		workingBuffer.remove(index);
    		SQLWriteThread swt = new SQLWriteThread();
    		swt.writeThread(statement);
    	}
    }

    
    public void pauseWrite(long wait) {
    	writePaused = true;
    	stopWrite();
    	lt.getServer().getScheduler().scheduleSyncDelayedTask(lt, new Runnable() {
		    public void run() {
		    	writePaused = false;
		    	startWrite();
		    }
		}, wait);
    }
    
    
    public void writeSuccess(String statement) {
    	buffer.remove(statement);
    	activethreads--;
    }
    
    public void writeFailed(String statement) {
    	Logger log = Logger.getLogger("Minecraft");
    	log.info(statement);
    	activethreads--;
    	workingBuffer.add(statement);
    	startWrite();
    }


	public void returnThreads(int threads) {
		activethreads = activethreads - threads;
	}
	
	public int getBufferSize() {
		return buffer.size();
	}
	
	public int getActiveThreads() {
		return activethreads;
	}
	
	public ArrayList<String> getBuffer() {
		return buffer;
	}
	
	public void closeConnections() {
		cp.closeConnections();
	}

}
