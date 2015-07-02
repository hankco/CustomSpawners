package com.fathzer.soft.javaluator;

public class Token
{
  private static enum Kind
  {
    OPEN_BRACKET,  CLOSE_BRACKET,  FUNCTION_SEPARATOR,  FUNCTION,  OPERATOR,  LITERAL;
    
    private Kind() {}
  }
  
  static final Token FUNCTION_ARG_SEPARATOR = new Token(Kind.FUNCTION_SEPARATOR, null);
  private Kind kind;
  private Object content;
  
  static Token buildLiteral(String literal)
  {
    return new Token(Kind.LITERAL, literal);
  }
  
  static Token buildOperator(Operator ope)
  {
    return new Token(Kind.OPERATOR, ope);
  }
  
  static Token buildFunction(Function function)
  {
    return new Token(Kind.FUNCTION, function);
  }
  
  static Token buildOpenToken(BracketPair pair)
  {
    return new Token(Kind.OPEN_BRACKET, pair);
  }
  
  static Token buildCloseToken(BracketPair pair)
  {
    return new Token(Kind.CLOSE_BRACKET, pair);
  }
  
  private Token(Kind kind, Object content)
  {
    if (((kind.equals(Kind.OPERATOR)) && (!(content instanceof Operator))) || ((kind.equals(Kind.FUNCTION)) && (!(content instanceof Function))) || ((kind.equals(Kind.LITERAL)) && (!(content instanceof String)))) {
      throw new IllegalArgumentException();
    }
    this.kind = kind;
    this.content = content;
  }
  
  BracketPair getBrackets()
  {
    return (BracketPair)this.content;
  }
  
  Operator getOperator()
  {
    return (Operator)this.content;
  }
  
  Function getFunction()
  {
    return (Function)this.content;
  }
  
  Kind getKind()
  {
    return this.kind;
  }
  
  public boolean isOperator()
  {
    return this.kind.equals(Kind.OPERATOR);
  }
  
  public boolean isFunction()
  {
    return this.kind.equals(Kind.FUNCTION);
  }
  
  public boolean isOpenBracket()
  {
    return this.kind.equals(Kind.OPEN_BRACKET);
  }
  
  public boolean isCloseBracket()
  {
    return this.kind.equals(Kind.CLOSE_BRACKET);
  }
  
  public boolean isFunctionArgumentSeparator()
  {
    return this.kind.equals(Kind.FUNCTION_SEPARATOR);
  }
  
  public boolean isLiteral()
  {
    return this.kind.equals(Kind.LITERAL);
  }
  
  Operator.Associativity getAssociativity()
  {
    return getOperator().getAssociativity();
  }
  
  int getPrecedence()
  {
    return getOperator().getPrecedence();
  }
  
  String getLiteral()
  {
    if (!this.kind.equals(Kind.LITERAL)) {
      throw new IllegalArgumentException();
    }
    return (String)this.content;
  }
}
