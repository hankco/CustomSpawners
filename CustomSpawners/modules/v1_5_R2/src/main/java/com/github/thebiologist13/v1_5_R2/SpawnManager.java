package com.github.thebiologist13.v1_5_R2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_5_R2.EntityEnderPearl;
import net.minecraft.server.v1_5_R2.EntityFallingBlock;
import net.minecraft.server.v1_5_R2.EntityLiving;
import net.minecraft.server.v1_5_R2.EntityMinecartAbstract;
import net.minecraft.server.v1_5_R2.EntityMinecartMobSpawner;
import net.minecraft.server.v1_5_R2.EntityPotion;
import net.minecraft.server.v1_5_R2.MobSpawnerAbstract;
import net.minecraft.server.v1_5_R2.NBTBase;
import net.minecraft.server.v1_5_R2.NBTTagCompound;
import net.minecraft.server.v1_5_R2.NBTTagList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_5_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftFallingSand;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftMinecart;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.PoweredMinecart;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import com.github.thebiologist13.api.AbstractSpawnManager;
import com.github.thebiologist13.api.ISpawnableEntity;
import com.github.thebiologist13.api.ISpawner;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Monster;

@SuppressWarnings("deprecation")
public class SpawnManager extends AbstractSpawnManager {

	public SpawnManager(ISpawner spawner) {
		super(spawner);
	}

	@Override
	public void forceSpawn() {
		mainSpawn(spawner.randType(), true);
	}

	@Override
	public void forceSpawn(ISpawnableEntity entity) {
		mainSpawn(entity, true);
	}

	@Override
	public boolean isSolidBlock(Block block){
		return block.getTypeId() != 0 && net.minecraft.server.v1_5_R2.Block.byId[block.getTypeId()].material.isSolid();
	}

	@Override
	public void mainSpawn(ISpawnableEntity spawnType, boolean ignoreLight) {

		//Loop to spawn until the mobs per spawn is reached
		for(int i = 0; i < spawner.getMobsPerSpawn(); i++) {

			if(spawner.getMobsIds().size() == spawner.getMaxMobs())
				return;

			Location spLoc = spawner.getLoc();

			if(!spLoc.getChunk().isLoaded())
				return;

			Entity e;

			if(spawnType.hasAllDimensions()) {
				Location spawnLocation = getSpawningLocation(spawnType, spawnType.requiresBlockBelow(), ignoreLight,
						spawnType.getHeight(), spawnType.getWidth(), spawnType.getLength());

				if(spawnLocation == null)
					continue;

				if(!spawnLocation.getChunk().isLoaded())
					continue;

				e = spawnTheEntity(spawnType, spawnLocation);

				//				net.minecraft.server.v1_5_R2.Entity nmEntity = ((CraftEntity) e).getHandle();
				//
				//				AxisAlignedBB bb = nmEntity.boundingBox;
				//
				//				spawnType.setHeight((float) (bb.d - bb.a));
				//				spawnType.setWidth((float) (bb.e - bb.b));
				//				spawnType.setLength((float) (bb.f - bb.c));
				//				spawnType.setBlockBelow(getBlockBelowFromEntity(e));
			} else { //If it doesn't have dimension data

				e = spawnTheEntity(spawnType, spLoc);

				net.minecraft.server.v1_5_R2.Entity nmEntity = ((CraftEntity) e).getHandle();

				spawnType.setHeight(nmEntity.height);
				spawnType.setWidth(nmEntity.width);
				spawnType.setLength(nmEntity.length);
				spawnType.setBlockBelow(getBlockBelowFromEntity(e));

				Location spawnLocation = getSpawningLocation(spawnType, getBlockBelowFromEntity(e),  ignoreLight,
						nmEntity.height, nmEntity.width, nmEntity.length);

				if(spawnLocation == null)
					continue;

				if(!spawnLocation.getChunk().isLoaded())
					continue;

				e.teleport(spawnLocation);
			}

			if(e != null) {

				assignMobProps(e, spawnType);
				spawner.addMob(e.getUniqueId(), spawnType);

				Heroes h = getHeroes();

				if(h != null && e instanceof LivingEntity) {
					LivingEntity le = (LivingEntity) e;
					h.getCharacterManager().addMonster(new Monster(h, le));
				}

			}

		}

	}

