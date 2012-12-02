package regalowl.logintools;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class LoginTools extends JavaPlugin {
	
	private Logger log = Logger.getLogger("Minecraft");
	private YamlFile yaml;
	public static LoginTools lt;
	private ConnectionPool cp;
	private SQLWrite sw;
	private SQL sql;
	private Timer ti;
	
    public void onEnable(){ 
    	lt = this;
    	yaml = new YamlFile();
    	sql = new SQL();
    	boolean databaseOk = sql.checkTables();
    	if (databaseOk) {
    		ti = new Timer(600L);
        	sw = new SQLWrite();
        	cp = new ConnectionPool();
        	new LoginListener();
    	} else {
			log.severe("[LoginTools] Database connection error.  Shutting down...");
			getServer().getScheduler().cancelTasks(this);
			getPluginLoader().disablePlugin(this);
    	}
    	log.info("LoginTools enabled!");
    }
    
    
    
    
    public void onDisable(){ 
    	log.info("LoginTools has been disabled!");
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = null;
		if (sender instanceof Player) {
    		player = (Player) sender;
    	}
     
    	if (cmd.getName().equalsIgnoreCase("playerip")){
    		String name = "";
    		if (args.length == 0 && player != null) {
    			name = player.getName();
    		} else if (args.length == 1) {
    			name = args[0];
    		} else {
    			sender.sendMessage("Invalid Usage.  Use /getip (name)");
    			return true;
    		}
    		ArrayList<String> ips = sql.getStringArray("SELECT DISTINCT IP FROM logintools_logins WHERE PLAYER = '" + name + "'");
    		
    		if (ips.size() == 0) {
    			sender.sendMessage(ChatColor.DARK_RED + "Player not found.");
    			return true;
    		}
    		sender.sendMessage(ChatColor.AQUA + name + ChatColor.BLUE + " has logged in from the following IP addresses: " + ChatColor.GREEN + ips.toString());
    		
    		//for(String ip:ips) {
    		//	sql.getIntValue("SELECT COUNT(*) FROM logintools_logins WHERE IP = '" + ip + "'");
    		//}
    		
    		return true;	
    	}
    	
    	
    	
    	if (cmd.getName().equalsIgnoreCase("sharedip")){
    		String name = "";
    		if (args.length == 0 && player != null) {
    			name = player.getName();
    		} else if (args.length == 1) {
    			name = args[0];
    		} else {
    			sender.sendMessage("Invalid Usage.  Use /altaccount (name)");
    			return true;
    		}
    		ArrayList<String> ips = sql.getStringArray("SELECT DISTINCT IP FROM logintools_logins WHERE PLAYER = '" + name + "'");
    		
    		ArrayList<String> altAccounts = new ArrayList<String>();
    		for(String ip:ips) {
    			ArrayList<String> acs = sql.getStringArray("SELECT DISTINCT PLAYER FROM logintools_logins WHERE IP = '" + ip + "'");
    			for (String act:acs) {
    				if (!altAccounts.contains(act)) {
    					altAccounts.add(act);
    				}
    			}
    		}
    		altAccounts.remove(name);
    		sender.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
    		sender.sendMessage(ChatColor.AQUA + name + ChatColor.BLUE + " has used the same IP as the following players: " + ChatColor.GREEN + altAccounts.toString());
    		sender.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
    		return true;	
    	}
    	
    	
    	if (cmd.getName().equalsIgnoreCase("playerinfo")){
    		String name = "";
    		if (args.length == 0 && player != null) {
    			name = player.getName();
    		} else if (args.length == 1) {
    			name = args[0];
    		} else {
    			sender.sendMessage("Invalid Usage.  Use /playerinfo (name)");
    			return true;
    		}
    		String firstLogin = sql.getStringValue("SELECT TIME FROM logintools_firstlogin WHERE PLAYER = '" + name + "'");
    		if (firstLogin == "") {
    			sender.sendMessage(ChatColor.DARK_RED + "Player not found.");
    			return true;
    		}
    		long playTime = sql.getLongValue("SELECT PLAYTIME FROM logintools_firstlogin WHERE PLAYER = '" + name + "'");
    		sender.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
    		sender.sendMessage(ChatColor.AQUA + name + ChatColor.BLUE + " first logged in at: " + ChatColor.GREEN + firstLogin);
    		sender.sendMessage(ChatColor.AQUA + name + ChatColor.BLUE + " has been online a total of: " + ChatColor.GREEN + (int)Math.floor((double)playTime/3600.0) + ChatColor.BLUE + " hours and " + ChatColor.GREEN + (playTime%3600)/60 + ChatColor.BLUE + " minutes");
    		sender.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
    		return true;
    	}
    	
    	if (cmd.getName().equalsIgnoreCase("lstats")){
    		sender.sendMessage("Buffer Size: " + sw.getBufferSize());
    		sender.sendMessage("Active Threads: " + sw.getActiveThreads());
    		return true;
    	}
    	
    	return false;
    }
	
	
    
    
    public YamlFile getYaml() {
    	return yaml;
    }
    
    public ConnectionPool getConnectionPool() {
    	return cp;
    }
    
    public SQLWrite getSQLWrite() {
    	return sw;
    }

    public SQL getSQL() {
    	return sql;
    }
    
    public Timer getTimer() {
    	return ti;
    }
    
	
}
