package com.github.thebiologist13.attributelib;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.NBTBase;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;

public class AttributeLib
{
  public static void addAttribute(LivingEntity entity, Attribute attribute)
  {
    List<Attribute> attributes = getAttributes(entity);
    attributes.add(attribute);
    setAttributes(entity, attributes);
  }
  
  public static List<Attribute> getAttributes(LivingEntity entity)
  {
    List<Attribute> att = new ArrayList<Attribute>();
    
    NBTTagCompound allNBT = getEntityNBT(entity);
    NBTTagList attNBT = allNBT.getList("Attributes", 0);
    if ((attNBT != null) && (attNBT.size() != 0)) {
      for (int i = 0; i < attNBT.size(); i++)
      {
        NBTBase b = attNBT.get(i);
        if ((b instanceof NBTTagCompound))
        {
          NBTTagCompound attComp = (NBTTagCompound)b;
          String name = attComp.getString("Name");
          VanillaAttribute baseAttribute = VanillaAttribute.fromName(name);
          if (baseAttribute != null)
          {
            Attribute attribute = new Attribute(baseAttribute);
            attribute.setBase(attComp.getDouble("Base"));
            
            List<Modifier> modsList = new ArrayList<Modifier>();
            NBTTagList mods = attComp.getList("Modifiers", i);
            if ((mods != null) && (mods.size() != 0)) {
              for (int j = 0; j < mods.size(); j++)
              {
                NBTBase b0 = mods.get(j);
                if ((b0 instanceof NBTTagCompound))
                {
                  NBTTagCompound mod = (NBTTagCompound)b0;
                  String modName = mod.getString("Name");
                  double amount = mod.getDouble("Amount");
                  Operation op = Operation.fromId(mod.getInt("Operation"));
                  UUID id = new UUID(mod.getLong("UUIDMost"), mod.getLong("UUIDLeast"));
                  
                  Modifier newMod = new Modifier(modName, op, amount, id);
                  modsList.add(newMod);
                }
              }
            }
            attribute.setModifiers(modsList);
            
            att.add(attribute);
          }
        }
      }
    }
    return att;
  }
  
  public static void removeAttribute(LivingEntity entity, Attribute attribute)
  {
    List<Attribute> attributes = getAttributes(entity);
    attributes.remove(attribute);
    setAttributes(entity, attributes);
  }
  
  public static void setAttributes(LivingEntity entity, List<Attribute> attributes)
  {
    NBTTagList attList = new NBTTagList();
    for (Attribute a : attributes)
    {
      NBTTagCompound attComp = new NBTTagCompound();
      attComp.setString("Name", a.getAttribute().getName());
      attComp.setDouble("Base", a.getBase());
      
      NBTTagList modList = new NBTTagList();
      for (Modifier m : a.getModifiers())
      {
        NBTTagCompound modComp = new NBTTagCompound();
        
        modComp.setString("Name", m.getName());
        modComp.setDouble("Amount", m.getAmount());
        modComp.setInt("Operation", m.getOp().getOperation());
        modComp.setLong("UUIDMost", m.getId().getMostSignificantBits());
        modComp.setLong("UUIDLeast", m.getId().getLeastSignificantBits());
        
        modList.add(modComp);
      }
      attComp.set("Modifiers", modList);
      
      attList.add(attComp);
    }
    setAttributeNBT(entity, attList);
  }
  
  private static <T extends org.bukkit.entity.Entity> NBTTagCompound getEntityNBT(T entity)
  {
    NBTTagCompound compound = new NBTTagCompound();
    if (!(entity instanceof LivingEntity)) {
      return null;
    }
    net.minecraft.server.v1_8_R3.Entity nms = ((CraftEntity)entity).getHandle();
    nms.e(compound);
    return compound;
  }
  
  private static void setAttributeNBT(LivingEntity entity, NBTTagList nbt)
  {
    NBTTagCompound allNBT = getEntityNBT(entity);
    allNBT.set("Attributes", nbt);
    
    net.minecraft.server.v1_8_R3.Entity nms = ((CraftEntity)entity).getHandle();
    Class<?> entityClass = nms.getClass();
    Method[] methods = entityClass.getMethods();
    for (Method method : methods) {
      if ((method.getName() == "a") && (method.getParameterTypes().length == 1) && (method.getParameterTypes()[0] == NBTTagCompound.class)) {
        try
        {
          method.setAccessible(true);
          method.invoke(nms, new Object[] { allNBT });
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
      }
    }
  }
}
