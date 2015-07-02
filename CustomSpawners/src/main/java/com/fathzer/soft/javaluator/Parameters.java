package com.fathzer.soft.javaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parameters
{
  private String functionSeparator;
  private final List<Operator> operators;
  private final List<Function> functions;
  private final List<Constant> constants;
  private final Map<String, String> translations;
  private final List<BracketPair> expressionBrackets;
  private final List<BracketPair> functionBrackets;
  
  public Parameters()
  {
    this.operators = new ArrayList();
    this.functions = new ArrayList();
    this.constants = new ArrayList();
    this.translations = new HashMap();
    this.expressionBrackets = new ArrayList();
    this.functionBrackets = new ArrayList();
    setFunctionArgumentSeparator(',');
  }
  
  public Collection<Operator> getOperators()
  {
    return this.operators;
  }
  
  public Collection<Function> getFunctions()
  {
    return this.functions;
  }
  
  public Collection<Constant> getConstants()
  {
    return this.constants;
  }
  
  public Collection<BracketPair> getExpressionBrackets()
  {
    return this.expressionBrackets;
  }
  
  public Collection<BracketPair> getFunctionBrackets()
  {
    return this.functionBrackets;
  }
  
  public void addOperators(Collection<Operator> operators)
  {
    this.operators.addAll(operators);
  }
  
  public void add(Operator operator)
  {
    this.operators.add(operator);
  }
  
  public void addFunctions(Collection<Function> functions)
  {
    this.functions.addAll(functions);
  }
  
  public void add(Function function)
  {
    this.functions.add(function);
  }
  
  public void addConstants(Collection<Constant> constants)
  {
    this.constants.addAll(constants);
  }
  
  public void add(Constant constant)
  {
    this.constants.add(constant);
  }
  
  public void addExpressionBracket(BracketPair pair)
  {
    this.expressionBrackets.add(pair);
  }
  
  public void addExpressionBrackets(Collection<BracketPair> brackets)
  {
    this.expressionBrackets.addAll(brackets);
  }
  
  public void addFunctionBracket(BracketPair pair)
  {
    this.functionBrackets.add(pair);
  }
  
  public void addFunctionBrackets(Collection<BracketPair> brackets)
  {
    this.functionBrackets.addAll(brackets);
  }
  
  public void setTranslation(Function function, String translatedName)
  {
    setTranslation(function.getName(), translatedName);
  }
  
  public void setTranslation(Constant constant, String translatedName)
  {
    setTranslation(constant.getName(), translatedName);
  }
  
  private void setTranslation(String name, String translatedName)
  {
    this.translations.put(name, translatedName);
  }
  
  String getTranslation(String originalName)
  {
    String translation = (String)this.translations.get(originalName);
    return translation == null ? originalName : translation;
  }
  
  public void setFunctionArgumentSeparator(char separator)
  {
    this.functionSeparator = new String(new char[] { separator });
  }
  
  public String getFunctionArgumentSeparator()
  {
    return this.functionSeparator;
  }
}
