package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AMapMapTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.AQuoteTypeCG;
import org.overture.codegen.cgast.types.ARealBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;
import org.overture.codegen.cgast.types.ATemplateTypeCG;
import org.overture.codegen.cgast.types.ATokenBasicTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class TypeVisitorCG extends AbstractVisitorCG<IRInfo, STypeCG>
{
	@Override
	public STypeCG caseAUnionType(AUnionType node, IRInfo question)
			throws AnalysisException
	{
		LinkedList<PType> types = node.getTypes();

		PTypeAssistantTC typeAssistant = question.getTcFactory().createPTypeAssistant();
		
		if (question.getTypeAssistant().isUnionOfType(node, ASetType.class))
		{
			ASetType setType = typeAssistant.getSet(node);
			return setType.apply(question.getTypeVisitor(), question);
			
		} else if (question.getTypeAssistant().isUnionOfType(node, SSeqType.class))
		{
			SSeqType seqType = typeAssistant.getSeq(node);
			return seqType.apply(question.getTypeVisitor(), question);
			
		} else if (question.getTypeAssistant().isUnionOfType(node, SMapType.class))
		{
			SMapType mapType = typeAssistant.getMap(node);
			return mapType.apply(question.getTypeVisitor(), question);
		} else
		{

			AUnionTypeCG unionTypeCg = new AUnionTypeCG();

			for (PType type : types)
			{
				STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
				unionTypeCg.getTypes().add(typeCg);
			}

			return unionTypeCg;
		}
	}
	
	@Override
	public STypeCG caseAUnknownType(AUnknownType node, IRInfo question)
			throws AnalysisException
	{
		return new AUnknownTypeCG(); // '?' Indicates an unknown type
	}
	
	@Override
	public STypeCG caseATokenBasicType(ATokenBasicType node, IRInfo question)
			throws AnalysisException
	{
		return new ATokenBasicTypeCG();
	}
	
	@Override
	public STypeCG caseASetType(ASetType node, IRInfo question)
			throws AnalysisException
	{
		PType setOf = node.getSetof();
		STypeCG typeCg = setOf.apply(question.getTypeVisitor(), question);
		boolean empty = node.getEmpty();
		
		ASetSetTypeCG setType = new ASetSetTypeCG();
		setType.setSetOf(typeCg);
		setType.setEmpty(empty);
		
		return setType;
	}
	
	@Override
	public STypeCG caseAMapMapType(AMapMapType node, IRInfo question)
			throws AnalysisException
	{
		PType from = node.getFrom();
		PType to = node.getTo();
		boolean empty = node.getEmpty();
		
		STypeCG fromCg = from.apply(question.getTypeVisitor(), question);
		STypeCG toCg = to.apply(question.getTypeVisitor(), question);
		
		AMapMapTypeCG mapType = new AMapMapTypeCG();
		mapType.setFrom(fromCg);
		mapType.setTo(toCg);
		mapType.setEmpty(empty);
		
		return mapType;
	}
	
	@Override
	public STypeCG caseAProductType(AProductType node, IRInfo question)
			throws AnalysisException
	{	
		ATupleTypeCG tuple = new ATupleTypeCG();
		
		LinkedList<PType> types = node.getTypes();
		
		for (PType type : types)
		{
			STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
			tuple.getTypes().add(typeCg);
			
		}
		
		return tuple;
	}
	
	@Override
	public STypeCG caseAParameterType(AParameterType node, IRInfo question)
			throws AnalysisException
	{
		String name = node.getName().getName();
		
		ATemplateTypeCG templateType = new ATemplateTypeCG();
		templateType.setName(name);
		
		return templateType;
	}

	@Override
	public STypeCG caseAOptionalType(AOptionalType node, IRInfo question)
			throws AnalysisException
	{
		STypeCG type = node.getType().apply(question.getTypeVisitor(), question);

		if (type instanceof AIntNumericBasicTypeCG)
			return new AIntBasicTypeWrappersTypeCG();
		else if (type instanceof ARealNumericBasicTypeCG)
			return new ARealBasicTypeWrappersTypeCG();
		else if (type instanceof ABoolBasicTypeCG)
			return new ABoolBasicTypeWrappersTypeCG();
		else if (type instanceof ACharBasicTypeCG)
			return new ACharBasicTypeWrappersTypeCG();
		
		return type;
	}

	@Override
	public STypeCG caseANamedInvariantType(ANamedInvariantType node,
			IRInfo question) throws AnalysisException
	{
		PType type = node.getType();

		if (type instanceof AUnionType)
		{
			AUnionType unionType = (AUnionType) type;

			if (question.getTypeAssistant().isUnionOfType(unionType, AQuoteType.class))
			{
				return new AIntNumericBasicTypeCG();
			}
		}

		return type.apply(question.getTypeVisitor(), question);
	}

	@Override
	public STypeCG caseAQuoteType(AQuoteType node, IRInfo question)
			throws AnalysisException
	{
		String value = node.getValue().getValue();
		
		AQuoteTypeCG quoteTypeCg = new AQuoteTypeCG();
		quoteTypeCg.setValue(value);
		
		return quoteTypeCg;
	}

	@Override
	public STypeCG caseARecordInvariantType(ARecordInvariantType node,
			IRInfo question) throws AnalysisException
	{
		ILexNameToken name = node.getName();
		
		ARecordTypeCG recordType = new ARecordTypeCG();
		
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setName(name.getName());
		typeName.setDefiningClass(name.getModule());

		recordType.setName(typeName);
		
		return recordType;
	}

	@Override
	public STypeCG caseASeqSeqType(ASeqSeqType node, IRInfo question)
			throws AnalysisException
	{
		return question.getTypeAssistant().constructSeqType(node, question);
	}
	
	@Override
	public STypeCG caseASeq1SeqType(ASeq1SeqType node, IRInfo question)
			throws AnalysisException
	{
		return question.getTypeAssistant().constructSeqType(node, question);
	}

	@Override
	public STypeCG caseAOperationType(AOperationType node, IRInfo question)
			throws AnalysisException
	{
		return question.getTypeAssistant().consMethodType(node, node.getParameters(), node.getResult(), question);
	}

	@Override
	public STypeCG caseAFunctionType(AFunctionType node, IRInfo question)
			throws AnalysisException
	{
		return question.getTypeAssistant().consMethodType(node, node.getParameters(), node.getResult(), question);
	}
	
	@Override
	public STypeCG caseAClassType(AClassType node, IRInfo question)
			throws AnalysisException
	{
		String typeName = node.getClassdef().getName().getName();

		AClassTypeCG classType = new AClassTypeCG();
		classType.setName(typeName);

		return classType;
	}

	@Override
	public STypeCG caseAVoidType(AVoidType node, IRInfo question)
			throws AnalysisException
	{
		return new AVoidTypeCG();
	}

	@Override
	public STypeCG caseAIntNumericBasicType(AIntNumericBasicType node,
			IRInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public STypeCG caseANatOneNumericBasicType(ANatOneNumericBasicType node,
			IRInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public STypeCG caseANatNumericBasicType(ANatNumericBasicType node,
			IRInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public STypeCG caseARealNumericBasicType(ARealNumericBasicType node,
			IRInfo question) throws AnalysisException
	{
		return new ARealNumericBasicTypeCG();
	}
	
	@Override
	public STypeCG caseARationalNumericBasicType(
			ARationalNumericBasicType node, IRInfo question)
			throws AnalysisException
	{
		return new ARealNumericBasicTypeCG();
	}

	@Override
	public STypeCG caseACharBasicType(ACharBasicType node, IRInfo question)
			throws AnalysisException
	{
		return new ACharBasicTypeCG();
	}

	@Override
	public STypeCG caseABooleanBasicType(ABooleanBasicType node,
			IRInfo question) throws AnalysisException
	{
		return new ABoolBasicTypeCG();
	}
}
