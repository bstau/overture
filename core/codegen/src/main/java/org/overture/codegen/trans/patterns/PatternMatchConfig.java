package org.overture.codegen.trans.patterns;

import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.patterns.ABoolPatternCG;
import org.overture.codegen.cgast.patterns.ACharPatternCG;
import org.overture.codegen.cgast.patterns.AIgnorePatternCG;
import org.overture.codegen.cgast.patterns.AIntPatternCG;
import org.overture.codegen.cgast.patterns.ANullPatternCG;
import org.overture.codegen.cgast.patterns.AQuotePatternCG;
import org.overture.codegen.cgast.patterns.ARealPatternCG;
import org.overture.codegen.cgast.patterns.AStringPatternCG;

public class PatternMatchConfig
{
	public String getIgnorePatternPrefix()
	{
		return "ignorePattern_";
	}
	
	public String getBoolPatternPrefix()
	{
		return "boolPattern_";
	}
	
	public String getCharPatternPrefix()
	{
		return "charPattern_";
	}
	
	public String getIntPatternPrefix()
	{
		return "intPattern_";
	}
	
	public String getNullPatternPrefix()
	{
		return "nullPattern_";
	}
	
	public String getQuotePatternPrefix()
	{
		return "quotePattern_";
	}
	
	public String getRealPatternPrefix()
	{
		return "realPattern_";
	}
	
	public String getStringPatternPrefix()
	{
		return "stringPattern_";
	}
	
	public String getMatchFailedMessage(SPatternCG pattern)
	{
		return patternToString(pattern) + " pattern match failed";
	}
	
	private String patternToString(SPatternCG pattern)
	{
		if(pattern instanceof AIgnorePatternCG)
		{
			return "Ignore";
		}
		else if(pattern instanceof ABoolPatternCG)
		{
			return "Bool";
		}
		else if(pattern instanceof ACharPatternCG)
		{
			return "Char";
		}
		else if(pattern instanceof AIntPatternCG)
		{
			return "Integer";
		}
		else if(pattern instanceof ANullPatternCG)
		{
			return "Nil";
		}
		else if(pattern instanceof AQuotePatternCG)
		{
			return "Quote";
		}
		else if(pattern instanceof ARealPatternCG)
		{
			return "Real";
		}
		else if(pattern instanceof AStringPatternCG)
		{
			return "String";
		}
		else
		{
			return null;
		}
	}
}
