package com.fathzer.soft.javaluator;

public class Function
{
  private String name;
  private int minArgumentCount;
  private int maxArgumentCount;
  
  public Function(String name, int argumentCount)
  {
    this(name, argumentCount, argumentCount);
  }
  
  public Function(String name, int minArgumentCount, int maxArgumentCount)
  {
    if ((minArgumentCount < 0) || (minArgumentCount > maxArgumentCount)) {
      throw new IllegalArgumentException("Invalid argument count");
    }
    if ((name == null) || (name.length() == 0)) {
      throw new IllegalArgumentException("Invalid function name");
    }
    this.name = name;
    this.minArgumentCount = minArgumentCount;
    this.maxArgumentCount = maxArgumentCount;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public int getMinimumArgumentCount()
  {
    return this.minArgumentCount;
  }
  
  public int getMaximumArgumentCount()
  {
    return this.maxArgumentCount;
  }
}
