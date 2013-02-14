package org.overture.parser.tests;

import java.io.File;
import java.util.List;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ClassReader;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.tests.framework.BaseParserTestCase;

public class SpecificatopnRtTestCase extends BaseParserTestCase<ClassReader,List<SClassDefinition>>
{
	static boolean hasRunBefore = false;
	public SpecificatopnRtTestCase(File file)
	{
		super(file);
	}
	
	public SpecificatopnRtTestCase()
	{
	
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_RT;
	}

	public SpecificatopnRtTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name, content);
	}

	@Override
	protected ClassReader getReader(LexTokenReader ltr)
	{
		return new ClassReader(ltr);
	}

	@Override
	protected List<SClassDefinition> read(ClassReader reader) throws ParserException, LexException
	{
		return reader.readClasses();
	}

	@Override
	protected String getReaderTypeName()
	{
		return "Specification RT";
	}

	@Override
	protected void setHasRunBefore(boolean b)
	{
		hasRunBefore = b;
	}

	@Override
	protected boolean hasRunBefore()
	{
		return hasRunBefore;
	}

	
}
