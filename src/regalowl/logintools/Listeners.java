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
	private SQL sql;
	
    public Listeners() {
    	lt = LoginTools.lt;
    	sql = lt.getSQL();
    	lt.getServer().getPluginManager().registerEvents(this, lt);
    }
    
    
    
    
    
	@EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerJoinEvent event) {  
    	String playername = event.getPlayer().getName();
    	String ipaddress = event.getPlayer().getAddress().getAddress().toString();
    	ipaddress = ipaddress.substring(1, ipaddress.length());
    	sql.checkPlayer(playername);
    	sql.addLogin(playername, ipaddress);
    	
    	ArrayList<String> sharedIpPlayer = sql.getStringArray("SELECT DISTINCT PLAYER FROM logintools_logins WHERE IP = '" + ipaddress + "'");
    	for (String player:sharedIpPlayer) {
    		OfflinePlayer p = Bukkit.getOfflinePlayer(player);
    		if (p.isBanned()) {
    			Bukkit.broadcast("Player " + playername + " has the same IP as banned player " + player + ".", "logintools.admin");
    		}
    	}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
    	Player p = event.getEntity();
    	String message = event.getDeathMessage();
    	String playername = p.getName();
    	sql.addDeath(playername, message);
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {  
		if (event.getEntity().getKiller() instanceof Player) {
			String type = event.getEntityType().getName();
			Player killer = event.getEntity().getKiller();
			sql.addKill(killer.getName(), type);
		}
	}
	
}