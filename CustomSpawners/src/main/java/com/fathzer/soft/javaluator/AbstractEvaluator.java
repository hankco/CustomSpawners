package com.fathzer.soft.javaluator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class AbstractEvaluator<T>
{
  private final Tokenizer tokenizer;
  private final Map<String, Function> functions;
  private final Map<String, List<Operator>> operators;
  private final Map<String, Constant> constants;
  private final String functionArgumentSeparator;
  private final Map<String, BracketPair> functionBrackets;
  private final Map<String, BracketPair> expressionBrackets;
  
  protected AbstractEvaluator(Parameters parameters)
  {
    ArrayList<String> tokenDelimitersBuilder = new ArrayList();
    this.functions = new HashMap();
    this.operators = new HashMap();
    this.constants = new HashMap();
    this.functionBrackets = new HashMap();
    for (BracketPair pair : parameters.getFunctionBrackets())
    {
      this.functionBrackets.put(pair.getOpen(), pair);
      this.functionBrackets.put(pair.getClose(), pair);
      tokenDelimitersBuilder.add(pair.getOpen());
      tokenDelimitersBuilder.add(pair.getClose());
    }
    this.expressionBrackets = new HashMap();
    for (BracketPair pair : parameters.getExpressionBrackets())
    {
      this.expressionBrackets.put(pair.getOpen(), pair);
      this.expressionBrackets.put(pair.getClose(), pair);
      tokenDelimitersBuilder.add(pair.getOpen());
      tokenDelimitersBuilder.add(pair.getClose());
    }
    if (this.operators != null) {
      for (Operator ope : parameters.getOperators())
      {
        tokenDelimitersBuilder.add(ope.getSymbol());
        List<Operator> known = (List)this.operators.get(ope.getSymbol());
        if (known == null)
        {
          known = new ArrayList();
          this.operators.put(ope.getSymbol(), known);
        }
        known.add(ope);
        if (known.size() > 1) {
          validateHomonyms(known);
        }
      }
    }
    boolean needFunctionSeparator = false;
    if (parameters.getFunctions() != null) {
      for (Function function : parameters.getFunctions())
      {
        this.functions.put(parameters.getTranslation(function.getName()), function);
        if (function.getMaximumArgumentCount() > 1) {
          needFunctionSeparator = true;
        }
      }
    }
    if (parameters.getConstants() != null) {
      for (Constant constant : parameters.getConstants()) {
        this.constants.put(parameters.getTranslation(constant.getName()), constant);
      }
    }
    this.functionArgumentSeparator = parameters.getFunctionArgumentSeparator();
    if (needFunctionSeparator) {
      tokenDelimitersBuilder.add(this.functionArgumentSeparator);
    }
    this.tokenizer = new Tokenizer(tokenDelimitersBuilder);
  }
  
  protected void validateHomonyms(List<Operator> operators)
  {
    if (operators.size() > 2) {
      throw new IllegalArgumentException();
    }
  }
  
  protected Operator guessOperator(Token previous, List<Operator> candidates)
  {
    int argCount = (previous != null) && ((previous.isCloseBracket()) || (previous.isLiteral())) ? 2 : 1;
    for (Operator operator : candidates) {
      if (operator.getOperandCount() == argCount) {
        return operator;
      }
    }
    return null;
  }
  
  private void output(Deque<T> values, Token token, Object evaluationContext)
  {
    if (token.isLiteral())
    {
      String literal = token.getLiteral();
      Constant ct = (Constant)this.constants.get(literal);
      T value = ct == null ? null : evaluate(ct, evaluationContext);
      if ((value == null) && (evaluationContext != null) && ((evaluationContext instanceof AbstractVariableSet))) {
        value = (T) ((AbstractVariableSet)evaluationContext).get(literal);
      }
      values.push(value != null ? value : toValue(literal, evaluationContext));
    }
    else if (token.isOperator())
    {
      Operator operator = token.getOperator();
      values.push(evaluate(operator, getArguments(values, operator.getOperandCount()), evaluationContext));
    }
    else
    {
      throw new IllegalArgumentException();
    }
  }
  
  protected T evaluate(Constant constant, Object evaluationContext)
  {
    throw new RuntimeException("evaluate(Constant) is not implemented for " + constant.getName());
  }
  
  protected T evaluate(Operator operator, Iterator<T> operands, Object evaluationContext)
  {
    throw new RuntimeException("evaluate(Operator, Iterator) is not implemented for " + operator.getSymbol());
  }
  
  protected T evaluate(Function function, Iterator<T> arguments, Object evaluationContext)
  {
    throw new RuntimeException("evaluate(Function, Iterator) is not implemented for " + function.getName());
  }
  
  private void doFunction(Deque<T> values, Function function, int argCount, Object evaluationContext)
  {
    if ((function.getMinimumArgumentCount() > argCount) || (function.getMaximumArgumentCount() < argCount)) {
      throw new IllegalArgumentException("Invalid argument count for " + function.getName());
    }
    values.push(evaluate(function, getArguments(values, argCount), evaluationContext));
  }
  
  private Iterator<T> getArguments(Deque<T> values, int nb)
  {
    if (values.size() < nb) {
      throw new IllegalArgumentException();
    }
    LinkedList<T> result = new LinkedList();
    for (int i = 0; i < nb; i++) {
      result.addFirst(values.pop());
    }
    return result.iterator();
  }
  
  protected abstract T toValue(String paramString, Object paramObject);
  
  public T evaluate(String expression)
  {
    return evaluate(expression, null);
  }
  
  public T evaluate(String expression, Object evaluationContext)
  {
    Deque<T> values = new ArrayDeque();
    Deque<Token> stack = new ArrayDeque();
    Deque<Integer> previousValuesSize = this.functions.isEmpty() ? null : new ArrayDeque();
    Iterator<String> tokens = tokenize(expression);
    Token previous = null;
    while (tokens.hasNext())
    {
      String strToken = (String)tokens.next();
      Token token = toToken(previous, strToken);
      if (token.isOpenBracket())
      {
        stack.push(token);
        if ((previous != null) && (previous.isFunction()))
        {
          if (!this.functionBrackets.containsKey(token.getBrackets().getOpen())) {
            throw new IllegalArgumentException("Invalid bracket after function: " + strToken);
          }
        }
        else if (!this.expressionBrackets.containsKey(token.getBrackets().getOpen())) {
          throw new IllegalArgumentException("Invalid bracket in expression: " + strToken);
        }
      }
      else if (token.isCloseBracket())
      {
        if (previous == null) {
          throw new IllegalArgumentException("expression can't start with a close bracket");
        }
        if (previous.isFunctionArgumentSeparator()) {
          throw new IllegalArgumentException("argument is missing");
        }
        BracketPair brackets = token.getBrackets();
        
        boolean openBracketFound = false;
        while (!stack.isEmpty())
        {
          Token sc = (Token)stack.pop();
          if (sc.isOpenBracket())
          {
            if (sc.getBrackets().equals(brackets))
            {
              openBracketFound = true;
              break;
            }
            throw new IllegalArgumentException("Invalid parenthesis match " + sc.getBrackets().getOpen() + brackets.getClose());
          }
          output(values, sc, evaluationContext);
        }
        if (!openBracketFound) {
          throw new IllegalArgumentException("Parentheses mismatched");
        }
        if ((!stack.isEmpty()) && (((Token)stack.peek()).isFunction()))
        {
          int argCount = values.size() - ((Integer)previousValuesSize.pop()).intValue();
          doFunction(values, ((Token)stack.pop()).getFunction(), argCount, evaluationContext);
        }
      }
      else if (token.isFunctionArgumentSeparator())
      {
        if (previous == null) {
          throw new IllegalArgumentException("expression can't start with a function argument separator");
        }
        if ((previous.isOpenBracket()) || (previous.isFunctionArgumentSeparator())) {
          throw new IllegalArgumentException("argument is missing");
        }
        boolean pe = false;
        while (!stack.isEmpty())
        {
          if (((Token)stack.peek()).isOpenBracket())
          {
            pe = true;
            break;
          }
          output(values, (Token)stack.pop(), evaluationContext);
        }
        if (!pe) {
          throw new IllegalArgumentException("Separator or parentheses mismatched");
        }
      }
      else if (token.isFunction())
      {
        stack.push(token);
        previousValuesSize.push(Integer.valueOf(values.size()));
      }
      else if (token.isOperator())
      {
        while (!stack.isEmpty())
        {
          Token sc = (Token)stack.peek();
          if ((!sc.isOperator()) || (((!token.getAssociativity().equals(Operator.Associativity.LEFT)) || (token.getPrecedence() > sc.getPrecedence())) && (token.getPrecedence() >= sc.getPrecedence()))) {
            break;
          }
          output(values, (Token)stack.pop(), evaluationContext);
        }
        stack.push(token);
      }
      else
      {
        if ((previous != null) && (previous.isLiteral())) {
          throw new IllegalArgumentException("A literal can't follow another literal");
        }
        output(values, token, evaluationContext);
      }
      previous = token;
    }
    while (!stack.isEmpty())
    {
      Token sc = (Token)stack.pop();
      if ((sc.isOpenBracket()) || (sc.isCloseBracket())) {
        throw new IllegalArgumentException("Parentheses mismatched");
      }
      output(values, sc, evaluationContext);
    }
    if (values.size() != 1) {
      throw new IllegalArgumentException();
    }
    return values.pop();
  }
  
  private Token toToken(Token previous, String token)
  {
    if (token.equals(this.functionArgumentSeparator)) {
      return Token.FUNCTION_ARG_SEPARATOR;
    }
    if (this.functions.containsKey(token)) {
      return Token.buildFunction((Function)this.functions.get(token));
    }
    if (this.operators.containsKey(token))
    {
      List<Operator> list = (List)this.operators.get(token);
      return list.size() == 1 ? Token.buildOperator((Operator)list.get(0)) : Token.buildOperator(guessOperator(previous, list));
    }
    BracketPair brackets = getBracketPair(token);
    if (brackets != null)
    {
      if (brackets.getOpen().equals(token)) {
        return Token.buildOpenToken(brackets);
      }
      return Token.buildCloseToken(brackets);
    }
    return Token.buildLiteral(token);
  }
  
  private BracketPair getBracketPair(String token)
  {
    BracketPair result = (BracketPair)this.expressionBrackets.get(token);
    return result == null ? (BracketPair)this.functionBrackets.get(token) : result;
  }
  
  public Collection<Operator> getOperators()
  {
    ArrayList<Operator> result = new ArrayList();
    Collection<List<Operator>> values = this.operators.values();
    for (List<Operator> list : values) {
      result.addAll(list);
    }
    return result;
  }
  
  public Collection<Function> getFunctions()
  {
    return this.functions.values();
  }
  
  public Collection<Constant> getConstants()
  {
    return this.constants.values();
  }
  
  protected Iterator<String> tokenize(String expression)
  {
    return this.tokenizer.tokenize(expression);
  }
}
