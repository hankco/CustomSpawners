package com.fathzer.soft.javaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer
{
  private Pattern pattern;
  private String tokenDelimiters;
  private boolean trimTokens;
  
  public Tokenizer(List<String> delimiters)
  {
    if (onlyOneChar(delimiters))
    {
      StringBuilder builder = new StringBuilder();
      for (String delimiter : delimiters) {
        builder.append(delimiter);
      }
      this.tokenDelimiters = builder.toString();
    }
    else
    {
      this.pattern = delimitersToRegexp(delimiters);
    }
    this.trimTokens = true;
  }
  
  public boolean isTrimTokens()
  {
    return this.trimTokens;
  }
  
  public void setTrimTokens(boolean trimTokens)
  {
    this.trimTokens = trimTokens;
  }
  
  private boolean onlyOneChar(List<String> delimiters)
  {
    for (String delimiter : delimiters) {
      if (delimiter.length() != 1) {
        return false;
      }
    }
    return true;
  }
  
  private static Pattern delimitersToRegexp(List<String> delimiters)
  {
    Collections.sort(delimiters, new Comparator<Object>()
    {
      @Override
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		return 0;
	}
    });
    StringBuilder result = new StringBuilder();
    result.append('(');
    for (String delim : delimiters)
    {
      if (result.length() != 1) {
        result.append('|');
      }
      result.append("\\Q").append(delim).append("\\E");
    }
    result.append(')');
    return Pattern.compile(result.toString());
  }
  
  private void addToTokens(List<String> tokens, String token)
  {
    if (this.trimTokens) {
      token = token.trim();
    }
    if (!token.isEmpty()) {
      tokens.add(token);
    }
  }
  
  public Iterator<String> tokenize(String string)
  {
    if (this.pattern != null)
    {
      List<String> res = new ArrayList<String>();
      Matcher m = this.pattern.matcher(string);
      int pos = 0;
      while (m.find())
      {
        if (pos != m.start()) {
          addToTokens(res, string.substring(pos, m.start()));
        }
        addToTokens(res, m.group());
        pos = m.end();
      }
      if (pos != string.length()) {
        addToTokens(res, string.substring(pos));
      }
      return res.iterator();
    }
    return new StringTokenizerIterator(new StringTokenizer(string, this.tokenDelimiters, true));
  }
  
  private class StringTokenizerIterator
    implements Iterator<String>
  {
    private StringTokenizer tokens;
    
    public StringTokenizerIterator(StringTokenizer tokens)
    {
      this.tokens = tokens;
    }
    
    private String nextToken = null;
    
    public boolean hasNext()
    {
      return buildNextToken();
    }
    
    public String next()
    {
      if (!buildNextToken()) {
        throw new NoSuchElementException();
      }
      String token = this.nextToken;
      this.nextToken = null;
      return token;
    }
    
    private boolean buildNextToken()
    {
      while ((this.nextToken == null) && (this.tokens.hasMoreTokens()))
      {
        this.nextToken = this.tokens.nextToken();
        if (Tokenizer.this.trimTokens) {
          this.nextToken = this.nextToken.trim();
        }
        if (this.nextToken.isEmpty()) {
          this.nextToken = null;
        }
      }
      return this.nextToken != null;
    }
    
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}
