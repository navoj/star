package org.star_lang.star.compiler.generate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.ASyntax;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.TypeAttribute;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.canonical.Application;
import org.star_lang.star.compiler.canonical.AssertAction;
import org.star_lang.star.compiler.canonical.Assignment;
import org.star_lang.star.compiler.canonical.CaseAction;
import org.star_lang.star.compiler.canonical.CaseExpression;
import org.star_lang.star.compiler.canonical.CastExpression;
import org.star_lang.star.compiler.canonical.CastPtn;
import org.star_lang.star.compiler.canonical.ConditionCondition;
import org.star_lang.star.compiler.canonical.ConditionalAction;
import org.star_lang.star.compiler.canonical.ConditionalExp;
import org.star_lang.star.compiler.canonical.Conjunction;
import org.star_lang.star.compiler.canonical.ConstructorPtn;
import org.star_lang.star.compiler.canonical.ConstructorTerm;
import org.star_lang.star.compiler.canonical.ContentCondition;
import org.star_lang.star.compiler.canonical.Disjunction;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ContractEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImplementationEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImportEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.TypeAliasEntry;
import org.star_lang.star.compiler.canonical.ExceptionHandler;
import org.star_lang.star.compiler.canonical.FalseCondition;
import org.star_lang.star.compiler.canonical.FieldAccess;
import org.star_lang.star.compiler.canonical.ForLoopAction;
import org.star_lang.star.compiler.canonical.FunctionLiteral;
import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.IStatement;
import org.star_lang.star.compiler.canonical.Ignore;
import org.star_lang.star.compiler.canonical.Implies;
import org.star_lang.star.compiler.canonical.IsTrue;
import org.star_lang.star.compiler.canonical.JavaEntry;
import org.star_lang.star.compiler.canonical.LetAction;
import org.star_lang.star.compiler.canonical.LetTerm;
import org.star_lang.star.compiler.canonical.ListSearch;
import org.star_lang.star.compiler.canonical.Matches;
import org.star_lang.star.compiler.canonical.MatchingPattern;
import org.star_lang.star.compiler.canonical.MemoExp;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.canonical.Negation;
import org.star_lang.star.compiler.canonical.NullAction;
import org.star_lang.star.compiler.canonical.NullExp;
import org.star_lang.star.compiler.canonical.OpenStatement;
import org.star_lang.star.compiler.canonical.Otherwise;
import org.star_lang.star.compiler.canonical.Overloaded;
import org.star_lang.star.compiler.canonical.OverloadedFieldAccess;
import org.star_lang.star.compiler.canonical.OverloadedVariable;
import org.star_lang.star.compiler.canonical.PackageTerm;
import org.star_lang.star.compiler.canonical.PatternAbstraction;
import org.star_lang.star.compiler.canonical.PatternApplication;
import org.star_lang.star.compiler.canonical.ProcedureCallAction;
import org.star_lang.star.compiler.canonical.RaiseAction;
import org.star_lang.star.compiler.canonical.RaiseExpression;
import org.star_lang.star.compiler.canonical.RecordPtn;
import org.star_lang.star.compiler.canonical.RecordSubstitute;
import org.star_lang.star.compiler.canonical.RecordTerm;
import org.star_lang.star.compiler.canonical.RegExpPattern;
import org.star_lang.star.compiler.canonical.Resolved;
import org.star_lang.star.compiler.canonical.Scalar;
import org.star_lang.star.compiler.canonical.ScalarPtn;
import org.star_lang.star.compiler.canonical.Search;
import org.star_lang.star.compiler.canonical.Sequence;
import org.star_lang.star.compiler.canonical.Shriek;
import org.star_lang.star.compiler.canonical.SyncAction;
import org.star_lang.star.compiler.canonical.TransformAction;
import org.star_lang.star.compiler.canonical.TransformCondition;
import org.star_lang.star.compiler.canonical.TransformExpression;
import org.star_lang.star.compiler.canonical.TransformPattern;
import org.star_lang.star.compiler.canonical.TransformStatement;
import org.star_lang.star.compiler.canonical.TrueCondition;
import org.star_lang.star.compiler.canonical.TypeDefinition;
import org.star_lang.star.compiler.canonical.TypeWitness;
import org.star_lang.star.compiler.canonical.ValisAction;
import org.star_lang.star.compiler.canonical.ValofExp;
import org.star_lang.star.compiler.canonical.VarDeclaration;
import org.star_lang.star.compiler.canonical.VarEntry;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.canonical.VoidExp;
import org.star_lang.star.compiler.canonical.WherePattern;
import org.star_lang.star.compiler.canonical.WhileAction;
import org.star_lang.star.compiler.canonical.Yield;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.BindingKind;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.ContinueFlag;
import org.star_lang.star.compiler.util.EntryVisitor;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.LayeredMap;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.compiler.util.Wrapper;
import org.star_lang.star.data.IArray;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IAlgebraicType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.RecordSpecifier;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterface;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.value.Array;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.data.value.ResourceURI;
import org.star_lang.star.operators.assignment.runtime.Assignments.Assign;
import org.star_lang.star.operators.assignment.runtime.Assignments.AssignRawBool;
import org.star_lang.star.operators.assignment.runtime.Assignments.AssignRawChar;
import org.star_lang.star.operators.assignment.runtime.Assignments.AssignRawFloat;
import org.star_lang.star.operators.assignment.runtime.Assignments.AssignRawInteger;
import org.star_lang.star.operators.assignment.runtime.Assignments.AssignRawLong;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawBoolRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawCharRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawFloatRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawIntegerRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRawLongRef;
import org.star_lang.star.operators.assignment.runtime.GetRefValue.GetRef;
import org.star_lang.star.operators.spawn.runtime.NotifyWait.Notify;
import org.star_lang.star.operators.spawn.runtime.NotifyWait.Wait;

/**
 * 
 * Copyright (C) 2013 Starview Inc
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * @author fgm
 * 
 */

