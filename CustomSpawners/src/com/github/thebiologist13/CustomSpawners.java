package com.github.thebiologist13;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.thebiologist13.listeners.DamageController;
import com.github.thebiologist13.listeners.ExpBottleHitEvent;
import com.github.thebiologist13.listeners.InteractEvent;
import com.github.thebiologist13.listeners.MobCombustEvent;
import com.github.thebiologist13.listeners.MobDamageEvent;
import com.github.thebiologist13.listeners.MobDeathEvent;
import com.github.thebiologist13.listeners.MobExplodeEvent;
import com.github.thebiologist13.listeners.MobRegenEvent;
import com.github.thebiologist13.listeners.PlayerLogoutEvent;
import com.github.thebiologist13.listeners.PlayerTargetEvent;
import com.github.thebiologist13.listeners.PotionHitEvent;
import com.github.thebiologist13.listeners.ProjectileFireEvent;
import com.github.thebiologist13.serialization.SPotionEffect;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

/**
 * 
 * CustomSpawners is a plugin for making customizable spawners for Bukkit servers.
 * 
 * Licensed under GNU-GPLv3
 * 
 * @author thebiologist13
 *
 */
public class CustomSpawners extends JavaPlugin {
	
	//Logger
	public Logger log = Logger.getLogger("Minecraft");
	
	//All the spawners in the server.
	public static ConcurrentHashMap<Integer, Spawner> spawners = new ConcurrentHashMap<Integer, Spawner>();
	
	//All of the entity types on the server
	public static ConcurrentHashMap<Integer, SpawnableEntity> entities = new ConcurrentHashMap<Integer, SpawnableEntity>();
	
	//Selected spawners for players
	public static ConcurrentHashMap<Player, Integer> spawnerSelection = new ConcurrentHashMap<Player, Integer>();
	
	//Selected entities for players
	public static ConcurrentHashMap<Player, Integer> entitySelection = new ConcurrentHashMap<Player, Integer>();
	
	//Selected spawner by console
	public static int consoleSpawner = -1;
	
	//Selected entity by console
	public static int consoleEntity = -1;
	
	//Player selection area Point 1
	public static ConcurrentHashMap<Player, Location> selectionPointOne = new ConcurrentHashMap<Player, Location>();
	
	//Player selection area Point 2
	public static ConcurrentHashMap<Player, Location> selectionPointTwo = new ConcurrentHashMap<Player, Location>();
	
	//Default Entity to use
	public static SpawnableEntity defaultEntity = null;
	
	//FileManager
	private FileManager fileManager = null;
	
	//YAML variable
	private FileConfiguration config;
	
	//YAML file variable
	private File configFile;
	
	//LogLevel
	private int logLevel;
	
	//Debug
	public static boolean debug = false;
	
	//WorldGuard
	public WorldGuardPlugin worldGuard = null;
	
