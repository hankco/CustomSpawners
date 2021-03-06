package com.fathzer.soft.javaluator;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

public class DoubleEvaluator
  extends AbstractEvaluator<Double>
{
  public static enum Style
  {
    STANDARD,  EXCEL;
    
    private Style() {}
  }
  
  public static final Constant PI = new Constant("pi");
  public static final Constant E = new Constant("e");
  public static final Function CEIL = new Function("ceil", 1);
  public static final Function FLOOR = new Function("floor", 1);
  public static final Function ROUND = new Function("round", 1);
  public static final Function ABS = new Function("abs", 1);
  public static final Function SINE = new Function("sin", 1);
  public static final Function COSINE = new Function("cos", 1);
  public static final Function TANGENT = new Function("tan", 1);
  public static final Function ACOSINE = new Function("acos", 1);
  public static final Function ASINE = new Function("asin", 1);
  public static final Function ATAN = new Function("atan", 1);
  public static final Function SINEH = new Function("sinh", 1);
  public static final Function COSINEH = new Function("cosh", 1);
  public static final Function TANGENTH = new Function("tanh", 1);
  public static final Function MIN = new Function("min", 1, 2147483647);
  public static final Function MAX = new Function("max", 1, 2147483647);
  public static final Function SUM = new Function("sum", 1, 2147483647);
  public static final Function AVERAGE = new Function("avg", 1, 2147483647);
  public static final Function LN = new Function("ln", 1);
  public static final Function LOG = new Function("log", 1);
  public static final Function RANDOM = new Function("random", 0);
  public static final Operator NEGATE = new Operator("-", 1, Operator.Associativity.RIGHT, 3);
  public static final Operator NEGATE_HIGH = new Operator("-", 1, Operator.Associativity.RIGHT, 5);
  public static final Operator MINUS = new Operator("-", 2, Operator.Associativity.LEFT, 1);
  public static final Operator PLUS = new Operator("+", 2, Operator.Associativity.LEFT, 1);
  public static final Operator MULTIPLY = new Operator("*", 2, Operator.Associativity.LEFT, 2);
  public static final Operator DIVIDE = new Operator("/", 2, Operator.Associativity.LEFT, 2);
  public static final Operator EXPONENT = new Operator("^", 2, Operator.Associativity.LEFT, 4);
  public static final Operator MODULO = new Operator("%", 2, Operator.Associativity.LEFT, 2);
  private static final Operator[] OPERATORS = { NEGATE, MINUS, PLUS, MULTIPLY, DIVIDE, EXPONENT, MODULO };
  private static final Operator[] OPERATORS_EXCEL = { NEGATE_HIGH, MINUS, PLUS, MULTIPLY, DIVIDE, EXPONENT, MODULO };
  private static final Function[] FUNCTIONS = { SINE, COSINE, TANGENT, ASINE, ACOSINE, ATAN, SINEH, COSINEH, TANGENTH, MIN, MAX, SUM, AVERAGE, LN, LOG, ROUND, CEIL, FLOOR, ABS, RANDOM };
  private static final Constant[] CONSTANTS = { PI, E };
  private static Parameters DEFAULT_PARAMETERS;
  private static final NumberFormat FORMATTER = NumberFormat.getNumberInstance(Locale.US);
  
  public static Parameters getDefaultParameters()
  {
    return getDefaultParameters(Style.STANDARD);
  }
  
  public static Parameters getDefaultParameters(Style style)
  {
    Parameters result = new Parameters();
    result.addOperators(style == Style.STANDARD ? Arrays.asList(OPERATORS) : Arrays.asList(OPERATORS_EXCEL));
    result.addFunctions(Arrays.asList(FUNCTIONS));
    result.addConstants(Arrays.asList(CONSTANTS));
    result.addFunctionBracket(BracketPair.PARENTHESES);
    result.addExpressionBracket(BracketPair.PARENTHESES);
    return result;
  }
  
  private static Parameters getParameters()
  {
    if (DEFAULT_PARAMETERS == null) {
      DEFAULT_PARAMETERS = getDefaultParameters();
    }
    return DEFAULT_PARAMETERS;
  }
  
  public DoubleEvaluator()
  {
    this(getParameters());
  }
  
  public DoubleEvaluator(Parameters parameters)
  {
    super(parameters);
  }
  
  protected Double toValue(String literal, Object evaluationContext)
  {
    ParsePosition p = new ParsePosition(0);
    Number result = FORMATTER.parse(literal, p);
    if ((p.getIndex() == 0) || (p.getIndex() != literal.length())) {
      throw new IllegalArgumentException(literal + " is not a number");
    }
    return Double.valueOf(result.doubleValue());
  }
  
  protected Double evaluate(Constant constant, Object evaluationContext)
  {
    if (PI.equals(constant)) {
      return Double.valueOf(3.141592653589793D);
    }
    if (E.equals(constant)) {
      return Double.valueOf(2.718281828459045D);
    }
    return (Double)super.evaluate(constant, evaluationContext);
  }
  
  protected Double evaluate(Operator operator, Iterator<Double> operands, Object evaluationContext)
  {
    if ((NEGATE.equals(operator)) || (NEGATE_HIGH.equals(operator))) {
      return Double.valueOf(-((Double)operands.next()).doubleValue());
    }
    if (MINUS.equals(operator)) {
      return Double.valueOf(((Double)operands.next()).doubleValue() - ((Double)operands.next()).doubleValue());
    }
    if (PLUS.equals(operator)) {
      return Double.valueOf(((Double)operands.next()).doubleValue() + ((Double)operands.next()).doubleValue());
    }
    if (MULTIPLY.equals(operator)) {
      return Double.valueOf(((Double)operands.next()).doubleValue() * ((Double)operands.next()).doubleValue());
    }
    if (DIVIDE.equals(operator)) {
      return Double.valueOf(((Double)operands.next()).doubleValue() / ((Double)operands.next()).doubleValue());
    }
    if (EXPONENT.equals(operator)) {
      return Double.valueOf(Math.pow(((Double)operands.next()).doubleValue(), ((Double)operands.next()).doubleValue()));
    }
    if (MODULO.equals(operator)) {
      return Double.valueOf(((Double)operands.next()).doubleValue() % ((Double)operands.next()).doubleValue());
    }
    return (Double)super.evaluate(operator, operands, evaluationContext);
  }
  
  protected Double evaluate(Function function, Iterator<Double> arguments, Object evaluationContext)
  {
    Double result = null;
    if (ABS.equals(function)){
      result = Double.valueOf(Math.abs(((Double)arguments.next()).doubleValue()));
    }else{
      if (CEIL.equals(function)){
        result = Double.valueOf(Math.ceil(((Double)arguments.next()).doubleValue()));
      }
      else
      {
        if (FLOOR.equals(function))
        {
          result = Double.valueOf(Math.floor(((Double)arguments.next()).doubleValue()));
        }
        else
        {
          if (ROUND.equals(function))
          {
            Double arg = (Double)arguments.next();
            if ((arg.doubleValue() == (-1.0D / 0.0D)) || (arg.doubleValue() == (1.0D / 0.0D))) {
              result = arg;
            } else {
              result = Double.valueOf(Math.round(arg.doubleValue()));
            }
          }
          else
          {
            if (SINEH.equals(function))
            {
              result = Double.valueOf(Math.sinh(((Double)arguments.next()).doubleValue()));
            }
            else
            {
              if (COSINEH.equals(function))
              {
                result = Double.valueOf(Math.cosh(((Double)arguments.next()).doubleValue()));
              }
              else
              {
                if (TANGENTH.equals(function))
                {
                  result = Double.valueOf(Math.tanh(((Double)arguments.next()).doubleValue()));
                }
                else
                {
                  if (SINE.equals(function))
                  {
                    result = Double.valueOf(Math.sin(((Double)arguments.next()).doubleValue()));
                  }
                  else
                  {
                    if (COSINE.equals(function))
                    {
                      result = Double.valueOf(Math.cos(((Double)arguments.next()).doubleValue()));
                    }
                    else
                    {
                      if (TANGENT.equals(function))
                      {
                        result = Double.valueOf(Math.tan(((Double)arguments.next()).doubleValue()));
                      }
                      else
                      {
                        if (ACOSINE.equals(function))
                        {
                          result = Double.valueOf(Math.acos(((Double)arguments.next()).doubleValue()));
                        }
                        else
                        {
                          if (ASINE.equals(function))
                          {
                            result = Double.valueOf(Math.asin(((Double)arguments.next()).doubleValue()));
                          }
                          else
                          {
                            if (ATAN.equals(function))
                            {
                              result = Double.valueOf(Math.atan(((Double)arguments.next()).doubleValue()));
                            }
                            else
                            {
                              if (MIN.equals(function))
                              {
                                while (arguments.hasNext()) {
                                  result = Double.valueOf(Math.min(result.doubleValue(), ((Double)arguments.next()).doubleValue()));
                                }
                              }
                              if (MAX.equals(function))
                              {
                                while (arguments.hasNext()) {
                                  result = Double.valueOf(Math.max(result.doubleValue(), ((Double)arguments.next()).doubleValue()));
                                }
                              }
                              if (SUM.equals(function))
                              {
                                while (arguments.hasNext()) {
                                  result = Double.valueOf(result.doubleValue() + ((Double)arguments.next()).doubleValue());
                                }
                              }
                              if (AVERAGE.equals(function))
                              {
                                int nb = 0;
                                while (arguments.hasNext())
                                {
                                  result = Double.valueOf(result.doubleValue() + ((Double)arguments.next()).doubleValue());
                                  nb++;
                                }
                                result = Double.valueOf(result.doubleValue() / nb);
                              }
                              else
                              {
                                if (LN.equals(function))
                                {
                                  result = Double.valueOf(Math.log(((Double)arguments.next()).doubleValue()));
                                }
                                else
                                {
                                  if (LOG.equals(function))
                                  {
                                    result = Double.valueOf(Math.log10(((Double)arguments.next()).doubleValue()));
                                  }
                                  else
                                  {
                                    if (RANDOM.equals(function)) {
                                      result = Double.valueOf(Math.random());
                                    } else {
                                      result = (Double)super.evaluate(function, arguments, evaluationContext);
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    errIfNaN(result, function);
    return result;
  }
  
  private void errIfNaN(Double result, Function function)
  {
    if (result.equals(Double.valueOf((0.0D / 0.0D)))) {
      throw new IllegalArgumentException("Invalid argument passed to " + function.getName());
    }
  }
}