public class GenerateCafe implements
    TransformAction<List<IAbstract>, IAbstract, IAbstract, IAbstract, List<IAbstract>, CContext>,
    TransformCondition<List<IAbstract>, IAbstract, IAbstract, IAbstract, List<IAbstract>, CContext>,
    TransformExpression<List<IAbstract>, IAbstract, IAbstract, IAbstract, List<IAbstract>, CContext>,
    TransformPattern<List<IAbstract>, IAbstract, IAbstract, IAbstract, List<IAbstract>, CContext>,
    TransformStatement<List<IAbstract>, IAbstract, IAbstract, IAbstract, List<IAbstract>, CContext>
{

  private final List<IAbstract> pkgDefs;

  private GenerateCafe(List<IAbstract> pkgDefs)
  {
    this.pkgDefs = pkgDefs;
  }

  public static IArray generatePackage(PackageTerm pkg, ErrorReport errors)
  {
    Location pkgLoc = pkg.getLoc();
    CContext pkgCxt = new CContext(pkgLoc, errors);

    List<IAbstract> pkgDefs = new ArrayList<>();
    GenerateCafe generator = new GenerateCafe(pkgDefs);

    for (Pair<Location, ResourceURI> im : pkg.getImports())
      pkgDefs.add(CafeSyntax.importSpec(im.left(), im.right()));

    Set<String> types = new HashSet<String>();

    Variable pkgVar = new Variable(pkgLoc, pkg.getPkgType(), pkg.getPkgName());
    pkgDefs.addAll(generator.compileVarDeclaration(pkgLoc, (IContentPattern) pkgVar, AccessMode.readOnly, pkg
        .getPkgValue(), pkgCxt));

    for (TypeDefinition type : pkg.getTypes())
      if (!types.contains(type.getName()) && !type.isImported()) {
        pkgDefs.add(generator.generateType(type.getTypeDescription(), pkgCxt));
        types.add(type.getName());
      }
    for (IAlgebraicType extra : pkgCxt.getIntroducedTypes().values())
      if (!types.contains(extra.getName())) {
        pkgDefs.add(generator.generateType(extra, pkgCxt));
        types.add(extra.getName());
      }

    return new Array(pkgDefs);
  }

  @Override
  public List<IAbstract> transformContractDefn(ContractEntry con, CContext context)
  {
    return FixedList.create();
  }

  @Override
  public List<IAbstract> transformContractImplementation(ImplementationEntry entry, CContext context)
  {
    return FixedList.create();
  }

  @Override
  public List<IAbstract> transformImportEntry(ImportEntry entry, CContext context)
  {
    pkgDefs.add(CafeSyntax.importSpec(entry.getLoc(), entry.getUri()));
    return FixedList.create();
  }

  @Override
  public List<IAbstract> transformJavaEntry(JavaEntry entry, CContext context)
  {
    pkgDefs.add(CafeSyntax.javaImport(entry.getLoc(), entry.getClassName()));
    return FixedList.create();
  }

  @Override
  public List<IAbstract> transformTypeAliasEntry(TypeAliasEntry entry, CContext context)
  {
    return FixedList.create();
  }

  @Override
  public List<IAbstract> transformTypeEntry(TypeDefinition type, CContext context)
  {
    if (!type.isImported())
      return FixedList.create(generateType(type.getTypeDescription(), context));
    else
      return FixedList.create();
  }

  @Override
  public List<IAbstract> transformVarEntry(VarEntry entry, CContext context)
  {
    IContentPattern defnPtn = entry.getVarPattern();

    IContentExpression value = entry.getValue();
    if (value instanceof FunctionLiteral)
      return FixedList.create(generateFunction(((Variable) defnPtn).getName(), (FunctionLiteral) value, context));
    else if (value instanceof PatternAbstraction)
      return FixedList.create(generatePtnAbstraction(((Variable) defnPtn).getName(), (PatternAbstraction) value,
          context));
    else if (value instanceof MemoExp)
      return FixedList.create(generateMemoFunction(entry.getVariable().getName(), (MemoExp) value, context));
    else {
      AccessMode access = entry.isReadOnly();

      Location loc = entry.getLoc();
      if (defnPtn instanceof Variable)
        return compileVarDeclaration(loc, defnPtn, access, value, context);
      else if (defnPtn instanceof ConstructorPtn && value instanceof ConstructorTerm) {
        List<IContentPattern> ptnEls = ((ConstructorPtn) defnPtn).getElements();
        List<IContentExpression> valEls = ((ConstructorTerm) value).getElements();
        if (ptnEls.size() != valEls.size()) {
          context.getErrors().reportError("inconsistent sizes in variable definition", loc);
          return FixedList.create();
        } else {
          /**
           * A definition of the form
           * 
           * <pre>
           * (v1,..,vn) = (e1,..,en)
           * </pre>
           * 
           * is broken up because ei may reference vj We therefore generate the equivalent of:
           * 
           * <pre>
           * (v1,..vn) = let{
           *  v1=e1; .. vn=en
           * } in (v1,..,vn)
           * </pre>
           */
          List<IAbstract> localDefs = new ArrayList<IAbstract>();
          for (int ix = 0; ix < ptnEls.size(); ix++) {
            IContentPattern ptn = ptnEls.get(ix);
            IContentExpression val = valEls.get(ix);
            localDefs.addAll(compileVarDeclaration(loc, ptn, access, val, context));
          }
          Pair<IAbstract, IAbstract> args = findVarsInDefs(localDefs, loc, context);
          IAbstract let = CafeSyntax.letExp(loc, localDefs, args.left());
          return FixedList.create(CafeSyntax.isDeclaration(loc, args.right(), let));
        }
      } else {
        if (access == AccessMode.readOnly)
          return compileVarDeclaration(loc, defnPtn, access, value, context);
        else {
          context.getErrors().reportError(
              "cannot handle assignment of " + defnPtn + "\n suggest not making its value mutually recursive", loc);
          return FixedList.create();
        }
      }
    }
  }

  public IAbstract generateFunction(String name, FunctionLiteral fun, CContext cxt)
  {
    ErrorReport errors = cxt.getErrors();
    LayeredMap<String, DictEntry> dict = cxt.getDict().fork();
    Location loc = fun.getLoc();
    CContext funCxt = cxt.fork(loc, dict);

    for (Variable free : fun.getFreeVars())
      funCxt.defineFree(free.getName(), free.getType(), AccessMode.readOnly);

    final IContentPattern[] args = fun.getArgs();
    List<IAbstract> argPtns = new ArrayList<IAbstract>();
    Wrapper<ICondition> condition = Wrapper.create(CompilerUtils.truth);
    CContext ptnCxt = funCxt.fork(condition).fork(AccessMode.readOnly);

    for (int ix = 0; ix < args.length; ix++)
      argPtns.add(generatePtn(args[ix], ptnCxt));

    if (!CompilerUtils.isTrivial(condition.get()))
      errors.reportError("function argument pattern is too complex", fun.getLoc());

    IAbstract exp = scopedExpression(fun.getBody(), funCxt);

    IAbstract funType = typeToAbstract(loc, cxt, fun.getType());

    return CafeSyntax.functionDefn(loc, name, argPtns, exp, funType);
  }

  public IAbstract generateLambda(FunctionLiteral fun, CContext cxt)
  {
    LayeredMap<String, DictEntry> dict = cxt.getDict().fork();
    Location loc = fun.getLoc();
    CContext funCxt = cxt.fork(loc, dict);

    for (Variable free : fun.getFreeVars())
      funCxt.defineFree(free.getName(), free.getType(), AccessMode.readOnly);

    final IContentPattern[] args = fun.getArgs();
    List<IAbstract> argPtns = new ArrayList<IAbstract>();
    Wrapper<ICondition> condition = Wrapper.create(CompilerUtils.truth);
    CContext ptnCxt = funCxt.fork(condition).fork(AccessMode.readOnly);

    for (int ix = 0; ix < args.length; ix++)
      argPtns.add(generatePtn(args[ix], ptnCxt));

    if (!CompilerUtils.isTrivial(condition.get()))
      cxt.getErrors().reportError("function argument pattern is too complex", fun.getLoc());

    IAbstract funType = typeToAbstract(loc, cxt, fun.getType());

    IAbstract exp = scopedExpression(fun.getBody(), funCxt);

    return CafeSyntax.lambdaFun(loc, argPtns, CafeSyntax.typeCast(loc, exp, CafeSyntax.arrowTypeRes(funType)), funType);
  }

  public IAbstract generateMemoFunction(String name, MemoExp memo, CContext cxt)
  {
    LayeredMap<String, DictEntry> dict = cxt.getDict().fork();
    Location loc = memo.getLoc();
    CContext funCxt = cxt.fork(loc, dict);

    IAbstract exp = scopedExpression(memo.getMemo(), funCxt);

    return CafeSyntax.memoDefn(loc, name, exp, memo.getType());
  }

  public IAbstract generatePtnAbstraction(String name, PatternAbstraction def, CContext cxt)
  {
    LayeredMap<String, DictEntry> dict = cxt.getDict().fork();
    Location loc = def.getLoc();
    CContext ptnCxt = cxt.fork(loc, dict);

    Wrapper<ICondition> condition = Wrapper.create(CompilerUtils.truth);
    ptnCxt = ptnCxt.fork(condition).fork(AccessMode.readOnly);

    IAbstract ptnCode = generatePtn(def.getMatch(), ptnCxt);

    if (!CompilerUtils.isTrivial(condition.get()))
      cxt.getErrors().reportError("argument pattern: " + def + " is too complex", loc);

    IAbstract exp = scopedExpression(def.getResult(), ptnCxt);

    return CafeSyntax.patternDefn(loc, name, exp, ptnCode, typeToAbstract(loc, cxt, def.getType()));
  }

  private Pair<IAbstract, IAbstract> findVarsInDefs(List<IAbstract> defs, Location loc, CContext cxt)
  {
    List<IAbstract> vars = new ArrayList<IAbstract>();
    List<IAbstract> args = new ArrayList<IAbstract>();
    List<IAbstract> types = new ArrayList<IAbstract>();

    for (IAbstract stmt : defs) {
      if (CafeSyntax.isIsDeclaration(stmt))
        findVarsInPtn(CafeSyntax.isDeclLval(stmt), vars, args, types);
      else if (CafeSyntax.isVarDeclaration(stmt))
        findVarsInPtn(CafeSyntax.varDeclLval(stmt), vars, args, types);
    }

    IAbstract tplType = CafeSyntax.tupleType(loc, types);
    IAbstract ptn = makeTuple(cxt, loc, tplType, args);
    IAbstract val = makeTuple(cxt, loc, tplType, vars);
    return Pair.pair(ptn, val);
  }

  private void findVarsInPtn(IAbstract ptn, List<IAbstract> vars, List<IAbstract> args, List<IAbstract> types)
  {
    if (CafeSyntax.isTypedTerm(ptn)) {
      vars.add(ptn);
      args.add(CafeSyntax.typedTerm(ptn));
      types.add(CafeSyntax.typedType(ptn));
    } else if (CafeSyntax.isConstructor(ptn))
      for (IValue arg : CafeSyntax.constructorArgs(ptn))
        findVarsInPtn((IAbstract) arg, vars, args, types);
  }

  @Override
  public List<IAbstract> transformOpenStatement(OpenStatement open, CContext context)
  {
    return FixedList.create();
  }

  @Override
  public List<IAbstract> transformWitness(TypeWitness stmt, CContext context)
  {
    return FixedList.create();
  }

  @Override
  public IAbstract transformRecordPtn(RecordPtn term, CContext context)
  {
    CContext ptnCxt = context.fork(AccessMode.readOnly);
    if (term.isAnonRecord()) {
      Location loc = term.getLoc();
      List<IAbstract> args = new ArrayList<>();

      for (Entry<String, IContentPattern> entry : term.getElements().entrySet()) {
        String field = entry.getKey();
        IContentPattern ptn = entry.getValue();

        IAbstract argPtn = generatePtn(ptn, ptnCxt);
        if (!CafeSyntax.isAnonymous(argPtn))
          args.add(CafeSyntax.field(ptn.getLoc(), field, argPtn));
      }
      return CafeSyntax.face(loc, term.anonLabel(), args);
    } else {
      Location loc = term.getLoc();
      Map<String, Integer> index = term.getIndex();

      IAbstract args[] = new IAbstract[index.size()];

      for (Entry<String, IContentPattern> entry : term.getElements().entrySet()) {
        String field = entry.getKey();
        IContentPattern ptn = entry.getValue();
        args[index.get(field)] = generatePtn(ptn, ptnCxt);
      }

      for (int ix = 0; ix < args.length; ix++)
        if (args[ix] == null)
          args[ix] = CafeSyntax.typeCast(loc, CafeSyntax.anonymous(loc), CafeSyntax.typeVar(loc, GenSym
              .genSym(StandardNames.ANONYMOUS_PREFIX)));

      return CafeSyntax.constructor(loc, scopedExpression(term.getFun(), ptnCxt), args);
    }
  }

  @Override
  public IAbstract transformCastPtn(CastPtn ptn, CContext context)
  {
    IAbstract cast = generatePtn(ptn.getInner(), context);
    return CafeSyntax.typeCast(ptn.getLoc(), cast, typeToAbstract(ptn.getLoc(), context, ptn.getType()));
  }

  @Override
  public IAbstract transformMatchingPtn(MatchingPattern matching, CContext context)
  {
    Variable var = matching.getVar();
    CompilerUtils.extendCondition(context.getCond(), new Matches(matching.getLoc(), matching.getVar(), matching
        .getPtn()));
    return varPattern(var, context);
  }

  private IAbstract varPattern(Variable var, CContext cxt)
  {
    String name = var.getName();
    cxt.defineLocal(name, var, cxt.getAccess());

    Location loc = var.getLoc();
    IAbstract varType = var instanceof OverloadedVariable ? typeToAbstract(loc, cxt, ((OverloadedVariable) var)
        .getDictType()) : typeToAbstract(loc, cxt, var.getType());

    return CafeSyntax.typeCast(loc, Abstract.name(loc, name), varType);
  }

  @Override
  public IAbstract transformPatternApplication(PatternApplication apply, CContext context)
  {
    Location loc = apply.getLoc();
    IAbstract pttrn = scopedExpression(apply.getAbstraction(), context);
    IAbstract reslt = generatePtn(apply.getArg(), context);

    if (pttrn instanceof Name)
      return CafeSyntax.callPtn(loc, (Name) pttrn, reslt);
    else {
      Name tmp = introduceVariable(loc, context, pttrn, apply.getAbstraction().getType());
      return CafeSyntax.callPtn(loc, tmp, reslt);
    }
  }

  @Override
  public IAbstract transformRegexpPtn(RegExpPattern ptn, CContext context)
  {
    // A regexp match like
    //
    // `alpha(.*:A)beta`
    //
    // is converted to regexp("alpha(.*)beta")(A)
    //
    // where regexp is a special form understood by Cafe
    //
    IContentPattern[] groups = ptn.getGroups();
    List<IAbstract> grpPtns = new ArrayList<IAbstract>();

    for (int ix = 0; ix < groups.length; ix++)
      grpPtns.add(generatePtn(groups[ix], context));

    return CafeSyntax.regexp(ptn.getLoc(), ptn.getRegexpPtn(), grpPtns);
  }

  @Override
  public IAbstract transformScalarPtn(ScalarPtn scalar, CContext context)
  {
    return AbstractValue.abstractValue(scalar.getLoc(), scalar.getValue(), context.getErrors());
  }

  @Override
  public IAbstract transformConstructorPtn(ConstructorPtn posPtn, CContext context)
  {
    String label = posPtn.getLabel();
    List<IContentPattern> elements = posPtn.getElements();
    List<IAbstract> elPtns = new ArrayList<IAbstract>();

    for (IContentPattern el : elements) {
      if (!(el instanceof Variable || el instanceof Scalar)) {
        Location loc = el.getLoc();
        Variable nVar = new Variable(el.getLoc(), el.getType(), GenSym.genSym("__x"));
        CompilerUtils.extendCondition(context.getCond(), new Matches(loc, nVar, el));
        elPtns.add(generatePtn(nVar, context));
      } else
        elPtns.add(generatePtn(el, context));
    }

    Location loc = posPtn.getLoc();
    return CafeSyntax.constructor(loc, label, elPtns);
  }

  @Override
  public IAbstract transformVariablePtn(Variable var, CContext context)
  {
    return varPattern(var, context);
  }

  @Override
  public IAbstract transformWherePattern(WherePattern where, CContext context)
  {
    Wrapper<ICondition> after = Wrapper.create(CompilerUtils.truth);
    IAbstract ptn = generatePtn(where.getPtn(), context.fork(after));
    CompilerUtils.extendCondition(after, where.getCond());
    return ptn;
  }

  @Override
  public IAbstract transformApplication(Application apply, CContext context)
  {
    Location loc = apply.getLoc();
    IContentExpression appliedFun = apply.getFunction();
    IAbstract fun = generateExp(appliedFun, context);
    IAbstract args = apply.getArgs().transform(this, context.fork(true));

    if (TypeUtils.isFunType(appliedFun.getType())) {
      if (fun instanceof Name)
        return CafeSyntax.funcall(loc, fun, args);
      else {
        // introduce a new variable
        Name tmp = introduceVariable(loc, context, fun, appliedFun.getType());
        return CafeSyntax.funcall(loc, tmp, args);
      }
    } else {
      assert TypeUtils.isConstructorType(appliedFun.getType());
      assert CafeSyntax.isTuple(args);
      if (fun instanceof Name)
        return CafeSyntax.constructor(loc, fun, CafeSyntax.constructorArgs(args));
      else {
        Name tmp = introduceVariable(loc, context, fun, appliedFun.getType());
        return CafeSyntax.constructor(loc, tmp, CafeSyntax.constructorArgs(args));
      }
    }
  }

  @Override
  public IAbstract transformRecord(RecordTerm record, CContext context)
  {
    IAbstract fun = generateExp(record.getFun(), context);
    SortedMap<String, IContentExpression> elements = record.getArguments();

    Location loc = record.getLoc();
    IType funType = record.getFun().getType();
    CContext dCxt = context.fork(true);

    if (TypeUtils.isFunType(funType)) {
      IAbstract[] args = new IAbstract[elements.size()];

      int offset = 0;
      for (Entry<String, IContentExpression> entry : elements.entrySet()) {
        args[offset++] = generateExp(entry.getValue(), dCxt);
      }
      if (fun instanceof Name)
        return CafeSyntax.funcall(loc, fun, CafeSyntax.tuple(loc, fun, args));
      else {
        // introduce a new variable
        Name tmp = introduceVariable(loc, context, fun, funType);
        return CafeSyntax.funcall(loc, tmp, CafeSyntax.tuple(loc, fun, args));
      }
    } else if (record.isAnonRecord()) {
      List<IAbstract> args = new ArrayList<>();

      for (Entry<String, IContentExpression> entry : elements.entrySet())
        args.add(CafeSyntax.field(loc, entry.getKey(), generateExp(entry.getValue(), dCxt)));
      return CafeSyntax.face(loc, record.anonLabel(), args);
    } else {
      assert TypeUtils.isConstructorType(funType);

      List<IAbstract> args = new ArrayList<>();

      for (Entry<String, IContentExpression> entry : elements.entrySet())
        args.add(CafeSyntax.field(loc, entry.getKey(), generateExp(entry.getValue(), dCxt)));

      if (fun instanceof Name)
        return CafeSyntax.record(loc, fun, args);
      else {
        Name tmp = introduceVariable(loc, context, fun, funType);
        return CafeSyntax.record(loc, tmp, args);
      }
    }
  }

  @Override
  public IAbstract transformRecordSubstitute(RecordSubstitute update, CContext context)
  {
    Location loc = update.getLoc();
    List<IAbstract> valActions = new ArrayList<IAbstract>();
    CContext dCxt = context.fork(true);

    IContentExpression route = update.getRoute();
    IAbstract rc = generateExp(route, dCxt);
    IContentExpression replace = update.getReplace();

    String tmpName = GenSym.genSym("__copy");
    IAbstract tmp = Abstract.name(loc, tmpName);
    valActions.add(CafeSyntax.varDeclaration(loc, CafeSyntax.typeCast(loc, tmp, typeToAbstract(loc, dCxt, route
        .getType())), CafeSyntax.copy(loc, rc)));

    if (replace instanceof RecordTerm) {
      RecordTerm record = (RecordTerm) replace;
      for (Entry<String, IContentExpression> f : record.getArguments().entrySet()) {
        String fld = f.getKey();
        IContentExpression fldVal = f.getValue();

        valActions.add(CafeSyntax.assignment(loc, CafeSyntax.dot(loc, tmp, fld), generateExp(fldVal, dCxt)));
      }
    } else {
      IAbstract repl = generateExp(replace, dCxt);
      IAbstract repVar = Abstract.name(loc, GenSym.genSym("__repl"));
      CafeSyntax.markTermWithType(repVar, replace.getType());
      // valActions.add(CafeSyntax.isDeclaration(loc, CafeSyntax.typeCast(loc, repVar,
      // typeToAbstract(loc, dCxt, replace
      // .getType())), repl));
      valActions.add(CafeSyntax.isDeclaration(loc, repVar, repl));

      TypeInterfaceType replType = (TypeInterfaceType) TypeUtils.deRef(replace.getType());

      Map<String, IType> replMembers = replType.getAllFields();

      for (Entry<String, IType> entry : replMembers.entrySet()) {
        String att = entry.getKey();
        valActions.add(CafeSyntax.assignment(loc, CafeSyntax.dot(loc, tmp, att), CafeSyntax.dot(loc, repVar, att)));
      }
    }

    valActions.add(CafeSyntax.valis(loc, tmp));

    return CafeSyntax.valof(loc, CafeSyntax.block(loc, valActions));
  }

  @Override
  public IAbstract transformCaseExpression(CaseExpression caseExp, CContext context)
  {
    List<IAbstract> cases = new ArrayList<IAbstract>();

    for (Pair<IContentPattern, IContentExpression> entry : caseExp.getCases()) {
      IContentPattern ptn = entry.getKey();
      IContentExpression caseVal = entry.getValue();

      if (ptn instanceof Variable)
        context.getErrors().reportError("variable not permitted in case: " + ptn, ptn.getLoc());
      else if (ptn instanceof ConstructorPtn || ptn instanceof RecordPtn) {
        int mark = context.getMark();
        Wrapper<ICondition> condition = Wrapper.create(CompilerUtils.truth);
        CContext pCxt = context.fork(condition).fork(AccessMode.readOnly);
        IAbstract ptnCode = generatePtn(ptn, pCxt);
        if (!CompilerUtils.isTrivial(condition.get()))
          context.getErrors().reportError("pattern " + ptn + " too complex", ptn.getLoc());

        IAbstract code = scopedExpression(caseVal, context);
        cases.add(CafeSyntax.caseRule(ptn.getLoc(), ptnCode, code));
        context.resetDict(mark);
      } else if (ptn instanceof ScalarPtn) {
        ScalarPtn key = (ScalarPtn) ptn;
        IContentExpression body = entry.getValue();
        IAbstract code = scopedExpression(body, context);
        cases.add(CafeSyntax.caseRule(key.getLoc(), AbstractValue.abstractValue(key.getLoc(), key.getValue(), context
            .getErrors()), code));
      } else if (ptn != null)
        context.getErrors().reportError("invalid pattern in case: " + ptn, ptn.getLoc());
    }

    IAbstract defCase;

    Location loc = caseExp.getLoc();
    if (caseExp.getDeflt() != null)
      defCase = scopedExpression(caseExp.getDeflt(), context);
    else
      defCase = CafeSyntax.throwExp(loc, Abstract.newString(loc, "switch failed"));

    IAbstract selector = scopedExpression(caseExp.getSelector(), context);

    if (selector instanceof Name)
      return CafeSyntax.switchAction(loc, selector, cases, defCase);
    else {
      // introduce a new variable
      Name tmp = introduceVariable(selector.getLoc(), context, selector, caseExp.getSelector().getType());
      return CafeSyntax.switchAction(loc, tmp, cases, defCase);
    }
  }

  @Override
  public IAbstract transformCastExpression(CastExpression exp, CContext context)
  {
    Location loc = exp.getLoc();
    IAbstract val = generateExp(exp.getInner(), context);
    IAbstract type = typeToAbstract(loc, context, exp.getType());
    return CafeSyntax.typeCast(loc, val, type);
  }

  @Override
  public IAbstract transformConditionalExp(ConditionalExp conditional, CContext context)
  {
    Location loc = conditional.getLoc();
    int mark = context.getMark();
    List<IAbstract> condActions = new ArrayList<>();
    CContext cCxt = context.fork(condActions);

    IAbstract tst = conditional.getCnd().transform(this, cCxt);
    IAbstract then = generateValis(conditional.getThExp(), cCxt);

    context.resetDict(mark);
    IAbstract els = generateValis(conditional.getElExp(), cCxt);
    context.resetDict(mark);

    IAbstract condAction = CafeSyntax.conditional(loc, tst, then, els);
    condActions.add(condAction);
    return CafeSyntax.valof(loc, CafeSyntax.block(loc, condActions));
  }

  private IAbstract generateValis(IContentExpression exp, CContext cxt)
  {
    IAbstract generated = scopedExpression(exp, cxt);
    if (CafeSyntax.isValof(generated))
      return CafeSyntax.valofAction(generated);
    else
      return CafeSyntax.valis(exp.getLoc(), generated);
  }

  @Override
  public IAbstract transformContentCondition(ContentCondition cond, CContext context)
  {
    return cond.getCondition().transform(this, context);
  }

  @Override
  public IAbstract transformFieldAccess(FieldAccess dot, CContext context)
  {
    Location loc = dot.getLoc();
    IContentExpression route = dot.getRecord();
    IAbstract rc = generateExp(route, context);

    if (rc instanceof Name)
      return CafeSyntax.dot(loc, rc, dot.getField());
    else {
      IAbstract tmp = introduceVariable(loc, context, rc, route.getType());
      return CafeSyntax.dot(loc, tmp, dot.getField());
    }
  }

  @Override
  public IAbstract transformMemo(MemoExp memo, CContext context)
  {
    Location loc = memo.getLoc();

    // We form a theta environment of the memo function. Similar to a anonymous function
    String name = GenSym.genSym("memo$");

    List<IAbstract> thetaDefs = new ArrayList<IAbstract>();
    thetaDefs.add(generateMemoFunction(name, memo, context));

    return CafeSyntax.letExp(loc, thetaDefs, CafeSyntax.variable(loc, name));
  }

  @Override
  public IAbstract transformNullExp(NullExp nil, CContext context)
  {
    return CafeSyntax.nullPtn(nil.getLoc());
  }

  @Override
  public IAbstract transformFunctionLiteral(FunctionLiteral f, CContext context)
  {
    return generateLambda(f, context);
  }

  @Override
  public IAbstract transformLetTerm(LetTerm let, CContext context)
  {
    final IContentExpression bound = let.getBoundExp();

    BoundCompiler<IAbstract> compileBound = new BoundCompiler<IAbstract>() {

      @Override
      public IAbstract compileBound(List<IAbstract> definitions, List<IAbstract> doActions, CContext thetaCxt)
      {
        Location loc = bound.getLoc();
        CContext subCxt = thetaCxt.fork(doActions);
        IAbstract boundExp = generateExp(bound, subCxt);
        if (!doActions.isEmpty()) {
          doActions.add(CafeSyntax.valis(loc, boundExp));
          boundExp = CafeSyntax.valof(loc, CafeSyntax.block(loc, doActions));
        }
        return CafeSyntax.letExp(loc, definitions, boundExp);
      }
    };

    return compileThetaEnv(let.getEnvironment(), compileBound, context);
  }

  @Override
  public IAbstract transformOverloaded(Overloaded over, CContext context)
  {
    context.getErrors().reportError("(internal) overloaded", over.getLoc());
    return generateExp(over.getInner(), context);
  }

  @Override
  public IAbstract transformOverloadedFieldAccess(OverloadedFieldAccess var, CContext context)
  {
    context.getErrors().reportError(StringUtils.msg("unresolved variable: ", var, " has type ", var.getType()),
        var.getLoc());

    return CafeSyntax.voidExp(var.getLoc());
  }

  @Override
  public IAbstract transformPatternAbstraction(PatternAbstraction pattern, CContext context)
  {
    Location loc = pattern.getLoc();

    IAbstract def = generatePtnAbstraction(pattern.getName(), pattern, context);
    List<IAbstract> definitions = FixedList.create(def);

    return CafeSyntax.letExp(pattern.getLoc(), definitions, new Name(loc, pattern.getName()));
  }

  @Override
  public IAbstract transformOverloadVariable(OverloadedVariable var, CContext context)
  {
    context.getErrors().reportError(StringUtils.msg("unresolved variable: ", var, " has type ", var.getType()),
        var.getLoc());

    return CafeSyntax.voidExp(var.getLoc());
  }

  @Override
  public IAbstract transformRaiseExpression(RaiseExpression exp, CContext context)
  {
    return CafeSyntax.throwExp(exp.getLoc(), generateExp(exp.getRaise(), context));
  }

  @Override
  public IAbstract transformReference(Shriek ref, CContext context)
  {
    Location loc = ref.getLoc();

    IAbstract arg = generateExp(ref.getReference(), context.fork(true));
    IType argType = ref.getReference().getType();
    assert TypeUtils.isReferenceType(argType);
    if (TypeUtils.isRawBoolType(TypeUtils.referencedType(argType)))
      return CafeSyntax.escape(loc, GetRawBoolRef.name, arg);
    else if (TypeUtils.isRawCharType(TypeUtils.referencedType(argType)))
      return CafeSyntax.escape(loc, GetRawCharRef.name, arg);
    else if (TypeUtils.isRawIntType(TypeUtils.referencedType(argType)))
      return CafeSyntax.escape(loc, GetRawIntegerRef.name, arg);
    else if (TypeUtils.isRawLongType(TypeUtils.referencedType(argType)))
      return CafeSyntax.escape(loc, GetRawLongRef.name, arg);
    else if (TypeUtils.isRawFloatType(TypeUtils.referencedType(argType)))
      return CafeSyntax.escape(loc, GetRawFloatRef.name, arg);
    else
      return CafeSyntax.escape(loc, GetRef.name, arg);
  }

  @Override
  public IAbstract transformResolved(Resolved res, CContext context)
  {
    IContentExpression dictVars = new ConstructorTerm(res.getLoc(), res.getDicts());

    IAbstract dicts = generateExp(dictVars, context.fork(false));
    // IAbstract dicts = generateExp(dictVars, context.fork(true));

    IContentExpression over = res.getOver();
    IAbstract fun = generateExp(over, context);
    Location loc = res.getLoc();

    if (fun instanceof Name)
      return CafeSyntax.funcall(loc, fun, dicts);
    else {
      // introduce a new variable
      Name tmp = introduceVariable(loc, context, fun, res.getDictType());
      return CafeSyntax.funcall(loc, tmp, dicts);
    }
  }

  @Override
  public IAbstract transformScalar(Scalar scalar, CContext context)
  {
    return AbstractValue.abstractValue(scalar.getLoc(), scalar.getValue(), context.getErrors());
  }

  @Override
  public IAbstract transformConstructor(ConstructorTerm tuple, CContext context)
  {
    Location loc = tuple.getLoc();

    List<IAbstract> elExps = compileExps(tuple.getElements(), context.fork(true));
    String label = tuple.getLabel();

    return makeTuple(context, tuple.getType(), label, loc, elExps);
  }

  @Override
  public IAbstract transformValofExp(ValofExp val, CContext context)
  {
    List<IAbstract> extra = new ArrayList<>();
    CContext valCxt = context.fork(extra);
    int mark = valCxt.getMark();
    List<IAbstract> action = val.getAction().transform(this, valCxt);
    valCxt.resetDict(mark);

    Location loc = val.getLoc();
    if (action.size() == 1 && CafeSyntax.isValis(action.get(0)) && extra.isEmpty())
      return CafeSyntax.valisExp(pickOne(loc, action));
    else if (!extra.isEmpty()) {
      extra.addAll(action);
      return CafeSyntax.valof(loc, CafeSyntax.block(loc, extra));
    } else
      return CafeSyntax.valof(loc, pickOne(loc, action));
  }

  @Override
  public IAbstract transformVariable(Variable var, CContext context)
  {
    Location loc = var.getLoc();
    String name = var.getName();

    IContentExpression rewrite = context.rewriteVar(name);

    if (rewrite instanceof Variable)
      return varReference(loc, ((Variable) rewrite).getName(), context);
    else
      return varReference(loc, name, context);
  }

  private IAbstract varReference(Location loc, String varName, CContext cxt)
  {
    IAbstract txVar = CafeSyntax.variable(loc, varName);
    DictEntry info = cxt.getDictInfo(varName);
    if (info != null) {
      if (info.getAccess() == AccessMode.readWrite && info.getBindingKind() == BindingKind.free) {
        String thisVar = findThisVariable(varName, cxt);
        if (thisVar != null)
          return CafeSyntax.dot(loc, CafeSyntax.variable(loc, thisVar), varName);
        else
          return txVar;
      }
    }

    return txVar;
  }

  private static String findThisVariable(final String name, CContext cxt)
  {
    final Wrapper<String> thisName = Wrapper.create(null);
    cxt.visitCContext(new EntryVisitor<String, DictEntry>() {

      @Override
      public ContinueFlag visit(String key, DictEntry var)
      {
        IType type = TypeUtils.unwrap(var.getType());
        if (type instanceof TypeInterfaceType) {
          if (((TypeInterfaceType) type).getAllFields().containsKey(name)) {
            thisName.set(var.getName());
            return ContinueFlag.stop;
          }
        }
        return ContinueFlag.cont;
      }
    });
    return thisName.get();
  }

  @Override
  public IAbstract transformVoidExp(VoidExp exp, CContext context)
  {
    return CafeSyntax.constructor(exp.getLoc(), NTuple.$0Enum.getLabel());
  }

  @Override
  public IAbstract transformConditionCondition(ConditionCondition cond, CContext context)
  {
    CContext deepCxt = context.fork(true);
    IAbstract left = cond.getLhs().transform(this, deepCxt);
    IAbstract right = cond.getRhs().transform(this, deepCxt);
    return CafeSyntax.conditional(cond.getLoc(), cond.getTest().transform(this, deepCxt), left, right);
  }

  @Override
  public IAbstract transformConjunction(Conjunction conj, CContext context)
  {
    IAbstract left = conj.getLhs().transform(this, context);
    IAbstract right = conj.getRhs().transform(this, context);
    return CafeSyntax.conjunction(conj.getLoc(), left, right);
  }

  @Override
  public IAbstract transformDisjunction(Disjunction disj, CContext context)
  {
    int mark = context.getMark();
    IAbstract left = disj.getLhs().transform(this, context);
    context.resetDict(mark);
    IAbstract right = disj.getRhs().transform(this, context);
    return CafeSyntax.disjunction(disj.getLoc(), left, right);
  }

  @Override
  public IAbstract transformFalseCondition(FalseCondition falseCondition, CContext context)
  {
    return CafeSyntax.falseness(falseCondition.getLoc());
  }

  @Override
  public IAbstract transformImplies(Implies implies, CContext context)
  {
    throw new UnsupportedOperationException("implies not implemented");
  }

  @Override
  public IAbstract transformIsTrue(IsTrue i, CContext context)
  {
    return scopedExpression(i.getExp(), context);
  }

  @Override
  public IAbstract transformListSearch(ListSearch ptn, CContext context)
  {
    throw new UnsupportedOperationException("list search not implemented");
  }

  @Override
  public IAbstract transformMatches(Matches matches, CContext context)
  {
    Wrapper<ICondition> cond = Wrapper.create(CompilerUtils.truth);
    CContext sub = context.fork(cond).fork(AccessMode.readOnly);

    IAbstract lhs = scopedExpression(matches.getExp(), sub);

    IAbstract rhs = generatePtn(matches.getPtn(), sub);

    if (CompilerUtils.isTrivial(cond.get()))
      return CafeSyntax.match(matches.getLoc(), lhs, rhs);
    else
      return CafeSyntax.conjunction(matches.getLoc(), CafeSyntax.match(matches.getLoc(), lhs, rhs), cond.get()
          .transform(this, context));
  }

  @Override
  public IAbstract transformMethodVariable(MethodVariable var, CContext context)
  {
    ExpressionGenerator conCompiler = DefaultContracts.getDefaultContract(var.getContractName());
    if (conCompiler != null)
      return conCompiler.generateExpression(var, context.isDeep(), context);
    else {
      context.getErrors().reportError("unresolved method: " + var, var.getLoc());

      return CafeSyntax.voidExp(var.getLoc());
    }
  }

  @Override
  public IAbstract transformNegation(Negation neg, CContext context)
  {
    int mark = context.getMark();
    IAbstract inner = neg.getNegated().transform(this, context);
    context.resetDict(mark);
    return CafeSyntax.negation(neg.getLoc(), inner);
  }

  @Override
  public IAbstract transformOtherwise(Otherwise other, CContext context)
  {
    int mark = context.getMark();
    IAbstract left = other.getLhs().transform(this, context);
    context.resetDict(mark);
    IAbstract rgt = other.getRhs().transform(this, context);
    if (CafeSyntax.isTruth(left))
      return left;
    else if (CafeSyntax.isFalse(left))
      return rgt;
    else
      return CafeSyntax.conditional(other.getLoc(), left, CafeSyntax.truth(other.getLoc()), rgt);
  }

  @Override
  public IAbstract transformSearch(Search predication, CContext context)
  {
    throw new UnsupportedOperationException("search not implemented");
  }

  @Override
  public IAbstract transformTrueCondition(TrueCondition trueCondition, CContext context)
  {
    return CafeSyntax.truth(trueCondition.getLoc());
  }

  @Override
  public List<IAbstract> transformAssertAction(AssertAction act, CContext context)
  {
    return FixedList.create(CafeSyntax.assertion(act.getLoc(), act.getAssertion().transform(this, context)));
  }

  @Override
  public List<IAbstract> transformAssignment(Assignment act, CContext context)
  {
    Location loc = act.getLoc();
    IType type = act.getValue().getType();
    CContext dpCxt = context.fork(true);
    IAbstract lval = generateExp(act.getLValue(), context);
    IAbstract rval = generateExp(act.getValue(), dpCxt);
    if (TypeUtils.isRawBoolType(type))
      return FixedList.create(CafeSyntax.escape(loc, AssignRawBool.name, lval, rval));
    else if (TypeUtils.isRawCharType(type))
      return FixedList.create(CafeSyntax.escape(loc, AssignRawChar.name, lval, rval));
    else if (TypeUtils.isRawIntType(type))
      return FixedList.create(CafeSyntax.escape(loc, AssignRawInteger.name, lval, rval));
    else if (TypeUtils.isRawLongType(type))
      return FixedList.create(CafeSyntax.escape(loc, AssignRawLong.name, lval, rval));
    else if (TypeUtils.isRawFloatType(type))
      return FixedList.create(CafeSyntax.escape(loc, AssignRawFloat.name, lval, rval));
    else if (CafeSyntax.isTuple(lval))
      return FixedList.create(CafeSyntax.assignment(loc, lval, rval));
    else
      return FixedList.create(CafeSyntax.escape(loc, Assign.name, lval, rval));
  }

  @Override
  public List<IAbstract> transformCaseAction(CaseAction caseAct, CContext cxt)
  {
    List<IAbstract> cases = new ArrayList<IAbstract>();

    for (Pair<IContentPattern, IContentAction> entry : caseAct.getCases()) {
      IContentPattern ptn = entry.getKey();
      IContentAction body = entry.getValue();
      Location loc = body.getLoc();

      if (ptn instanceof Variable)
        cxt.getErrors().reportError("variable not permitted in case: " + ptn, ptn.getLoc());
      else if (ptn instanceof ConstructorPtn || ptn instanceof RecordPtn || ptn instanceof ScalarPtn) {
        int mark = cxt.getMark();
        Wrapper<ICondition> condition = Wrapper.create(CompilerUtils.truth);
        CContext ptnCxt = cxt.fork(condition).fork(AccessMode.readOnly);
        IAbstract ptnCode = generatePtn(ptn, ptnCxt);
        if (!CompilerUtils.isTrivial(condition.get()))
          cxt.getErrors().reportError("pattern " + ptn + " too complex", ptn.getLoc());

        List<IAbstract> code = generateSubAction(body, cxt);

        cases.add(CafeSyntax.caseRule(ptn.getLoc(), ptnCode, pickOne(loc, code)));

        cxt.resetDict(mark);
      } else if (ptn != null)
        cxt.getErrors().reportError("invalid pattern in case: " + ptn, ptn.getLoc());
    }

    IContentAction deflt = caseAct.getDeflt();
    Location defltLoc = deflt.getLoc();
    List<IAbstract> defCase = generateSubAction(deflt, cxt);

    IAbstract selector = scopedExpression(caseAct.getSelector(), cxt.fork(true));

    if (selector instanceof Name)
      return FixedList.create(CafeSyntax.switchAction(caseAct.getLoc(), selector, cases, pickOne(defltLoc, defCase)));
    else {
      // introduce a new variable
      Name tmp = introduceVariable(selector.getLoc(), cxt, selector, caseAct.getSelector().getType());
      return FixedList.create(CafeSyntax.switchAction(caseAct.getLoc(), tmp, cases, pickOne(defltLoc, defCase)));
    }
  }

  @Override
  public List<IAbstract> transformConditionalAction(ConditionalAction action, CContext context)
  {
    Location loc = action.getLoc();
    int mark = context.getMark();
    IAbstract test = action.getCond().transform(this, context);
    List<IAbstract> then = generateSubAction(action.getThPart(), context);
    context.resetDict(mark);
    List<IAbstract> els = generateSubAction(action.getElPart(), context);
    return FixedList.create(CafeSyntax.conditional(loc, test, pickOne(loc, then), pickOne(loc, els)));
  }

  @Override
  public List<IAbstract> transformExceptionHandler(ExceptionHandler except, CContext context)
  {
    Location loc = except.getLoc();
    int mark = context.getMark();
    CContext dpCxt = context.fork(true);
    List<IAbstract> body = generateSubAction(except.getBody(), dpCxt);
    context.resetDict(mark);

    List<IAbstract> handler = generateSubAction(except.getHandler(), dpCxt);

    context.resetDict(mark);

    return FixedList.create(CafeSyntax.catchAction(loc, pickOne(loc, body), pickOne(loc, handler)));
  }

  @Override
  public List<IAbstract> transformForLoop(ForLoopAction loop, CContext context)
  {
    throw new UnsupportedOperationException("for loop not implemented");
  }

  @Override
  public List<IAbstract> transformIgnored(Ignore ignore, CContext context)
  {
    return FixedList.create(CafeSyntax.ignore(ignore.getLoc(), generateExp(ignore.getIgnored(), context)));
  }

  @Override
  public List<IAbstract> transformLetAction(LetAction let, CContext context)
  {
    final Location loc = let.getLoc();
    final IContentAction bound = let.getBoundAction();

    BoundCompiler<List<IAbstract>> compileBound = new BoundCompiler<List<IAbstract>>() {

      @Override
      public List<IAbstract> compileBound(List<IAbstract> definitions, List<IAbstract> doActions, CContext thetaCxt)
      {
        IAbstract boundAct = pickOne(bound.getLoc(), generateSubAction(bound, thetaCxt));
        return FixedList.create(CafeSyntax.letExp(loc, definitions, boundAct));
      }
    };

    return compileThetaEnv(let.getEnvironment(), compileBound, context);
  }

  @Override
  public List<IAbstract> transformWhileLoop(WhileAction loop, CContext cxt)
  {
    Location loc = loop.getLoc();

    int mark = cxt.getMark();
    IAbstract control = loop.getControl().transform(this, cxt);

    IAbstract loopBody = pickOne(loc, generateSubAction(loop.getBody(), cxt));

    cxt.resetDict(mark);
    return FixedList.create(CafeSyntax.whileLoop(loc, control, loopBody));
  }

  @Override
  public List<IAbstract> transformNullAction(NullAction act, CContext context)
  {
    return FixedList.create();
  }

  @Override
  public List<IAbstract> transformProcedureCallAction(ProcedureCallAction call, CContext context)
  {
    IAbstract proc = scopedExpression(call.getProc(), context);
    IAbstract args[] = compileExps(call.getArgs(), context);
    return FixedList.create(CafeSyntax.escape(call.getLoc(), ((Name) proc).getId(), args));
  }

  @Override
  public List<IAbstract> transformRaiseAction(RaiseAction raise, CContext context)
  {
    return FixedList.create(CafeSyntax.throwExp(raise.getLoc(), generateExp(raise.getRaised(), context)));
  }

  @Override
  public List<IAbstract> transformSequence(Sequence sequence, CContext context)
  {
    List<IAbstract> actions = new ArrayList<>();
    CContext extraCxt = context.fork(actions);

    for (IContentAction act : sequence.getActions())
      actions.addAll(act.transform(this, extraCxt));

    return actions;
  }

  @Override
  public List<IAbstract> transformSyncAction(SyncAction sync, CContext cxt)
  {
    Location loc = sync.getLoc();

    IAbstract sel = sync.getSel().transform(this, cxt);

    if (!(sel instanceof Name)) {
      // introduce a new variable, because cafe sync must have a variable
      sel = introduceVariable(loc, cxt, sel, sync.getSel().getType());
    }

    Map<ICondition, IContentAction> syncCases = sync.getBody();
    if (syncCases.containsKey(CompilerUtils.truth)) {
      IAbstract body = pickOne(loc, generateSubAction(syncCases.get(CompilerUtils.truth), cxt));
      return FixedList.create(CafeSyntax.sync(loc, sel, body));
    } else {
      /**
       * We build this loop:
       * 
       * <pre>
       * <emph>sel</emph> sync {
       *   LL:while true do{
       *     if <emph>cond1</emph> then{
       *       <emph>A1</emph>
       *       leave LL
       *     }
       *     else if ...
       *     else
       *       __wait(<emph>sel</emph>)
       *   }
       *   __notify(sel);
       * }
       * </pre>
       */
      String LL = GenSym.genSym("LL");
      IAbstract leave = CafeSyntax.leave(loc, LL);
      IAbstract body = CafeSyntax.escape(loc, Wait.name, sel);
      for (Entry<ICondition, IContentAction> entry : syncCases.entrySet()) {
        IAbstract caseCond = entry.getKey().transform(this, cxt);
        IAbstract caseBody = pickOne(loc, mergeActions(generateSubAction(entry.getValue(), cxt), leave));
        body = CafeSyntax.conditional(loc, caseCond, caseBody, body);
      }
      IAbstract whileStmt = CafeSyntax.whileLoop(loc, CafeSyntax.truth(loc), body);
      whileStmt = CafeSyntax.labeled(loc, LL, whileStmt);
      IAbstract syncBody = CafeSyntax.block(loc, whileStmt, CafeSyntax.escape(loc, Notify.name, sel));
      return FixedList.create(CafeSyntax.sync(loc, sel, syncBody));
    }
  }

  @Override
  public List<IAbstract> transformValisAction(ValisAction valis, CContext cxt)
  {
    IContentExpression value = valis.getValue();
    if (value instanceof ValofExp)
      return ((ValofExp) value).getAction().transform(this, cxt);
    else
      return FixedList.create(CafeSyntax.valis(valis.getLoc(), generateExp(value, cxt)));
  }

  @Override
  public List<IAbstract> transformVarDeclaration(VarDeclaration decl, CContext context)
  {
    return compileVarDeclaration(decl.getLoc(), decl.getPattern(), decl.isReadOnly(), decl.getValue(), context);
  }

  @Override
  public List<IAbstract> transformYield(Yield act, CContext context)
  {
    List<IAbstract> yielded = act.getYielded().transform(this, context);
    Location loc = act.getLoc();
    return FixedList.create(CafeSyntax.yieldAction(loc, pickOne(loc, yielded)));
  }

  // Utilities
  private static IAbstract typeToAbstract(final Location loc, final CContext cxt, IType type)
  {
    TypeAbstract<Boolean> converter = new TypeAbstract<Boolean>(loc) {

      @Override
      public IAbstract transformTypeExp(TypeExp t, Boolean cxt)
      {
        if (TypeUtils.isRecordConstructorType(t)) {
          IAbstract tyCon = t.getTypeCon().transform(this, cxt);
          List<IAbstract> typeArgs = new ArrayList<IAbstract>();
          typeArgs.add(TypeUtils.getConstructorArgType(t).transform(this, false));
          typeArgs.add(TypeUtils.getConstructorResultType(t).transform(this, cxt));

          return CafeSyntax.apply(loc, tyCon, typeArgs);
        } else
          return super.transformTypeExp(t, cxt);
      }

    };
    return converter.convertType(type, true);
  }

  private List<IAbstract> compileExps(List<IContentExpression> args, CContext cxt)
  {
    List<IAbstract> exps = new ArrayList<IAbstract>();
    CContext deepCxt = cxt.fork(true);
    for (IContentExpression arg : args)
      exps.add(generateExp(arg, deepCxt));
    return exps;
  }

  private IAbstract[] compileExps(IContentExpression args[], CContext cxt)
  {
    CContext deepCxt = cxt.fork(true);
    IAbstract exps[] = new IAbstract[args.length];
    for (int ix = 0; ix < args.length; ix++)
      exps[ix] = generateExp(args[ix], deepCxt);
    return exps;
  }

  private IAbstract makeTuple(CContext cxt, IType type, String label, Location loc, List<IAbstract> elExps)
  {
    return CafeSyntax.constructor(loc, label, elExps);
  }

  private IAbstract makeTuple(CContext cxt, Location loc, IAbstract tplType, List<IAbstract> els)
  {
    int arity = els.size();
    String label = TypeUtils.tupleLabel(arity);

    return CafeSyntax.constructor(loc, label, els);
  }

  private static IAbstract pickOne(Location loc, List<IAbstract> els)
  {
    if (els.size() == 1)
      return els.get(0);
    else if (els.isEmpty())
      return CafeSyntax.nothing(loc);
    else
      return CafeSyntax.block(loc, els);
  }

  private List<IAbstract> generateSubAction(IContentAction action, CContext cxt)
  {
    List<IAbstract> extra = new ArrayList<>();
    CContext extraCxt = cxt.fork(extra);

    List<IAbstract> code = action.transform(this, extraCxt);

    return mergeActions(extra, code);
  }

  private static List<IAbstract> mergeActions(List<IAbstract> lhs, IAbstract rhs)
  {
    IAbstract els[] = new IAbstract[lhs.size() + 1];
    int ix = 0;
    for (IAbstract el : lhs)
      els[ix++] = el;
    els[ix] = rhs;
    return FixedList.create(els);
  }

  private static List<IAbstract> mergeActions(List<IAbstract> lhs, List<IAbstract> rhs)
  {
    if (lhs.isEmpty())
      return rhs;
    else if (rhs.isEmpty())
      return lhs;
    else {
      IAbstract els[] = new IAbstract[lhs.size() + rhs.size()];
      int ix = 0;
      for (IAbstract el : lhs)
        els[ix++] = el;
      for (IAbstract el : rhs)
        els[ix++] = el;
      return FixedList.create(els);
    }
  }

  public IAbstract generateExp(IContentExpression exp, CContext cxt)
  {
    IAbstract generated = exp.transform(this, cxt);
    setType((ASyntax) generated, exp.getType());

    if (cxt.isDeep() && !isSimple(generated))
      return introduceVariable(generated.getLoc(), cxt, generated, exp.getType());

    return generated;
  }

  private static boolean isSimple(IAbstract exp)
  {
    return !(exp instanceof Apply) || CafeSyntax.isThrow(exp) || CafeSyntax.isLetExp(exp);
  }

  private IAbstract scopedExpression(IContentExpression exp, CContext cxt)
  {
    List<IAbstract> extra = new ArrayList<>();
    CContext sub = cxt.fork(extra).fork(false);
    IAbstract code = generateExp(exp, sub);

    if (!extra.isEmpty()) {
      Location loc = exp.getLoc();
      extra.add(CafeSyntax.valis(loc, code));
      return CafeSyntax.markTermWithType(CafeSyntax.valof(loc, CafeSyntax.block(loc, extra)), exp.getType());
    } else
      return code;
  }

  private Name introduceVariable(Location loc, CContext cxt, IAbstract val, IType type)
  {
    IAbstract abType = typeToAbstract(loc, cxt, type);

    Name tmp = CafeSyntax.variable(loc, GenSym.genSym("$V"));

    IAbstract lVal = CafeSyntax.typeCast(loc, tmp, abType);
    IAbstract decl = CafeSyntax.isDeclaration(loc, lVal, val);
    cxt.getExtra().add(decl);
    return tmp;
  }

  private List<IAbstract> compileVarDeclaration(Location loc, IContentPattern lval, AccessMode access,
      IContentExpression exp, CContext cxt)
  {
    CContext shallow = cxt.fork(false);
    IAbstract init = scopedExpression(exp, shallow);
    Wrapper<ICondition> condition = Wrapper.create(CompilerUtils.truth);
    CContext condCxt = cxt.fork(condition);
    IAbstract ptn = generatePtn(lval, condCxt);

    final List<IAbstract> res = new ArrayList<IAbstract>();
    switch (access) {
    case readOnly:
      res.add(CafeSyntax.isDeclaration(loc, ptn, init));
      break;
    default:
      res.add(CafeSyntax.varDeclaration(loc, ptn, init));
    }

    ICondition cond = condition.get();
    if (!CompilerUtils.isTrivial(cond))
      res.addAll(generateConditionVarDeclarations(loc, cond, access, cxt));
    return res;
  }

  public IAbstract generatePtn(IContentPattern ptn, CContext cxt)
  {
    IAbstract generated = ptn.transformPattern(this, cxt);
    setType((ASyntax) generated, ptn.getType());

    return generated;
  }

  /*
   * This really is a temporary solution: we should be generating the var declarations directly,
   * rather than reconstructing them from the pattern.
   */
  private List<IAbstract> generateConditionVarDeclarations(Location loc, ICondition cond, AccessMode access,
      CContext cxt)
  {
    if (cond instanceof Conjunction) {
      Conjunction conj = (Conjunction) cond;
      List<IAbstract> lhs = generateConditionVarDeclarations(loc, conj.getLhs(), access, cxt);
      List<IAbstract> rhs = generateConditionVarDeclarations(loc, conj.getRhs(), access, cxt);
      return mergeActions(lhs, rhs);
    } else if (cond instanceof Matches) {
      Matches match = (Matches) cond;
      return compileVarDeclaration(loc, match.getPtn(), access, match.getExp(), cxt);
    } else {
      cxt.getErrors().reportError("unknown condition in variable declaration pattern: " + cond, cond.getLoc());
      return null;
    }
  }

  private interface BoundCompiler<A>
  {
    A compileBound(List<IAbstract> definitions, List<IAbstract> doActions, CContext boundCxt);
  }

  private <A> A compileThetaEnv(List<IStatement> env, BoundCompiler<A> bound, CContext cxt)
  {
    int mark = cxt.getMark();

    List<IAbstract> thetaIns = new ArrayList<IAbstract>();
    List<IAbstract> doActions = new ArrayList<IAbstract>();
    CContext thetaCxt = cxt.fork(doActions);

    for (IStatement stmt : env)
      thetaIns.addAll(stmt.transform(this, thetaCxt));

    A boundExp = bound.compileBound(thetaIns, doActions, cxt);

    cxt.resetDict(mark);
    return boundExp;
  }

  private IAbstract generateType(IAlgebraicType desc, CContext cxt)
  {
    List<IAbstract> conSpecs = new ArrayList<IAbstract>();
    Location loc = desc.getLoc();

    for (IValueSpecifier spec : desc.getValueSpecifiers()) {
      List<IAbstract> conArgs = new ArrayList<IAbstract>();

      if (spec instanceof RecordSpecifier) {
        RecordSpecifier record = (RecordSpecifier) spec;
        Map<String, Integer> index = record.getIndex();
        TypeInterface members = record.getTypeInterface();
        for (int ix = 0; ix < record.arity(); ix++)
          conArgs.add(null); // fill out the arrow, so we can use set. :(

        for (Entry<String, IType> entry : members.getAllFields().entrySet()) {
          IType memberType = entry.getValue();
          String fldName = entry.getKey();
          Name field = new Name(loc, fldName);
          IAbstract fieldType = typeToAbstract(loc, cxt, memberType);
          Integer ix = index.get(fldName);
          conArgs.set(ix, CafeSyntax.typeCast(loc, field, fieldType));
        }

        IAbstract recordSpec = CafeSyntax.record(loc, new Name(loc, record.getLabel()), conArgs);

        for (String eVar : members.getAllTypes().keySet())
          recordSpec = CafeSyntax.existentialType(loc, new Name(loc, eVar), recordSpec);

        conSpecs.add(recordSpec);
      } else {
        IType[] argTypes = TypeUtils.getConstructorArgTypes(TypeUtils.unwrap(spec.getConType()));

        for (int ix = 0; ix < argTypes.length; ix++) {
          IType argType = argTypes[ix];
          conArgs.add(typeToAbstract(loc, cxt, argType));
        }
        conSpecs.add(CafeSyntax.constructorSpec(loc, ((ConstructorSpecifier) spec).getLabel(), conArgs));
      }
    }
    return CafeSyntax.typeDef(loc, typeToAbstract(loc, cxt, TypeUtils.unwrap(desc.getType())), conSpecs);
  }

  private static void setType(ASyntax exp, IType type)
  {
    exp.setAttribute(Names.TYPE, new TypeAttribute(type));
  }
}
