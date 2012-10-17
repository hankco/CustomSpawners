package com.github.thebiologist13.commands.spawners;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.TileEntityMobSpawner;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import com.github.thebiologist13.CustomSpawners;
import com.github.thebiologist13.Spawner;
import com.github.thebiologist13.commands.SpawnerCommand;

public class ConvertCommand extends SpawnerCommand {

	public ConvertCommand(CustomSpawners plugin) {
		super(plugin);
	}

	@Override
	public void run(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		
		/*
		 * Command Usage -> /css convert [id]
		 *   Arg#:           CMD    0      1          
		 *   Purposes:
		 *     CMD: Command itself.
		 *     0: The base command signaling this class.
		 *     1: ID of custom spawner to convert if none is selected.
		 *     2*: Type of conversion
		 *       full: All available properties converted. Includes surrounding mechanisms for light.
		 *       block: All available properties converted.
		 *       part: Basic properties only (type, delay, range). Includes copy of CustomSpawners spawner.
		 *       base: Basic properties only (type, delay, range).
		 *       
		 *   *Unimplemented
		 */
		
		//Player
		Player p = null;
		//Spawner
		Spawner s = null;
		//Tile Entity
		TileEntityMobSpawner tems = null;
		//Data
		NBTTagCompound data = new NBTTagCompound();
		//CraftWorld
		CraftWorld cw = null;
		
		if(arg0 instanceof Player) {
			p = (Player) arg0;
		}
		
		if(p == null) {
			
			if(CustomSpawners.consoleSpawner != -1 && arg3.length == 1) {
				
				s = plugin.getSpawner(String.valueOf(CustomSpawners.consoleSpawner));
				
			} else if(arg3.length == 1) {
				
				plugin.sendMessage(arg0, NEEDS_SELECTION);
				return;
				
			} else if(arg3.length == 2) {
				
				s = plugin.getSpawner(arg3[1]);

				if(s == null) {
					plugin.sendMessage(arg0, NO_ID);
					return;
				}
				
			} else {
				
				plugin.sendMessage(arg0, GENERAL_ERROR);
				return;
				
			}
			
			cw = (CraftWorld) s.getLoc().getWorld();
			TileEntity te = cw.getTileEntityAt(s.getLoc().getBlockX(), s.getLoc().getBlockY(), s.getLoc().getBlockZ());
			
			if(s.isConverted()) { //If converting back to a CustomSpawner
				
				
				
			} else { //If converting to a mob spawner block
				
				if(!(te instanceof TileEntityMobSpawner)) {
					Block blk = cw.getBlockAt(s.getLoc());
					blk.setTypeIdAndData(52, (byte) 0, true);
					te = cw.getTileEntityAt(s.getLoc().getBlockX(), s.getLoc().getBlockY(), s.getLoc().getBlockZ());
				}
				
				tems = (TileEntityMobSpawner) te;
				data = plugin.getSpawnerNBT(s); //Gets the converted data for a spawner
				tems.a(data); //Sets the data to the spawner.
				
			}
			
			s.setConverted(!s.isConverted());
			
		} else {
			
		}
		
	}

}
