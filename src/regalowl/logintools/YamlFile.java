package regalowl.logintools;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;




/**
 * 
 *This class handles the items, config, and enchants yaml files.
 *
 */
public class YamlFile {

    FileConfiguration config;
    File configFile;
    
	/**
	 * 
	 * This is run when the plugin is enabled, it calls the firstRun() method to create the yamls if they don't exist and then loads the yaml files with the loadYamls() method.
	 * 
	 */
	YamlFile() {
		

        configFile = new File(Bukkit.getServer().getPluginManager().getPlugin("LoginTools").getDataFolder(), "config.yml");  
        
        try {
            firstRun();
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        config = new YamlConfiguration();
        loadYamls();
		
	}
	
	
	/**
	 * 
	 * Checks if the yamls exist, and if they don't, it copies the default files to the plugin's folder.
	 * 
	 */
    private void firstRun() throws Exception {
        if(!configFile.exists()){              
            configFile.getParentFile().mkdirs();         
            copy(this.getClass().getResourceAsStream("/config.yml"), configFile);
        }
    }


	/**
	 * 
	 * This actually copies the files.
	 * 
	 */
    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**
	 * 
	 * This loads the yaml files.
	 * 
	 */
    public void loadYamls() {
        try {
            config.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("[LoginTools]Bad config.yml file.");
        }
    }

	/**
	 * 
	 * This saves the yaml files.
	 * 
	 */
    public void saveYamls() {
        try {
        	config.save(configFile); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    

	
	/**
	 * 
	 * This gets the config FileConfiguraiton.
	 * 
	 */
	public FileConfiguration getConfig(){
		return config;
	}

}
