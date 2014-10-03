package org.star_lang.star.compiler.cafe.compile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.star_lang.star.code.CafeCode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.code.repository.RepositoryException;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.cont.CallCont;
import org.star_lang.star.compiler.cafe.compile.cont.FailCont;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.cafe.compile.cont.JumpCont;
import org.star_lang.star.compiler.cafe.compile.cont.NullCont;
import org.star_lang.star.compiler.cafe.compile.cont.PatternCont;
import org.star_lang.star.compiler.cafe.compile.cont.ReturnCont;
import org.star_lang.star.compiler.cafe.compile.cont.ThrowContinuation;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.sources.JavaImport;
import org.star_lang.star.compiler.sources.JavaInfo;
import org.star_lang.star.compiler.sources.NestedBuiltin;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.Triple;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.operators.CafeEnter;
import org.star_lang.star.operators.ICafeBuiltin;

/**
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

public class Theta
{
  @SuppressWarnings("unused")
  private static final String CafeEnterAnnotation = Utils.javaInternalClassName(CafeEnter.class);
  public static final int THIS_OFFSET = 0;
  public static final int FIRST_OFFSET = 1;
  public static final String AssertFlag = "__assert$Enabled";
  public static final int MAX_ARGS = 120;
  public static final int BITS_IN_INTEGER = 32;
  public static final int HASH_PRIME = 37;

  /**
   * Some special rules:
   * <p/>
   * A definition of a variable may be one of:
   * <p/>
   * Ptn is Exp
   * <p/>
   * or
   * <p/>
   * var Ptn := Exp
   * <p/>
   * Ptn may be either
   * <p/>
   * name : type
   * <p/>
   * or a more complex pattern. In the latter case, the right hand side is not permitted to be a
   * function definition.
   * <p/>
   * To do mutual recursion of functions as part of a larger group do:
   * <p/>
   * let{
   * <p/>
   * (f,g) is let{ f(X) is g(X); g(U) is f(U) } in (f,g)
   * <p/>
   * ...
   * <p/>
   * } in ...
   * 
   * @param defs
   *          the definitions in the theta
   * @param outer
   *          the outer dictionary
   * @param endLabel
   *          end label
   * @param inFunction
   *          which function is this part of
   * @param definer
   *          how to declare variables
   * @param loc
   *          source location
   * @param errors
   *          error reporter
   * @param ccxt
   *          context
   * @return spec of result
   */
  public static ISpec compileDefinitions(IList defs, CafeDictionary dict, CafeDictionary outer, LabelNode endLabel,
      String inFunction, Definer definer, IThetaBody bodyCompiler, Location loc, ErrorReport errors, CodeContext ccxt)
  {
    CafeDictionary thetaDict = dict.fork();
    Map<String, CafeDictionary> funDictionaries = new HashMap<>();
    CodeCatalog bldCat = ccxt.getBldCat();
    CodeRepository repository = ccxt.getRepository();

    Triple<List<IAbstract>, List<IAbstract>, List<IAbstract>> partitions = partition(defs, errors);

    List<IAbstract> imports = partitions.left;
    List<List<IAbstract>> sortedTypes = Dependencies.dependencySort(repository, partitions.middle, loc, bldCat, errors);
    List<List<IAbstract>> sortedVars = Dependencies.dependencySort(repository, partitions.right, loc, bldCat, errors);

    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();

    // Handle imports
    for (IAbstract def : imports)
      if (CafeSyntax.isImport(def))
        CompileCafe.pkgImport(repository, def, thetaDict, errors);
      else if (CafeSyntax.isJavaImport(def))
        javaImport(CafeSyntax.javaImportClass(def), thetaDict, errors, def.getLoc(), repository);

    // Handle types
    for (List<IAbstract> group : sortedTypes) {
      for (IAbstract def : group)
        if (CafeSyntax.isTypeDef(def))
          TypeAnalyser.defineType(CafeSyntax.typeDefType(def), thetaDict, errors);

      for (IAbstract def : group)
        if (CafeSyntax.isTypeDef(def))
          bodyCompiler.introduceType(Constructors.compileTypeDef(def, thetaDict, errors, ccxt));
    }

    for (List<IAbstract> group : sortedVars) {
      // Three phases for the implementation of a group in a theta environment
      checkMixedGroup(group, errors);

      // First phase: declare variables
      for (IAbstract def : group)
        declareProgram(def, repository, definer, errors, thetaDict);

      // Second phase: compile programs
      for (IAbstract def : group) {
        Actions.doLineNumber(def.getLoc(), mtd);
        compileDef(def, repository, bldCat, outer, hwm, mtd, endLabel, inFunction, definer, bodyCompiler, errors,
            thetaDict, funDictionaries, ccxt);
      }

      // Third phase: fix up program definitions
      for (IAbstract def : group) {
        Location defLoc = def.getLoc();
        if (CafeSyntax.isFunctionDefn(def)) {
          String funName = CafeSyntax.definedFunctionName(def);
          VarInfo fun = thetaDict.find(funName);
          assert fun != null;
          handleFixups(fun, funDictionaries.get(funName), thetaDict, outer, bldCat, mtd, hwm, defLoc, errors);
        } else if (CafeSyntax.isPatternDefn(def)) {
          String pttrnName = CafeSyntax.definedPatternName(def);
          VarInfo prc = thetaDict.find(pttrnName);
          assert prc != null;
          handleFixups(prc, funDictionaries.get(pttrnName), thetaDict, outer, bldCat, mtd, hwm, defLoc, errors);
        } else if (CafeSyntax.isMemoDefn(def)) {
          String thunkName = CafeSyntax.definedMemoName(def);
          VarInfo var = thetaDict.find(thunkName);
          assert var != null;
          handleFixups(var, funDictionaries.get(thunkName), thetaDict, outer, bldCat, mtd, hwm, defLoc, errors);
        }
      }
    }

    // Finally call the local init procedure if it is defined
    CafeDictionary localDo = funDictionaries.get(Names.DO);
    if (localDo != null) {
      VarInfo var = Theta.varReference(Names.DO, thetaDict, outer, loc, errors);
      InsnList ins = mtd.instructions;

      if (var != null && var.getKind() == JavaKind.general) {
        CallCont callCont = new CallCont(ins, new NullCont());

        Expressions.compileFunCall(loc, var, CafeSyntax.tuple(loc, CafeSyntax.tupleType(loc)), errors, dict, outer,
            inFunction, callCont, null, ccxt);
      } else
        errors.reportError(Names.DO + " not declared", loc);
    }

    ISpec reslt = bodyCompiler.compile(thetaDict, bldCat, errors, repository);

    dict.migrateFreeVars(thetaDict);
    return reslt;
  }

  private static Triple<List<IAbstract>, List<IAbstract>, List<IAbstract>> partition(IList defs, ErrorReport errors)
  {
    List<IAbstract> types = new ArrayList<>();
    List<IAbstract> imports = new ArrayList<>();
    List<IAbstract> vars = new ArrayList<>();

    for (IValue d : defs) {
      IAbstract def = (IAbstract) d;
      if (CafeSyntax.isImport(def))
        imports.add(def);
      else if (CafeSyntax.isTypeDef(def))
        types.add(def);
      else if (CafeSyntax.isJavaImport(def))
        imports.add(def);
      else if (CafeSyntax.isIsDeclaration(def) || CafeSyntax.isVarDeclaration(def))
        vars.add(def);
      else
        errors.reportError("invalid element of theta: " + def, def.getLoc());
    }

    return Triple.create(imports, types, vars);
  }

  private static void compileDef(IAbstract def, CodeRepository repository, CodeCatalog bldCat, CafeDictionary outer,
      HWM hwm, MethodNode mtd, LabelNode endLabel, String inFunction, Definer definer, IThetaBody bodyCompiler,
      ErrorReport errors, CafeDictionary thetaDict, Map<String, CafeDictionary> funDictionaries, CodeContext ccxt)
  {
    if (CafeSyntax.isTypeDef(def))
      bodyCompiler.introduceType(Constructors.compileTypeDef(def, thetaDict, errors, ccxt));
    else if (CafeSyntax.isFunctionDefn(def))
      compileFunction(repository, def, bldCat, errors, thetaDict, funDictionaries, ccxt);
    else if (CafeSyntax.isPatternDefn(def))
      compileNamedPattern(repository, def, bldCat, errors, thetaDict, funDictionaries, ccxt);
    else if (CafeSyntax.isMemoDefn(def))
      compileMemo(repository, def, bldCat, errors, endLabel, thetaDict, funDictionaries, ccxt);
    else if (CafeSyntax.isIsDeclaration(def)) {
      // Ptn is Value
      int mark = hwm.bump(0);
      LabelNode next = new LabelNode();
      IAbstract lv = CafeSyntax.isDeclLval(def);
      IAbstract exp = CafeSyntax.isDeclValue(def);

      declareArg(lv, AccessMode.readOnly, false, thetaDict, errors, definer, 0);

      Expressions.compileExp(exp, errors, thetaDict, outer, inFunction, new PatternCont(lv, thetaDict, outer,
          AccessMode.readOnly, mtd, endLabel, errors, new JumpCont(next),
          new ThrowContinuation("initialization failed")), null, ccxt);

      Utils.jumpTarget(mtd.instructions, next);
      hwm.reset(mark);
    } else if (CafeSyntax.isVarDeclaration(def)) {
      // var Ptn := Value
      int mark = hwm.bump(0);
      LabelNode next = new LabelNode();
      IAbstract lv = CafeSyntax.varDeclLval(def);

      declareArg(lv, AccessMode.readWrite, false, thetaDict, errors, definer, 0);

      Expressions.compileExp(CafeSyntax.varDeclValue(def), errors, thetaDict, outer, inFunction, new PatternCont(lv,
          thetaDict, outer, AccessMode.readWrite, mtd, endLabel, errors, new JumpCont(next), new ThrowContinuation(
              "initialization failed")), null, ccxt);

      Utils.jumpTarget(mtd.instructions, next);
      hwm.reset(mark);
    }
  }

  private static void declareProgram(IAbstract def, CodeRepository repository, Definer definer, ErrorReport errors,
      CafeDictionary thetaDict)
  {
    if (CafeSyntax.isImport(def))
      CompileCafe.pkgImport(repository, def, thetaDict, errors);
    else if (CafeSyntax.isTypeDef(def))
      TypeAnalyser.defineType(CafeSyntax.typeDefType(def), thetaDict, errors);
    else if (CafeSyntax.isFunctionDefn(def))
      declareArg(CafeSyntax.functionLval(def), AccessMode.readOnly, true, thetaDict, errors, definer, 0);
    else if (CafeSyntax.isPatternDefn(def))
      declareArg(CafeSyntax.patternLval(def), AccessMode.readOnly, true, thetaDict, errors, definer, 0);
    else if (CafeSyntax.isMemoDefn(def))
      declareArg(CafeSyntax.memoLval(def), AccessMode.readOnly, true, thetaDict, errors, definer, 0);
    else if (CafeSyntax.isIsDeclaration(def))
      ;
    else if (CafeSyntax.isVarDeclaration(def))
      ;
    else if (CafeSyntax.isJavaImport(def))
      javaImport(CafeSyntax.javaImportClass(def), thetaDict, errors, def.getLoc(), repository);
    else
      errors.reportError("invalid element of theta: " + def, def.getLoc());
  }

  private static void javaImport(String className, CafeDictionary thetaDict, ErrorReport errors, Location loc,
      CodeRepository repository)
  {
    try {
      JavaInfo javaInfo = repository.locateJava(className);
      for (Entry<String, ICafeBuiltin> entry : javaInfo.getMethods().entrySet()) {
        ICafeBuiltin builtin = entry.getValue();
        String name = builtin instanceof NestedBuiltin ? entry.getKey() : JavaImport.javaName(entry.getKey());
        thetaDict.declare(name, new VarInfo(loc, name, true, builtin.isStatic() ? VarSource.staticMethod
            : VarSource.field, null, JavaKind.builtin, 0, AccessMode.readOnly, builtin.getType(),
            builtin.getJavaName(), null, builtin.getJavaType(), builtin.getJavaSig(), builtin.getJavaInvokeSignature(),
            builtin.getJavaInvokeName(), builtin.getJavaType()));
      }
      for (ITypeDescription type : javaInfo.getTypes())
        thetaDict.defineType(type);
    } catch (RepositoryException e) {
      errors.reportError("could not import java class " + className, loc);
    }
  }

  private static void checkMixedGroup(List<IAbstract> defs, ErrorReport errors)
  {
    boolean hasPrograms = false;
    boolean hasTypes = false;

    for (IAbstract def : defs) {
      if (CafeSyntax.isTypeDef(def))
        hasTypes = true;
      else if (CafeSyntax.isFunctionDefn(def))
        hasPrograms = true;
      else if (CafeSyntax.isPatternDefn(def))
        hasPrograms = true;
      else if (CafeSyntax.isMemoDefn(def))
        hasPrograms = true;
    }

    if (hasPrograms) {
      for (IAbstract def : defs) {
        if (CafeSyntax.isImport(def))
          errors.reportError("programs depend on imports, which depend on programs defined at " + showDeps(defs, def),
              def.getLoc());
        else if (CafeSyntax.isTypeDef(def) || CafeSyntax.isFunctionDefn(def) || CafeSyntax.isPatternDefn(def)
            || CafeSyntax.isMemoDefn(def))
          ;
        else if (CafeSyntax.isIsDeclaration(def))
          errors.reportError("variable: " + CafeSyntax.isDeclLval(def) + " depends on programs defined at "
              + showDeps(defs, def), def.getLoc());
        else if (CafeSyntax.isVarDeclaration(def))
          errors.reportError("variable: " + CafeSyntax.varDeclLval(def)
              + " depends on programs which depend on it, defined at " + showDeps(defs, def), def.getLoc());
      }
    } else if (defs.size() > 1 && !hasTypes)
      errors.reportError("group of variables, defined at " + showDeps(defs, null)
          + " are mutually dependent on each other without being programs", defs.get(0).getLoc());
  }

  private static String showDeps(List<IAbstract> defs, IAbstract ignore)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    for (int ix = 0; ix < defs.size(); ix++) {
      IAbstract def = defs.get(ix);
      if (!def.equals(ignore)) {
        def.getLoc().prettyPrint(disp);
        if (ix < defs.size() - 1)
          disp.append(", ");
        else
          disp.append(" and ");
      }
    }
    return disp.toString();
  }

  public static boolean mtdHasLocalVar(MethodNode mtd, String name)
  {
    List<LocalVariableNode> localVariables = mtd.localVariables;
    for (LocalVariableNode v : localVariables) {
      if (v.name.equals(name))
        return true;
    }
    return false;
  }

  public interface IThetaBody
  {
    ISpec compile(CafeDictionary thetaDict, CodeCatalog bldCat, ErrorReport errors, CodeRepository repository);

    void introduceType(CafeTypeDescription type);
  }

  private static void compileFunction(CodeRepository repository, IAbstract def, CodeCatalog bldCat, ErrorReport errors,
      CafeDictionary thetaDict, Map<String, CafeDictionary> funDictionaries, CodeContext ocxt)
  {
    assert CafeSyntax.isFunctionDefn(def);

    Location loc = def.getLoc();
    String funName = CafeSyntax.definedFunctionName(def);

    VarInfo var = thetaDict.find(funName);
    assert var != null;

    IType funType = var.getType();
    IType refreshed = Freshen.freshenForUse(funType);

    var.setInited(true);

    String javaName = CompileCafe.extendPath(thetaDict.getPath(), munge(Utils.javaIdentifierOf(funName)));
    String javaSig = var.getJavaSig();
    String javaInvokeSig = var.getJavaInvokeSig();
    String javaInvokeName = var.getJavaInvokeName();

    ClassNode closure = new ClassNode();
    IList args = CafeSyntax.functionArgs(def);
    IAbstract exp = CafeSyntax.functionExp(def);

    HWM hwm = new HWM();

    closure.version = Opcodes.V1_6;
    closure.access = Opcodes.ACC_PUBLIC;
    closure.name = javaName;
    closure.sourceFile = loc.getSrc();
    closure.signature = javaSig;

    CafeDictionary funDict = thetaDict.funDict(thetaDict.getPath(), closure);
    funDictionaries.put(funName, funDict);

    IType resType = TypeUtils.getFunResultType(refreshed);
    ISpec resSpec = SrcSpec.generic(loc, resType, thetaDict, repository, errors);
    closure.superName = Utils.javaInternalClassName(Object.class);
    closure.interfaces.addAll(functionTypeSignatures(thetaDict, refreshed, ocxt, bldCat, errors, loc));
    closure.interfaces.add(Types.IFUNC);
    closure.interfaces.add(Types.IVALUE);

    MethodNode funMtd = new MethodNode(Opcodes.ACC_PUBLIC, Names.ENTER, javaInvokeSig, javaInvokeSig, new String[] {});

    LabelNode firstLabel = new LabelNode();
    LabelNode endFunLabel = new LabelNode();

    InsnList funIns = funMtd.instructions;
    Actions.doLineNumber(loc, funMtd);
    funIns.add(firstLabel);

    List<LocalVariableNode> localVariables = funMtd.localVariables;

    localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, closure.signature, null, firstLabel, endFunLabel,
        THIS_OFFSET));
    funDict.declareLocal(loc, Names.PRIVATE_THIS, true, funType, javaName, closure.signature, javaInvokeSig,
        javaInvokeName, AccessMode.readOnly);

    CodeContext funCxt = ocxt.fork(closure, funMtd, hwm, funDict.getLocalAvail());

    defineArgs(args, loc, bldCat, errors, hwm, funDict, funMtd, endFunLabel, false, funCxt);

    Expressions.compileExp(exp, errors, funDict, thetaDict, funName, new ReturnCont(resType, resSpec, funDict), null,
        funCxt);
    funMtd.instructions.add(endFunLabel);

    funMtd.maxLocals = funDict.getLocalHWM();
    funMtd.maxStack = hwm.getHwm();

    closure.methods.add(funMtd);

    // Implement the IFunc interface...
    closure.methods.add(IFuncImplementation.ifunc(loc, javaName, javaInvokeSig, thetaDict, TypeUtils
        .getFunArgTypes(refreshed), resType, bldCat, errors));

    MethodNode constructor = closureConstructor(loc, thetaDict, errors, closure, funDict);
    closure.methods.add(constructor);
    closure.methods.add(genHashCode(loc, javaName, thetaDict, errors, closure, funDict));

    Constructors.genFunctionVisit(closure);
    closure.methods.add(TypeGen.genType(loc, funType, closure, thetaDict, errors, repository, bldCat));

    // set up static initializers for any builtins
    setupReferenceInitializers(funCxt, funDict.getBuiltinReferences(), loc);

    funCxt.installInitMtd();

    CompileCafe.genByteCode(javaName, loc, closure, bldCat, errors);

    MethodNode mtd = ocxt.getMtd();
    HWM outerHwm = ocxt.getMtdHwm();

    int mark = outerHwm.bump(2);
    mtd.instructions.add(new TypeInsnNode(Opcodes.NEW, closure.name));
    mtd.instructions.add(new InsnNode(Opcodes.DUP));
    mtd.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, closure.name, Types.INIT, Types.VOID_SIG));

    assert mtdHasLocalVar(mtd, var.getJavaSafeName());

    var.storeValue(mtd, outerHwm, thetaDict);
    outerHwm.reset(mark);
  }

  public static ISpec compileLambda(IAbstract lambda, IType funType, ErrorReport errors, CafeDictionary dict,
      CafeDictionary outer, CodeContext ccxt)
  {
    assert CafeSyntax.isLambda(lambda);
    String funName = GenSym.genSym("lambda");
    CodeCatalog bldCat = ccxt.getBldCat();
    CodeRepository repository = ccxt.getRepository();

    Location loc = lambda.getLoc();

    IType refreshed = Freshen.freshenForUse(funType);

    ISpec lambdaSpec = SrcSpec.generic(loc, funType, dict, repository, errors);

    String javaName = CompileCafe.extendPath(dict.getPath(), munge(Utils.javaIdentifierOf(funName)));
    String javaSig = lambdaSpec.getJavaSig();
    String javaInvokeSig = lambdaSpec.getJavaInvokeSig();
    String javaInvokeName = lambdaSpec.getJavaInvokeName();

    ClassNode closure = new ClassNode();
    IList args = CafeSyntax.lambdaArgs(lambda);
    IAbstract exp = CafeSyntax.typedTerm(CafeSyntax.lambdaValue(lambda));

    HWM hwm = new HWM();

    closure.version = Opcodes.V1_6;
    closure.access = Opcodes.ACC_PUBLIC;
    closure.name = javaName;
    closure.sourceFile = loc.getSrc();
    closure.signature = javaSig;

    CafeDictionary funDict = dict.funDict(dict.getPath(), closure);

    IType resType = TypeUtils.getFunResultType(refreshed);
    ISpec resSpec = SrcSpec.generic(loc, resType, dict, repository, errors);
    closure.superName = Utils.javaInternalClassName(Object.class);
    closure.interfaces.addAll(functionTypeSignatures(dict, refreshed, ccxt, bldCat, errors, loc));
    closure.interfaces.add(Types.IFUNC);
    closure.interfaces.add(Types.IVALUE);

    MethodNode funMtd = new MethodNode(Opcodes.ACC_PUBLIC, Names.ENTER, javaInvokeSig, javaInvokeSig, new String[] {});
    CodeContext lcxt = ccxt.fork(closure, funMtd, hwm, funDict.getLocalAvail());

    LabelNode firstLabel = new LabelNode();
    LabelNode endFunLabel = new LabelNode();

    InsnList funIns = funMtd.instructions;
    Actions.doLineNumber(loc, funMtd);
    funIns.add(firstLabel);

    List<LocalVariableNode> localVariables = funMtd.localVariables;

    localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, closure.signature, null, firstLabel, endFunLabel,
        THIS_OFFSET));
    funDict.declareLocal(loc, Names.PRIVATE_THIS, true, funType, javaName, closure.signature, javaInvokeSig,
        javaInvokeName, AccessMode.readOnly);

    defineArgs(args, loc, bldCat, errors, hwm, funDict, funMtd, endFunLabel, false, ccxt);

    Expressions.compileExp(exp, errors, funDict, dict, funName, new ReturnCont(resType, resSpec, funDict), null, lcxt);
    funMtd.instructions.add(endFunLabel);

    funMtd.maxLocals = funDict.getLocalHWM();
    funMtd.maxStack = hwm.getHwm();

    closure.methods.add(funMtd);

    // Implement the IFunc interface...
    closure.methods.add(IFuncImplementation.ifunc(loc, javaName, javaInvokeSig, dict, TypeUtils
        .getFunArgTypes(refreshed), resType, bldCat, errors));

    MethodNode constructor = closureConstructor(loc, dict, errors, closure, funDict);
    closure.methods.add(constructor);
    closure.methods.add(genHashCode(loc, javaName, dict, errors, closure, funDict));

    Constructors.genFunctionVisit(closure);
    closure.methods.add(TypeGen.genType(loc, funType, closure, dict, errors, repository, bldCat));

    // set up static initializers for any builtins
    setupReferenceInitializers(lcxt, funDict.getBuiltinReferences(), loc);
    lcxt.installInitMtd();

    CompileCafe.genByteCode(javaName, loc, closure, bldCat, errors);
    MethodNode mtd = ccxt.getMtd();
    HWM stackHWM = ccxt.getMtdHwm();

    int oMark = stackHWM.bump(2);
    mtd.instructions.add(new TypeInsnNode(Opcodes.NEW, closure.name));
    mtd.instructions.add(new InsnNode(Opcodes.DUP));
    mtd.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, closure.name, Types.INIT, Types.VOID_SIG));

    // handle free variables

    for (Entry<String, VarInfo> entry : funDict.allEntries().entrySet()) {
      String vrName = entry.getKey();
      VarInfo info = entry.getValue();
      if (info.getWhere() == VarSource.freeVar) {
        VarInfo free = Theta.varReference(vrName, dict, outer, loc, errors);
        int mark = stackHWM.bump(2);
        mtd.instructions.add(new InsnNode(Opcodes.DUP));

        free.loadValue(mtd, hwm, dict);

        Expressions.checkType(info, free, mtd, funDict, stackHWM, loc, errors, bldCat);

        // We are updating the owner of the variable...
        mtd.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, funDict.getOwnerName(), free.getJavaSafeName(), free
            .getJavaSig()));
        stackHWM.reset(mark);
      }
    }

    stackHWM.reset(oMark);
    stackHWM.bump(1);
    return lambdaSpec;
  }

  static IType computeLambdaType(IAbstract lambda, CafeDictionary dict, ErrorReport errors)
  {
    assert CafeSyntax.isTypedTerm(CafeSyntax.lambdaValue(lambda));

    IType resltType = TypeAnalyser.parseType(CafeSyntax.typedType(CafeSyntax.lambdaValue(lambda)), dict, errors);

    IList args = CafeSyntax.lambdaArgs(lambda);

    List<IType> argTypes = new ArrayList<>();

    for (int ix = 0; ix < args.size(); ix++) {
      IAbstract arg = (IAbstract) args.getCell(ix);

      if (!CafeSyntax.isTypedTerm(arg) && Abstract.isName(CafeSyntax.typedTerm(arg)))
        errors.reportError("expecting a variable, not " + arg, arg.getLoc());
      else
        argTypes.add(TypeAnalyser.parseType(CafeSyntax.typedType(arg), dict, errors));
    }

    return TypeUtils.functionType(argTypes, resltType);
  }

  public static void defineArgs(IList args, Location loc, CodeCatalog bldCat, ErrorReport errors, HWM hwm,
      CafeDictionary funDict, MethodNode funMtd, LabelNode endFunLabel, boolean isStatic, CodeContext ccxt)
  {
    final Definer definer;

    if (args.size() < MAX_ARGS)
      definer = new ArgDefiner(funMtd, hwm, endFunLabel, bldCat, ccxt);
    else {
      VarInfo argArray = new VarInfo(loc, Names.ARG_ARRAY, true, VarSource.localVar, null, JavaKind.general,
          FIRST_OFFSET, AccessMode.readOnly, TypeUtils.arrayType(StandardTypes.anyType), Utils
              .javaIdentifierOf(Names.ARG_ARRAY), null, Types.IVALUE_ARRAY, Types.IVALUE_ARRAY, null, null, funDict
              .getOwnerName());
      definer = new LongArgDefiner(argArray, bldCat, ccxt.getRepository());
    }

    for (int ix = 0; ix < args.size(); ix++) {
      IAbstract arg = (IAbstract) args.getCell(ix);

      if (!CafeSyntax.isTypedTerm(arg) && Abstract.isName(CafeSyntax.typedTerm(arg)))
        errors.reportError("expecting a variable, not " + arg, arg.getLoc());
      else {
        String id = ((Name) CafeSyntax.typedTerm(arg)).getId();
        IType varType = TypeAnalyser.parseType(CafeSyntax.typedType(arg), funDict, errors);
        definer.declareArg(loc, id, ix, varType, funDict, AccessMode.readOnly, true, errors);
      }
    }
  }

  public static void compileMemo(CodeRepository repository, IAbstract def, CodeCatalog bldCat, ErrorReport errors,
      LabelNode endLabel, CafeDictionary thetaDict, Map<String, CafeDictionary> funDictionaries, CodeContext ccxt)
  {
    assert CafeSyntax.isMemoDefn(def);

    Location loc = def.getLoc();
    String funName = CafeSyntax.definedMemoName(def);

    VarInfo var = thetaDict.find(funName);
    assert var != null;
    var.setInited(true);

    IType memoType = var.getType();
    IType refreshed = Freshen.freshenForUse(memoType);
    IType resType = TypeUtils.getFunResultType(refreshed);

    String javaName = CompileCafe.extendPath(thetaDict.getPath(), munge(Utils.javaIdentifierOf(funName)));
    String javaSig = var.getJavaSig();
    String javaInvokeSig = "()" + Types.IVALUE_SIG;
    String javaInvokeName = Names.ENTER;

    ClassNode closure = new ClassNode();
    IAbstract exp = CafeSyntax.memoExp(def);

    HWM hwm = new HWM();

    closure.version = Opcodes.V1_6;
    closure.access = Opcodes.ACC_PUBLIC;
    closure.name = javaName;
    closure.sourceFile = loc.getSrc();
    closure.signature = javaSig;

    CafeDictionary funDict = thetaDict.funDict(thetaDict.getPath(), closure);
    funDictionaries.put(funName, funDict);

    ISpec resSpec = SrcSpec.generic(loc, resType, thetaDict, repository, errors);
    closure.superName = Utils.javaInternalClassName(Object.class);
    closure.interfaces.addAll(functionTypeSignatures(thetaDict, refreshed, ccxt, bldCat, errors, loc));
    closure.interfaces.add(Types.IFUNC);
    closure.interfaces.add(Types.IVALUE);

    addField(closure, var.getJavaSafeName(), resSpec.getJavaSig(), 0);

    MethodNode funMtd = new MethodNode(Opcodes.ACC_PUBLIC, Names.ENTER, javaInvokeSig, javaInvokeSig, new String[] {});
    LabelNode firstLabel = new LabelNode();
    LabelNode endFunLabel = new LabelNode();

    LabelNode syncStart = new LabelNode();
    LabelNode syncEnd = new LabelNode();
    LabelNode syncExcept = new LabelNode();

    InsnList ins = funMtd.instructions;

    CodeContext memoCxt = ccxt.fork(closure, funMtd, hwm, funDict.getLocalAvail());

    Actions.doLineNumber(loc, funMtd);
    ins.add(firstLabel);

    List<LocalVariableNode> localVariables = funMtd.localVariables;

    localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, closure.signature, null, firstLabel, endFunLabel,
        THIS_OFFSET));
    funDict.declareLocal(loc, Names.PRIVATE_THIS, true, memoType, javaName, closure.signature, javaInvokeSig,
        javaInvokeName, AccessMode.readOnly);

    int mark = hwm.bump(3);
    ins.add(syncStart);
    ins.add(new VarInsnNode(Opcodes.ALOAD, THIS_OFFSET));

    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new InsnNode(Opcodes.MONITORENTER));

    ins.add(new FieldInsnNode(Opcodes.GETFIELD, javaName, var.getJavaSafeName(), resSpec.getJavaSig()));
    ins.add(new InsnNode(Opcodes.DUP));

    LabelNode evalLabel = new LabelNode();
    ins.add(new JumpInsnNode(Opcodes.IFNULL, evalLabel));
    // remove the lock
    ins.add(new VarInsnNode(Opcodes.ALOAD, THIS_OFFSET));
    ins.add(new InsnNode(Opcodes.MONITOREXIT));

    ins.add(new InsnNode(Opcodes.ARETURN));

    ins.add(evalLabel);
    ins.add(new InsnNode(Opcodes.POP));
    hwm.reset(mark);

    Expressions.compileExp(exp, errors, funDict, thetaDict, funName, new JumpCont(endFunLabel), null, memoCxt);
    Utils.jumpTarget(ins, endFunLabel);

    hwm.bump(2);
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new VarInsnNode(Opcodes.ALOAD, THIS_OFFSET));
    ins.add(new InsnNode(Opcodes.SWAP));
    ins.add(new TypeInsnNode(Opcodes.CHECKCAST, resSpec.getJavaType()));
    ins.add(new FieldInsnNode(Opcodes.PUTFIELD, javaName, var.getJavaSafeName(), resSpec.getJavaSig()));

    ins.add(syncEnd);
    ins.add(new VarInsnNode(Opcodes.ALOAD, THIS_OFFSET));
    ins.add(new InsnNode(Opcodes.MONITOREXIT));
    ins.add(new InsnNode(Opcodes.ARETURN));

    ins.add(syncExcept);
    ins.add(new VarInsnNode(Opcodes.ALOAD, THIS_OFFSET));
    ins.add(new InsnNode(Opcodes.MONITOREXIT));
    ins.add(new InsnNode(Opcodes.ATHROW));

    funMtd.tryCatchBlocks.add(new TryCatchBlockNode(syncStart, syncEnd, syncExcept, null));

    funMtd.maxLocals = funDict.getLocalHWM();
    funMtd.maxStack = hwm.getHwm();

    closure.methods.add(funMtd);

    // Implement the IFunc interface...
    closure.methods.add(IFuncImplementation.ifunc(loc, javaName, javaInvokeSig, thetaDict, new IType[] {}, resType,
        bldCat, errors));

    MethodNode constructor = closureConstructor(loc, thetaDict, errors, closure, funDict);
    closure.methods.add(constructor);
    closure.methods.add(genHashCode(loc, javaName, thetaDict, errors, closure, funDict));

    Constructors.genFunctionVisit(closure);
    closure.methods.add(TypeGen.genType(loc, memoType, closure, thetaDict, errors, repository, bldCat));

    // set up static initializers for any builtins
    setupReferenceInitializers(memoCxt, funDict.getBuiltinReferences(), loc);

    CompileCafe.genByteCode(javaName, loc, closure, bldCat, errors);

    MethodNode mtd = ccxt.getMtd();
    HWM stackHWM = ccxt.getMtdHwm();

    mark = stackHWM.bump(2);
    mtd.instructions.add(new TypeInsnNode(Opcodes.NEW, closure.name));
    mtd.instructions.add(new InsnNode(Opcodes.DUP));
    mtd.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, closure.name, Types.INIT, Types.VOID_SIG));

    assert mtdHasLocalVar(mtd, var.getJavaSafeName());

    var.storeValue(mtd, stackHWM, thetaDict);
    stackHWM.reset(mark);
  }

  public static void compileNamedPattern(CodeRepository repository, IAbstract def, CodeCatalog bldCat,
      ErrorReport errors, CafeDictionary thetaDict, Map<String, CafeDictionary> funDictionaries, CodeContext ccxt)
  {
    assert CafeSyntax.isPatternDefn(def);

    Location loc = def.getLoc();
    String pttrnName = CafeSyntax.definedPatternName(def);

    VarInfo var = thetaDict.find(pttrnName);
    assert var != null;
    var.setInited(true);

    IType pttrnType = var.getType();
    IType refreshed = Freshen.freshenForUse(pttrnType);
    assert TypeUtils.isPatternType(refreshed);

    String javaName = CompileCafe.extendPath(thetaDict.getPath(), munge(Utils.javaIdentifierOf(pttrnName)));
    String javaSig = var.getJavaSig();
    String javaInvokeSig = var.getJavaInvokeSig();
    String javaInvokeName = var.getJavaInvokeName();

    ClassNode closure = new ClassNode();

    HWM hwm = new HWM();

    closure.version = Opcodes.V1_6;
    closure.access = Opcodes.ACC_PUBLIC;
    closure.name = javaName;
    closure.sourceFile = loc.getSrc();
    closure.signature = javaSig;

    closure.superName = Utils.javaInternalClassName(Object.class);
    closure.interfaces.addAll(patternTypeSignatures(thetaDict, refreshed, ccxt, bldCat, errors, loc));
    closure.interfaces.add(Types.IPATTERN);
    closure.interfaces.add(Types.IVALUE);

    CafeDictionary pttrnDict = thetaDict.funDict(thetaDict.getPath(), closure);
    funDictionaries.put(pttrnName, pttrnDict);

    MethodNode matchMtd = new MethodNode(Opcodes.ACC_PUBLIC, Names.MATCH, javaInvokeSig, javaInvokeSig, new String[] {});
    CodeContext mcxt = ccxt.fork(closure, matchMtd, hwm, pttrnDict.getLocalAvail());

    LabelNode firstLabel = new LabelNode();
    LabelNode endFunLabel = new LabelNode();

    InsnList ins = matchMtd.instructions;
    ins.add(firstLabel);

    List<LocalVariableNode> localVariables = matchMtd.localVariables;

    localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, closure.signature, null, firstLabel, endFunLabel,
        THIS_OFFSET));
    pttrnDict.declareLocal(loc, Names.PRIVATE_THIS, true, pttrnType, javaName, closure.signature, javaInvokeSig,
        javaInvokeName, AccessMode.readOnly);

    IContinuation fail = new FailCont();

    IType ptnType = TypeUtils.getPtnMatchType(refreshed);
    ISpec ptnSpec = SrcSpec.typeSpec(loc, ptnType, thetaDict, bldCat, errors);

    IType resType = TypeUtils.getPtnResultType(refreshed);
    ISpec resSpec = SrcSpec.typeSpec(loc, resType, thetaDict, bldCat, errors);

    declareArg(loc, Names.PTN_ARG, ptnType, AccessMode.readOnly, true, matchMtd, hwm, pttrnDict, endFunLabel, errors,
        bldCat, repository);

    // Load up the pattern argument to match against
    LabelNode testLabel = new LabelNode();
    Expressions.compileExp(Abstract.name(loc, Names.PTN_ARG), errors, pttrnDict, thetaDict, pttrnName, new JumpCont(
        testLabel), null, mcxt);
    Utils.jumpTarget(ins, testLabel);

    LabelNode firstOkLabel = new LabelNode();
    IAbstract ptn = CafeSyntax.patternPtn(def);

    Patterns.compilePttrn(ptn, AccessMode.readOnly, ptnSpec, pttrnDict, thetaDict, endFunLabel, errors, new JumpCont(
        firstOkLabel), fail, mcxt);

    Utils.jumpTarget(ins, firstOkLabel);
    Expressions.compileExp(CafeSyntax.patternResult(def), errors, pttrnDict, thetaDict, pttrnName, new ReturnCont(
        resType, resSpec, pttrnDict), null, mcxt);

    ins.add(endFunLabel);

    matchMtd.maxLocals = pttrnDict.getLocalHWM();
    matchMtd.maxStack = hwm.getHwm();

    closure.methods.add(matchMtd);

    MethodNode constructor = closureConstructor(loc, thetaDict, errors, closure, pttrnDict);
    closure.methods.add(constructor);
    closure.methods.add(genHashCode(loc, javaName, thetaDict, errors, closure, pttrnDict));

    Constructors.genPatternVisit(closure);
    closure.methods.add(TypeGen.genType(loc, pttrnType, closure, thetaDict, errors, repository, bldCat));

    // set up static initializers for any builtins
    setupReferenceInitializers(mcxt, pttrnDict.getBuiltinReferences(), loc);
    mcxt.installInitMtd();

    CompileCafe.genByteCode(javaName, loc, closure, bldCat, errors);

    MethodNode outerMtd = ccxt.getMtd();
    HWM outerHWM = ccxt.getMtdHwm();

    int mark = outerHWM.bump(2);
    outerMtd.instructions.add(new TypeInsnNode(Opcodes.NEW, closure.name));
    outerMtd.instructions.add(new InsnNode(Opcodes.DUP));
    outerMtd.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, closure.name, Types.INIT, Types.VOID_SIG));

    assert mtdHasLocalVar(outerMtd, var.getJavaSafeName());

    var.storeValue(outerMtd, outerHWM, thetaDict);

    outerHWM.reset(mark);
  }

  public static ISpec compilePattern(IAbstract def, ErrorReport errors, CafeDictionary dict, CodeContext ocxt)
  {
    assert CafeSyntax.isPatternDefn(def);

    Location loc = def.getLoc();
    CodeCatalog bldCat = ocxt.getBldCat();
    CodeRepository repository = ocxt.getRepository();

    String pttrnName = CafeSyntax.definedPatternName(def);

    VarInfo var = dict.find(pttrnName);
    assert var != null;
    var.setInited(true);

    IType pttrnType = var.getType();
    IType refreshed = Freshen.freshenForUse(pttrnType);
    assert TypeUtils.isPatternType(refreshed);

    ISpec lambdaSpec = SrcSpec.generic(loc, pttrnType, dict, repository, errors);

    String javaName = CompileCafe.extendPath(dict.getPath(), munge(Utils.javaIdentifierOf(pttrnName)));
    String javaSig = var.getJavaSig();
    String javaInvokeSig = var.getJavaInvokeSig();
    String javaInvokeName = var.getJavaInvokeName();

    ClassNode closure = new ClassNode();

    HWM hwm = new HWM();

    closure.version = Opcodes.V1_6;
    closure.access = Opcodes.ACC_PUBLIC;
    closure.name = javaName;
    closure.sourceFile = loc.getSrc();
    closure.signature = javaSig;

    closure.superName = Utils.javaInternalClassName(Object.class);
    closure.interfaces.addAll(patternTypeSignatures(dict, refreshed, ocxt, bldCat, errors, loc));
    closure.interfaces.add(Types.IPATTERN);
    closure.interfaces.add(Types.IVALUE);

    CafeDictionary pttrnDict = dict.funDict(dict.getPath(), closure);

    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, Names.MATCH, javaInvokeSig, javaInvokeSig, new String[] {});
    CodeContext ccxt = ocxt.fork(closure, mtd, hwm, pttrnDict.getLocalAvail());
    LabelNode firstLabel = new LabelNode();
    LabelNode endFunLabel = new LabelNode();

    InsnList ins = mtd.instructions;
    ins.add(firstLabel);

    List<LocalVariableNode> localVariables = mtd.localVariables;

    localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, closure.signature, null, firstLabel, endFunLabel,
        THIS_OFFSET));
    pttrnDict.declareLocal(loc, Names.PRIVATE_THIS, true, pttrnType, javaName, closure.signature, javaInvokeSig,
        javaInvokeName, AccessMode.readOnly);

    IContinuation fail = new FailCont();

    IType ptnType = TypeUtils.getPtnMatchType(refreshed);
    ISpec ptnSpec = SrcSpec.typeSpec(loc, ptnType, dict, bldCat, errors);

    IType resType = TypeUtils.getPtnResultType(refreshed);
    ISpec resSpec = SrcSpec.typeSpec(loc, resType, dict, bldCat, errors);

    declareArg(loc, Names.PTN_ARG, ptnType, AccessMode.readOnly, true, mtd, hwm, pttrnDict, endFunLabel, errors,
        bldCat, repository);

    // Load up the pattern argument to match against
    LabelNode testLabel = new LabelNode();
    Expressions.compileExp(Abstract.name(loc, Names.PTN_ARG), errors, pttrnDict, dict, pttrnName, new JumpCont(
        testLabel), null, ccxt);
    Utils.jumpTarget(ins, testLabel);

    LabelNode firstOkLabel = new LabelNode();
    IAbstract ptn = CafeSyntax.patternPtn(def);

    Patterns.compilePttrn(ptn, AccessMode.readOnly, ptnSpec, pttrnDict, dict, endFunLabel, errors, new JumpCont(
        firstOkLabel), fail, ccxt);

    Utils.jumpTarget(ins, firstOkLabel);
    Expressions.compileExp(CafeSyntax.patternResult(def), errors, pttrnDict, dict, pttrnName, new ReturnCont(resType,
        resSpec, pttrnDict), null, ccxt);

    ins.add(endFunLabel);

    mtd.maxLocals = pttrnDict.getLocalHWM();
    mtd.maxStack = hwm.getHwm();

    closure.methods.add(mtd);

    MethodNode constructor = closureConstructor(loc, dict, errors, closure, pttrnDict);
    closure.methods.add(constructor);
    closure.methods.add(genHashCode(loc, javaName, dict, errors, closure, pttrnDict));

    Constructors.genPatternVisit(closure);
    closure.methods.add(TypeGen.genType(loc, pttrnType, closure, dict, errors, repository, bldCat));

    // set up static initializers for any builtins
    setupReferenceInitializers(ccxt, pttrnDict.getBuiltinReferences(), loc);
    ccxt.installInitMtd();

    CompileCafe.genByteCode(javaName, loc, closure, bldCat, errors);

    MethodNode outerMtd = ocxt.getMtd();
    HWM outerHWM = ocxt.getMtdHwm();
    outerHWM.probe(1);
    outerMtd.instructions.add(new TypeInsnNode(Opcodes.NEW, closure.name));
    outerMtd.instructions.add(new InsnNode(Opcodes.DUP));
    outerMtd.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, closure.name, Types.INIT, Types.VOID_SIG));
    return lambdaSpec;
  }

  private static String munge(String lbl)
  {
    if (lbl.endsWith("$package$"))
      return lbl;
    else
      return GenSym.genSym(lbl);
  }

  private static void handleFixups(VarInfo var, CafeDictionary funDict, CafeDictionary thetaDict, CafeDictionary outer,
      CodeCatalog bldCat, MethodNode mtd, HWM hwm, Location loc, ErrorReport errors)
  {
    InsnList ins = mtd.instructions;

    for (Entry<String, VarInfo> entry : funDict.allEntries().entrySet()) {
      String vrName = entry.getKey();
      VarInfo info = entry.getValue();
      if (info.getWhere() == VarSource.freeVar) {
        VarInfo free = Theta.varReference(vrName, thetaDict, outer, loc, errors);
        int mark = hwm.bump(2);
        var.loadValue(mtd, hwm, funDict);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, funDict.getOwnerName()));
        free.loadValue(mtd, hwm, thetaDict);

        Expressions.checkType(info, free, mtd, funDict, hwm, loc, errors, bldCat);

        // We are updating the owner of the variable...
        ins.add(new FieldInsnNode(Opcodes.PUTFIELD, funDict.getOwnerName(), free.getJavaSafeName(), free.getJavaSig()));
        hwm.reset(mark);
      }
    }
  }

  public static boolean addField(ClassNode node, String name, String signature, int modifier)
  {
    List<FieldNode> fields = node.fields;
    for (FieldNode field : fields) {
      if (field.name.equals(name) && field.desc.equals(signature)) {
        assert field.access == Opcodes.ACC_PUBLIC + modifier;
        return true;
      }
    }
    fields.add(new FieldNode(Opcodes.ACC_PUBLIC + modifier, name, signature, null, null));
    return false;
  }

  static MethodNode closureConstructor(Location loc, CafeDictionary dict, ErrorReport errors, ClassNode closure,
      CafeDictionary funDict)
  {
    // Closure is a constructor
    MethodNode constructor = new MethodNode(Opcodes.ACC_PUBLIC, Types.INIT, Types.VOID_SIG, null, new String[] {});

    InsnList conIns = constructor.instructions;
    List<LocalVariableNode> localVariables = constructor.localVariables;

    LabelNode firstLabel = new LabelNode();
    LabelNode endLabel = new LabelNode();
    conIns.add(firstLabel);

    conIns.add(new VarInsnNode(Opcodes.ALOAD, THIS_OFFSET));
    conIns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", Types.INIT, Types.VOID_SIG));

    Actions.doLineNumber(loc, constructor);

    for (VarInfo frVar : funDict.getFreeVars()) {
      VarInfo var = dict.find(frVar.getName());
      if (var == null)
        errors.reportError("free variable " + frVar + " not declared", frVar.getLoc());
      else
        addField(closure, var.getJavaSafeName(), var.getJavaSig(), 0);
    }

    conIns.add(new InsnNode(Opcodes.RETURN));
    conIns.add(endLabel);
    localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, "L" + closure.name + ";", null, firstLabel, endLabel,
        THIS_OFFSET));
    constructor.maxLocals = 1;
    constructor.maxStack = 1;
    return constructor;
  }

  public static MethodNode genHashCode(Location loc, String functionSig, CafeDictionary dict, ErrorReport errors,
      ClassNode closure, CafeDictionary funDict)
  {
    // compute hashCode of a function
    MethodNode hashMtd = new MethodNode(Opcodes.ACC_PUBLIC, "hashCode", Types.HASH_SIG, null, new String[] {});

    LabelNode firstLabel = new LabelNode();
    LabelNode endLabel = new LabelNode();
    InsnList ins = hashMtd.instructions;
    hashMtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, closure.signature, null, firstLabel, endLabel,
        Theta.THIS_OFFSET));

    ins.add(firstLabel);

    HWM hwm = new HWM();

    Actions.doLineNumber(loc, hashMtd);

    hwm.bump(1);
    ins.add(new LdcInsnNode(functionSig));// hash of the label
    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.OBJECT, "hashCode", Types.HASH_SIG));

    for (VarInfo frVar : funDict.getFreeVars()) {
      VarInfo free = dict.find(frVar.getName());
      if (free == null)
        errors.reportError("free variable " + frVar + " not declared", frVar.getLoc());
      else {
        hwm.probe(1);
        Expressions.genIntConst(ins, hwm, HASH_PRIME); // Hash code multiplier
        ins.add(new InsnNode(Opcodes.IMUL));

        switch (free.getKind()) {
        case rawBool:
        case rawChar:
        case rawInt:
          hwm.probe(2);
          ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
          ins.add(new FieldInsnNode(Opcodes.GETFIELD, closure.name, free.getJavaSafeName(), free.getJavaSig()));
          ins.add(new InsnNode(Opcodes.IADD));
          break;
        case rawLong:
          hwm.probe(6);
          // Copied from java.lang.Long
          ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
          ins.add(new FieldInsnNode(Opcodes.GETFIELD, closure.name, free.getJavaSafeName(), free.getJavaSig()));
          ins.add(new InsnNode(Opcodes.DUP2));
          ins.add(new IntInsnNode(Opcodes.BIPUSH, BITS_IN_INTEGER));
          ins.add(new InsnNode(Opcodes.LUSHR));
          ins.add(new InsnNode(Opcodes.LXOR));
          ins.add(new InsnNode(Opcodes.L2I));
          ins.add(new InsnNode(Opcodes.IADD));
          break;
        case rawFloat:
          hwm.probe(6);
          ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
          ins.add(new FieldInsnNode(Opcodes.GETFIELD, closure.name, free.getJavaSafeName(), free.getJavaSig()));
          ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Types.JAVA_DOUBLE_TYPE, "doubleToLongBits", "(D)J"));
          ins.add(new InsnNode(Opcodes.DUP2));
          ins.add(new IntInsnNode(Opcodes.BIPUSH, BITS_IN_INTEGER));
          ins.add(new InsnNode(Opcodes.LUSHR));
          ins.add(new InsnNode(Opcodes.LXOR));
          ins.add(new InsnNode(Opcodes.L2I));
          ins.add(new InsnNode(Opcodes.IADD));
          break;
        case rawBinary:
        case rawString:
        case rawDecimal:
        case general:
          hwm.probe(4);
          ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
          ins.add(new FieldInsnNode(Opcodes.GETFIELD, closure.name, frVar.getJavaSafeName(), frVar.getJavaSig()));
          ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.OBJECT, "hashCode", Types.HASH_SIG));
          ins.add(new InsnNode(Opcodes.IADD));
          break;
        default:
          errors.reportError("illegal type of field", free.getLoc());
        }
      }
    }

    // return
    ins.add(new InsnNode(Opcodes.IRETURN));
    ins.add(endLabel);
    hashMtd.maxLocals = 1;
    hashMtd.maxStack = hwm.getHwm();
    return hashMtd;
  }

  public static void setupReferenceInitializers(CodeContext ccxt, Map<String, Inliner> references, Location loc)
  {
    if (!references.isEmpty()) {
      MethodNode initMtd = ccxt.getClassInit();
      HWM hwm = ccxt.getClsHwm();

      for (Entry<String, Inliner> entry : references.entrySet()) {
        Inliner inline = entry.getValue();

        int mark = hwm.getDepth();
        inline.inline(ccxt.getKlass(), initMtd, hwm, loc);
        hwm.reset(mark);
      }
    }
  }

  // Construct the interface class for a given function or procedure type
  public static List<String> functionTypeSignatures(CafeDictionary dict, IType funType, CodeContext cxt,
      CodeCatalog snCat, ErrorReport errors, Location loc)
  {
    funType = TypeUtils.unwrap(funType);
    String funSig = Types.functionClassName(funType);
    CodeCatalog synCat = cxt.getSynthCode();
    List<String> sigs = new ArrayList<>();

    if (!synCat.isPresent(funSig, CafeCode.EXTENSION))
      buildFunctionInterface(funType, synCat, errors, loc, funSig);
    sigs.add(funSig);

    try {
      assert synCat.resolve(funSig, CafeCode.EXTENSION) != null || !errors.isErrorFree();
    } catch (RepositoryException e) {
      errors.reportError("cannot access " + funSig + "\nbecause " + e.getMessage(), loc);
    }

    IType genericFunType = Types.genericFunType(funType);
    String erased = Types.functionClassName(genericFunType);

    if (!erased.equals(funSig)) {
      if (!synCat.isPresent(erased, CafeCode.EXTENSION))
        buildFunctionInterface(genericFunType, synCat, errors, loc, erased);
      sigs.add(erased);
    }
    return sigs;
  }

  public static void buildFunInterface(IType funType, CodeCatalog bldCat, ErrorReport errors, Location loc,
      String funSig)
  {
    if (!bldCat.isPresent(funSig, CafeCode.EXTENSION))
      buildFunctionInterface(funType, bldCat, errors, loc, funSig);
  }

  public static void buildFunctionInterface(IType funType, CodeCatalog bldCat, ErrorReport errors, Location loc,
      String funSig)
  {
    ClassNode klass = new ClassNode();
    assert TypeUtils.isFunType(funType);

    klass.version = Opcodes.V1_6;
    klass.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE;
    klass.name = funSig;
    klass.superName = Utils.javaInternalClassName(Object.class);
    klass.interfaces.add(Types.IFUNC);

    funType = TypeUtils.unwrap(funType);
    String generics = Types.genericFunctionClassName(funType);
    if (!generics.equals(funSig))
      klass.interfaces.add(generics);

    String javaMethodSig = Types.javaMethodSig(funType);
    MethodNode enter = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, Names.ENTER, javaMethodSig, null,
        new String[] {});
    klass.methods.add(enter);
    CompileCafe.genByteCode(funSig, loc, klass, bldCat, errors);
    if (!generics.equals(funSig)) {
      try {
        if (bldCat.resolve(generics, CafeCode.EXTENSION) == null)
          buildFunctionInterface(Types.genericFunType(funType), bldCat, errors, loc, generics);
      } catch (RepositoryException e) {
        errors.reportError("cannot resolve for " + funSig + "\nbecause " + e.getMessage(), loc);
      }
    }
  }

  public static List<String> patternTypeSignatures(CafeDictionary dict, IType funType, CodeContext cxt,
      CodeCatalog snCat, ErrorReport errors, Location loc)
  {
    String funSig = Types.functionClassName(funType);
    CodeCatalog synCat = cxt.getSynthCode();

    if (!synCat.isPresent(funSig, CafeCode.EXTENSION))
      buildPatternInterface(funType, synCat, errors, loc, funSig);

    assert synCat.isPresent(funSig, CafeCode.EXTENSION);

    IType genericFunType = Types.genericFunType(funType);
    String erased = Types.functionClassName(genericFunType);

    if (!erased.equals(funSig)) {
      if (!synCat.isPresent(erased, CafeCode.EXTENSION))
        buildPatternInterface(genericFunType, synCat, errors, loc, erased);

      return FixedList.create(erased, funSig);
    } else
      return FixedList.create(funSig);
  }

  public static void buildPatternInterface(IType progType, CodeCatalog synCat, ErrorReport errors, Location loc,
      String funSig)
  {
    ClassNode klass = new ClassNode();
    assert TypeUtils.isPatternType(progType);

    klass.version = Opcodes.V1_6;
    klass.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE;
    klass.name = funSig;
    klass.superName = Utils.javaInternalClassName(Object.class);
    klass.interfaces.add(Types.IPATTERN);

    progType = TypeUtils.unwrap(progType);
    String generics = Types.genericFunctionClassName(progType);
    if (!generics.equals(funSig))
      klass.interfaces.add(generics);

    String javaMethodSig = Types.javaMethodSig(progType);
    MethodNode enter = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, Names.MATCH, javaMethodSig, null,
        new String[] {});
    klass.methods.add(enter);
    CompileCafe.genByteCode(funSig, loc, klass, synCat, errors);
    if (!generics.equals(funSig)) {
      if (!synCat.isPresent(generics, CafeCode.EXTENSION))
        buildPatternInterface(Types.genericFunType(progType), synCat, errors, loc, generics);
    }
  }

  // Construct the interface class for a given constructor type
  public static List<String> constructorTypeSignatures(CafeDictionary dict, IType funType, CodeContext cxt,
      CodeCatalog snCat, ErrorReport errors, Location loc)
  {
    assert TypeUtils.isConstructorType(funType);
    CodeCatalog synCat = cxt.getSynthCode();
    List<String> sigs = new ArrayList<>();

    funType = TypeUtils.unwrap(funType);

    String funSig = Types.functionClassName(funType);

    sigs.add(funSig);

    if (!synCat.isPresent(funSig, CafeCode.EXTENSION))
      buildConstructorInterface(funType, synCat, errors, loc, funSig);

    assert synCat.isPresent(funSig, CafeCode.EXTENSION) || !errors.isErrorFree();

    IType genericFunType = Types.genericFunType(funType);
    String erased = Types.functionClassName(genericFunType);

    if (!erased.equals(funSig)) {
      if (!synCat.isPresent(erased, CafeCode.EXTENSION))
        buildConstructorInterface(genericFunType, synCat, errors, loc, erased);
      sigs.add(erased);
    }
    sigs.addAll(functionTypeSignatures(dict, TypeUtils.funTypeFromConType(funType), cxt, snCat, errors, loc));
    return sigs;
  }

  public static void buildConstructorInterface(IType funType, CodeCatalog bldCat, ErrorReport errors, Location loc,
      String funSig)
  {
    ClassNode klass = new ClassNode();
    assert TypeUtils.isConstructorType(funType);

    klass.version = Opcodes.V1_6;
    klass.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE;
    klass.name = funSig;
    klass.superName = Utils.javaInternalClassName(Object.class);
    klass.interfaces.add(Types.IFUNC);
    klass.interfaces.add(Types.ICONSTRUCTOR_FUNCTION);

    funType = TypeUtils.unwrap(funType);
    String generics = Types.genericFunctionClassName(funType);
    if (!generics.equals(funSig))
      klass.interfaces.add(generics);

    MethodNode enter = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, Names.ENTER, Types
        .javaMethodSig(TypeUtils.funTypeFromConType(funType)), null, new String[] {});
    klass.methods.add(enter);

    CompileCafe.genByteCode(funSig, loc, klass, bldCat, errors);
    if (!generics.equals(funSig)) {
      try {
        if (bldCat.resolve(generics, CafeCode.EXTENSION) == null)
          buildConstructorInterface(Types.genericFunType(funType), bldCat, errors, loc, generics);
      } catch (RepositoryException e) {
        errors.reportError("cannot resolve for " + funSig + "\nbecause " + e.getMessage(), loc);
      }
    }
  }

  private static void declareArgs(IList args, AccessMode access, boolean isInited, CafeDictionary dict,
      ErrorReport errors, Definer definer)
  {
    for (int ix = 0; ix < args.size(); ix++) {
      IAbstract arg = (IAbstract) args.getCell(ix);

      if (!CafeSyntax.isTypedTerm(arg) && Abstract.isName(CafeSyntax.typedTerm(arg)))
        errors.reportError("expecting a variable, not " + arg, arg.getLoc());
      else
        declareArg(arg, access, isInited, dict, errors, definer, ix + 1);
    }
  }

  public static void declareArg(IAbstract term, AccessMode access, boolean isInited, CafeDictionary dict,
      ErrorReport errors, Definer definer, int varOffset)
  {
    if (Abstract.isIdentifier(term) && CafeSyntax.termHasType(term)) {
      Location loc = term.getLoc();
      String id = Abstract.getId(term);
      definer.declareArg(loc, id, varOffset, CafeSyntax.termType(term), dict, access, isInited, errors);
    } else if (CafeSyntax.isTypedTerm(term)) {
      Location loc = term.getLoc();
      String id = ((Name) CafeSyntax.typedTerm(term)).getId();
      IType varType = TypeAnalyser.parseType(CafeSyntax.typedType(term), dict, errors);
      definer.declareArg(loc, id, varOffset, varType, dict, access, isInited, errors);
    } else if (CafeSyntax.isConstructor(term) && !isInited)
      declareArgs(CafeSyntax.constructorArgs(term), access, isInited, dict, errors, definer);
    else
      errors.reportError("expecting a variable, not " + term, term.getLoc());
  }

  static VarInfo declareArg(Location loc, String name, IType varType, AccessMode access, boolean isInited,
      MethodNode mtd, HWM hwm, CafeDictionary dict, LabelNode endLabel, ErrorReport errors, CodeCatalog bldCat,
      CodeRepository repository)
  {
    ISpec vrSpec = SrcSpec.generic(loc, varType, dict, repository, errors);

    VarInfo var = dict.declareLocal(name, vrSpec, isInited, access);
    if (isInited) {
      LabelNode startLabel = new LabelNode();
      mtd.localVariables.add(new LocalVariableNode(var.getJavaSafeName(), var.getJavaSig(), null, startLabel, endLabel,
          var.getOffset()));
      InsnList ins = mtd.instructions;
      ins.add(startLabel);
      if (!TypeUtils.isRawType(varType)) {
        hwm.probe(1);
        ins.add(new VarInsnNode(Opcodes.ALOAD, var.getOffset()));
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, var.getJavaType()));
        ins.add(new VarInsnNode(Opcodes.ASTORE, var.getOffset()));
      }
    }
    return var;
  }

  public static class LongArgDefiner implements Definer
  {
    protected final CodeCatalog bldCat;
    protected final VarInfo argArray;
    protected final CodeRepository repository;

    public LongArgDefiner(VarInfo argArray, CodeCatalog bldCat, CodeRepository repository)
    {
      this.bldCat = bldCat;
      this.argArray = argArray;
      this.repository = repository;
    }

    @Override
    public VarInfo declareArg(Location loc, String name, int varOffset, IType varType, CafeDictionary dict,
        AccessMode access, boolean isInited, ErrorReport errors)
    {
      ISpec vrSpec = SrcSpec.generic(loc, varType, dict, repository, errors);

      VarInfo var = new VarInfo(loc, name, isInited, VarSource.arrayArg, argArray, JavaKind.general, varOffset, access,
          varType, Utils.javaIdentifierOf(name), null, vrSpec.getJavaType(), vrSpec.getJavaSig(), vrSpec
              .getJavaInvokeSig(), vrSpec.getJavaInvokeName(), dict.getOwnerName());
      dict.declare(name, var);
      return var;
    }
  }

  // Convert a reference to a free reference
  public static VarInfo varReference(String name, CafeDictionary dict, CafeDictionary outer, Location loc,
      ErrorReport errors)
  {
    VarInfo var = dict.find(name);

    if (var == null && outer != null) {
      VarInfo ref = varReference(name, outer, outer.getParent(), loc, errors);

      if (ref != null) {
        if (ref.getKind() == JavaKind.builtin || ref.getKind() == JavaKind.constructor)
          return ref;

        if (!ref.isInited())
          errors.reportError("accessing uninitialized free variable: " + ref + "@" + ref.getLoc(), loc, ref.getLoc());
        return dict.declareFree(ref.getAccess().downGrade(), ref);
      }
    }

    return var;
  }
}
