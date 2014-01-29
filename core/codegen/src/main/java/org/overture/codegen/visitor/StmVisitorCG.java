package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.expressions.AElseIfExp;
import org.overture.ast.expressions.AIfExp;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ASkipStm;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.statements.PObjectDesignator;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.PType;
import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.assistant.StmAssistantCG;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.ALetDefExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectStmCG;
import org.overture.codegen.cgast.statements.ACallStmCG;
import org.overture.codegen.cgast.statements.AElseIfStmCG;
import org.overture.codegen.cgast.statements.AForIndexStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALetDefStmCG;
import org.overture.codegen.cgast.statements.ANotImplementedStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.statements.ASkipStmCG;
import org.overture.codegen.cgast.statements.PObjectDesignatorCG;
import org.overture.codegen.cgast.statements.PStateDesignatorCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.ooast.OoAstInfo;


public class StmVisitorCG extends AbstractVisitorCG<OoAstInfo, PStmCG>
{
	public StmVisitorCG()
	{
	}
	
	@Override
	public PStmCG caseANotYetSpecifiedStm(ANotYetSpecifiedStm node,
			OoAstInfo question) throws AnalysisException
	{
		return new ANotImplementedStmCG();
	}
	
	@Override
	public PStmCG defaultPExp(PExp node, OoAstInfo question)
			throws AnalysisException
	{
		
		PExpCG exp =  node.apply(question.getExpVisitor(), question);
		
		if(exp instanceof ALetDefExpCG)
			return StmAssistantCG.convertToLetDefStm((ALetDefExpCG) exp);
		else
		{
			AReturnStmCG returnStm = new AReturnStmCG();
			returnStm.setExp(exp);
			
			return returnStm;
		}
	}	
	
	@Override
	public PStmCG caseABlockSimpleBlockStm(ABlockSimpleBlockStm node,
			OoAstInfo question) throws AnalysisException
	{
		ABlockStmCG blockStm = new ABlockStmCG();

		LinkedList<AAssignmentDefinition> assignmentDefs = node.getAssignmentDefs();
		
		for (AAssignmentDefinition def : assignmentDefs)
		{
			//FIXME: No protection against hidden definitions
			// dcl s : real := 1
			// dcl s : real := 2
			PType type = def.getType();
			String name = def.getName().getName();
			PExp exp = def.getExpression();
			
			PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
			PExpCG expCg = exp.apply(question.getExpVisitor(), question);
			
			ALocalVarDeclCG localDecl = new ALocalVarDeclCG();
			localDecl.setType(typeCg);
			localDecl.setName(name);
			localDecl.setExp(expCg);
			
			if(exp instanceof AUndefinedExp)
				DeclAssistantCG.setDefaultValue(localDecl, typeCg);
			else
				localDecl.setExp(def.getExpression().apply(question.getExpVisitor(), question));
			
			blockStm.getLocalDefs().add(localDecl);
		}
		
		LinkedList<PStm> stms = node.getStatements();
		
		for (PStm pStm : stms)
		{
			PStmCG stmCg = pStm.apply(question.getStatementVisitor(), question);
			
			if(stmCg != null)
				blockStm.getStatements().add(stmCg);
		}
		
		return blockStm;
	}
	
	@Override
	public PStmCG caseAAssignmentStm(AAssignmentStm node, OoAstInfo question)
			throws AnalysisException
	{
		PStateDesignatorCG target = node.getTarget().apply(question.getStateDesignatorVisitor(), question);
		PExpCG exp = node.getExp().apply(question.getExpVisitor(), question);
		
		AAssignmentStmCG assignment = new AAssignmentStmCG();
		assignment.setTarget(target);
		assignment.setExp(exp);
		
		return assignment;
	}
	
	@Override
	public PStmCG caseALetStm(ALetStm node, OoAstInfo question)
			throws AnalysisException
	{
		ALetDefStmCG localDefStm = new ALetDefStmCG();
		
		DeclAssistantCG.setLocalDefs(node.getLocalDefs(), localDefStm.getLocalDefs(), question);
		
		PStmCG stm = node.getStatement().apply(question.getStatementVisitor(), question);
		localDefStm.setStm(stm);
		
		return localDefStm;
	}
		
	@Override
	public PStmCG caseAReturnStm(AReturnStm node, OoAstInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExpression();
		
		AReturnStmCG returnStm = new AReturnStmCG();
		
		if(exp != null)
		{
			PExpCG expCg = exp.apply(question.getExpVisitor(), question);
			if(expCg instanceof ALetDefExpCG)
				return StmAssistantCG.convertToLetDefStm((ALetDefExpCG) expCg);
			
			returnStm.setExp(expCg);
		}
		
		return returnStm;
	}
	
	@Override
	public PStmCG caseACallStm(ACallStm node, OoAstInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		ILexNameToken nameToken = node.getName();
		String name = nameToken.getName();
		LinkedList<PExp> args = node.getArgs();

		AClassTypeCG classType = null;
		
		if (nameToken != null && nameToken.getExplicit())
		{
			String className = nameToken.getModule();
			classType = new AClassTypeCG();
			classType.setName(className);
		}
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		
		ACallStmCG callStm = new ACallStmCG();
		callStm.setClassType(classType);
		callStm.setName(name);
		callStm.setType(typeCg);
		StmAssistantCG.generateArguments(args, callStm.getArgs(), question);
		
		return callStm;
	}
	
