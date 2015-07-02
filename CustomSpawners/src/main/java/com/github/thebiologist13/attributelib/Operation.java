package com.github.thebiologist13.attributelib;

public enum Operation
{
  INCREMENT(0, "Increment"),  ADDITIVE(1, "Additive"),  MULTIPLICATIVE(2, "Multiplicative");
  
  private final int OP;
  private final String NAME;
  
  private Operation(int op, String name)
  {
    this.OP = op;
    this.NAME = name;
  }
  
  public static Operation fromId(int id)
  {
    switch (id)
    {
    case 0: 
      return INCREMENT;
    case 1: 
      return ADDITIVE;
    case 2: 
      return MULTIPLICATIVE;
    }
    return null;
  }
  
  public static Operation fromName(String name)
  {
    if (name.equalsIgnoreCase(INCREMENT.NAME)) {
      return INCREMENT;
    }
    if (name.equalsIgnoreCase(ADDITIVE.NAME)) {
      return ADDITIVE;
    }
    if (name.equalsIgnoreCase(MULTIPLICATIVE.NAME)) {
      return MULTIPLICATIVE;
    }
    return null;
  }
  
  public int getOperation()
  {
    return this.OP;
  }
}
