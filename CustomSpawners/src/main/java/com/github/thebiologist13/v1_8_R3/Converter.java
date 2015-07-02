package com.github.thebiologist13.v1_8_R3;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagShort;
import net.minecraft.server.v1_8_R3.NBTTagString;
import net.minecraft.server.v1_8_R3.TileEntity;
import net.minecraft.server.v1_8_R3.TileEntityMobSpawner;
import net.minecraft.server.v1_8_R3.WorldServer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.github.thebiologist13.api.IConverter;
import com.github.thebiologist13.api.ISpawnableEntity;
import com.github.thebiologist13.api.ISpawner;
import com.github.thebiologist13.v1_8_R3.CSMinecart;

public class Converter implements IConverter {
	
	/*
	 * Steps to make a mob spawner:
	 * 1. Instantiate a TileEntityMobSpawner or EntityMinecartMobSpawner
	 * 2. Call a(NBTTagCompound) on the new object and pass in entity props.
	 *     - This sets the properties
	 * 3. Call setTileEntity(int i, int j, int k, TileEntity l) on the world.
	 *     - Pass in x,y,z coords for location.
	 * 4. Set the type ID of the block to 52.
	 */
	
	@Override
	public Entity addFallingSpawner(Location loc, ISpawner data0) {
		
		NBTTagCompound data = getNBTWithoutLocations(data0);
		if (data == null || data.isEmpty()){
			return null;
		}
		
		WorldServer w = ((CraftWorld) loc.getWorld()).getHandle();
		
		EntityFallingBlock blk = new EntityFallingBlock(w);
		blk.tileEntityData = data;
		
		w.addEntity(blk);
		
		return blk.getBukkitEntity();
	}

	@Override
	public Entity addSpawnerMinecart(Location loc, ISpawner data0) {
		NBTTagCompound data = getNBTWithoutLocations(data0);
		
		if (data == null || data.isEmpty())
			return null;
		
		WorldServer w = ((CraftWorld) loc.getWorld()).getHandle();
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		
		CSMinecart mc = new CSMinecart(w, x, y, z);
		mc.setNBT(data);
		
		w.addEntity(mc);
		
		return mc.getBukkitEntity();
	}

	@Override
	public void addTileEntity(Block b, ISpawner data0) {
		NBTTagCompound data = getSpawnerNBT(data0);
		
		if (data == null || data.isEmpty())
			return;
		
		int x = b.getX();
		int y = b.getY();
		int z = b.getZ();
		
		BlockPosition BlockPos = new BlockPosition(x, y, z);
		
		
		WorldServer ws = ((CraftWorld) b.getWorld()).getHandle(); //Get the world
		b.setType(Material.MOB_SPAWNER); //Makes the block a mob spawner
		
		TileEntityMobSpawner te = (TileEntityMobSpawner)ws.getTileEntity(BlockPos);
		te.a(data);
	}

	@Override
	public void convert(ISpawner spawner) {
		Block block = spawner.getBlock();

		if (spawner.isConverted()) { //Is NOT CustomSpawners spawner
			block.setType(spawner.getMaterial(), true);
		} else {
			spawner.setActive(false);

			addTileEntity(block, spawner);

		}

		spawner.setConverted(!spawner.isConverted());

	}

	public <T extends Entity> NBTTagCompound getEntityNBT(T entity) {
		NBTTagCompound compound = new NBTTagCompound();
		
		if(!(entity instanceof Entity))
			return null;
		
		net.minecraft.server.v1_8_R3.Entity nms = ((CraftEntity) entity).getHandle();

		nms.e(compound);
		
		return compound;
	}
	
	public NBTTagCompound getNBTWithoutLocations(ISpawner data) {
		NBTBase[] base = getPropertyArray(data);
		NBTTagCompound returnMe = new NBTTagCompound();
		for(NBTBase b : base) {
			if((!b.toString().equals("id")) && (!b.toString().equals("x")) || 
					(!b.toString().equals("y")) || (!b.toString().equals("z"))) {
				returnMe.set(b.toString(), b);
			}	
		}
		return returnMe;
	}
	
