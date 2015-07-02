package com.fathzer.soft.javaluator;

public class Operator
{
  private String symbol;
  private int precedence;
  private int operandCount;
  private Associativity associativity;
  
  public static enum Associativity
  {
    LEFT,  RIGHT,  NONE;
    
    private Associativity() {}
  }
  
  public Operator(String symbol, int operandCount, Associativity associativity, int precedence)
  {
    if ((symbol == null) || (associativity == null)) {
      throw new NullPointerException();
    }
    if (symbol.length() == 0) {
      throw new IllegalArgumentException("Operator symbol can't be null");
    }
    if ((operandCount < 1) || (operandCount > 2)) {
      throw new IllegalArgumentException("Only unary and binary operators are supported");
    }
    if (Associativity.NONE.equals(associativity)) {
      throw new IllegalArgumentException("None associativity operators are not supported");
    }
    this.symbol = symbol;
    this.operandCount = operandCount;
    this.associativity = associativity;
    this.precedence = precedence;
  }
  
  public String getSymbol()
  {
    return this.symbol;
  }
  
  public int getOperandCount()
  {
    return this.operandCount;
  }
  
  public Associativity getAssociativity()
  {
    return this.associativity;
  }
  
  public int getPrecedence()
  {
    return this.precedence;
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + this.operandCount;
    result = 31 * result + (this.associativity == null ? 0 : this.associativity.hashCode());
    result = 31 * result + (this.symbol == null ? 0 : this.symbol.hashCode());
    result = 31 * result + this.precedence;
    return result;
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || ((obj instanceof Operator))) {
      return false;
    }
    Operator other = (Operator)obj;
    if ((this.operandCount != other.operandCount) || (this.associativity != other.associativity)) {
      return false;
    }
    if (this.symbol == null)
    {
      if (other.symbol != null) {
        return false;
      }
    }
    else if (!this.symbol.equals(other.symbol)) {
      return false;
    }
    if (this.precedence != other.precedence) {
      return false;
    }
    return true;
  }
}
