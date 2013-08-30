package regalowl.logintools;





import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;


public class Listeners implements Listener {
	
	private LoginTools lt;
	
    public Listeners() {
    	lt = LoginTools.lt;
    	lt.getServer().getPluginManager().registerEvents(this, lt);
    }
    
    
    
    
    
	@EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerJoinEvent event) {  
    	String playername = event.getPlayer().getName();
    	String ipaddress = event.getPlayer().getAddress().getAddress().toString();
    	ipaddress = ipaddress.substring(1, ipaddress.length());
    	new PlayerLogin(ipaddress, playername);

	}
	
	
	
	private class PlayerLogin {
		private String ipaddress;
		private String playername;
		private ArrayList<String> sharedIpPlayer;
		private LoginTools lt;
		PlayerLogin(String ip, String name) {
			lt = LoginTools.lt;
			ipaddress = ip;
			playername = name;
			lt.getServer().getScheduler().runTaskAsynchronously(lt, new Runnable() {
	    		public void run() {
	    			sharedIpPlayer = lt.getDb().getSQLRead().getStringList("SELECT DISTINCT PLAYER FROM logintools_logins WHERE IP = '" + ipaddress + "'");
	    	    	lt.checkPlayer(playername);
	    	    	lt.addLogin(playername, ipaddress);
	    			lt.getServer().getScheduler().runTask(lt, new Runnable() {
	    	    		public void run() {
	    	    	    	for (String player:sharedIpPlayer) {
	    	    	    		OfflinePlayer p = Bukkit.getOfflinePlayer(player);
	    	    	    		if (p.isBanned()) {
	    	    	    			Bukkit.broadcast("Player " + playername + " has the same IP as banned player " + player + ".", "logintools.admin");
	    	    	    		}
	    	    	    	}
	    	    		}
	    	    	});
	    		}
	    	});
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
    	Player p = event.getEntity();
    	String message = event.getDeathMessage().replace(p.getName(), "");
    	String playername = p.getName();
    	lt.addDeath(playername, message, p.getLocation());
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {  
		if (event.getEntity().getKiller() instanceof Player) {
			String type = event.getEntityType().getName();
			Player killer = event.getEntity().getKiller();
			lt.addKill(killer.getName(), type, event.getEntity().getLocation());
		}
	}
	
}