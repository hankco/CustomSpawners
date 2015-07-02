package com.github.thebiologist13.attributelib;

import java.util.HashMap;
import java.util.Map;

public enum VanillaAttribute
{
  MAX_HEALTH("generic.maxHealth", 20.0D, 0.0D, -1.0D),  FOLLOW_RANGE("generic.followRange", 32.0D, 0.0D, 2048.0D),  KNOCKBACK_RESISTANCE("generic.knockbackResistance", 0.0D, 0.0D, 1.0D),  MOVEMENT_SPEED("generic.movementSpeed", 0.7D, 0.0D, -1.0D),  ATTACK_DAMAGE("generic.attackDamage", 2.0D, 0.0D, -1.0D),  JUMP_STRENGTH("horse.jumpStrength", 0.7D, 0.0D, 2.0D),  SPAWN_REINFORCEMENTS("zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D);
  
  private static final Map<String, VanillaAttribute> NAME_MAP;
  private final String NAME;
  private final double BASE;
  private final double MINIMUM;
  private final double MAXIMUM;
  
  private VanillaAttribute(String name, double base, double min, double max)
  {
    this.NAME = name;
    this.BASE = base;
    this.MINIMUM = min;
    this.MAXIMUM = max;
  }
  
  public static VanillaAttribute fromName(String name)
  {
    return (VanillaAttribute)NAME_MAP.get(name);
  }
  
  public String getName()
  {
    return this.NAME;
  }
  
  public double getDefaultBase()
  {
    return this.BASE;
  }
  
  public double getMinimum()
  {
    return this.MINIMUM;
  }
  
  public double getMaximum()
  {
    return this.MAXIMUM;
  }
  
  static
  {
    NAME_MAP = new HashMap();
    for (VanillaAttribute v : values()) {
      NAME_MAP.put(v.getName(), v);
    }
  }
}