	@Override
	public void setCustomName(LivingEntity entity, ISpawnableEntity data) {
		CraftLivingEntity le = (CraftLivingEntity) entity;
		le.setCustomName(data.getName());
		le.setCustomNameVisible(true);
	}

	//Sets unimplemented data
	@Override
	public void setNBT(Entity entity, ISpawnableEntity data) {
		Converter nbt = new Converter();
		NBTTagCompound nbtComp = nbt.getEntityNBT(entity);

		if(entity instanceof Creeper) {
			byte rad = (byte) Math.round(data.getYield(entity));
			nbtComp.setByte("ExplosionRadius", rad);
			nbtComp.setShort("Fuse", (short) data.getFuseTicks(entity));
		}

		if(entity instanceof Ghast) {
			int rad = Math.round(data.getYield(entity));
			nbtComp.setInt("ExplosionPower", rad);
		}

		if(entity instanceof Minecart) {

			EntityMinecartAbstract abs = ((CraftMinecart) entity).getHandle();

			if(!data.getItemType().getType().equals(Material.AIR)) {
				int id = data.getItemType().getTypeId();
				int dur = (int) data.getItemType().getDurability();
				abs.a(true); //Show tile
				abs.k(id); //Tile ID
				abs.l(dur); //Tile durability
			}

			if(data.getSpawnerData() != null && entity instanceof SpawnerMinecart) {
				EntityMinecartMobSpawner cart = (EntityMinecartMobSpawner) abs;

				NBTTagCompound spawnerData = new NBTTagCompound();
				NBTBase[] props = nbt.getPropertyArray(data.getSpawnerData());
				for(NBTBase base : props) {
					if(base.getName().equals("id") || base.getName().equals("x") || 
							base.getName().equals("y") || base.getName().equals("z")) {
						continue;
					}
					nbtComp.set(base.getName(), base);
					spawnerData.set(base.getName(), base);
				}

				try {
					Field theSpawner = cart.getClass().getDeclaredField("a");
					theSpawner.setAccessible(true);
					MobSpawnerAbstract spawner = (MobSpawnerAbstract) theSpawner.get(cart);
					spawner.a(spawnerData);
				} catch(Exception e) {
					e.printStackTrace();
				}

			}

			return;

		} 

		if(entity instanceof FallingBlock) {
			if(data.getSpawnerData() != null) {
				EntityFallingBlock block = ((CraftFallingSand) entity).getHandle();
				nbtComp.setCompound("TileEntityData", nbt.getSpawnerNBT((ISpawner) data.getSpawnerData()));
				try {
					Class<?> entityClass = block.getClass();
					Method[] methods = entityClass.getDeclaredMethods();
					for (Method method : methods) {
						if ((method.getName() == "a")
								&& (method.getParameterTypes().length == 1)
								&& (method.getParameterTypes()[0] == NBTTagCompound.class)) {
							method.setAccessible(true);
							method.invoke(block, nbtComp);
						}
					}	
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}

		nbt.setEntityNBT(entity, nbtComp);

	}

	@Override
	public void spawn() {

		boolean hasPower = spawner.getBlock().isBlockPowered() 
				|| spawner.getBlock().isBlockIndirectlyPowered();

		/*
		 * This block checks if the conditions are met to spawn mobs
		 */
		if(spawner.isRedstoneTriggered() && !hasPower) {
			return;
		} else if(!isPlayerNearby()) {
			return;
		} else if(spawner.getMobsIds().size() > spawner.getMaxMobs()) {
			return;
		} else if(!((getLight() <= spawner.getMaxLightLevel()) && (getLight() >= spawner.getMinLightLevel()))) {
			return;
		}

		mainSpawn(spawner.randType(), false);
	}

	@Override
	public Entity spawnMobAt(ISpawnableEntity entity, Location location) {
		Entity e = spawnTheEntity(entity, location);
		assignMobProps(e, entity);
		return e;
	}

	@Override
	public Entity spawnTheEntity(ISpawnableEntity spawnType, Location spawnLocation) {

		Entity e;

		if(spawnType.getType().equals(EntityType.DROPPED_ITEM)) {
			return spawnLocation.getWorld().dropItem(spawnLocation, spawnType.getItemType());
		} else if(spawnType.getType().equals(EntityType.FALLING_BLOCK)) {
			return spawnLocation.getWorld().spawnFallingBlock(spawnLocation, spawnType.getItemType().getType(), (byte) spawnType.getItemType().getDurability());
		} else if(spawnType.getType().equals(EntityType.SPLASH_POTION)) {
			World world = spawnLocation.getWorld();
			PotionEffect effect = spawnType.getPotionEffectBukkit();
			PotionType type = PotionType.getByEffect(effect.getType());
			Potion p = new Potion(type);
			int data = p.toDamageValue();

			net.minecraft.server.v1_5_R2.World nmsWorld = ((CraftWorld) world).getHandle();
			EntityPotion ent = new EntityPotion(nmsWorld, spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), new net.minecraft.server.v1_5_R2.ItemStack(373, 1, data));
			NBTTagCompound nbt = new NBTTagCompound();

			ent.b(nbt); //Gets all the normal tags
			NBTTagCompound potionTag = nbt.getCompound("Potion");
			NBTTagCompound tagTag = new NBTTagCompound();

			NBTTagList list = new NBTTagList();
			NBTTagCompound potionType = new NBTTagCompound();
			potionType.setByte("Id", (byte) effect.getType().getId());
			potionType.setByte("Amplifier", (byte) effect.getAmplifier());
			potionType.setInt("Duration", effect.getDuration());
			potionType.setByte("Ambient", (byte) 0);
			list.add(potionType);

			if(potionTag == null) {
				potionTag = new NBTTagCompound();
				potionTag.setShort("id", (short) 373);
				potionTag.setShort("Damage", (short) data);
				potionTag.setByte("Count", (byte) 1);
				tagTag.set("CustomPotionEffects", list);
			} else {
				tagTag = potionTag.getCompound("tag");
				tagTag.set("CustomPotionEffects", list);
			}

			potionTag.setCompound("tag", tagTag);
			nbt.setCompound("Potion", potionTag);
			ent.a(nbt);

			nmsWorld.addEntity(ent);
			return ent.getBukkitEntity();
		} else if(spawnType.getType().equals(EntityType.ENDER_PEARL)) {
			World world = spawnLocation.getWorld();
			List<Player> nearby = getNearbyPlayers(spawnLocation, spawner.getMaxPlayerDistance() + 1);
			int index = (new Random()).nextInt(nearby.size());
			EntityLiving nearPlayer = ((CraftLivingEntity) nearby.get(index)).getHandle();
			net.minecraft.server.v1_5_R2.World nmsWorld = ((CraftWorld) world).getHandle();
			EntityEnderPearl ent = new EntityEnderPearl(nmsWorld, nearPlayer);
			ent.setLocation(spawnLocation.getX(), spawnLocation.getY(), spawnLocation.getZ(), 0, 0);
			nmsWorld.addEntity(ent);
			return ent.getBukkitEntity();
		} else if(spawnType.getType().equals(EntityType.LIGHTNING)) {
			return spawnLocation.getWorld().strikeLightningEffect(spawnLocation);
		} else if(spawnType.getType().equals(EntityType.MINECART)) { 
			if(spawnType.getItemType().getType().equals(Material.MOB_SPAWNER)) {
				e = spawnLocation.getWorld().spawn(spawnLocation, SpawnerMinecart.class);
			} else if(spawnType.getItemType().getType().equals(Material.TNT)) {
				e = spawnLocation.getWorld().spawn(spawnLocation, ExplosiveMinecart.class);
			} else if(spawnType.getItemType().getTypeId() == 154) {
				e = spawnLocation.getWorld().spawn(spawnLocation, HopperMinecart.class);
			} else if(spawnType.getItemType().getType().equals(Material.CHEST)) {
				e = spawnLocation.getWorld().spawn(spawnLocation, StorageMinecart.class);
			} else if(spawnType.getItemType().getType().equals(Material.FURNACE)) {
				e = spawnLocation.getWorld().spawn(spawnLocation, PoweredMinecart.class);
			} else if(spawnType.getItemType().getType().equals(Material.DISPENSER)) {
				e = spawnLocation.getWorld().spawn(spawnLocation, Minecart.class);
			} else {
				e = spawnLocation.getWorld().spawn(spawnLocation, Minecart.class);
			}
		} else {
			e = spawnLocation.getWorld().spawn(spawnLocation, spawnType.getType().getEntityClass());;
		}

		net.minecraft.server.v1_5_R2.Entity entity0 = ((CraftEntity) e).getHandle();
		entity0.yaw = (float) randomRotation();

		return e;

	}

}
