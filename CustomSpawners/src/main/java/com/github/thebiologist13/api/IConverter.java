package com.github.thebiologist13.api;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public abstract interface IConverter {
	
	public void addTileEntity(Block b, ISpawner data);

	public Entity addSpawnerMinecart(Location loc, ISpawner data);
	
	public abstract Entity addFallingSpawner(Location loc, ISpawner data);
	
	public void convert(ISpawner spawner);
	
}
