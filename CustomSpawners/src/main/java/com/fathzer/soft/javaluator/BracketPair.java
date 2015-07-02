package com.fathzer.soft.javaluator;

public class BracketPair
{
  public static final BracketPair PARENTHESES = new BracketPair('(', ')');
  public static final BracketPair BRACKETS = new BracketPair('[', ']');
  public static final BracketPair BRACES = new BracketPair('{', '}');
  public static final BracketPair ANGLES = new BracketPair('<', '>');
  private String open;
  private String close;
  
  public BracketPair(char open, char close)
  {
    this.open = new String(new char[] { open });
    this.close = new String(new char[] { close });
  }
  
  public String getOpen()
  {
    return this.open;
  }
  
  public String getClose()
  {
    return this.close;
  }
  
  public String toString()
  {
    return this.open + this.close;
  }
}
