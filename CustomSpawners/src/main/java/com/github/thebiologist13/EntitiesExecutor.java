package com.github.thebiologist13;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.thebiologist13.api.IObject;
import com.github.thebiologist13.commands.SelectionParser;
import com.github.thebiologist13.commands.entities.*;
import com.github.thebiologist13.commands.groups.ParentChildException;
import com.github.thebiologist13.commands.groups.TypeException;

/**
 * This executes commands related to the /entities command.
 * 
 * @author thebiologist13
 */
public class EntitiesExecutor extends Executor implements CommandExecutor {

	public EntitiesExecutor(CustomSpawners plugin) {
		super(plugin);

		EntityCommand effects = new EntityActiveEffectsCommand(plugin, "customspawners.entities.effects");
		EntityCommand age = new EntityAgeCommand(plugin, "customspawners.entities.setage");
		EntityCommand air = new EntityAirCommand(plugin, "customspawners.entities.setair");
		EntityCommand angry = new EntityAngryCommand(plugin, "customspawners.entities.setangry");
		EntityCommand cat = new EntityCatTypeCommand(plugin, "customspawners.entities.setcattype");
		EntityCommand charged = new EntityChargedCommand(plugin, "customspawners.entities.setcharged");
		EntityCommand color = new EntityColorCommand(plugin, "customspawners.entities.setcolor");
		EntityCommand create = new EntityCreateCommand(plugin, "customspawners.entities.create");
		EntityCommand ender = new EntityEnderBlockCommand(plugin, "customspawners.entities.setenderblock");
		EntityCommand health = new EntityHealthCommand(plugin, "customspawners.entities.sethealth");
		EntityCommand info = new EntityInfoCommand(plugin, "customspawners.entities.info");
		EntityCommand jockey = new EntityJockeyCommand(plugin, "customspawners.entities.setjockey");
		EntityCommand name = new EntityNameCommand(plugin, "customspawners.entities.setname");
		EntityCommand profession = new EntityProfessionCommand(plugin, "customspawners.entities.setprofession");
		EntityCommand remove = new EntityRemoveCommand(plugin, "customspawners.entities.remove");
		EntityCommand saddled = new EntitySaddledCommand(plugin, "customspawners.entities.setsaddled");
		EntityCommand select = new EntitySelectCommand(plugin, "customspawners.entities.select");
		EntityCommand type = new EntitySetTypeCommand(plugin, "customspawners.entities.settype");
		EntityCommand sitting = new EntitySittingCommand(plugin, "customspawners.entities.sitting");
		EntityCommand slime = new EntitySlimeSizeCommand(plugin, "customspawners.entities.setslimesize");
		EntityCommand tamed = new EntityTamedCommand(plugin, "customspawners.entities.settamed");
		EntityCommand vector = new EntityVelocityCommand(plugin, "customspawners.entities.setvelocity");
		EntityCommand list = new EntityListAllCommand(plugin, "customspawners.entities.listall");
		EntityCommand passive = new EntityPassiveCommand(plugin, "customspawners.entities.setpassive");
		EntityCommand fire = new EntityFireTicksCommand(plugin, "customspawners.entities.setfireticks");
		EntityCommand blacklist = new EntityBlackListCommand(plugin, "customspawners.entities.blacklist");
		EntityCommand whitelist = new EntityWhiteListCommand(plugin, "customspawners.entities.whitelist");
		EntityCommand itemlist = new EntityItemListCommand(plugin, "customspawners.entities.itemlist");
		EntityCommand damage = new EntityDamageCommand(plugin, "customspawners.entities.damage");
		EntityCommand drops = new EntityDropsCommand(plugin, "customspawners.entities.drops");
		EntityCommand experience = new EntityExpCommand(plugin, "customspawners.entities.setexp");
		EntityCommand fuse = new EntityFuseCommand(plugin, "customspawners.entities.setfuseticks");
		EntityCommand incendiary = new EntityIncendiaryCommand(plugin, "customspawners.entities.setincendiary");
		EntityCommand yield = new EntityYieldCommand(plugin, "customspawners.entities.setyield");
		EntityCommand itemType = new EntityItemTypeCommand(plugin, "customspawners.entities.setitemtype");
		EntityCommand potionType = new EntityPotionTypeCommand(plugin, "customspawners.entities.setpotiontype");
		EntityCommand invulnerable = new EntityInvulnerableCommand(plugin, "customspawners.entities.setinvulnerable");
		EntityCommand inventory = new EntityInventoryCommand(plugin, "customspawners.entities.inventory");
		EntityCommand wither = new EntityWitherCommand(plugin, "customspawners.entities.setwither");
		EntityCommand villager = new EntityVillagerCommand(plugin, "customspawners.entities.setvillager");
		EntityCommand modify = new EntityModifierCommand(plugin, "customspawners.entities.modifiers");
		EntityCommand rider = new EntityRiderCommand(plugin, "customspawners.entities.rider");
		EntityCommand minecart = new EntityCartSpeedCommand(plugin, "customspawners.entities.minecartspeed");
		EntityCommand spawn = new EntitySpawnCommand(plugin, "customspawners.entities.spawn");
		EntityCommand clone = new EntityCloneCommand(plugin, "customspawners.entities.clone"); //TODO WIKI: Add entity clone.
		EntityCommand horse = new EntityHorseCommand(plugin, "customspawners.entities.horses"); //TODO WIKI: Add horse stuff.
		EntityCommand att = new EntityAttributeCommand(plugin, "customspawners.entities.attributes"); //TODO WIKI: Add attributes.

		create.setNeedsObject(false);
		select.setNeedsObject(false);
		list.setNeedsObject(false);

		addCommand("addeffect", effects, new String[] {
				"addeffects",
				"neweffect",
				"neweffects"
		});
		addCommand("seteffect", effects, new String[] {
				"seteffects"
		});
		addCommand("cleareffect", effects, new String[] {
				"cleareffects",
				"noeffect",
				"noeffects"
		});
		addCommand("setage", age, new String[] {
				"age",
				"howold",
				"old"
		});
		addCommand("setair", air, new String[] {
				"air",
				"breath",
				"oxygen"
		});
		addCommand("setangry", angry, new String[] {
				"angry",
				"mad",
				"ticked"
		});
		addCommand("setuseblacklist", blacklist, new String[] {
				"setusingblacklist",
				"useblacklist",
				"usingblacklist",
				"useblack"
		});
		addCommand("setblacklist", blacklist, new String[] {
				"blacklist",
				"black",
				"immuneto"
		});
		addCommand("addblacklist", blacklist, new String[] {
				"addblacklistitem",
				"addblack",
				"addb",
				"addimmuneto"
		});
		addCommand("clearblacklist", blacklist, new String[] {
				"clearblack",
				"noblack",
				"clearimmuneto"
		});
		addCommand("setcattype", cat, new String[] {
				"setcat",
				"cat",
				"cattype"
		});
		addCommand("setcharged", charged, new String[] {
				"setcharge",
				"charge",
				"crepp"
		});
		addCommand("setcolor", color, new String[] {
				"color"
		});
		addCommand("createentity", create, new String[] {
				"create",
				"new",
				"makenew",
				"summon"
		});
		addCommand("setcustomdamage", damage, new String[] {
				"usedamage",
				"setusingcustomdamage",
				"usingcustomdamage",
				"usecustomdamage",
				"setusedamage",
				"setusecustomdamage",
				"norrismode"
		});
		addCommand("setdamageamount", damage, new String[] {
				"setdamage",
				"damage",
				"damageamount",
				"punishvalue",
				"ownedfactor",
				"kdquotient"
		});
		addCommand("adddrop", drops, new String[] {
				"adddrops",
				"addd"
		});
		addCommand("setdrop", drops, new String[] {
				"setdrops",
				"setd",
				"dropthis"
		});
		addCommand("cleardrop", drops, new String[] {
				"cleardrops",
				"cleard",
				"nodrop",
				"nodrops"
		});
		addCommand("usedrop", drops, new String[] {
				"usedrops",
				"setusingdrops",
				"setusingcustomdrops",
				"dropstuff"
		});
		addCommand("setendermanblock", ender, new String[] {
				"setenderblock",
				"endermanblock",
				"enderblock",
				"endermanholds"
		});
		addCommand("setdroppedexperience", experience, new String[] {
				"setdroppedexp",
				"setdroppedxp",
				"setexperience",
				"setexp",
				"setxp",
				"exp",
				"xp"
		});
		addCommand("setfireticks", fire, new String[] {
				"setfire",
				"fireticks",
				"fire",
				"setfirelength",
				"firelength"
		});
		addCommand("setfuseticks", fuse, new String[] {
				"setfuse",
				"fuseticks",
				"fuse",
				"setfuselength",
				"fuselength"
		});
		addCommand("sethealth", health, new String[] {
				"health",
				"sethp",
				"hp",
				"setlifepoints",
				"setlife",
				"setlp",
				"lp"
		});
		addCommand("setincendiary", incendiary, new String[] {
				"incendiary",
				"setnapalm",
				"napalm",
				"setfireexplosion",
				"fireexplosion",
				"setfirebomb",
				"firebomb"
		});
		addCommand("info", info, new String[] {
				"getinfo",
				"geti",
				"i"
		});
		addCommand("clearinventory", inventory, new String[] {
				"clearinv",
				"noinventory",
				"noinv"
		});
		addCommand("addinventoryitem", inventory, new String[] {
				"addinvitem",
				"addinv",
				"additem"
		});
		addCommand("setinventory", inventory, new String[] {
				"setinv"
		});
		addCommand("sethand", inventory, new String[] {
				"hand",
				"setholding",
				"holding",
				"hold",
				"setequippeditem",
				"setequipped",
				"setiteminhand"
		});
		addCommand("sethelmet", inventory, new String[] {
				"helmet",
				"sethat",
				"hat",
				"sethead",
				"head",
				"sethelm",
				"helm"
		});
		addCommand("setchest", inventory, new String[] {
				"chest",
				"setshirt",
				"shirt",
				"settorso",
				"torso",
				"setchestplate",
				"chestplate"
		});
		addCommand("setleggings", inventory, new String[] {
				"leggings",
				"setlegs",
				"legs",
				"setpants",
				"pants"
		});
		addCommand("setboots", inventory, new String[] {
				"boots",
				"setshoes",
				"shoes",
				"setfeet",
				"feet"
		});
		addCommand("setinvulnerable", invulnerable, new String[] {
				"invulnerable",
				"setinvul",
				"invul",
				"setinvincible",
				"invincible",
				"nohurt"
		});
		addCommand("additemdamage", itemlist, new String[] {
				"additemlist"
		});
		addCommand("setitemdamage", itemlist, new String[] {
				"setitemlist",
				"itemlist"
		});
		addCommand("clearitemdamage", itemlist, new String[] {
				"clearitemlist"
		});
		addCommand("setitemtype", itemType, new String[] {
				"itemtype",
				"item"
		});
		addCommand("setjockey", jockey, new String[] {
				"jockey",
				"spiderjockey",
				"skeletonjockey",
				"cowboyskeleton",
				"ghostrider"
		});
		addCommand("listallentities", list, new String[] {
				"listall",
				"list",
				"showentities",
				"displayentities",
				"show",
				"display"
		});
		addCommand("setname", name, new String[] {
				"name",
				"callit"
		});
		addCommand("showname", name, new String[] {
				"displayname",
				"overheadname"
		});
		addCommand("setpassive", passive, new String[] {
				"passive",
				"provokeattack",
				"noattack"
		});
		addCommand("setpotiontype", potionType, new String[] {
				"potiontype",
				"potioneffect",
				"setpotion",
				"potion"
		});
		addCommand("setvillagerprofession", profession, new String[] {
				"setprofession",
				"profession",
				"setvillagertype",
				"setvillager",
				"villagertype",
				"villager",
				"villagerjob",
				"job"
		});
		addCommand("removeentity", remove, new String[] {
				"remove",
				"rem",
				"deleteentity",
				"delete",
				"del"
		});
		addCommand("setsaddled", saddled, new String[] {
				"saddled",
				"setsaddle",
				"saddle",
				"cowboymode"
		});
		addCommand("selectentity", select, new String[] {
				"select",
				"sel",
				"choose"
		});
		addCommand("setentitytype", type, new String[] {
				"entitytype",
				"settype",
				"type",
				"setentity",
				"setmobtype",
				"setmob",
				"mobtype",
				"mob",
				"setmonstertype",
				"setmonster",
				"monster",
				"setanimaltype",
				"setanimal",
				"animal"
		});
		addCommand("setsitting", sitting, new String[] {
				"sitting",
				"sit"
		});
		addCommand("setslimesize", slime, new String[] {
				"slimesize",
				"slime",
				"setslime",
				"setsize",
				"size",
				"howbig"
		});
		addCommand("settamed", tamed, new String[] {
				"tamed",
				"settame",
				"tame",
				"setdomesticated",
				"domesticated",
				"domesticate"
		});
		addCommand("setvelocity", vector, new String[] {
				"velocity",
				"velo",
				"setvector",
				"vector",
				"vec",
				"setdirection",
				"direction",
				"dir"
		});
		addCommand("setvelocity2", vector, new String[] {
				"velocity2",
				"velo2",
				"setvector2",
				"vector2",
				"vec2",
				"setdirection2",
				"direction2",
				"dir2"
		});
		addCommand("setusewhitelist", whitelist, new String[] {
				"setusingwhitelist",
				"usewhitelist",
				"usingwhitelist",
				"usewhite"
		});
		addCommand("setwhitelist", whitelist, new String[] {
				"whitelist",
				"white",
				"notimmuneto"
		});
		addCommand("addwhitelist", whitelist, new String[] {
				"addwhitelistitem",
				"addwhite",
				"addb",
				"addnotimmuneto"
		});
		addCommand("clearwhitelist", whitelist, new String[] {
				"clearwhite",
				"nowhite",
				"clearnotimmuneto"
		});
		addCommand("setzombievillager", villager, new String[] {
				"zombievillager",
				"setzombienpc",
				"zombienpc",
				"setinfected",
				"infected",
				"setzombie",
				"zombie",
				"zombify",
				"setvillagerzombie",
				"villagerzombie"
		});
		addCommand("setwither", wither, new String[] {
				"wither",
				"setwitherskeleton",
				"witherskeleton",
				"setwitherskele",
				"witherskele"
		});
		addCommand("setyield", yield, new String[] {
				"yield",
				"setexplosivepower",
				"explosivepower",
				"setexpower",
				"expower"
		});
		addCommand("setmodifier", modify, new String[] {
				"modifier",
				"setmodifiers",
				"modifiers",
				"setdynamicproperty",
				"dynamicproperty",
				"setdynamic",
				"mod",
				"setmod",
				"modify"
		});
		addCommand("addmodifier", modify, new String[] {
				"adddynamicproperty",
				"adddynamic",
				"addmod",
				"addmodifiers",
				"addmods"
		});
		addCommand("clearmodifier", modify, new String[] {
				"nomodifiers",
				"nomodifier",
				"clearmodifiers",
				"clearmods",
				"clearmod",
				"nomods",
				"nomod"
		});
		addCommand("setrider", rider, new String[] {
				"rider",
				"setriding",
				"riding"
		});
		addCommand("setminecartspeed", minecart, new String[] {
				"minecartspeed",
				"speed",
				"setspeed",
				"minecart"
		});
		addCommand("spawn", spawn, new String[] {
				"spawnmob",
				"spawnentity",
				"spawnmobs",
				"spawnentites",
				"conjure",
				"summon"
		});
		addCommand("clone", clone, new String[] {
				"cloneentity",
				"copy",
				"copyentity"
		});
		addCommand("horsecolor", horse, new String[] {
				"hcolor",
				"sethorsecolor",
				"sethcolor"
		});
		addCommand("horsetype", horse, new String[] {
				"htype",
				"sethorsetype",
				"sethtype"
		});
		addCommand("horsevariant", horse, new String[] {
				"hvariant",
				"hvar",
				"sethvariant",
				"sethvar",
				"sethorsevariant"
		});
		addCommand("chests", horse, new String[] {
				"horsechests",
				"donkeychests",
				"horsechest",
				"donkeychest"
		});
		addCommand("clearattributes", att, new String[] {
				"clearattribute",
				"clearatts",
				"clearatt",
				"noatts",
				"noatt"
		});
		addCommand("addattribute", att, new String[] {
				"addatt",
				"adda"
		});
		addCommand("setattribute", att, new String[] {
				"setattributes",
				"setatt",
				"setatts",
				"seta"
		});
	}

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {

		final String INVALID_PARAMS = ChatColor.RED + "You entered invalid parameters.";

		if(arg1.getName().equalsIgnoreCase("entities")) {

			if(arg3.length < 1) {
				PLUGIN.sendMessage(arg0, ChatColor.GREEN + "This is the command used for entity " +
						"modification. See the wiki for commands!");
				return true;
			}

			IObject entityRef = null;
			String sub = arg3[0].toLowerCase();
			String objId = "";
			String[] params;

			if(arg3.length > 1)
				objId = arg3[1];

			if(arg3.length == 0) {
				PLUGIN.sendMessage(arg0, ChatColor.RED + "You must enter a command.");
				return true;
			}

			EntityCommand cmd = (EntityCommand) super.getCommand(sub);

			if(cmd == null) {
				PLUGIN.sendMessage(arg0, ChatColor.RED + "\"" + sub + "\" is not valid for the entities command.");
				return true;
			}

			sub = cmd.getCommand(sub); //Aliases

			if(!cmd.permissible(arg0, null)) {
				PLUGIN.sendMessage(arg0, cmd.NO_PERMISSION);
				return true;
			}

			try {
				entityRef = SelectionParser.getEntitySelection(objId, arg0);
			} catch (ParentChildException e) {
				PLUGIN.sendMessage(arg0, cmd.PARENT_CHILD);
				return true;
			} catch (TypeException e) {
				PLUGIN.sendMessage(arg0, cmd.NOT_SAME_TYPE);
				return true;
			}

			if(!cmd.permissibleForObject(arg0, null, entityRef)) {
				PLUGIN.sendMessage(arg0, cmd.NO_PERMISSION);
				return true;
			}

			if(cmd.needsObject()) {
				if(arg0 instanceof Player) {
					Player p = (Player) arg0;

					if(!CustomSpawners.entitySelection.containsKey(p)
							|| objId.startsWith("t:") || objId.startsWith("g:")) {
						params = makeParams(arg3, 2);
					} else {
						params = makeParams(arg3, 1);
					}
				} else {
					if(CustomSpawners.consoleEntity == -1
							|| objId.startsWith("t:") || objId.startsWith("g:")) {
						params = makeParams(arg3, 2);
					} else {
						params = makeParams(arg3, 1);
					}
				}

				if(entityRef == null) {
					PLUGIN.sendMessage(arg0, cmd.NO_ENTITY);
					return true;
				}
			} else {
				params = makeParams(arg3, 1);

				try {
					cmd.run(null, arg0, sub, params);
				} catch(Exception e) {
					cmd.crash(e, arg0);
				}
				
				return true;
			}

			try {
				if(entityRef instanceof SpawnableEntity) {
					SpawnableEntity entity = (SpawnableEntity) entityRef;

					cmd.run(entity, arg0, sub, params);
				} else if(entityRef instanceof Group) {
					Group group = (Group) entityRef;

					//Lag warning for modifying 50 or more at one time.
					if(group.getGroup().size() >= 50) {
						if(cmd.warnLag(arg0))
							return true;
					}

					runGroup(cmd, group, arg0, sub, params);
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				PLUGIN.sendMessage(arg0, INVALID_PARAMS);
				return true;
			} catch(Exception e) {
				cmd.crash(e, arg0);
				return true;
			}

			if(CONFIG.getBoolean("data.autosave") && CONFIG.getBoolean("data.saveOnCommand")) {
				PLUGIN.getFileManager().autosaveAll();
			}

			return true;
		}

		return false;

	}

	private void runGroup(EntityCommand cmd, Group g, CommandSender sender, String sub, String[] args) {
		for(IObject obj : g.getGroup().keySet()) {
			if(obj instanceof SpawnableEntity) {
				SpawnableEntity entity = (SpawnableEntity) obj;

				cmd.run(entity, sender, sub, args);
			} else if(obj instanceof Group) {
				Group g0 = (Group) obj;

				runGroup(cmd, g0, sender, sub, args);
			}
		}
	}

}