	public NBTBase[] getPropertyArray(ISpawner spawner) {
		ArrayList<NBTBase> props = new ArrayList<NBTBase>();
		
		Location spawnLocation = null;

		//XXX This can be changed. Really just needs a single point to spawn to. Should add option to disable.
		if (spawner.isUsingSpawnArea()) 
			spawnLocation = spawner.getAreaPoints()[0]; 
		
		//Location to spawn to when getting NBT
		Location pos = (spawnLocation == null) ? spawner.getLoc() : spawnLocation;
		
		List<ISpawnableEntity> typeData = spawner.getTypesEntities();
		NBTTagCompound[] potentials = new NBTTagCompound[typeData.size()];

		for (int i = 0; i < typeData.size(); i++) {
			NBTTagCompound potentialData = new NBTTagCompound();
			ISpawnableEntity se = typeData.get(i);
			Entity e = spawner.forceSpawnOnLoc(se, pos);
			NBTTagCompound eData = new NBTTagCompound(); 
			
			//The following is related to removing passengers recursively
			Entity curVehicle = e;
			Entity curPassenger = e.getPassenger();
			ArrayList<Entity> entityStack = new ArrayList<Entity>();
			
			while(curPassenger != null) {
				entityStack.add(curPassenger);
				curVehicle = curPassenger;
				curPassenger = curVehicle.getPassenger();
			}
			
			//I get the topmost entity data because the rider tag goes recursively from the top entity.
			Entity top;
			if(entityStack.size() == 0)
				top = e;
			else
				top = entityStack.get(entityStack.size() - 1);
			String name = getEntityName(top.getType());
			eData = getEntityNBT(top); 
			eData.setString("id", name);
			props.add(new NBTTagString("id"));
			
			for(Entity rem : entityStack) {
				rem.remove();
			}
			
			e.remove();
			
			if (eData.isEmpty()) // If empty
				return null;

			if (eData.hasKey("Pos") && spawnLocation == null)
				eData.remove("Pos");

			eData.set("Motion", makeDoubleList(new double[] { se.getXVelocity(e),
					se.getYVelocity(e), se.getZVelocity(e) }));

			potentialData.set("Properties", eData);
			potentialData.setInt("Weight", 1);
			potentialData.setString("Type", name);
			potentials[i] = potentialData;
		}

		NBTTagList potentialNBTList = new NBTTagList();
		for(NBTTagCompound comp : potentials) {
			potentialNBTList.add(comp);
		}
		props.add(potentialNBTList);

		// Spawner Data

		props.add(new NBTTagInt(spawner.getLoc().getBlockX()));
		props.add(new NBTTagInt(spawner.getLoc().getBlockY()));
		props.add(new NBTTagInt(spawner.getLoc().getBlockZ()));
		props.add(new NBTTagShort((short) spawner.getMobsPerSpawn()));
		props.add(new NBTTagShort((short) spawner.getRadius()));
		props.add(new NBTTagShort((short) spawner.getRate()));
		props.add(new NBTTagShort((short) spawner.getRate()));
		props.add(new NBTTagShort((short) (spawner.getRate() + 1)));
		props.add(new NBTTagShort((short) spawner.getMaxMobs()));
		props.add(new NBTTagShort((short) spawner.getMaxPlayerDistance()));
		
		return props.toArray(new NBTBase[props.size()]);
	}
	
	public NBTTagCompound getSpawnerNBT(ISpawner s) {
		NBTTagCompound sData = new NBTTagCompound();
		
		NBTBase[] dataArray = getPropertyArray(s);
		for(NBTBase base : dataArray) {
			sData.set(base.toString(), base);
		}
		
		return sData;
	}
	
	public void setEntityNBT(Entity e, NBTTagCompound n) {
		net.minecraft.server.v1_8_R3.Entity nms = ((CraftEntity) e).getHandle();
		Class<?> entityClass = nms.getClass();
		Method[] methods = entityClass.getMethods();
		for (Method method : methods) {
			if ((method.getName() == "a")
					&& (method.getParameterTypes().length == 1)
					&& (method.getParameterTypes()[0] == NBTTagCompound.class)) {
				try {
					method.setAccessible(true);
					method.invoke(nms, n);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}	
	}

	private String getEntityName(EntityType type) {
		
		String id = String.valueOf(type);

		if (id == null || id.isEmpty()) {
			switch (type) {
			case SPLASH_POTION:
				id = "ThrownPotion";
				break;
			case EGG:
				id = "Egg";
				break;
			default:
				return null;
			}
		}
		return id;
	}
	
	private NBTTagList makeDoubleList(double[] d0) {
		NBTTagList list = new NBTTagList();
		int i = d0.length;

		for (int j = 0; j < i; j++) {
			double d = d0[j];

			list.add(new NBTTagDouble(d));
		}

		return list;

	}

	public boolean isTileEntity(Block b) {
		CraftWorld w = (CraftWorld) b.getWorld();
		TileEntity e = w.getTileEntityAt(b.getX(), b.getY(), b.getZ());

		if (e != null)
			return true;

		return false;
	}
	
}