	public void onEnable() {
		
		//Config
		config = getCustomConfig();
		
		//Default Entity
		defaultEntity = new SpawnableEntity(EntityType.fromName(config.getString("entities.type", "Pig")), -2);
		defaultEntity.setName("Default");
		
		//FileManager assignment
		fileManager = new FileManager(this);
		
		//Debug
		debug = config.getBoolean("data.debug", false);
		
		//LogLevel
		logLevel = config.getInt("data.logLevel", 2);
		
		//Setup WG
		worldGuard = setupWG();

		if(worldGuard == null) {

			if(logLevel > 0) {
				log.info("Cannot hook into WorldGuard.");
			}

		}
				
		
		//Commands
		SpawnerExecutor se = new SpawnerExecutor(this);
		CustomSpawnersExecutor cse = new CustomSpawnersExecutor(this);
		EntitiesExecutor ee = new EntitiesExecutor(this);
		getCommand("customspawners").setExecutor(cse);
		getCommand("spawners").setExecutor(se);
		getCommand("entities").setExecutor(ee);
		
		//Listeners
		getServer().getPluginManager().registerEvents(new PlayerLogoutEvent(), this);
		getServer().getPluginManager().registerEvents(new MobDamageEvent(this), this);
		getServer().getPluginManager().registerEvents(new MobCombustEvent(), this);
		getServer().getPluginManager().registerEvents(new PlayerTargetEvent(this), this);
		getServer().getPluginManager().registerEvents(new MobDeathEvent(this), this);
		getServer().getPluginManager().registerEvents(new InteractEvent(this), this);
		getServer().getPluginManager().registerEvents(new ExpBottleHitEvent(this), this);
		getServer().getPluginManager().registerEvents(new MobExplodeEvent(this), this);
		getServer().getPluginManager().registerEvents(new MobRegenEvent(this), this);
		getServer().getPluginManager().registerEvents(new PotionHitEvent(this), this);
		getServer().getPluginManager().registerEvents(new ProjectileFireEvent(this), this);
		
		//Load entities from file
		fileManager.loadEntities();
		
		//Load spawners from files
		fileManager.loadSpawners();
		
		//Load spawners and entities from world files
		Iterator<World> worldItr = this.getServer().getWorlds().iterator();
		while(worldItr.hasNext()) {
			World w = worldItr.next();
			
			Iterator<SpawnableEntity> entitiesFromWorld = loadAllEntitiesFromWorld(w).iterator();
			while(entitiesFromWorld.hasNext()) {
				SpawnableEntity e = entitiesFromWorld.next();
				entities.put(getNextEntityId(), e);
			}
			
			Iterator<Spawner> spawnersFromWorld = loadAllSpawnersFromWorld(w).iterator();
			while(spawnersFromWorld.hasNext()) {
				Spawner s = spawnersFromWorld.next();
				boolean sameSpawner = false;
				
				for(Spawner s1 : spawners.values()) {
					if(s1.getLoc().equals(s.getLoc())) {
						sameSpawner = true;
					}
				}
				
				if(sameSpawner) {
					if(logLevel > 1) {
						log.info("Canceled load of spawner from world, same locationa as existing one.");
					}
					continue;
				} else {
					spawners.put(getNextSpawnerId(), s);
				}
			}
		}
		
		/*
		 * Spawning Thread
		 */
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			public void run() {
				
				Iterator<Spawner> spawnerItr = spawners.values().iterator();
				
				while(spawnerItr.hasNext()) {
					Spawner s = spawnerItr.next();
					
					if(!s.getLoc().getChunk().isLoaded())
						continue;
					
					s.tick();
				}
				
			}
			
		}, 20, 1);
		
		/*
		 * Removal Check Thread
		 * This thread verifies that all spawned mobs still exist and removes used explosions. 
		 * For example, if a mob despawned, it will be removed.
		 */
		
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				try {
					Iterator<Spawner> spawnerItr = spawners.values().iterator();
					while(spawnerItr.hasNext()) {
						Spawner s = spawnerItr.next();
						List<Entity> worldEntities = s.getLoc().getWorld().getEntities();
						if(worldEntities == null)
							return;
						Iterator<Entity> entityItr = s.getLoc().getWorld().getEntities().iterator();
						
						if(entityItr == null)
							return;
						
						while(entityItr.hasNext()) {
							Entity nextEntity = entityItr.next();
							
							/*
							 * Removes entities greater than 192 blocks from spawner. This is equal to 12 chunks, 
							 * between Normal (8 chunk radius) and Far (16 chunk radius) render distance.
							 */
							if(nextEntity.getLocation().distance(s.getLoc()) > 192) {
								int entId = nextEntity.getEntityId();
								nextEntity.remove();
								
								if(s.getMobs().containsKey(entId)) {
									s.removeMob(entId);
								}
								
							}
							
						}	
						
					}
					
				} catch(ConcurrentModificationException e) {
					return;
				}
				
			}
			
		}, 20, 20);
		
		/*
		 * Autosave Thread
		 * This thread manages autosaving
		 */
		if(config.getBoolean("data.autosave") && config.getBoolean("data.saveOnClock")) {
			
			int interval = config.getInt("data.interval") * 1200;
			
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

				@Override
				public void run() {
					
					fileManager.autosaveAll();
					
				}
				
			}, 20, interval);
		}
		
		//Enable message
		log.info("CustomSpawners by thebiologist13 has been enabled!");
	}
	
	public void onDisable() {
		
		//Saving Entities
		fileManager.saveEntities();
		//Saving spawners
		fileManager.saveSpawners();
		
		//Stop Tasks
		getServer().getScheduler().cancelTasks(this);
		
		//Disable message
		log.info("CustomSpawners by thebiologist13 has been disabled!");
	}
	
	//Config stuff
	//Credit goes to respective owners.
	public void reloadCustomConfig() {
		if (configFile == null) {
		    configFile = new File(getDataFolder(), "config.yml");
		    
		    if(!configFile.exists()){
		        configFile.getParentFile().mkdirs();
		        copy(getResource("config.yml"), configFile);
		    }
		    
		}
		
		config = YamlConfiguration.loadConfiguration(configFile);
		 
		// Look for defaults in the jar
		InputStream defConfigStream = this.getResource("config.yml");
		if (defConfigStream != null) {
		    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
		    config.options().copyDefaults(true);
		    config.setDefaults(defConfig);
		}
		
	}
	
	public FileConfiguration getCustomConfig() {
		if (config == null) {
	        reloadCustomConfig();
	    }
	    return config;
	}
	
	public void saveCustomConfig() {
		if (config == null || configFile == null) {
		    return;
		}
		try {
			config.save(configFile);
		} catch (IOException ex) {
			log.severe("Could not save config to " + configFile.getPath());
		}
	}
	
	public void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			log.severe("Could not copy config from jar!");
			e.printStackTrace();
		}
	}
	
	//Gets a spawner
	public static Spawner getSpawner(String ref) {
		if(isInteger(ref)) {
			int id = Integer.parseInt(ref);
			Iterator<Integer> spawnerItr = spawners.keySet().iterator();
			
			while(spawnerItr.hasNext()) {
				int currentId = spawnerItr.next();
				
				if(currentId == id) {
					return spawners.get(id);
				}
			}
		} else {
			Iterator<Integer> spawnerItr = spawners.keySet().iterator();
			
			while(spawnerItr.hasNext()) {
				Integer id = spawnerItr.next();
				Spawner s = spawners.get(id);
				String name = s.getName();
				
				if(name == null) {
					return null;
				}
				
				if(name.equalsIgnoreCase(ref)) {
					return s;
				}
			}
		}
		
		return null;
	}
	
	//Gets an entity
	public static SpawnableEntity getEntity(String ref) {
		if(isInteger(ref)) {
			int id = Integer.parseInt(ref);
			
			if(id == -2)
				return defaultEntity;
			
			Iterator<Integer> entityItr = entities.keySet().iterator();
			while(entityItr.hasNext()) {
				int currentId = entityItr.next();
				
				if(currentId == id) {
					return entities.get(id);
				}
			}
			
		} else {
			
			Iterator<Integer> entityItr = entities.keySet().iterator();
			while(entityItr.hasNext()) {
				Integer id = entityItr.next();
				SpawnableEntity s = entities.get(id);
				String name = s.getName();
				
				if(name == null) {
					return null;
				}
				
				if(name.equalsIgnoreCase(ref)) {
					return s;
				}
			}
		}
		
		return null;
	}
	
	//Next available spawner ID
	public int getNextSpawnerId() {
		List<Integer> spawnerIDs = new ArrayList<Integer>();
		
		Iterator<Integer> spawnerItr = spawners.keySet().iterator();
		while(spawnerItr.hasNext()) {
			spawnerIDs.add(spawnerItr.next());
		}
		
		return getNextID(spawnerIDs);
	}
	
	//Next available entity id
	public int getNextEntityId() {
		List<Integer> entityIDs = new ArrayList<Integer>();
		
		Iterator<Integer> entityItr = entities.keySet().iterator();
		while(entityItr.hasNext()) {
			entityIDs.add(entityItr.next());
		}
		
		return getNextID(entityIDs);
	}
	
	//Gets the next available ID number in a list
	public static int getNextID(List<Integer> set) {
		int returnID = 0;
		boolean taken = true;
		
		while(taken) {
			
			if(set.size() == 0) {
				return 0;
			}
			
			for(Integer i : set) {
				if(returnID == i) {
					taken = true;
					break;
				} else {
					taken = false;
				}
			}
			
			if(taken) {
				returnID++;
			}
		}
		
		return returnID;
	}
	
	//Remove a spawner
	public void removeSpawner(Spawner s) {
		if(spawners.containsValue(s)) {
			resetSpawnerSelections(s.getId());
			spawners.remove(s.getId());
		}
	}
	
	//Remove an entity
	public void removeEntity(SpawnableEntity e) {
		if(entities.containsValue(e)) {
			resetEntitySelections(e.getId());
			entities.remove(e.getId());
			for(Spawner s : spawners.values()) {
				s.removeTypeData(e);
			}
		}
	}
	
	//Convenience method for accurately testing if a string can be parsed to an integer.
	public static boolean isInteger(String what) {
		try {
			Integer.parseInt(what);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	
	//Convenience method for accurately testing if a string can be parsed to an double.
	public static boolean isDouble(String what) {
		try {
			Double.parseDouble(what);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	
	//Convenience method for accurately testing if a string can be parsed to an double.
	public static boolean isFloat(String what) {
		try {
			Float.parseFloat(what);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	
	//Gets a string to represent the name of the spawner (String version of ID or name)
	public String getFriendlyName(Spawner s) {
		if(s.getName().isEmpty()) {
			return String.valueOf(s.getId());
		} else {
			return s.getName();
		}
	}
	
	//Gets a string to represent the name of the entity (String version of ID or name)
	public String getFriendlyName(SpawnableEntity e) {
		if(e.getName().isEmpty()) {
			return String.valueOf(e.getId());
		} else {
			return e.getName();
		}
	}
	
	//Converts ticks to MM:SS
	public String convertTicksToTime(int ticks) {
		int minutes = 0;
		float seconds = 0;
		
		if(ticks >= 1200) {
			
			if((ticks % 1200) == 0) {
				minutes = ticks / 1200;
			} else {
				seconds = (ticks % 1200) / 20;
				minutes = (ticks - (ticks % 1200)) / 1200;
			}
			
		} else {
			seconds = ticks / 20;
		}
		
		return minutes + ":" + seconds;
	}
	
	//Gets a ItemStack from string with id and damage value
	public ItemStack getItemStack(String value) {
		//Format should be either <data value:damage value> or <data value>
		int id = 0;
		short damage = 0;
		
		//Version 0.0.5b - Tweaked this so it would register right
		int index = value.indexOf(":");
		
		if(index == -1) {
			index = value.indexOf("-");
		}
		
		if(index == -1) {
			
			String itemId = value.substring(0, value.length());
			
			if(!isInteger(itemId)) 
				return null;
			
			id = Integer.parseInt(itemId);
			
			if(Material.getMaterial(id) == null)
				return null;
			
		} else {
			String itemId = value.substring(0, index);
			String itemDamage = value.substring(index + 1, value.length());
			
			if(!isInteger(itemId) || !isInteger(itemDamage)) 
				return null;
			
			id = Integer.parseInt(itemId);
			damage = (short) Integer.parseInt(itemDamage);
			
			if(Material.getMaterial(id) == null)
				return null;
		}
		
		return new ItemStack(id, 1, damage);
	}
	
	//Gets the proper name of an ItemStack
	public String getItemName(ItemStack item) {
		String name = "";
		
		if(item == null) {
			return "AIR (0)";
		}
		
		if(item.getType() != null) {
			name += item.getType().toString() + " (" + item.getTypeId() + ")";
		} else {
			name += item.getTypeId();
		}
		
		if(item.getDurability() != 0) {
			name += ":" + item.getDurability();
		}
		
		return name;
	}
	
	//Gets an EntityPotionEffect from format <PotionEffectType>_<level>_<minutes>:<seconds>
	public SPotionEffect getPotion(String value) {
		int index1 = value.indexOf("_");
		int index2 = value.indexOf("_", index1 + 1);
		int index3 = value.indexOf(":");
		if(index1 == -1 || index2 == -1 || index3 == -1) {
			value = "REGENERATION_1_0:0";
			index1 = value.indexOf("_");
			index2 = value.indexOf("_", index1 + 1);
			index3 = value.indexOf(":");
		}
		
		PotionEffectType effectType = PotionEffectType.getByName(value.substring(0, index1));
		int effectLevel = Integer.parseInt(value.substring(index1 + 1, index2));
		int minutes = Integer.parseInt(value.substring(index2 + 1, index3));
		int seconds = Integer.parseInt(value.substring(index3 + 1, value.length()));
		int effectDuration = (minutes * 1200) + (seconds * 20);
		
		return new SPotionEffect(effectType, effectDuration,  effectLevel);
	}
	
	//Resets selections if a spawner is removed
	public void resetSpawnerSelections(int id) {
		Iterator<Player> pItr = entitySelection.keySet().iterator();
		
		while(pItr.hasNext()) {
			Player p = pItr.next();
			
			if(spawnerSelection.get(p) == id) {
				p.sendMessage(ChatColor.RED + "Your selected spawner has been removed.");
				spawnerSelection.remove(p);
			}
		}
		
	}
	
	//Resets selections if a SpawnableEntity has been removed
	public void resetEntitySelections(int id) {
		Iterator<Player> pItr = entitySelection.keySet().iterator();
		
		while(pItr.hasNext()) {
			Player p = pItr.next();
			
			if(entitySelection.get(p) == id) {
				p.sendMessage(ChatColor.RED + "Your selected entity has been removed.");
				entitySelection.remove(p);
			}
		}
		
	}
	
	//Removes mobs spawned by a certain spawner
	public synchronized void removeMobs(final Spawner s) { //Called in the removemobs command
		Iterator<Integer> mobs = s.getMobs().keySet().iterator();

		while(mobs.hasNext()) {
			int spawnerMobId = mobs.next();
			Iterator<Entity> livingEntities = s.getLoc().getWorld().getEntities().iterator();
			
			while(livingEntities.hasNext()) {
				Entity l = livingEntities.next();
				
				int entityId = l.getEntityId();

				if(spawnerMobId == entityId) {
					if(l.getPassenger() != null) {
						l.getPassenger().remove();
					}
					l.remove();
					mobs.remove();
					if(DamageController.extraHealthEntities.containsKey(spawnerMobId)) 
						DamageController.extraHealthEntities.remove(spawnerMobId);
				}
				
			}
			
		}
		
		s.getMobs().clear();
		
	}
	
	//Removes a spawner from a mob list when it dies
	public synchronized void removeMob(final Entity e) { //Called when an entity dies. l is the dead entity.
		int entityId = e.getEntityId();

		Iterator<Spawner> itr = CustomSpawners.spawners.values().iterator();

		while(itr.hasNext()) {
			Spawner s = itr.next();
			
			Iterator<Integer> mobs = s.getMobs().keySet().iterator();

			while(mobs.hasNext()) {
				int spawnerMobId = mobs.next();
				
				if(spawnerMobId == entityId) {
					mobs.remove();
					if(DamageController.extraHealthEntities.containsKey(spawnerMobId)) 
						DamageController.extraHealthEntities.remove(spawnerMobId);
				}

			}
			
		}
		
	}

	public FileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	
	public SpawnableEntity getEntityFromSpawner(Entity entity) {
		
		if(entity == null) {
			return null;
		}
		
		int entityId = entity.getEntityId();
		Iterator<Spawner> spawnerItr = spawners.values().iterator();
		
		while(spawnerItr.hasNext()) {
			Spawner s = spawnerItr.next();
			Iterator<Integer> mobItr = s.getMobs().keySet().iterator();

			while(mobItr.hasNext()) {
				int currentMob = mobItr.next();
				
				if(currentMob == entityId) {
					return s.getMobs().get(currentMob);
				}
				
			}
			
		}
		
		return null;
		
	}
	
	public Spawner getSpawnerWithEntity(Entity entity) {
		EntityType type = entity.getType();
		int entityId = entity.getEntityId();
		ArrayList<Spawner> validSpawners = new ArrayList<Spawner>();
		Iterator<Spawner> spawnerItr = spawners.values().iterator();
		
		while(spawnerItr.hasNext()) {
			Spawner s = spawnerItr.next();
			
			for(Integer i : s.getTypeData()) {
				if(CustomSpawners.getEntity(i.toString()).getType().equals(type)) {
					validSpawners.add(s);
					break;
				}
			}
		}
		
		for(Spawner s : validSpawners) {
			Iterator<Integer> mobItr = s.getMobs().keySet().iterator();

			while(mobItr.hasNext()) {
				int currentMob = mobItr.next();
				
				if(currentMob == entityId) {
					return s;
				}
				
			}
			
		}
		
		return null;
		
	}
	
	public void printDebugMessage(String message) {
		if(debug) {
			log.info("[CS_DEBUG] " + message);
		}
		
	}
	
	public void printDebugMessage(String message, Class<?> clazz) {
		if(debug) {
			if(clazz != null) {
				log.info("[CS_DEBUG] " + clazz.getName() + ": " + message);
			} else {
				log.info("[CS_DEBUG] " + message);
			}
			
		}
		
	}
	
	public void sendMessage(CommandSender sender, String message) {
		
		if(sender == null) 
			return;
		
		Player p = null;
		
		if(sender instanceof Player)
			p = (Player) sender;
		
		if(p == null) {
			message = "[CUSTOMSPAWNERS] " + ChatColor.stripColor(message);
			log.info(message);
		} else {
			p.sendMessage(message);
		}
		
	}
	
	public void sendMessage(CommandSender sender, String[] message) {
		
		if(sender == null) 
			return;
		
		Player p = null;
		
		if(sender instanceof Player)
			p = (Player) sender;
		
		if(p == null) {
			
			for(String s : message) {
				s = "[CUSTOMSPAWNERS] " + ChatColor.stripColor(s);
				log.info(s);
			}
			
		} else {
			p.sendMessage(message);
		}
		
	}
	
	//Parses the entity name
	public String parseEntityName(EntityType type) {
		String nameOfType = type.getName();
		
		if(nameOfType == null) {
			return type.toString();
		} else {
			return nameOfType;
		}
		
	}
	
	//Parses the entity type from it's name
	public EntityType parseEntityType(String entityType, boolean hasOverride) {
		EntityType type = null;
		
		List<?> notAllowed = config.getList("mobs.blacklist");
		
		if(entityType.equalsIgnoreCase("irongolem")) {
			
			type = EntityType.IRON_GOLEM;
			
		} else if(entityType.equalsIgnoreCase("mooshroom")) {
			
			type = EntityType.MUSHROOM_COW;
			
		} else if(entityType.equalsIgnoreCase("zombiepigman")) {
			
			type = EntityType.PIG_ZOMBIE;
			
		} else if(entityType.equalsIgnoreCase("magmacube") || entityType.equalsIgnoreCase("fireslime") || entityType.equalsIgnoreCase("firecube")) {
			
			type = EntityType.MAGMA_CUBE;
			
		} else if(entityType.equalsIgnoreCase("snowman") || entityType.equalsIgnoreCase("snowgolem")) {
			
			type = EntityType.SNOWMAN;
			
		} else if(entityType.equalsIgnoreCase("ocelot") || entityType.equalsIgnoreCase("ozelot")) {
			
			type = EntityType.OCELOT;
			
		} else if(entityType.equalsIgnoreCase("arrow")) {
			
			type = EntityType.ARROW;
			
		} else if(entityType.equalsIgnoreCase("snowball")) {
			
			type = EntityType.SNOWBALL;
			
		} else if(entityType.equalsIgnoreCase("falling_block") || entityType.equalsIgnoreCase("fallingblock") ||
				entityType.equalsIgnoreCase("sand") || entityType.equalsIgnoreCase("gravel")) {
			
			type = EntityType.FALLING_BLOCK;
			
		} else if(entityType.equalsIgnoreCase("tnt") || entityType.equalsIgnoreCase("primed_tnt")
				|| entityType.equalsIgnoreCase("primed_tnt")) {
			
			type = EntityType.PRIMED_TNT;
			
		} else if(entityType.equalsIgnoreCase("firecharge") || entityType.equalsIgnoreCase("smallfireball")
				|| entityType.equalsIgnoreCase("fire_charge")|| entityType.equalsIgnoreCase("small_fireball")) {
			
			type = EntityType.SMALL_FIREBALL;
			
		} else if(entityType.equalsIgnoreCase("fireball") || entityType.equalsIgnoreCase("ghastball")
				|| entityType.equalsIgnoreCase("fire_ball")|| entityType.equalsIgnoreCase("ghast_ball")) {
			
			type = EntityType.FIREBALL;
			
		} else if(entityType.equalsIgnoreCase("potion") || entityType.equalsIgnoreCase("splashpotion")
				|| entityType.equalsIgnoreCase("splash_potion")) {
			
			type = EntityType.SPLASH_POTION;
			
		} else if(entityType.equalsIgnoreCase("experience_bottle") || entityType.equalsIgnoreCase("experiencebottle")
				|| entityType.equalsIgnoreCase("xpbottle") || entityType.equalsIgnoreCase("xp_bottle")
				|| entityType.equalsIgnoreCase("expbottle") || entityType.equalsIgnoreCase("exp_bottle")) {
			
			type = EntityType.THROWN_EXP_BOTTLE;
			
		} else if(entityType.equalsIgnoreCase("item") || entityType.equalsIgnoreCase("drop")) {
			
			type = EntityType.DROPPED_ITEM;
			
		} else if(entityType.equalsIgnoreCase("enderpearl") || entityType.equalsIgnoreCase("ender_pearl")
				|| entityType.equalsIgnoreCase("enderball") || entityType.equalsIgnoreCase("ender_ball")) {
			
			type = EntityType.ENDER_PEARL;
			
		} else if(entityType.equalsIgnoreCase("endercrystal") || entityType.equalsIgnoreCase("ender_crystal")
				|| entityType.equalsIgnoreCase("enderdragoncrystal") || entityType.equalsIgnoreCase("enderdragon_crystal")) {
			
			type = EntityType.ENDER_CRYSTAL;
			
		} else if(entityType.equalsIgnoreCase("egg")) {
			
			type = EntityType.EGG;
			
		} else if(entityType.equalsIgnoreCase("wither") || entityType.equalsIgnoreCase("witherboss")
				|| entityType.equalsIgnoreCase("wither_boss")) {
			
			type = EntityType.WITHER;
			
		} else if(entityType.equalsIgnoreCase("witherskeleton") || entityType.equalsIgnoreCase("wither_skeleton")
				|| entityType.equalsIgnoreCase("nether_skeleton")) {
			
			type = EntityType.SKELETON;
			
		} else {
			
			//Try to parse an entity type from input. Null if invalid.
			type = EntityType.fromName(entityType);
			
			if(type == null || !type.isSpawnable()) {
				return null;
			}
			
		}
		
		if(notAllowed.contains(type.getName()) && !hasOverride) {
			return null;
		}
		
		return type;
		
	}
	
	//This saves a Spawner to the world folder. Kind of "cheating" to make it so custom spawners can be recovered from the world.
	public void saveCustomSpawnerToWorld(Spawner data) {
		World w = data.getLoc().getWorld();
		
		String ch = File.separator;
		String worldDir = w.getWorldFolder() + ch + "cs_data" + ch;
		String entityDir = worldDir + ch + "entity";
		String spawnerDir = worldDir + ch + "spawner";
		
		String spawnerPath = spawnerDir + ch + data.getId() + ".dat";
		
		File spawnerFile = new File(spawnerPath);
		
		File entityFilesDir = new File(entityDir);
		
		List<Integer> types = data.getTypeData();
		
		File[] entityFilesList = entityFilesDir.listFiles();
		ArrayList<String> entityFiles = new ArrayList<String>();
		
		for(File f : entityFilesList) {
			entityFiles.add(f.getPath());
		}
		
		Iterator<Integer> tItr = types.iterator();
		while(tItr.hasNext()) {
			int i = tItr.next();
			
			printDebugMessage("Checking if entity files exist");
			
			String fileName = entityDir + ch + i + ".dat";
			
			printDebugMessage("File to check: " + fileName);
			
			if(!entityFiles.contains(fileName)) {
				printDebugMessage("Doesn't contain file. Creating...");
				saveCustomEntityToWorld(getEntity(String.valueOf(i)), new File(fileName));
			}
		}
		
		printDebugMessage("World Folder: " + spawnerFile.getPath());
		
		fileManager.saveSpawner(data, spawnerFile);
	}
	
	public void saveCustomEntityToWorld(SpawnableEntity data, File path) {
		fileManager.saveEntity(data, path);
	}
	
	public List<Spawner> loadAllSpawnersFromWorld(World w) {
		List<Spawner> list = new ArrayList<Spawner>();
		
		String ch = File.separator;
		String worldDir = w.getWorldFolder() + ch + "cs_data" + ch;
		String entityDir = worldDir + ch + "entity";
		String spawnerDir = worldDir + ch + "spawner";
		
		File spawnerFiles = new File(spawnerDir);
		File entityFiles = new File(entityDir);
		
		if(!spawnerFiles.exists())
			spawnerFiles.mkdirs();
		
		if(!entityFiles.exists())
			entityFiles.mkdirs();
		
		for(File spawnerFile : spawnerFiles.listFiles()) {
			
			Spawner s = fileManager.loadSpawner(spawnerFile);
			List<Integer> sEntsAsIDs = s.getTypeData();
			List<SpawnableEntity> sEnts = new ArrayList<SpawnableEntity>();
			ArrayList<SpawnableEntity> containedEntities = new ArrayList<SpawnableEntity>();
			
			for(Integer i : sEntsAsIDs) {
				sEnts.add(getEntity(i.toString()));
			}
			
			for(File f : entityFiles.listFiles()) {
				containedEntities.add(fileManager.loadEntity(f));
			}
			
			if(containedEntities.containsAll(sEnts))
				list.add(s);
			
		}
		
		return list;
	}
	
	public List<SpawnableEntity> loadAllEntitiesFromWorld(World w) {
		List<SpawnableEntity> list = new ArrayList<SpawnableEntity>();
		
		String ch = File.separator;
		String worldDir = w.getWorldFolder() + ch + "cs_data" + ch;
		String entityDir = worldDir + ch + "entity";
		
		File entityFiles = new File(entityDir);
		
		if(!entityFiles.exists())
			entityFiles.mkdirs();
		
		for(File f : entityFiles.listFiles()) {
			list.add(fileManager.loadEntity(f));
		}
		
		return list;
	}
	
	//Gets a spawner from a location
	public Spawner getSpawnerAt(Location loc) {
		Iterator<Spawner> spItr = CustomSpawners.spawners.values().iterator();

		while(spItr.hasNext()) {
			Spawner s = spItr.next();

			if(s.getLoc().equals(loc)) {
				return s;
			}

		}

		return null;

	}
	
	//Sets up WorldGuard
	private WorldGuardPlugin setupWG() {
		Plugin wg = this.getServer().getPluginManager().getPlugin("WorldGuard");
		
		if(wg != null || !(wg instanceof WorldGuardPlugin)) 
			return null;
		
		return (WorldGuardPlugin) wg;
	}
	
}