	@Override
	public PStmCG caseACallObjectStm(ACallObjectStm node, OoAstInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PObjectDesignator objectDesignator = node.getDesignator();
		ILexNameToken field = node.getField();		
		LinkedList<PExp> args = node.getArgs();
		
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PObjectDesignatorCG objectDesignatorCg = objectDesignator.apply(question.getObjectDesignatorVisitor(), question);
		
		String classNameCg = null;
		
		if(node.getExplicit())
			classNameCg = field.getModule();
			
		String fieldNameCg = field.getName();
		
		ACallObjectStmCG callObject = new ACallObjectStmCG();
		callObject.setType(typeCg);
		callObject.setDesignator(objectDesignatorCg);
		callObject.setClassName(classNameCg);
		callObject.setFieldName(fieldNameCg);
		StmAssistantCG.generateArguments(args, callObject.getArgs(), question);
		
		return callObject;
	}
		
	@Override
	public PStmCG caseAElseIfStm(AElseIfStm node, OoAstInfo question)
			throws AnalysisException
	{
		//Don't visit it but create it directly if needed in the ifStm in order to avoid casting
		return null;
	}
	
	@Override
	public PStmCG caseAIfExp(AIfExp node, OoAstInfo question)
			throws AnalysisException
	{
		PExpCG ifExp = node.getTest().apply(question.getExpVisitor(), question);
		PStmCG then = node.getThen().apply(question.getStatementVisitor(), question);

		AIfStmCG ifStm = new AIfStmCG();

		ifStm.setIfExp(ifExp);
		ifStm.setThenStm(then);
		LinkedList<AElseIfExp> elseIfs = node.getElseList();	
		
		for (AElseIfExp exp : elseIfs)
		{
			ifExp = exp.getElseIf().apply(question.getExpVisitor(), question);
			then = exp.getThen().apply(question.getStatementVisitor(), question);
						
			AElseIfStmCG elseIfStm = new AElseIfStmCG();
			elseIfStm.setElseIf(ifExp);
			elseIfStm.setThenStm(then);
			
			ifStm.getElseIf().add(elseIfStm);
		}
		
		if(node.getElse() != null)
		{
			PStmCG elseStm = node.getElse().apply(question.getStatementVisitor(), question);
			ifStm.setElseStm(elseStm);
		}

		return ifStm;
	}
	
	@Override
	public PStmCG caseAIfStm(AIfStm node, OoAstInfo question)
			throws AnalysisException
	{
		PExpCG ifExp = node.getIfExp().apply(question.getExpVisitor(), question);
		PStmCG thenStm = node.getThenStm().apply(question.getStatementVisitor(), question);
		
		
		AIfStmCG ifStm = new AIfStmCG();
		
		ifStm.setIfExp(ifExp);
		ifStm.setThenStm(thenStm);
		
		LinkedList<AElseIfStm> elseIfs = node.getElseIf();	
		
		for (AElseIfStm stm : elseIfs)
		{
			ifExp = stm.getElseIf().apply(question.getExpVisitor(), question);
			thenStm = stm.getThenStm().apply(question.getStatementVisitor(), question);
			
			AElseIfStmCG elseIfStm = new AElseIfStmCG();
			elseIfStm.setElseIf(ifExp);
			elseIfStm.setThenStm(thenStm);
			
			
			ifStm.getElseIf().add(elseIfStm);
		}
		
		if(node.getElseStm() != null)
		{
			PStmCG elseStm = node.getElseStm().apply(question.getStatementVisitor(), question);
			ifStm.setElseStm(elseStm);
		}
		
		return ifStm;
		
	}
	
	
	@Override
	public PStmCG caseASkipStm(ASkipStm node, OoAstInfo question)
			throws AnalysisException
	{
		return new ASkipStmCG();
	}
	
	@Override
	public PStmCG caseASubclassResponsibilityStm(
			ASubclassResponsibilityStm node, OoAstInfo question)
			throws AnalysisException
	{
		return null;//Indicates an abstract body
	}
	
	@Override
	public PStmCG caseAForIndexStm(AForIndexStm node, OoAstInfo question)
			throws AnalysisException
	{
		ILexNameToken var = node.getVar();
		PExp from = node.getFrom();
		PExp to = node.getTo();
		PExp by = node.getBy();
		PStm stm = node.getStatement();
		
		String varCg = var.getName();
		PExpCG fromCg = from.apply(question.getExpVisitor(), question);
		PExpCG toCg = to.apply(question.getExpVisitor(), question);
		PExpCG byCg = by != null ? by.apply(question.getExpVisitor(), question) : null;
		PStmCG bodyCg = stm.apply(question.getStatementVisitor(), question);
		
		AForIndexStmCG forStm = new AForIndexStmCG();
		forStm.setVar(varCg);
		forStm.setFrom(fromCg);
		forStm.setTo(toCg);
		forStm.setBy(byCg);
		forStm.setBody(bodyCg);
		
		return forStm;
	}
	
}