package regalowl.logintools;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Timer {
	
	private int timerID;
	private LoginTools lt;
	private long lastTime;
	private long serverUptime;
	private long frequency;
	
	Timer(long tickFrequency) {
		frequency = tickFrequency;
		lastTime = System.currentTimeMillis();
		lt = LoginTools.lt;
		startTimer();
	}
	
	
	public void startTimer() {
		timerID = lt.getServer().getScheduler().scheduleSyncRepeatingTask(lt, new Runnable() {
		    public void run() {
		    	long time = System.currentTimeMillis();
		    	serverUptime += (time - lastTime);
		    	updateOnlinePlayers(time - lastTime);
		    	lastTime = time;
		    }
		}, frequency, frequency);
	}
	
	public void stopTimer() {
		lt.getServer().getScheduler().cancelTask(timerID);
	}
	
	public long getServerUptime() {
		return serverUptime;
	}
	
	private void updateOnlinePlayers(long msecs) {
		Player[] players = Bukkit.getOnlinePlayers();
		for (Player player: players) {
			String name = player.getName();
			lt.addPlaytime(name, msecs/1000);
		}
	}
	
}
