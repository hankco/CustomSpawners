package com.github.thebiologist13.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.thebiologist13.CustomSpawners;
import com.github.thebiologist13.SpawnableEntity;
import com.github.thebiologist13.Spawner;

public class MobDamageByEntityEvent implements Listener {
	
	private CustomSpawners plugin = null;
	
	public MobDamageByEntityEvent(CustomSpawners plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMobDamage(EntityDamageByEntityEvent ev) {
		//DamageController
		DamageController dc = new DamageController(plugin);
		//Damaged Entity
		Entity damaged = ev.getEntity();
		//Damager
		Entity damager = ev.getDamager();

		if(damaged instanceof Player) {
			
			ev.setDamage(dc.getModifiedDamage(ev));
			
		} else {

			//SpawnableEntity
			SpawnableEntity e = plugin.getEntityFromSpawner(damaged);
			//Spawner
			Spawner s = plugin.getSpawnerWithEntity(damaged);
			
			if(damager instanceof Player) {
				
				if(e != null) {
					ev.setDamage(dc.getModifiedDamage(ev));
					if(s.getPassiveMobs().containsKey(damaged)) {
						int id = damaged.getEntityId();
						s.removePassiveMob(id);
						s.addMob(id, e);
					}
				}
				
				return;
				
			} else {
				
				if(e != null) {
					ev.setDamage(dc.getModifiedDamage(ev));
				}
				
				return;
				
			}
			
		}
		
	}
	
}
