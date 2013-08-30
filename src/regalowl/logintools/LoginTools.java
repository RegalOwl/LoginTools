package regalowl.logintools;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import regalowl.databukkit.DataBukkit;
import regalowl.databukkit.DataBukkitPlugin;
import regalowl.databukkit.QueryResult;
import regalowl.databukkit.SQLRead;
import regalowl.databukkit.SQLWrite;


public class LoginTools extends JavaPlugin {
	
	private Logger log = Logger.getLogger("Minecraft");
	private DataBukkit db;
	public static LoginTools lt;
	private SQLWrite sw;
	private SQLRead sr;
	private Timer ti;
	
    public void onEnable(){ 
    	lt = this;
		db = DataBukkitPlugin.dataBukkit.getDataBukkit(this);
		db.createDatabase();
		sw = db.getSQLWrite();
		sr = db.getSQLRead();
		checkTables();
    	ti = new Timer(600L);
        new Listeners();
    	log.info("LoginTools enabled!");
    }
    
    
    
    
    public void onDisable(){ 
    	db.shutDown();
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
    		ArrayList<String> ips = sr.getStringList("SELECT DISTINCT IP FROM logintools_logins WHERE PLAYER = '" + name + "'");
    		
    		if (ips.size() == 0) {
    			sender.sendMessage(ChatColor.DARK_RED + "Player not found.");
    			return true;
    		}
    		sender.sendMessage(ChatColor.AQUA + name + ChatColor.BLUE + " has logged in from the following IP addresses: " + ChatColor.GREEN + ips.toString());
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
    		ArrayList<String> ips = sr.getStringList("SELECT DISTINCT IP FROM logintools_logins WHERE PLAYER = '" + name + "'");
    		
    		ArrayList<String> altAccounts = new ArrayList<String>();
    		for(String ip:ips) {
    			ArrayList<String> acs = sr.getStringList("SELECT DISTINCT PLAYER FROM logintools_logins WHERE IP = '" + ip + "'");
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
    	
    	
    	if (cmd.getName().equalsIgnoreCase("playertime")){
    		String name = "";
    		if (args.length == 0 && player != null) {
    			name = player.getName();
    		} else if (args.length == 1) {
    			name = args[0];
    		} else {
    			sender.sendMessage("Invalid Usage.  Use /playerinfo (name)");
    			return true;
    		}
    		String firstLogin = sr.getString("SELECT TIME FROM logintools_firstlogin WHERE PLAYER = '" + name + "'");
    		if (firstLogin == "") {
    			sender.sendMessage(ChatColor.DARK_RED + "Player not found.");
    			return true;
    		}
    		long playTime = sr.getLong("SELECT PLAYTIME FROM logintools_firstlogin WHERE PLAYER = '" + name + "'");
    		sender.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
    		sender.sendMessage(ChatColor.AQUA + name + ChatColor.BLUE + " first logged in at: " + ChatColor.GREEN + firstLogin);
    		sender.sendMessage(ChatColor.AQUA + name + ChatColor.BLUE + " has been online a total of: " + ChatColor.GREEN + (int)Math.floor((double)playTime/3600.0) + ChatColor.BLUE + " hours and " + ChatColor.GREEN + (playTime%3600)/60 + ChatColor.BLUE + " minutes");
    		sender.sendMessage(ChatColor.GRAY + "-----------------------------------------------------");
    		return true;
    	}
    	
    	
    	
    	if (cmd.getName().equalsIgnoreCase("toptime")){
    		int ps = 0;
    		if (args.length == 1) {
    			ps = Integer.parseInt(args[0]) - 1;
    		} else if (args.length > 1) {
    			sender.sendMessage(ChatColor.DARK_RED + "Use /toptime (page)");
    		}
    		ArrayList<String> players = sr.getStringList("SELECT PLAYER FROM logintools_firstlogin ORDER BY PLAYTIME DESC");
    		ArrayList<Long> playtime = sr.getLongList("SELECT PLAYTIME FROM logintools_firstlogin ORDER BY PLAYTIME DESC");

    		if (ps < 0) {
    			ps = 0;
    		}
    		
    		sender.sendMessage("----- Top Play Time -----");
    		sender.sendMessage("----- Page (" + (ps + 1) + "/" + (int)Math.ceil(players.size()/10.0) + ") -----");
    		
			int pe = ps + 1;
			ps *= 10;
			pe *= 10;
			for (int i = ps; i < pe; i++) {
				if (i > (playtime.size() - 1)) {
					sender.sendMessage(ChatColor.WHITE + "You have reached the end.");
					return true;
				}
				sender.sendMessage((i + 1) + " " + ChatColor.AQUA + players.get(i) + ChatColor.WHITE + ": " + ChatColor.GREEN + (int)Math.floor((double)playtime.get(i)/3600.0) + ChatColor.BLUE + " hours and " + ChatColor.GREEN + (playtime.get(i)%3600)/60 + ChatColor.BLUE + " minutes");
			}

    		return true;
    	}
    	
    	
    	
    	if (cmd.getName().equalsIgnoreCase("topkills")){
    		int ps = 0;
    		if (args.length == 1) {
    			ps = Integer.parseInt(args[0]) - 1;
    		} else if (args.length > 1) {
    			sender.sendMessage(ChatColor.DARK_RED + "Use /topkills (page)");
    		}
    		ArrayList<String> players = sr.getStringList("SELECT PLAYER FROM logintools_firstlogin ORDER BY KILLS DESC");
    		ArrayList<Long> kills = sr.getLongList("SELECT KILLS FROM logintools_firstlogin ORDER BY KILLS DESC");

    		if (ps < 0) {
    			ps = 0;
    		}
    		
    		sender.sendMessage("----- Top Kills -----");
    		sender.sendMessage("----- Page (" + (ps + 1) + "/" + (int)Math.ceil(players.size()/10.0) + ") -----");
			int pe = ps + 1;
			
			ps *= 10;
			pe *= 10;
			for (int i = ps; i < pe; i++) {
				if (i > (kills.size() - 1)) {
					sender.sendMessage(ChatColor.WHITE + "You have reached the end.");
					return true;
				}
				sender.sendMessage((i + 1) + " " + ChatColor.AQUA + players.get(i) + ChatColor.WHITE + ": " + ChatColor.GREEN + kills.get(i));
			}

    		return true;
    	}
    	
    	
    	if (cmd.getName().equalsIgnoreCase("topdeaths")){
    		int ps = 0;
    		if (args.length == 1) {
    			ps = Integer.parseInt(args[0]) - 1;
    		} else if (args.length > 1) {
    			sender.sendMessage(ChatColor.DARK_RED + "Use /topdeaths (page)");
    		}
    		ArrayList<String> players = sr.getStringList("SELECT PLAYER FROM logintools_firstlogin ORDER BY DEATHS DESC");
    		ArrayList<Long> deaths = sr.getLongList("SELECT DEATHS FROM logintools_firstlogin ORDER BY DEATHS DESC");

    		if (ps < 0) {
    			ps = 0;
    		}
    		
    		sender.sendMessage("----- Top Deaths -----");
    		sender.sendMessage("----- Page (" + (ps + 1) + "/" + (int)Math.ceil(players.size()/10.0) + ") -----");
			int pe = ps + 1;
			ps *= 10;
			pe *= 10;
			for (int i = ps; i < pe; i++) {
				if (i > (deaths.size() - 1)) {
					sender.sendMessage(ChatColor.WHITE + "You have reached the end.");
					return true;
				}
				sender.sendMessage((i + 1) + " " + ChatColor.AQUA + players.get(i) + ChatColor.WHITE + ": " + ChatColor.GREEN + deaths.get(i));
			}

    		return true;
    	}
    	
    	
    	
    	if (cmd.getName().equalsIgnoreCase("lstats")){
    		sender.sendMessage("Buffer Size: " + sw.getBufferSize());
    		sender.sendMessage("Active Threads: " + sw.getActiveThreads());
    		return true;
    	}
    	
    	return false;
    }
	
	
    
    public SQLWrite getSQLWrite() {
    	return sw;
    }

    
    public Timer getTimer() {
    	return ti;
    }
    
	public DataBukkit getDb() {
		return db;
	}
    
    
	public boolean inDatabase(String player) {
		QueryResult result = db.getSQLRead().aSyncSelect("SELECT * FROM logintools_firstlogin WHERE PLAYER = '" + player + "'");
		while (result.next()) {
			result.close();
			return true;
		}
		result.close();
		return false;
	}

	
	
	public void checkPlayer(String player) {
		if (!inDatabase(player)) {
			String statement = "";
			if (db.useMySQL()) {
				statement = "INSERT INTO logintools_firstlogin (PLAYER, TIME, PLAYTIME) VALUES ('" + player + "', NOW(), '0')";
			} else {
				statement = "INSERT INTO logintools_firstlogin (PLAYER, TIME, PLAYTIME) VALUES ('" + player + "', datetime('NOW', 'localtime'), '0')";
			}
			sw.executeSQL(statement);
		}
	}
	
	public void addPlaytime(String player, long seconds) {
		String statement = "UPDATE logintools_firstlogin SET PLAYTIME = PLAYTIME + '" + seconds + "' WHERE PLAYER = '" + player + "'";
		sw.executeSQL(statement);		
	}
	
	public void addLogin(String player, String ip) {
		String statement = "";
		if (db.useMySQL()) {
			statement = "INSERT INTO logintools_logins (PLAYER, LOGIN_TIME, IP) VALUES ('" + player + "', NOW(), '" + ip + "')";
		} else {
			statement = "INSERT INTO logintools_logins (PLAYER, LOGIN_TIME, IP) VALUES ('" + player + "', datetime('NOW', 'localtime'), '" + ip + "')";
		}
		sw.executeSQL(statement);
	}
	
	public void addKill(String player, String kill, Location loc) {
		String statement = "";
		if (db.useMySQL()) {
			statement = "INSERT INTO logintools_kills (TIME, PLAYER, KILLED, X, Y, Z, WORLD) VALUES (NOW(), '" + player + "','" + kill + "','" + loc.getBlockX() + "','" + loc.getBlockY() + "','" + loc.getBlockZ() + "','" + loc.getWorld().getName() + "')";
		} else {
			statement = "INSERT INTO logintools_kills (TIME, PLAYER, KILLED, X, Y, Z, WORLD) VALUES (datetime('NOW', 'localtime'), '" + player + "','" + kill + "','" + loc.getBlockX() + "','" + loc.getBlockY() + "','" + loc.getBlockZ() + "','" + loc.getWorld().getName() + "')";
		}
		sw.executeSQL(statement);
		statement = "UPDATE logintools_firstlogin SET KILLS = KILLS + '" + 1 + "' WHERE PLAYER = '" + player + "'";
		sw.executeSQL(statement);		
	}
	
	public void addDeath(String player, String message, Location loc) {
		String statement = "";
		if (db.useMySQL()) {
			statement = "INSERT INTO logintools_deaths (TIME, PLAYER, MESSAGE, X, Y, Z, WORLD) VALUES (NOW(), '" + player + "','" + message + "','" + loc.getBlockX() + "','" + loc.getBlockY() + "','" + loc.getBlockZ() + "','" + loc.getWorld().getName() + "')";
		} else {
			statement = "INSERT INTO logintools_deaths (TIME, PLAYER, MESSAGE, X, Y, Z, WORLD) VALUES (datetime('NOW', 'localtime'), '" + player + "','" + message + "','" + loc.getBlockX() + "','" + loc.getBlockY() + "','" + loc.getBlockZ() + "','" + loc.getWorld().getName() + "')";
		}
		sw.executeSQL(statement);
		statement = "UPDATE logintools_firstlogin SET DEATHS = DEATHS + '" + 1 + "' WHERE PLAYER = '" + player + "'";
		sw.executeSQL(statement);
	}
	

	
	
	
	public void checkTables() {
		if (db.useMySQL()) {
			sw.executeSQL("CREATE TABLE IF NOT EXISTS logintools_firstlogin (ID INT NOT NULL AUTO_INCREMENT, PLAYER TINYTEXT NOT NULL, TIME DATETIME NOT NULL, PLAYTIME BIGINT(64) NOT NULL DEFAULT '0', KILLS INT NOT NULL DEFAULT '0', DEATHS INT NOT NULL DEFAULT '0', PRIMARY KEY (ID))");
			sw.executeSQL("CREATE TABLE IF NOT EXISTS logintools_logins (ID INT NOT NULL AUTO_INCREMENT, PLAYER TINYTEXT NOT NULL, LOGIN_TIME DATETIME NOT NULL, IP TEXT NOT NULL, PRIMARY KEY (ID))");
			sw.executeSQL("CREATE TABLE IF NOT EXISTS logintools_deaths (ID INT NOT NULL AUTO_INCREMENT, TIME DATETIME NOT NULL, PLAYER TINYTEXT NOT NULL, MESSAGE TEXT NOT NULL, X INT NOT NULL, Y INT NOT NULL, Z INT NOT NULL, WORLD VARCHAR(64), PRIMARY KEY (ID))");
			sw.executeSQL("CREATE TABLE IF NOT EXISTS logintools_kills (ID INT NOT NULL AUTO_INCREMENT, TIME DATETIME NOT NULL, PLAYER TINYTEXT NOT NULL, KILLED TINYTEXT NOT NULL, X INT NOT NULL, Y INT NOT NULL, Z INT NOT NULL, WORLD VARCHAR(64), PRIMARY KEY (ID))");
		} else {
			sw.executeSQL("CREATE TABLE IF NOT EXISTS logintools_firstlogin (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, PLAYER TINYTEXT NOT NULL, TIME DATETIME NOT NULL, PLAYTIME BIGINT(64) NOT NULL DEFAULT '0', KILLS INT NOT NULL DEFAULT '0', DEATHS INT NOT NULL DEFAULT '0')");
			sw.executeSQL("CREATE TABLE IF NOT EXISTS logintools_logins (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, PLAYER TINYTEXT NOT NULL, LOGIN_TIME DATETIME NOT NULL, IP TEXT NOT NULL)");
			sw.executeSQL("CREATE TABLE IF NOT EXISTS logintools_deaths (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, TIME DATETIME NOT NULL, PLAYER TINYTEXT NOT NULL, MESSAGE TEXT NOT NULL, X INT NOT NULL, Y INT NOT NULL, Z INT NOT NULL, WORLD VARCHAR(64))");
			sw.executeSQL("CREATE TABLE IF NOT EXISTS logintools_kills (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, TIME DATETIME NOT NULL, PLAYER TINYTEXT NOT NULL, KILLED TINYTEXT NOT NULL, X INT NOT NULL, Y INT NOT NULL, Z INT NOT NULL, WORLD VARCHAR(64))");
		}
	}
    
}
