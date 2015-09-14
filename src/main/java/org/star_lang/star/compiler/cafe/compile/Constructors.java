package org.star_lang.star.compiler.cafe.compile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.Theta.LongArgDefiner;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.cafe.compile.cont.JumpCont;
import org.star_lang.star.compiler.cafe.type.CafeRecordSpecifier;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.compiler.cafe.type.ICafeConstructorSpecifier;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.RecordSpecifier;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.type.TypeDescription;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.value.Array;
import org.star_lang.star.data.value.NTuple;
import org.star_lang.star.data.value.NTuple.NTpl;

/**
 * Handle the compilation of constructors
 *
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
  */
public class Constructors {
  public static final String SET_MEMBER_SIG = "(" + Types.JAVA_STRING_SIG + Types.IVALUE_SIG + ")V";
  public static final String SET_MEMBER = "setMember";
  public static final String VERIFY_INVOKE_SIG = "(" + Types.IVALUE_SIG + ")Z";
  public static final String GETCELLS_INVOKESIG = "()[" + Types.IVALUE_SIG;
  public static final String GET_CELLS = "getCells";
  public static final String SET_CELL = "setCell";
  public static final String GET_CELL = "getCell";
  private static final String JAVA_STRING_VOID_SIG = "(" + Types.JAVA_STRING_SIG + ")V";
  private static final String ILLEGAL_ARGUMENT_EXCEPTION = Type.getInternalName(IllegalArgumentException.class);
  // Constructor index method name
  public static final String CONIX = "conIx";
  public static final String ICONSTRUCTOR = Utils.javaInternalClassName(IConstructor.class);
  private static final String STRINGBUILDER = Utils.javaInternalClassName(StringBuilder.class);
  private static final String STRINGBUILDER_APPEND_SIG = "(" + Types.JAVA_STRING_SIG + ")L" + STRINGBUILDER + ";";
  private static final String TOSTRING_SIG = "()" + Types.JAVA_STRING_SIG;
  private static final String GET_LABEL = "getLabel";
  private static final String EQUALS = "equals";
  public static final String VERIFY = "verify";

  public static CafeTypeDescription compileTypeDef(IAbstract def, CafeDictionary dict, ErrorReport errors,
                                                   CodeContext ccxt) {
    assert CafeSyntax.isTypeDef(def);
    CodeCatalog bldCat = ccxt.getBldCat();
    Location loc = def.getLoc();
    CafeTypeDescription templateDesc = TypeAnalyser.parseAlgebraicDefn(def, dict, errors);
    IType definedType = templateDesc.getType();
    String javaSrTypeName = Types.javaTypeName(dict.getPath(), templateDesc.getName());

    ClassNode typeNode = new ClassNode();
    typeNode.version = Opcodes.V1_6;
    typeNode.access = Opcodes.ACC_PUBLIC;
    typeNode.name = javaSrTypeName;
    typeNode.sourceFile = loc.getSrc();
    typeNode.signature = "L" + javaSrTypeName + ";";
    typeNode.superName = Type.getInternalName(Object.class);
    typeNode.interfaces.add(Types.IVALUE);
    typeNode.interfaces.add(ICONSTRUCTOR);

    MethodNode rootCon = new MethodNode(Opcodes.ACC_PROTECTED, Types.INIT, "()V", null, new String[]{});
    typeNode.methods.add(rootCon);

    InsnList conIns = rootCon.instructions;
    List<LocalVariableNode> localVariables = rootCon.localVariables;

    LabelNode firstLabel = new LabelNode();
    LabelNode endLabel = new LabelNode();
    conIns.add(firstLabel);

    conIns.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    conIns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Types.OBJECT, Types.INIT, "()V"));

    conIns.add(new InsnNode(Opcodes.RETURN));
    conIns.add(endLabel);
    localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, typeNode.signature, null, firstLabel, endLabel,
        Theta.THIS_OFFSET));
    rootCon.maxLocals = 1;
    rootCon.maxStack = 2;

    CafeTypeDescription newDesc = (CafeTypeDescription) dict.declareType(loc, definedType, javaSrTypeName);

    MethodNode typeInit = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, Types.CLASS_INIT, "()V", null,
        new String[]{});
    HWM initHWM = new HWM();
    LabelNode endInit = new LabelNode();
    CafeDictionary initDict = dict.funDict(typeNode);

    Map<String, VarInfo> fields = new HashMap<>();
    Map<String, VarInfo> enums = new HashMap<>();

    int ix = 0;
    for (IValue sp : CafeSyntax.typeDefSpecs(def)) {
      IAbstract spec = (IAbstract) sp;
      while (CafeSyntax.isExistentialType(spec))
        spec = CafeSyntax.existentialBoundType(spec);
      if (CafeSyntax.isConstructorSpec(spec)) {
        ConstructorSpecifier conSpec = compileConstructor(spec, definedType, templateDesc, ix++, javaSrTypeName, dict,
            errors, enums, ccxt);
        compileConstructorFunction(conSpec, dict, ccxt, loc, errors);
      } else if (CafeSyntax.isRecord(spec)) {
        RecordSpecifier recSpec = compileRecord(spec, definedType, templateDesc, ix++, javaSrTypeName, dict, errors,
            fields, enums, ccxt);
        compileRecordFunction(recSpec, dict, ccxt, loc, errors);
      } else
        errors.reportError("invalid element of type definition: " + spec, spec.getLoc());
    }

    genGettersAndSetters(definedType.typeLabel(), typeNode, fields, dict, errors);
    genEnums(enums, typeNode, typeInit, initHWM);

    MethodNode abstractConIx = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, CONIX, "()I", null,
        new String[]{});
    typeNode.methods.add(abstractConIx);

    if (CodeContext.realCode(typeInit.instructions)) {
      typeInit.instructions.add(endInit);
      typeInit.instructions.add(new InsnNode(Opcodes.RETURN));
      typeInit.maxLocals = initDict.getLocalHWM();
      typeInit.maxStack = initHWM.getHwm();

      typeNode.methods.add(typeInit);
    }

    CompileCafe.genByteCode(javaSrTypeName, loc, typeNode, bldCat, errors);

    return newDesc;
  }

  private static void genEnums(Map<String, VarInfo> enums, ClassNode typeNode, MethodNode typeInit, HWM hwm) {
    if (!enums.isEmpty()) {
      InsnList ins = typeInit.instructions;

      for (Entry<String, VarInfo> entry : enums.entrySet()) {
        VarInfo enumVar = entry.getValue();

        Theta
            .addField(typeNode, enumVar.getJavaSafeName(), enumVar.getJavaSig(), Opcodes.ACC_ENUM + Opcodes.ACC_STATIC);

        // Create the enumerated symbols
        hwm.probe(2);
        ins.add(new TypeInsnNode(Opcodes.NEW, enumVar.getJavaType()));
        ins.add(new InsnNode(Opcodes.DUP));
        ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, enumVar.getJavaType(), Types.INIT, "()V"));
        ins.add(new FieldInsnNode(Opcodes.PUTSTATIC, typeNode.name, enumVar.getJavaSafeName(), enumVar.getJavaSig()));
      }
    }
  }

  /*
   * A constructor class is generated for each constructor.
   * 
   * in addition, a special function is created as a factory for the constructor and which can test
   * values to verify that they are instances of the constructor
   */
  private static ConstructorSpecifier compileConstructor(IAbstract con, IType type, CafeTypeDescription desc,
                                                         int conIx, String javaOwner, CafeDictionary dict, ErrorReport errors, Map<String, VarInfo> enums, CodeContext ccxt) {
    assert CafeSyntax.isConstructorSpec(con);
    CodeCatalog bldCat = ccxt.getBldCat();

    IList args = CafeSyntax.constructorSpecArgs(con);
    Location loc = con.getLoc();
    List<IType> argTypes = new ArrayList<>();
    List<ISpec> argSpecs = new ArrayList<>();
    Map<String, VarInfo> fields = new HashMap<>();

    for (IValue a : args) {
      IAbstract arg = (IAbstract) a;
      if (CafeSyntax.isTypedTerm(arg)) {
        errors.reportError("expecting a type expression, not " + arg, arg.getLoc());
        IType argType = TypeAnalyser.parseType(CafeSyntax.typedType(arg), dict, errors);
        argTypes.add(argType);
        argSpecs.add(SrcSpec.generic(loc, argType, dict, ccxt.getRepository(), errors));
      } else {
        IType argType = TypeAnalyser.parseType(arg, dict, errors);
        argTypes.add(argType);
        argSpecs.add(SrcSpec.generic(loc, argType, dict, ccxt.getRepository(), errors));
      }
    }

    String conLabel = CafeSyntax.constructorSpecLabel(con);
    String conOp = Utils.javaIdentifierOf(conLabel);
    String javaName = javaOwner + "$" + conOp;

    ClassNode conNode = new ClassNode();
    conNode.version = Opcodes.V1_6;
    conNode.access = Opcodes.ACC_PUBLIC;
    conNode.name = javaName;
    conNode.sourceFile = con.getLoc().getSrc();
    conNode.signature = "L" + javaName + ";";
    conNode.superName = javaOwner;

    // Sort out the indices of individual attributes
    Map<String, Integer> index = new HashMap<>();
    final boolean isEnum = args.isEmpty();
    VarInfo thisVar = new VarInfo(loc, Names.PRIVATE_THIS, true, VarSource.localVar, null, JavaKind.general,
        Theta.THIS_OFFSET, AccessMode.readOnly, type, Names.PRIVATE_THIS, null, javaName, conNode.signature, null,
        Types.INIT, javaName);

    for (int ix = 0; ix < args.size(); ix++) {
      IAbstract arg = (IAbstract) args.getCell(ix);

      String id = conargId(ix);
      ISpec argSpec = argSpecs.get(ix);
      String javaType = argSpec.getJavaType();

      index.put(id, ix);

      if (!fields.containsKey(id))
        fields.put(id, new VarInfo(arg.getLoc(), id, false, VarSource.freeVar, thisVar, Types
            .varType(argSpec.getType()), ix, AccessMode.readOnly, argSpec.getType(), Utils.javaIdentifierOf(id), null,
            javaType, argSpec.getJavaSig(), argSpec.getJavaInvokeSig(), argSpec.getJavaInvokeName(), javaOwner));
    }

    conNode.interfaces.add(Types.ICONSTRUCTOR);
    genConstructorVisit(conNode);
    genToString(conNode);

    String javaSafeName = isEnum ? Utils.javaIdentifierOf(conLabel) + Types.ENUM_SFX : Utils.javaIdentifierOf(conLabel);

    if (isEnum)
      enums.put(conLabel, new VarInfo(loc, conLabel, true, VarSource.staticField, null, Types.varType(type), -1,
          AccessMode.readOnly, type, javaSafeName, null, conNode.name, conNode.signature, null, null, javaOwner));

    String javaConSig = Types.javaConstructorSig(argTypes);

    List<MethodNode> methods = conNode.methods;
    methods.add(genInit(loc, conNode, type, javaConSig, args, dict, javaOwner, javaName, errors, bldCat, ccxt));

    if (args.size() < Theta.MAX_ARGS && noRawTypes(argTypes))
      methods.add(genArrayInit(loc, conNode, type, "(" + Types.IVALUE_ARRAY + ")V", args, dict, javaOwner, javaName,
          errors, bldCat, ccxt));

    methods.add(genLabel(CafeSyntax.constructorSpecLabel(con), conNode));
    methods.add(genSize(CafeSyntax.constructorSpecArgs(con).size(), conNode));
    methods.add(genGetCell(conNode, argSpecs, index, dict, loc));
    methods.add(genSetCell(conNode, argSpecs, index, dict, bldCat, errors, loc));
    methods.add(genGetCells(conNode, argSpecs, fields, index, dict));
    genConSpecificGetters(conNode, fields, errors, dict);
    methods.add(genEquals(conNode, fields, index, errors, loc));
    methods.add(genHashcode(conNode, conLabel, fields, index, errors, loc));
    // Deep
    methods.add(genCopy(conNode, javaConSig, args, argSpecs, false));
    methods.add(genCopyBridge(conNode, Types.ICONSTRUCTOR_SIG, false));
    methods.add(genCopyBridge(conNode, Types.IVALUE_SIG, false));
    // Shallow
    methods.add(genCopy(conNode, javaConSig, args, argSpecs, true));
    methods.add(genCopyBridge(conNode, Types.ICONSTRUCTOR_SIG, true));
    methods.add(genCopyBridge(conNode, Types.IVALUE_SIG, true));

    methods.add(genConix(conNode, conIx));

    genGetMember(conNode, argSpecs, index, dict, loc);

    IType conType = Freshen.generalizeType(desc.getValueSpecifier(conLabel).getConType());

    methods.add(TypeGen.genType(desc, conLabel, conNode, dict, errors, ccxt.getRepository(), bldCat));

    CompileCafe.genByteCode(javaName, loc, conNode, bldCat, errors);

    ConstructorSpecifier conSpec = new ConstructorSpecifier(loc, conLabel, conIx, null, conType, javaSafeName, Utils
        .javaPublicName(javaName), javaConSig, javaOwner);
    dict.declareConstructor(conSpec);
    return conSpec;
  }

  /**
   * Compile the special constructor function/pattern for a positional constructor. Constructor
   * functions combine both an enter method and a match method.
   *
   * @param con    specification of constructor function
   * @param dict   dictionary
   * @param loc    location of constructor function definition
   * @param errors errors
   * @param cxt    code context
   */

  private static void compileConstructorFunction(ConstructorSpecifier con, CafeDictionary dict, CodeContext cxt,
                                                 Location loc, ErrorReport errors) {
    CodeCatalog bldCat = cxt.getBldCat();
    int arity = con.arity();

    IType conType = con.getConType();
    IType refreshed = Freshen.freshen(conType, AccessMode.readWrite, AccessMode.readOnly).left();

    assert TypeUtils.isConstructorType(refreshed);

    IType argTypes[] = TypeUtils.getConstructorArgTypes(refreshed);
    IType resType = TypeUtils.getConstructorResultType(refreshed);

    String conLabel = con.getLabel();

    String javaName = Utils.javaInternalName(con.getJavaClassName()) + "$_C";
    String javaSig = "L" + javaName + ";";
    String enterSig = Types.javaMethodSig(TypeUtils.funTypeFromConType(conType));

    VarInfo tgt = dict.declare(conLabel, new VarInfo(loc, conLabel, true, VarSource.staticField, null,
        JavaKind.general, 0, AccessMode.readOnly, conType, conLabel, null, javaName, javaSig, enterSig, Names.ENTER,
        cxt.getKlass().name));

    Theta.addField(cxt.getKlass(), conLabel, javaSig, Opcodes.ACC_STATIC);

    ClassNode closure = new ClassNode();

    closure.version = Opcodes.V1_6;
    closure.access = Opcodes.ACC_PUBLIC;
    closure.name = javaName;
    closure.sourceFile = loc.getSrc();
    closure.signature = javaSig;

    closure.superName = Types.JAVA_OBJECT_TYPE;
    closure.interfaces.addAll(Theta.constructorTypeSignatures(refreshed, cxt, loc));
    closure.interfaces.add(Types.IFUNC);
    closure.interfaces.add(Types.ICONSTRUCTOR_FUNCTION);
    closure.interfaces.add(Types.IVALUE);

    MethodNode enterMtd = new MethodNode(Opcodes.ACC_PUBLIC, Names.ENTER, enterSig, enterSig, new String[]{});

    LabelNode firstLabel = new LabelNode();
    LabelNode endFunLabel = new LabelNode();

    InsnList enterIns = enterMtd.instructions;
    Actions.doLineNumber(loc, enterMtd);
    enterIns.add(firstLabel);

    List<LocalVariableNode> funVariables = enterMtd.localVariables;

    HWM hwm = new HWM();
    CafeDictionary funDict = dict.funDict(dict.getPath(), closure);

    funVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, javaSig, null, firstLabel, endFunLabel,
        Theta.THIS_OFFSET));
    funDict.declareLocal(loc, Names.PRIVATE_THIS, true, conType, javaName, javaSig, enterSig, javaName,
        AccessMode.readOnly);

    CodeContext fcxt = cxt.fork(closure, enterMtd, hwm, cxt.getClassInit(), cxt.getClsHwm(), funDict.getLocalAvail(), Names.ENTER).fork(funDict, dict);

    defineArgs(argTypes, loc, bldCat, errors, hwm, funDict, enterMtd, endFunLabel, fcxt);

    int mark = hwm.getDepth();

    if (arity > 0) {
      // pre-amble to access the appropriate constructor
      enterIns.add(new TypeInsnNode(Opcodes.NEW, con.getJavaType()));
      enterIns.add(new InsnNode(Opcodes.DUP));
      hwm.bump(2);

      compileArgs(loc, argTypes, errors, enterMtd, hwm, funDict, dict);

      // actually invoke the constructor
      enterIns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, con.getJavaType(), Types.INIT, con.getJavaConSig()));
    } else
      enterIns.add(new FieldInsnNode(Opcodes.GETSTATIC, con.getJavaOwner(), Utils.javaIdentifierOf(conLabel)
          + Types.ENUM_SFX, "L" + con.getJavaType() + ";"));

    enterIns.add(new InsnNode(Opcodes.ARETURN));

    hwm.reset(mark);
    hwm.bump(Types.stackAmnt(Types.varType(resType)));

    enterMtd.instructions.add(endFunLabel);

    enterMtd.maxLocals = funDict.getLocalHWM();
    enterMtd.maxStack = hwm.getHwm();

    closure.methods.add(enterMtd);

    hwm.clear();

    // Implement the IFunc interface...
    closure.methods.add(IFuncImplementation.ifunc(loc, javaName, enterSig, dict, argTypes, resType, bldCat, errors));

    MethodNode constructor = Theta.closureConstructor(loc, dict, errors, closure, funDict);
    closure.methods.add(constructor);
    closure.methods.add(Theta.genHashCode(loc, javaName, dict, errors, closure, funDict));

    Constructors.genFunctionVisit(closure);
    closure.methods.add(TypeGen.genType(loc, conType, closure, dict, errors, cxt.getRepository(), bldCat));
    closure.methods.add(genVerify(closure, conLabel, con.getConIx()));

    // set up static initializers for any builtins
    Theta.setupReferenceInitializers(fcxt, funDict.getBuiltinReferences(), loc);

    CompileCafe.genByteCode(javaName, loc, closure, bldCat, errors);

    cxt.getClsHwm().probe(2);
    InsnList initIns = cxt.getClassInit().instructions;
    initIns.add(new TypeInsnNode(Opcodes.NEW, closure.name));
    initIns.add(new InsnNode(Opcodes.DUP));
    initIns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, closure.name, Types.INIT, "()V"));

    tgt.storeValue(cxt.getClassInit(), cxt.getClsHwm(), dict);
  }

  private static void defineArgs(IType[] argTypes, Location loc, CodeCatalog bldCat, ErrorReport errors, HWM hwm,
                                 CafeDictionary funDict, MethodNode funMtd, LabelNode endFunLabel, CodeContext ccxt) {
    final Definer definer;
    CodeRepository repository = ccxt.getRepository();

    if (argTypes.length < Theta.MAX_ARGS)
      definer = new ArgDefiner(funMtd, hwm, endFunLabel, bldCat, ccxt);
    else {
      VarInfo argArray = new VarInfo(loc, Names.ARG_ARRAY, true, VarSource.localVar, null, JavaKind.general,
          Theta.FIRST_OFFSET, AccessMode.readOnly, TypeUtils.arrayType(StandardTypes.anyType), Utils
          .javaIdentifierOf(Names.ARG_ARRAY), null, Types.IVALUE_ARRAY, Types.IVALUE_ARRAY, null, null, funDict
          .getOwnerName());
      definer = new LongArgDefiner(argArray, repository);
    }

    for (int ix = 0; ix < argTypes.length; ix++) {
      String id = "_" + ix;
      definer.declareArg(loc, id, ix, argTypes[ix], funDict, AccessMode.readOnly, true, errors);
    }
  }

  private static void compileArgs(Location loc, IType[] argTypes, ErrorReport errors, MethodNode mtd, HWM hwm,
                                  CafeDictionary dict, CafeDictionary outer) {
    int arity = argTypes.length;

    if (arity < Theta.MAX_ARGS) {
      for (int ix = 0; ix < arity; ix++) {
        String id = "_" + ix;

        VarInfo var = Theta.varReference(id, dict, outer, loc, errors);

        assert var != null && var.isInited();

        var.loadValue(mtd, hwm, dict);
      }
    } else {
      InsnList ins = mtd.instructions;

      Expressions.genIntConst(ins, hwm, arity);
      ins.add(new TypeInsnNode(Opcodes.ANEWARRAY, Types.IVALUE));

      for (int ix = 0; ix < arity; ix++) {
        String id = "_" + ix;

        VarInfo var = Theta.varReference(id, dict, outer, loc, errors);

        assert var != null && var.isInited();
        int mark = hwm.bump(2);

        ins.add(new InsnNode(Opcodes.DUP));

        Expressions.genIntConst(ins, hwm, ix);

        var.loadValue(mtd, hwm, dict);

        ins.add(new InsnNode(Opcodes.AASTORE));
        hwm.reset(mark);
      }
    }
  }

  private static boolean noRawTypes(List<IType> types) {
    for (IType type : types)
      if (TypeUtils.isRawType(type))
        return false;
    return true;
  }

  public static String conargId(int ix) {
    return "__" + ix;
  }

  private static CafeRecordSpecifier compileRecord(IAbstract con, IType type, TypeDescription desc, int conIx,
                                                   String javaOwner, CafeDictionary dict, ErrorReport errors, Map<String, VarInfo> fields,
                                                   Map<String, VarInfo> enums, CodeContext ocxt) {
    assert CafeSyntax.isRecord(con);

    CodeCatalog bldCat = ocxt.getBldCat();
    CodeRepository repository = ocxt.getRepository();

    String conLabel = CafeSyntax.recordLabel(con);
    String conOp = Utils.javaIdentifierOf(conLabel);
    String javaName = javaOwner + "$" + conOp;

    ClassNode conNode = new ClassNode();
    conNode.version = Opcodes.V1_6;
    conNode.access = Opcodes.ACC_PUBLIC;
    conNode.name = javaName;
    conNode.sourceFile = con.getLoc().getSrc();
    conNode.signature = "L" + javaName + ";";
    conNode.superName = javaOwner;

    RecordSpecifier record = (RecordSpecifier) desc.getValueSpecifier(conLabel);

    assert record != null;

    IType recordArgTypes = TypeUtils.unwrap(TypeUtils.getConstructorArgType(record.getConType()));
    assert recordArgTypes instanceof TypeInterfaceType;

    TypeInterfaceType recordFace = (TypeInterfaceType) recordArgTypes;

    IList args = CafeSyntax.recordArgs(con);
    Location loc = con.getLoc();
    List<IType> argTypes = new ArrayList<>();
    List<ISpec> argSpecs = new ArrayList<>();
    List<Pair<String, String>> fieldNames = new ArrayList<>();

    // Sort out the indices of individual attributes
    SortedMap<String, Integer> index = new TreeMap<>();

    SortedMap<String, IType> members = recordFace.getAllFields();

    int ix = 0;
    for (Entry<String, IType> field : members.entrySet()) {
      String fieldId = field.getKey();

      IType argType = field.getValue();
      argTypes.add(argType);
      SrcSpec argSpec = SrcSpec.generic(loc, argType, dict, repository, errors);
      argSpecs.add(argSpec);

      String javaIdOfField = Utils.javaIdentifierOf(fieldId);
      fieldNames.add(Pair.pair(fieldId, javaIdOfField));

      String javaType = argSpec.getJavaType();
      String javaSig = argSpec.getJavaSig();
      String javaInvokeSig = argSpec.getJavaInvokeSig();
      String javaInvokeName = argSpec.getJavaInvokeName();

      index.put(fieldId, ix++);

      if (!fields.containsKey(fieldId))
        fields.put(fieldId, new VarInfo(loc, fieldId, false, VarSource.field, null, Types.varType(argType), ix,
            AccessMode.readOnly, argType, javaIdOfField, Types.getterName(javaIdOfField), javaType, javaSig,
            javaInvokeSig, javaInvokeName, javaOwner));
    }

    conNode.interfaces.add(Types.IRECORD);
    genRecordVisit(conNode);
    genToString(conNode);

    String javaConSig = Types.javaConstructorSig(argTypes);

    List<MethodNode> methods = conNode.methods;
    methods.add(genInit(loc, conNode, type, javaConSig, args, dict, javaOwner, javaName, errors, bldCat, ocxt));
    // Allow zero-argument constructor
    if (!args.isEmpty())
      methods.add(genInit0(loc, type, conNode, dict, javaOwner, javaName, errors, bldCat, ocxt));
    else
      enums.put(conLabel, new VarInfo(loc, conLabel, true, VarSource.staticField, null, Types.varType(type), -1,
          AccessMode.readOnly, type, Utils.javaIdentifierOf(conLabel) + Types.ENUM_SFX, null, conNode.name,
          conNode.signature, null, null, javaOwner));

    // Special methods

    methods.add(genLabel(CafeSyntax.recordLabel(con), conNode));
    methods.add(genSize(CafeSyntax.recordArgs(con).size(), conNode));
    methods.add(genGetCell(conNode, argSpecs, index, dict, loc));
    methods.add(genGetCells(conNode, argSpecs, fields, index, dict));
    genConSpecificGetters(conNode, fields, errors, dict);
    methods.add(genSetCell(conNode, argSpecs, index, dict, bldCat, errors, loc));
    methods.add(genEquals(conNode, fields, index, errors, loc));
    methods.add(genHashcode(conNode, conLabel, fields, index, errors, loc));
    // Deep
    methods.add(genCopy(conNode, javaConSig, args, argSpecs, false));
    methods.add(genCopyBridge(conNode, Types.ICONSTRUCTOR_SIG, false));
    methods.add(genCopyBridge(conNode, Types.IRECORD_SIG, false));
    methods.add(genCopyBridge(conNode, Types.IVALUE_SIG, false));
    // Shallow
    methods.add(genCopy(conNode, javaConSig, args, argSpecs, true));
    methods.add(genCopyBridge(conNode, Types.ICONSTRUCTOR_SIG, true));
    methods.add(genCopyBridge(conNode, Types.IRECORD_SIG, true));
    methods.add(genCopyBridge(conNode, Types.IVALUE_SIG, true));

    methods.add(genMembers(conNode, fieldNames));

    methods.add(genConix(conNode, conIx));

    genGetMember(conNode, argSpecs, index, dict, loc);
    genSetMember(conNode, argSpecs, index);

    CafeRecordSpecifier recSpec = (CafeRecordSpecifier) dict.declareConstructor(loc, type, Freshen.generalizeType(desc
            .getValueSpecifier(conLabel).getConType()), javaName, javaConSig, javaName, javaOwner, conOp, conIx, argSpecs,
        index, errors);

    methods.add(TypeGen.genType(desc, conLabel, conNode, dict, errors, ocxt.getRepository(), bldCat));

    CompileCafe.genByteCode(javaName, loc, conNode, bldCat, errors);

    return recSpec;
  }

  /**
   * Compile the special constructor function/pattern for a record constructor.
   * <p>
   * Constructor functions combine both an enter method and a match method.
   *
   * @param con    specification of record constructor
   * @param dict   dictionary
   * @param loc    source location of definition
   * @param errors errors
   * @param cxt    code context
   */

  private static void compileRecordFunction(RecordSpecifier con, CafeDictionary dict, CodeContext cxt, Location loc,
                                            ErrorReport errors) {
    CodeCatalog bldCat = cxt.getBldCat();
    int arity = con.arity();

    IType conType = con.getConType();
    IType refreshed = Freshen.freshen(conType, AccessMode.readWrite, AccessMode.readOnly).left();

    assert TypeUtils.isConstructorType(refreshed);

    String conLabel = con.getLabel();

    String javaName = Utils.javaInternalName(con.getJavaClassName()) + "$_R";
    String javaSig = "L" + javaName + ";";
    String enterSig = Types.javaMethodSig(TypeUtils.funTypeFromConType(conType));

    VarInfo tgt = dict.declare(conLabel, new VarInfo(loc, conLabel, true, VarSource.staticField, null,
        JavaKind.general, 0, AccessMode.readOnly, conType, conLabel, null, javaName, javaSig, enterSig, Names.ENTER,
        cxt.getKlass().name));
    Theta.addField(cxt.getKlass(), conLabel, javaSig, Opcodes.ACC_STATIC);

    ClassNode closure = new ClassNode();

    closure.version = Opcodes.V1_6;
    closure.access = Opcodes.ACC_PUBLIC;
    closure.name = javaName;
    closure.sourceFile = loc.getSrc();
    closure.signature = javaSig;

    closure.superName = Types.JAVA_OBJECT_TYPE;

    closure.interfaces.addAll(Theta.constructorTypeSignatures(refreshed, cxt, loc));
    closure.interfaces.add(Types.IFUNC);
    closure.interfaces.add(Types.ICONSTRUCTOR_FUNCTION);
    closure.interfaces.add(Types.IVALUE);

    MethodNode enterMtd = new MethodNode(Opcodes.ACC_PUBLIC, Names.ENTER, enterSig, enterSig, new String[]{});

    LabelNode firstLabel = new LabelNode();
    LabelNode endLabel = new LabelNode();

    InsnList enterIns = enterMtd.instructions;
    Actions.doLineNumber(loc, enterMtd);
    enterIns.add(firstLabel);

    List<LocalVariableNode> funVariables = enterMtd.localVariables;

    HWM hwm = new HWM();
    CafeDictionary funDict = dict.funDict(dict.getPath(), closure);

    funVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, javaSig, null, firstLabel, endLabel, Theta.THIS_OFFSET));
    funDict.declareLocal(loc, Names.PRIVATE_THIS, true, conType, javaName, javaSig, enterSig, javaName,
        AccessMode.readOnly);

    CodeContext fcxt = cxt.fork(closure, enterMtd, hwm, cxt.getClassInit(), cxt.getClsHwm(), funDict.getLocalAvail(), Names.ENTER).fork(funDict, dict);

    TypeInterfaceType conArgType = TypeUtils.getRecordConstructorArgs(refreshed);
    IType argTypes[] = TypeUtils.tupleOfInterface(conArgType);
    IType resType = TypeUtils.getConstructorResultType(refreshed);

    if (argTypes.length >= Theta.MAX_ARGS) {
      enterMtd.localVariables.add(new LocalVariableNode(Names.ARG_ARRAY, Types.IVALUE_ARRAY, null, firstLabel,
          endLabel, Theta.FIRST_OFFSET));
      VarInfo argArray = new VarInfo(loc, Names.ARG_ARRAY, true, VarSource.localVar, null, JavaKind.general,
          Theta.FIRST_OFFSET, AccessMode.readOnly, TypeUtils.arrayType(StandardTypes.anyType), Utils
          .javaIdentifierOf(Names.ARG_ARRAY), null, Types.IVALUE_ARRAY, Types.IVALUE_ARRAY, null, null, funDict
          .getOwnerName());
      funDict.declare(Names.ARG_ARRAY, argArray);

      hwm.probe(3);
      enterIns.add(new TypeInsnNode(Opcodes.NEW, con.getJavaType()));
      enterIns.add(new InsnNode(Opcodes.DUP));
      enterIns.add(new VarInsnNode(Opcodes.ALOAD, Theta.FIRST_OFFSET));
      enterIns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, con.getJavaType(), Types.INIT, con.getJavaConSig()));
    } else {
      defineArgs(argTypes, loc, bldCat, errors, hwm, funDict, enterMtd, endLabel, fcxt);

      int mark = hwm.getDepth();

      if (arity > 0) {
        // pre-amble to access the appropriate constructor
        enterIns.add(new TypeInsnNode(Opcodes.NEW, con.getJavaType()));
        enterIns.add(new InsnNode(Opcodes.DUP));
        hwm.bump(2);

        compileArgs(loc, argTypes, errors, enterMtd, hwm, funDict, dict);

        // actually invoke the constructor
        enterIns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, con.getJavaType(), Types.INIT, con.getJavaConSig()));
      } else
        enterIns.add(new FieldInsnNode(Opcodes.GETSTATIC, con.getJavaOwner(), Utils.javaIdentifierOf(conLabel)
            + Types.ENUM_SFX, "L" + con.getJavaType() + ";"));
      hwm.reset(mark);
    }

    enterIns.add(new InsnNode(Opcodes.ARETURN));

    hwm.bump(Types.stackAmnt(Types.varType(resType)));

    enterMtd.instructions.add(endLabel);

    enterMtd.maxLocals = funDict.getLocalHWM();
    enterMtd.maxStack = hwm.getHwm();

    closure.methods.add(enterMtd);

    hwm.clear();

    // Implement the IFunc interface
    if (arity < Theta.MAX_ARGS)
      closure.methods.add(IFuncImplementation.ifunc(loc, javaName, enterSig, dict, argTypes, resType, bldCat, errors));

    MethodNode constructor = Theta.closureConstructor(loc, dict, errors, closure, funDict);
    closure.methods.add(constructor);
    closure.methods.add(Theta.genHashCode(loc, javaName, dict, errors, closure, funDict));

    Constructors.genFunctionVisit(closure);
    closure.methods.add(TypeGen.genType(loc, conType, closure, dict, errors, cxt.getRepository(), bldCat));
    closure.methods.add(genVerify(closure, conLabel, con.getConIx()));

    // set up static initializers for any builtins
    Theta.setupReferenceInitializers(fcxt, funDict.getBuiltinReferences(), loc);

    CompileCafe.genByteCode(javaName, loc, closure, bldCat, errors);

    cxt.getClsHwm().probe(2);
    InsnList initIns = cxt.getClassInit().instructions;
    initIns.add(new TypeInsnNode(Opcodes.NEW, closure.name));
    initIns.add(new InsnNode(Opcodes.DUP));
    initIns.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, closure.name, Types.INIT, "()V"));

    tgt.storeValue(cxt.getClassInit(), cxt.getClsHwm(), dict);
  }

  static MethodNode genMembers(ClassNode conNode, List<Pair<String, String>> fieldNames) {
    MethodNode members = new MethodNode(Opcodes.ACC_PUBLIC, "getMembers", "()[Ljava/lang/String;", null,
        new String[]{});
    InsnList mmIns = members.instructions;
    LabelNode mmStart = new LabelNode();
    LabelNode mmEnd = new LabelNode();

    members.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, mmStart, mmEnd,
        Theta.THIS_OFFSET));
    mmIns.add(mmStart);

    HWM hwm = new HWM();
    Expressions.genIntConst(mmIns, hwm, fieldNames.size());
    hwm.bump(1);
    mmIns.add(new TypeInsnNode(Opcodes.ANEWARRAY, Types.JAVA_STRING_TYPE));
    for (int ix = 0; ix < fieldNames.size(); ix++) {
      int mark = hwm.bump(2);
      mmIns.add(new InsnNode(Opcodes.DUP));
      Expressions.genIntConst(mmIns, hwm, ix);
      mmIns.add(new LdcInsnNode(fieldNames.get(ix).left()));
      mmIns.add(new InsnNode(Opcodes.AASTORE));
      hwm.reset(mark);
    }

    mmIns.add(new InsnNode(Opcodes.ARETURN));
    mmIns.add(mmEnd);
    members.maxLocals = 1;
    members.maxStack = hwm.getHwm();
    return members;
  }

  public static MethodNode genInit0(Location loc, IType type, ClassNode conNode, CafeDictionary dict,
                                    String javaSrTypeName, String javaName, ErrorReport errors, CodeCatalog bldCat, CodeContext ccxt) {
    return genInit(loc, conNode, type, "()V", Array.nilArray, dict, javaSrTypeName, javaName, errors, bldCat, ccxt);
  }

  private static MethodNode genInit(Location loc, ClassNode conNode, IType type, String javaConSig, IList args,
                                    CafeDictionary dict, String javaSrTypeName, String javaName, ErrorReport errors, CodeCatalog bldCat,
                                    CodeContext ccxt) {
    // Closure is a constructor
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, Types.INIT, javaConSig, null, new String[]{});

    InsnList ins = mtd.instructions;
    List<LocalVariableNode> localVariables = mtd.localVariables;

    LabelNode firstLabel = new LabelNode();
    LabelNode endLabel = new LabelNode();

    localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, firstLabel, endLabel,
        Theta.THIS_OFFSET));

    ins.add(firstLabel);

    Actions.doLineNumber(loc, mtd);

    HWM hwm = new HWM();
    hwm.probe(1);

    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, javaSrTypeName, Types.INIT, "()V"));

    CafeDictionary conDict = dict.funDict(dict.getPath(), conNode);
    conDict.declare(Names.PRIVATE_THIS, conDict.reserve(loc, Names.PRIVATE_THIS, true, javaSrTypeName, conNode.name,
        conNode.signature, null, null, type, AccessMode.readWrite, VarSource.localVar, JavaKind.general));

    // Define the arguments
    final Definer definer;

    if (args.size() < Theta.MAX_ARGS)
      definer = new ArgDefiner(mtd, hwm, endLabel, bldCat, ccxt);
    else {
      VarInfo argArray = new VarInfo(loc, Names.ARG_ARRAY, true, VarSource.localVar, null, JavaKind.general,
          Theta.FIRST_OFFSET, AccessMode.readOnly, TypeUtils.arrayType(StandardTypes.anyType), Utils
          .javaIdentifierOf(Names.ARG_ARRAY), null, Types.IVALUE_ARRAY, Types.IVALUE_ARRAY, null, null, conDict
          .getOwnerName());
      definer = new LongArgDefiner(argArray, ccxt.getRepository());
      localVariables.add(new LocalVariableNode(Names.ARG_ARRAY, argArray.getJavaSig(), null, firstLabel, endLabel,
          Theta.FIRST_OFFSET));
      conDict.declare(Names.ARG_ARRAY, argArray);
    }

    // Store the arguments in fields
    for (int ix = 0; ix < args.size(); ix++) {
      int mark = hwm.getDepth();
      IAbstract arg = (IAbstract) args.getCell(ix);
      String id;
      IAbstract tp;

      if (CafeSyntax.isTypedTerm(arg)) {
        id = ((Name) CafeSyntax.typedTerm(arg)).getId();
        tp = CafeSyntax.typedType(arg);
      } else {
        id = conargId(ix);
        tp = arg;
      }
      IType fieldType = TypeAnalyser.parseType(tp, conDict, errors);

      VarInfo field = definer.declareArg(arg.getLoc(), id, ix, fieldType, conDict, AccessMode.readOnly, true, errors);
      assert field != null;

      ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
      field.loadValue(mtd, hwm, conDict);

      String javaSig = field.getJavaSig();
      String javaSafeName = field.getJavaSafeName();

      Theta.addField(conNode, javaSafeName, javaSig, 0);

      hwm.bump(1);
      ins.add(new FieldInsnNode(Opcodes.PUTFIELD, javaName, javaSafeName, javaSig));
      hwm.reset(mark);
    }

    ins.add(new InsnNode(Opcodes.RETURN));
    ins.add(endLabel);

    mtd.maxLocals = conDict.getLocalHWM();
    mtd.maxStack = hwm.getHwm();
    return mtd;
  }

  private static MethodNode genArrayInit(Location loc, ClassNode conNode, IType type, String javaConSig, IList args,
                                         CafeDictionary dict, String javaSrTypeName, String javaName, ErrorReport errors, CodeCatalog bldCat,
                                         CodeContext ccxt) {
    // Closure is a constructor
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, Types.INIT, javaConSig, null, new String[]{});

    InsnList ins = mtd.instructions;
    List<LocalVariableNode> localVariables = mtd.localVariables;

    LabelNode firstLabel = new LabelNode();
    LabelNode endLabel = new LabelNode();

    localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, firstLabel, endLabel,
        Theta.THIS_OFFSET));

    ins.add(firstLabel);

    Actions.doLineNumber(loc, mtd);

    HWM hwm = new HWM();
    hwm.probe(1);

    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, javaSrTypeName, Types.INIT, "()V"));

    CafeDictionary conDict = dict.funDict(dict.getPath(), conNode);
    conDict.declare(Names.PRIVATE_THIS, conDict.reserve(loc, Names.PRIVATE_THIS, true, javaSrTypeName, conNode.name,
        conNode.signature, null, null, type, AccessMode.readWrite, VarSource.localVar, JavaKind.general));

    // Define the arguments
    final Definer definer;

    VarInfo argArray = new VarInfo(loc, Names.ARG_ARRAY, true, VarSource.localVar, null, JavaKind.general,
        Theta.FIRST_OFFSET, AccessMode.readOnly, TypeUtils.arrayType(StandardTypes.anyType), Utils
        .javaIdentifierOf(Names.ARG_ARRAY), null, Types.IVALUE_ARRAY, Types.IVALUE_ARRAY, null, null, conDict
        .getOwnerName());
    definer = new LongArgDefiner(argArray, ccxt.getRepository());
    localVariables.add(new LocalVariableNode(Names.ARG_ARRAY, argArray.getJavaSig(), null, firstLabel, endLabel,
        Theta.FIRST_OFFSET));
    conDict.declare(Names.ARG_ARRAY, argArray);

    // Store the arguments in fields
    for (int ix = 0; ix < args.size(); ix++) {
      int mark = hwm.getDepth();
      IAbstract arg = (IAbstract) args.getCell(ix);
      String id;
      IAbstract tp;

      if (CafeSyntax.isTypedTerm(arg)) {
        id = ((Name) CafeSyntax.typedTerm(arg)).getId();
        tp = CafeSyntax.typedType(arg);
      } else {
        id = conargId(ix);
        tp = arg;
      }
      IType fieldType = TypeAnalyser.parseType(tp, conDict, errors);

      VarInfo field = definer.declareArg(arg.getLoc(), id, ix, fieldType, conDict, AccessMode.readOnly, true, errors);
      assert field != null;

      ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
      field.loadValue(mtd, hwm, conDict);

      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, field.getJavaType()));

      String javaSig = field.getJavaSig();
      String javaSafeName = field.getJavaSafeName();

      hwm.bump(1);
      ins.add(new FieldInsnNode(Opcodes.PUTFIELD, javaName, javaSafeName, javaSig));
      hwm.reset(mark);
    }

    ins.add(new InsnNode(Opcodes.RETURN));
    ins.add(endLabel);

    mtd.maxLocals = conDict.getLocalHWM();
    mtd.maxStack = hwm.getHwm();
    return mtd;
  }

  private static MethodNode genConix(ClassNode conNode, int conIx) {
    MethodNode getLabel = new MethodNode(Opcodes.ACC_PUBLIC, CONIX, "()I", null, new String[]{});
    LabelNode fLabel = new LabelNode();
    LabelNode eLabel = new LabelNode();

    getLabel.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, fLabel, eLabel,
        Theta.THIS_OFFSET));

    InsnList ins = getLabel.instructions;
    ins.add(fLabel);
    HWM hwm = new HWM();
    Expressions.genIntConst(ins, hwm, conIx);
    ins.add(new InsnNode(Opcodes.IRETURN));
    ins.add(eLabel);
    getLabel.maxLocals = 1;
    getLabel.maxStack = hwm.getHwm();

    return getLabel;
  }

  public static MethodNode genLabel(String conLbl, ClassNode conNode) {
    MethodNode getLabel = new MethodNode(Opcodes.ACC_PUBLIC, GET_LABEL, "()Ljava/lang/String;", null, new String[]{});
    LabelNode fLabel = new LabelNode();
    LabelNode eLabel = new LabelNode();

    getLabel.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, fLabel, eLabel,
        Theta.THIS_OFFSET));

    InsnList ins = getLabel.instructions;
    ins.add(fLabel);

    ins.add(new LdcInsnNode(conLbl));
    ins.add(new InsnNode(Opcodes.ARETURN));
    ins.add(eLabel);
    getLabel.maxLocals = 1;
    getLabel.maxStack = 1;
    return getLabel;
  }

  private static MethodNode genVerify(ClassNode conNode, String lbl, int conIx) {
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, VERIFY, VERIFY_INVOKE_SIG, null, new String[]{});
    LabelNode fLabel = new LabelNode();
    LabelNode eLabel = new LabelNode();
    LabelNode fail = new LabelNode();

    mtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, fLabel, eLabel,
        Theta.THIS_OFFSET));

    mtd.localVariables.add(new LocalVariableNode(Names.PTN_ARG, Types.IVALUE_SIG, null, fLabel, eLabel,
        Theta.FIRST_OFFSET));

    HWM hwm = new HWM();

    InsnList ins = mtd.instructions;
    ins.add(fLabel);
    int mark = hwm.bump(1);
    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.FIRST_OFFSET));
    ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.ICONSTRUCTOR));
    ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR, CONIX, "()I"));
    Expressions.genIntConst(ins, hwm, conIx);
    ins.add(new JumpInsnNode(Opcodes.IF_ICMPNE, fail));
    hwm.reset(mark);

    hwm.bump(1);
    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.FIRST_OFFSET));
    ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.ICONSTRUCTOR));
    ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR, GET_LABEL, TOSTRING_SIG));

    ins.add(new LdcInsnNode(lbl));

    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.OBJECT, EQUALS, Types.EQUALS_SIG));
    ins.add(new InsnNode(Opcodes.IRETURN));
    ins.add(fail);
    Expressions.genIntConst(ins, hwm, 0);
    ins.add(new InsnNode(Opcodes.IRETURN));
    ins.add(eLabel);
    mtd.maxLocals = 2;
    mtd.maxStack = hwm.getHwm();
    return mtd;
  }

  public static MethodNode genSize(int size, ClassNode conNode) {
    MethodNode getLabel = new MethodNode(Opcodes.ACC_PUBLIC, "size", "()I", null, new String[]{});
    LabelNode fLabel = new LabelNode();
    LabelNode eLabel = new LabelNode();

    getLabel.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, fLabel, eLabel,
        Theta.THIS_OFFSET));

    InsnList ins = getLabel.instructions;
    ins.add(fLabel);
    HWM hwm = new HWM();
    Expressions.genIntConst(ins, hwm, size);
    ins.add(new InsnNode(Opcodes.IRETURN));
    ins.add(eLabel);
    getLabel.maxLocals = 1;
    getLabel.maxStack = hwm.getHwm();
    return getLabel;
  }

  private static void genGettersAndSetters(String typeLabel, ClassNode typeNode, Map<String, VarInfo> fields,
                                           CafeDictionary dict, ErrorReport errors) {
    TypeDescription desc = (TypeDescription) dict.findType(typeLabel);
    for (Entry<String, VarInfo> entry : fields.entrySet()) {
      VarInfo var = entry.getValue();
      genGetter(typeNode, var, desc, errors);
      genSetter(typeNode, var, desc, errors);
    }
  }

  private static void genGetter(ClassNode typeNode, VarInfo var, TypeDescription desc, ErrorReport errors) {
    String javaSig = var.getJavaSig();
    String id = var.getName();
    String javaName = var.getJavaSafeName();
    String getterName = Types.getterName(javaName);

    MethodNode getter = new MethodNode(Opcodes.ACC_PUBLIC, getterName, "()" + javaSig, null, new String[]{});
    HWM hwm = new HWM();
    InsnList ins = getter.instructions;
    LabelNode firstLabel = new LabelNode();
    LabelNode endLabel = new LabelNode();
    ins.add(firstLabel);
    getter.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, typeNode.signature, null, firstLabel, endLabel,
        Theta.THIS_OFFSET));

    Actions.doLineNumber(var.getLoc(), getter);

    if (desc.getValueSpecifiers().size() > 1) {
      int maxIx = desc.maxConIx();

      LabelNode labels[] = new LabelNode[maxIx + 1];
      for (int ix = 0; ix < labels.length; ix++)
        labels[ix] = new LabelNode();

      LabelNode defltLbl = new LabelNode();

      hwm.probe(1);
      ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.ICONSTRUCTOR));
      ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR, Constructors.CONIX, "()I"));
      ins.add(new TableSwitchInsnNode(0, maxIx, defltLbl, labels));

      for (IValueSpecifier spec : desc.getValueSpecifiers()) {
        ICafeConstructorSpecifier con = (ICafeConstructorSpecifier) spec;
        int conIx = con.getConIx();
        LabelNode caseLbl = labels[conIx];
        ins.add(caseLbl);
        if (con.hasMember(id)) {
          hwm.bump(1);
          ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
          String conJavaType = con.getJavaType();
          ins.add(new TypeInsnNode(Opcodes.CHECKCAST, conJavaType));
          ins.add(new FieldInsnNode(Opcodes.GETFIELD, conJavaType, javaName, javaSig));
          switch (var.getKind()) {
            case general:
            case constructor:
            case rawBinary:
            case rawString:
            case rawDecimal:
              genNullTest(desc.getLoc(), hwm, getter);
              ins.add(new InsnNode(Opcodes.ARETURN));
              break;
            case rawBool:
            case rawChar:
            case rawInt:
              ins.add(new InsnNode(Opcodes.IRETURN));
              break;
            case rawLong:
              hwm.bump(1);
              ins.add(new InsnNode(Opcodes.LRETURN));
              break;
            case rawFloat:
              hwm.bump(1);
              ins.add(new InsnNode(Opcodes.DRETURN));
              break;
            case builtin:
            case userJava:
              errors.reportError("invalid variable type: " + id, var.getLoc());
              break;
          }
        } else
          ins.add(new JumpInsnNode(Opcodes.GOTO, defltLbl));
      }
      ins.add(defltLbl);
      hwm.probe(3);
      ins.add(new TypeInsnNode(Opcodes.NEW, ILLEGAL_ARGUMENT_EXCEPTION));
      ins.add(new InsnNode(Opcodes.DUP));
      ins.add(new LdcInsnNode(var.getLoc().toString()));
      ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ILLEGAL_ARGUMENT_EXCEPTION, Types.INIT, JAVA_STRING_VOID_SIG));
      ins.add(new InsnNode(Opcodes.ATHROW));
    } else if (desc.getValueSpecifiers().size() == 1) {
      ICafeConstructorSpecifier con = (ICafeConstructorSpecifier) desc.getOnlyValueSpecifier();
      if (con.hasMember(id)) {
        hwm.probe(1);
        ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
        String conJavaType = con.getJavaType();
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, conJavaType));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, conJavaType, Utils.javaIdentifierOf(id), javaSig));
        switch (var.getKind()) {
          case general:
          case constructor:
          case rawBinary:
          case rawString:
          case rawDecimal:
            genNullTest(desc.getLoc(), hwm, getter);
            ins.add(new InsnNode(Opcodes.ARETURN));
            break;
          case rawBool:
          case rawChar:
          case rawInt:
            ins.add(new InsnNode(Opcodes.IRETURN));
            break;
          case rawLong:
            hwm.bump(2);
            ins.add(new InsnNode(Opcodes.LRETURN));
            break;
          case rawFloat:
            hwm.bump(2);
            ins.add(new InsnNode(Opcodes.DRETURN));
            break;
          case builtin:
          case userJava:
            errors.reportError("invalid variable type: " + id, var.getLoc());
            break;
        }
      } else {
        hwm.bump(2);
        ins.add(new TypeInsnNode(Opcodes.NEW, ILLEGAL_ARGUMENT_EXCEPTION));
        ins.add(new InsnNode(Opcodes.DUP));
        ins.add(new LdcInsnNode(var.getLoc().toString()));
        ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ILLEGAL_ARGUMENT_EXCEPTION, Types.INIT, JAVA_STRING_VOID_SIG));
        ins.add(new InsnNode(Opcodes.ATHROW));
      }
    } else
      errors.reportError("no known value specifiers for " + desc.getName(), desc.getLoc());

    ins.add(endLabel);

    getter.maxLocals = 1;
    getter.maxStack = hwm.getHwm();

    typeNode.methods.add(getter);
  }

  private static void genConSpecificGetter(ClassNode conNode, VarInfo var, ErrorReport errors, CafeDictionary dict) {
    String javaSig = var.getJavaSig();

    String id = var.getName();
    String javaName = var.getJavaSafeName();
    String getterName = Types.getterName(javaName);

    MethodNode getter = new MethodNode(Opcodes.ACC_PUBLIC, getterName, "()" + javaSig, null, new String[]{});
    InsnList ins = getter.instructions;
    LabelNode firstLabel = new LabelNode();
    LabelNode endLabel = new LabelNode();
    ins.add(firstLabel);
    getter.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, firstLabel, endLabel,
        Theta.THIS_OFFSET));

    Actions.doLineNumber(var.getLoc(), getter);

    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, javaName, javaSig));
    switch (var.getKind()) {
      case general:
      case constructor:
      case rawBinary:
      case rawString:
      case rawDecimal:
        ins.add(new InsnNode(Opcodes.ARETURN));
        break;
      case rawBool:
      case rawChar:
      case rawInt:
        ins.add(new InsnNode(Opcodes.IRETURN));
        break;
      case rawLong:
        ins.add(new InsnNode(Opcodes.LRETURN));
        break;
      case rawFloat:
        ins.add(new InsnNode(Opcodes.DRETURN));
        break;
      case builtin:
      case userJava:
        errors.reportError(StringUtils.msg(id, " has invalid variable type: ", var.getType()), var.getLoc());
        break;
    }

    ins.add(endLabel);

    getter.maxLocals = 1;
    getter.maxStack = 4;

    conNode.methods.add(getter);

    if (!javaSig.equals(Types.IVALUE_SIG))
      genConSpecificIValueGetter(conNode, var, dict);
  }

  private static void genConSpecificIValueGetter(ClassNode conNode, VarInfo var, CafeDictionary dict) {
    String javaSig = var.getJavaSig();
    String javaName = var.getJavaSafeName();
    String getterName = Types.getterName(javaName);

    MethodNode getter = new MethodNode(Opcodes.ACC_PUBLIC, getterName, "()" + Types.IVALUE_SIG, null, new String[]{});
    InsnList ins = getter.instructions;
    LabelNode firstLabel = new LabelNode();
    LabelNode endLabel = new LabelNode();
    ins.add(firstLabel);
    getter.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, firstLabel, endLabel,
        Theta.THIS_OFFSET));

    Actions.doLineNumber(var.getLoc(), getter);

    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));

    ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, javaName, javaSig));
    AutoBoxing.boxValue(var.getType(), ins, dict);
    ins.add(new InsnNode(Opcodes.ARETURN));

    ins.add(endLabel);

    getter.maxLocals = 1;
    getter.maxStack = 4;

    conNode.methods.add(getter);
  }

  static void genConSpecificGetters(ClassNode conNode, Map<String, VarInfo> fields, ErrorReport errors,
                                    CafeDictionary dict) {
    for (Entry<String, VarInfo> entry : fields.entrySet())
      genConSpecificGetter(conNode, entry.getValue(), errors, dict);
  }

  static MethodNode genGetCell(ClassNode typeNode, List<ISpec> argSpecs, Map<String, Integer> index,
                               CafeDictionary dict, Location loc) {
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, GET_CELL, "(I)" + Types.IVALUE_SIG, null, new String[]{});

    // Build the hash table for switching on member name
    int keys[] = new int[index.size()];
    LabelNode labels[] = new LabelNode[index.size()];

    for (int ix = 0; ix < index.size(); ix++) {
      labels[ix] = new LabelNode();
      keys[ix] = ix;
    }

    LabelNode firstLbl = new LabelNode();
    LabelNode defltLbl = new LabelNode();
    LabelNode endLbl = new LabelNode();
    InsnList ins = mtd.instructions;
    Actions.doLineNumber(loc, mtd);

    ins.add(firstLbl);
    int XXoffset = 1;
    mtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, typeNode.signature, null, firstLbl, endLbl,
        Theta.THIS_OFFSET));
    mtd.localVariables.add(new LocalVariableNode("IX", "I", null, firstLbl, endLbl, XXoffset));

    // switch(XX) ...
    ins.add(new VarInsnNode(Opcodes.ILOAD, XXoffset));
    ins.add(new LookupSwitchInsnNode(defltLbl, keys, labels));

    // case "ix": ...
    for (int ix = 0; ix < index.size(); ix++) {
      ins.add(labels[ix]);
      for (Entry<String, Integer> entry : index.entrySet()) {
        if (entry.getValue() == ix) {
          ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
          ins.add(new FieldInsnNode(Opcodes.GETFIELD, typeNode.name, Utils.javaIdentifierOf(entry.getKey()), argSpecs
              .get(ix).getJavaSig()));
          AutoBoxing.boxValue(argSpecs.get(ix).getType(), ins, dict);
          ins.add(new InsnNode(Opcodes.ARETURN));
        }
      }
    }

    // default: throw new IllegalArgumentException("member not present")
    ins.add(defltLbl);
    ins.add(new TypeInsnNode(Opcodes.NEW, ILLEGAL_ARGUMENT_EXCEPTION));
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new LdcInsnNode("index out of range"));
    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ILLEGAL_ARGUMENT_EXCEPTION, Types.INIT, JAVA_STRING_VOID_SIG));
    ins.add(new InsnNode(Opcodes.ATHROW));

    ins.add(endLbl);

    mtd.maxLocals = 2;
    mtd.maxStack = 3;
    return mtd;
  }

  private static MethodNode genSetCell(ClassNode typeNode, List<ISpec> argSpecs, Map<String, Integer> index,
                                       CafeDictionary dict, CodeCatalog bldCat, ErrorReport errors, Location loc) {
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, SET_CELL, "(I" + Types.IVALUE_SIG + ")V", null, new String[]{});

    // Build the hash table for switching on member name
    int keys[] = new int[index.size()];
    LabelNode labels[] = new LabelNode[index.size()];

    for (int ix = 0; ix < index.size(); ix++) {
      labels[ix] = new LabelNode();
      keys[ix] = ix;
    }

    LabelNode firstLbl = new LabelNode();
    LabelNode defltLbl = new LabelNode();
    LabelNode endLbl = new LabelNode();
    InsnList ins = mtd.instructions;

    HWM hwm = new HWM();

    ins.add(firstLbl);
    int IXoffset = 1;
    int VOffset = 2;
    mtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, typeNode.signature, null, firstLbl, endLbl,
        Theta.THIS_OFFSET));
    mtd.localVariables.add(new LocalVariableNode("IX", "I", null, firstLbl, endLbl, IXoffset));
    mtd.localVariables.add(new LocalVariableNode("V", Types.IVALUE_SIG, null, firstLbl, endLbl, VOffset));

    if (labels.length > 0) {
      // switch(XX) ...
      hwm.bump(1);
      ins.add(new VarInsnNode(Opcodes.ILOAD, IXoffset));
      ins.add(new LookupSwitchInsnNode(defltLbl, keys, labels));
      hwm.bump(-1);

      // case "ix": ...
      for (int ix = 0; ix < index.size(); ix++) {
        ins.add(labels[ix]);
        for (Entry<String, Integer> entry : index.entrySet()) {
          if (entry.getValue() == ix) {
            int mark = hwm.getDepth();
            hwm.bump(2);
            ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
            ins.add(new VarInsnNode(Opcodes.ALOAD, VOffset));

            Expressions.checkType(SrcSpec.generalSrc, argSpecs.get(ix), mtd, dict, hwm);

            ins.add(new FieldInsnNode(Opcodes.PUTFIELD, typeNode.name, Utils.javaIdentifierOf(entry.getKey()), argSpecs
                .get(ix).getJavaSig()));
            ins.add(new InsnNode(Opcodes.RETURN));

            hwm.reset(mark);
          }
        }
      }
    }

    // default: throw new IllegalArgumentException("member not present")
    ins.add(defltLbl);
    hwm.bump(3);
    ins.add(new TypeInsnNode(Opcodes.NEW, ILLEGAL_ARGUMENT_EXCEPTION));
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new LdcInsnNode("index out of range"));
    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ILLEGAL_ARGUMENT_EXCEPTION, Types.INIT, JAVA_STRING_VOID_SIG));
    ins.add(new InsnNode(Opcodes.ATHROW));

    ins.add(endLbl);

    mtd.maxLocals = 3;
    mtd.maxStack = hwm.getHwm();
    return mtd;
  }

  static MethodNode genGetCells(ClassNode typeNode, List<ISpec> argSpecs, Map<String, VarInfo> fields,
                                Map<String, Integer> index, CafeDictionary dict) {
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, GET_CELLS, GETCELLS_INVOKESIG, null, new String[]{});
    InsnList ins = mtd.instructions;
    LabelNode firstLbl = new LabelNode();
    LabelNode endLbl = new LabelNode();

    mtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, typeNode.signature, null, firstLbl, endLbl,
        Theta.THIS_OFFSET));

    HWM hwm = new HWM();

    Expressions.genIntConst(ins, hwm, fields.size());

    ins.add(new TypeInsnNode(Opcodes.ANEWARRAY, Types.IVALUE));

    for (Entry<String, Integer> entry : index.entrySet()) {
      String field = entry.getKey();
      int ix = entry.getValue();

      int mark = hwm.getDepth();

      hwm.bump(1);
      ins.add(new InsnNode(Opcodes.DUP));

      Expressions.genIntConst(ins, hwm, ix);

      hwm.bump(3);
      ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));

      ISpec ixSpec = argSpecs.get(ix);
      ins.add(new FieldInsnNode(Opcodes.GETFIELD, typeNode.name, Utils.javaIdentifierOf(field), ixSpec.getJavaSig()));

      AutoBoxing.boxValue(ixSpec.getType(), ins, dict);
      ins.add(new InsnNode(Opcodes.AASTORE));

      hwm.reset(mark);
    }
    ins.add(new InsnNode(Opcodes.ARETURN));

    ins.add(endLbl);

    mtd.maxLocals = 2;
    mtd.maxStack = hwm.getHwm();
    return mtd;
  }

  private static Map<Integer, Pair<List<String>, LabelNode>> buildIndexIns(Collection<String> keys, MethodNode mtd,
                                                                           LabelNode deflt) {
    SortedMap<Integer, Pair<List<String>, LabelNode>> hashes = new TreeMap<>();

    for (String key : keys) {
      int keyHash = key.hashCode();
      Pair<List<String>, LabelNode> indexEntry = hashes.get(keyHash);
      if (indexEntry == null) {
        List<String> lft = new ArrayList<>();
        lft.add(key);
        indexEntry = Pair.pair(lft, new LabelNode());
        hashes.put(keyHash, indexEntry);
      } else
        indexEntry.left().add(key);
    }

    InsnList ins = mtd.instructions;
    int size = hashes.size();
    int keyTable[] = new int[size];
    LabelNode labels[] = new LabelNode[size];

    int ix = 0;
    for (Entry<Integer, Pair<List<String>, LabelNode>> entry : hashes.entrySet()) {
      keyTable[ix] = entry.getKey();
      labels[ix] = entry.getValue().getValue();
      ix++;
    }
    assert ix == size;

    ins.add(new LookupSwitchInsnNode(deflt, keyTable, labels));

    return hashes;
  }

  static void genGetMember(ClassNode typeNode, List<ISpec> argSpecs, Map<String, Integer> index, CafeDictionary dict,
                           Location loc) {
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, "getMember", "(" + Types.JAVA_STRING_SIG + ")"
        + Types.IVALUE_SIG, null, new String[]{});

    LabelNode firstLbl = new LabelNode();
    LabelNode defltLbl = new LabelNode();
    LabelNode endLbl = new LabelNode();
    InsnList ins = mtd.instructions;
    HWM hwm = new HWM();

    Actions.doLineNumber(loc, mtd);
    ins.add(firstLbl);
    int XXoffset = 1;
    mtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, typeNode.signature, null, firstLbl, endLbl,
        Theta.THIS_OFFSET));
    mtd.localVariables.add(new LocalVariableNode("XX", Types.JAVA_STRING_SIG, null, firstLbl, endLbl, XXoffset));

    // switch(XX) ...
    hwm.bump(1);
    ins.add(new VarInsnNode(Opcodes.ALOAD, XXoffset));
    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.JAVA_OBJECT_TYPE, "hashCode", "()I"));

    Map<Integer, Pair<List<String>, LabelNode>> hashes = buildIndexIns(index.keySet(), mtd, defltLbl);

    // case "member": ...
    for (Entry<Integer, Pair<List<String>, LabelNode>> entry : hashes.entrySet()) {
      ins.add(entry.getValue().right());

      List<String> names = entry.getValue().left();
      LabelNode next;
      for (int ix = 0; ix < names.size(); ix++) {
        String field = names.get(ix);
        String memberName = Utils.javaIdentifierOf(field);
        int fieldIx = index.get(field);
        next = null;

        if (ix < names.size() - 1) {
          next = new LabelNode();
          hwm.probe(2);
          ins.add(new VarInsnNode(Opcodes.ALOAD, XXoffset));
          ins.add(new LdcInsnNode(field));
          ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.JAVA_OBJECT_TYPE, "equals", Types.EQUALS_SIG));
          ins.add(new JumpInsnNode(Opcodes.IFEQ, next));
        }
        hwm.probe(1);
        ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, typeNode.name, memberName, argSpecs.get(fieldIx).getJavaSig()));
        AutoBoxing.boxValue(argSpecs.get(fieldIx).getType(), ins, dict);
        ins.add(new InsnNode(Opcodes.ARETURN));
        if (next != null)
          ins.add(next);
      }
    }

    // default: throw new IllegalArgumentException("member not present")
    ins.add(defltLbl);
    memberNotPreset(ins, XXoffset, hwm);

    ins.add(endLbl);

    mtd.maxLocals = 2;
    mtd.maxStack = hwm.getHwm();
    typeNode.methods.add(mtd);
  }

  private static boolean isEnum(VarInfo var) {
    return TypeUtils.isConstructorType(var.getType()) && TypeUtils.arityOfConstructorType(var.getType()) == 0;
  }

  private static void memberNotPreset(InsnList ins, int offset, HWM hwm) {
    hwm.probe(5);
    ins.add(new TypeInsnNode(Opcodes.NEW, ILLEGAL_ARGUMENT_EXCEPTION));
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new TypeInsnNode(Opcodes.NEW, STRINGBUILDER));
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new LdcInsnNode("member "));
    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, STRINGBUILDER, Types.INIT, JAVA_STRING_VOID_SIG));
    ins.add(new VarInsnNode(Opcodes.ALOAD, offset));
    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, STRINGBUILDER, "append", STRINGBUILDER_APPEND_SIG));
    ins.add(new LdcInsnNode(" not present"));
    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, STRINGBUILDER, "append", STRINGBUILDER_APPEND_SIG));
    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, STRINGBUILDER, "toString", TOSTRING_SIG));

    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ILLEGAL_ARGUMENT_EXCEPTION, Types.INIT, JAVA_STRING_VOID_SIG));
    ins.add(new InsnNode(Opcodes.ATHROW));
  }

  private static void genSetMember(ClassNode typeNode, List<ISpec> argSpecs, Map<String, Integer> index) {
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, SET_MEMBER, SET_MEMBER_SIG, null, new String[]{});

    LabelNode firstLbl = new LabelNode();
    LabelNode defltLbl = new LabelNode();
    LabelNode endLbl = new LabelNode();
    InsnList ins = mtd.instructions;
    HWM hwm = new HWM();

    ins.add(firstLbl);
    int XXoffset = 1;
    int valueOffset = 2;
    mtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, typeNode.signature, null, firstLbl, endLbl,
        Theta.THIS_OFFSET));
    mtd.localVariables.add(new LocalVariableNode("key", Types.JAVA_STRING_SIG, null, firstLbl, endLbl, XXoffset));
    mtd.localVariables
        .add(new LocalVariableNode("value", Types.IVALUEVISITOR_SIG, null, firstLbl, endLbl, valueOffset));

    // switch(XX) ...
    hwm.bump(1);
    hwm.probe(1);
    ins.add(new VarInsnNode(Opcodes.ALOAD, XXoffset));
    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.JAVA_OBJECT_TYPE, "hashCode", "()I"));

    Map<Integer, Pair<List<String>, LabelNode>> hashes = buildIndexIns(index.keySet(), mtd, defltLbl);

    // case "member": ...
    for (Entry<Integer, Pair<List<String>, LabelNode>> entry : hashes.entrySet()) {
      ins.add(entry.getValue().right());

      List<String> names = entry.getValue().left();
      LabelNode next;
      for (int ix = 0; ix < names.size(); ix++) {
        String field = names.get(ix);
        String memberName = Utils.javaIdentifierOf(field);
        int fieldIx = index.get(field);
        ISpec fieldSpec = argSpecs.get(fieldIx);

        next = null;

        if (ix < names.size() - 1) {
          next = new LabelNode();
          hwm.probe(2);
          ins.add(new VarInsnNode(Opcodes.ALOAD, XXoffset));
          ins.add(new LdcInsnNode(field));
          ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.JAVA_OBJECT_TYPE, "equals", Types.EQUALS_SIG));
          ins.add(new JumpInsnNode(Opcodes.IFEQ, next));
        }

        hwm.probe(2);
        ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
        ins.add(new VarInsnNode(Opcodes.ALOAD, valueOffset));

        AutoBoxing.unboxValue(mtd, hwm, fieldSpec.getType());
        // FIXME: This probably should be done in unboxValue..
        if (Types.varType(fieldSpec.getType()) == JavaKind.general)
          ins.add(new TypeInsnNode(Opcodes.CHECKCAST, fieldSpec.getJavaType()));
        ins.add(new FieldInsnNode(Opcodes.PUTFIELD, typeNode.name, memberName, fieldSpec.getJavaSig()));

        ins.add(new InsnNode(Opcodes.RETURN));

        if (next != null)
          ins.add(next);
      }
    }

    // default: throw new IllegalArgumentException("member not present")
    ins.add(defltLbl);
    memberNotPreset(ins, XXoffset, hwm);

    ins.add(endLbl);

    mtd.maxLocals = 3;
    mtd.maxStack = hwm.getHwm();
    typeNode.methods.add(mtd);
  }

  static MethodNode genEquals(ClassNode conNode, Map<String, VarInfo> fields, Map<String, Integer> index,
                              ErrorReport errors, Location loc) {
    MethodNode equals = new MethodNode(Opcodes.ACC_PUBLIC, "equals", Types.EQUALS_SIG, null, new String[]{});
    LabelNode fLabel = new LabelNode();
    LabelNode eLabel = new LabelNode();

    equals.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, fLabel, eLabel,
        Theta.THIS_OFFSET));
    equals.localVariables.add(new LocalVariableNode("XX", Types.OBJECT_SIG, null, fLabel, eLabel, Theta.FIRST_OFFSET));

    InsnList ins = equals.instructions;
    ins.add(fLabel);
    HWM hwm = new HWM();

    LabelNode failLabel = new LabelNode();

    Actions.doLineNumber(loc, equals);

    hwm.probe(1);
    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.FIRST_OFFSET));
    ins.add(new TypeInsnNode(Opcodes.INSTANCEOF, conNode.name));
    ins.add(new JumpInsnNode(Opcodes.IFEQ, failLabel));

    for (Entry<String, VarInfo> entry : fields.entrySet()) {
      if (index.containsKey(entry.getKey())) {
        VarInfo field = entry.getValue();
        switch (field.getKind()) {
          case rawBool:
          case rawChar:
          case rawInt:
            hwm.probe(4);
            ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
            ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, field.getJavaSafeName(), field.getJavaSig()));
            ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.FIRST_OFFSET));
            ins.add(new TypeInsnNode(Opcodes.CHECKCAST, conNode.name));
            ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, field.getJavaSafeName(), field.getJavaSig()));
            ins.add(new JumpInsnNode(Opcodes.IF_ICMPNE, failLabel));
            break;
          case rawLong:
            hwm.probe(6);
            ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
            ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, field.getJavaSafeName(), field.getJavaSig()));
            ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.FIRST_OFFSET));
            ins.add(new TypeInsnNode(Opcodes.CHECKCAST, conNode.name));
            ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, field.getJavaSafeName(), field.getJavaSig()));
            ins.add(new InsnNode(Opcodes.LCMP));
            ins.add(new JumpInsnNode(Opcodes.IFNE, failLabel));
            break;
          case rawFloat:
            hwm.probe(6);
            ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
            ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, field.getJavaSafeName(), field.getJavaSig()));
            ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.FIRST_OFFSET));
            ins.add(new TypeInsnNode(Opcodes.CHECKCAST, conNode.name));
            ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, field.getJavaSafeName(), field.getJavaSig()));
            ins.add(new InsnNode(Opcodes.DCMPG));
            ins.add(new JumpInsnNode(Opcodes.IFNE, failLabel));
            break;
          case rawBinary:
          case rawString:
          case rawDecimal:
          case general:
            hwm.probe(4);
            ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
            ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, field.getJavaSafeName(), field.getJavaSig()));
            ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.FIRST_OFFSET));
            ins.add(new TypeInsnNode(Opcodes.CHECKCAST, conNode.name));
            ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, field.getJavaSafeName(), field.getJavaSig()));
            ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.OBJECT, "equals", Types.EQUALS_SIG));
            ins.add(new JumpInsnNode(Opcodes.IFEQ, failLabel));
            break;
          default:
            errors.reportError("illegal type of field", field.getLoc());
        }
      }
    }

    // Success return
    ins.add(new InsnNode(Opcodes.ICONST_1));
    ins.add(new InsnNode(Opcodes.IRETURN));
    ins.add(failLabel);
    ins.add(new InsnNode(Opcodes.ICONST_0));
    ins.add(new InsnNode(Opcodes.IRETURN));

    ins.add(eLabel);
    equals.maxLocals = 2;
    equals.maxStack = hwm.getHwm();
    return equals;
  }

  static MethodNode genHashcode(ClassNode conNode, String conLabel, Map<String, VarInfo> fields,
                                Map<String, Integer> index, ErrorReport errors, Location loc) {
    MethodNode hashMtd = new MethodNode(Opcodes.ACC_PUBLIC, "hashCode", Types.HASH_SIG, null, new String[]{});
    LabelNode fLabel = new LabelNode();
    LabelNode eLabel = new LabelNode();

    hashMtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, fLabel, eLabel,
        Theta.THIS_OFFSET));

    InsnList ins = hashMtd.instructions;
    ins.add(fLabel);
    HWM hwm = new HWM();

    Actions.doLineNumber(loc, hashMtd);

    hwm.bump(1);
    ins.add(new LdcInsnNode(conLabel));// hash of the label
    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.OBJECT, "hashCode", Types.HASH_SIG));

    for (Entry<String, VarInfo> entry : fields.entrySet()) {
      hwm.probe(1);
      Expressions.genIntConst(ins, hwm, 37); // Hash code multiplier
      ins.add(new InsnNode(Opcodes.IMUL));

      if (index.containsKey(entry.getKey())) {
        VarInfo field = entry.getValue();
        switch (field.getKind()) {
          case rawBool:
          case rawChar:
          case rawInt:
            hwm.probe(2);
            ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
            ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, field.getJavaSafeName(), field.getJavaSig()));
            ins.add(new InsnNode(Opcodes.IADD));
            break;
          case rawLong:
            hwm.probe(6);
            // Copied from java.lang.Long
            ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
            ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, field.getJavaSafeName(), field.getJavaSig()));
            ins.add(new InsnNode(Opcodes.DUP2));
            ins.add(new IntInsnNode(Opcodes.BIPUSH, 32));
            ins.add(new InsnNode(Opcodes.LUSHR));
            ins.add(new InsnNode(Opcodes.LXOR));
            ins.add(new InsnNode(Opcodes.L2I));
            ins.add(new InsnNode(Opcodes.IADD));
            break;
          case rawFloat:
            hwm.probe(6);
            ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
            ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, field.getJavaSafeName(), field.getJavaSig()));
            ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Types.JAVA_DOUBLE_TYPE, "doubleToLongBits", "(D)J"));
            ins.add(new InsnNode(Opcodes.DUP2));
            ins.add(new IntInsnNode(Opcodes.BIPUSH, 32));
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
            ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, field.getJavaSafeName(), field.getJavaSig()));
            ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, Types.OBJECT, "hashCode", Types.HASH_SIG));
            ins.add(new InsnNode(Opcodes.IADD));
            break;
          default:
            errors.reportError("illegal type of field", field.getLoc());
        }
      }
    }

    // return
    ins.add(new InsnNode(Opcodes.IRETURN));
    ins.add(eLabel);
    hashMtd.maxLocals = 2;
    hashMtd.maxStack = hwm.getHwm();
    return hashMtd;
  }

  // Does not work for circular references
  static MethodNode genCopy(ClassNode conNode, String javaConSig, IList args, List<ISpec> argSpecs, boolean shallow) {
    MethodNode copy = new MethodNode(Opcodes.ACC_PUBLIC, shallow ? "shallowCopy" : "copy", "()" + conNode.signature,
        null, new String[]{Types.EVALUATION_EXCEPTION});

    LabelNode fLabel = new LabelNode();
    LabelNode eLabel = new LabelNode();

    HWM hwm = new HWM();
    hwm.bump(2);

    copy.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, fLabel, eLabel,
        Theta.THIS_OFFSET));

    InsnList ins = copy.instructions;
    ins.add(fLabel);
    ins.add(new TypeInsnNode(Opcodes.NEW, conNode.name));
    ins.add(new InsnNode(Opcodes.DUP));

    // Prepare arguments
    if (args.size() < Theta.MAX_ARGS) {
      for (int ix = 0; ix < args.size(); ix++) {
        hwm.bump(1);
        int mark = hwm.bump(1);
        IAbstract arg = (IAbstract) args.getCell(ix);
        ISpec argSpec = argSpecs.get(ix);
        hwm.reset(mark);
        copyArg(arg, argSpec, conNode, shallow, ins, ix, hwm);
      }
    } else {
      Expressions.genIntConst(ins, hwm, args.size());
      ins.add(new TypeInsnNode(Opcodes.ANEWARRAY, Types.IVALUE));

      for (int ix = 0; ix < args.size(); ix++) {
        int mark = hwm.bump(2);
        IAbstract arg = (IAbstract) args.getCell(ix);
        ISpec argSpec = argSpecs.get(ix);

        ins.add(new InsnNode(Opcodes.DUP));
        Expressions.genIntConst(ins, hwm, ix);

        copyArg(arg, argSpec, conNode, shallow, ins, ix, hwm);
        ins.add(new InsnNode(Opcodes.AASTORE));

        hwm.reset(mark);
      }
    }

    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, conNode.name, Types.INIT, javaConSig));
    ins.add(new InsnNode(Opcodes.ARETURN));
    ins.add(eLabel);

    copy.maxLocals = 2;
    copy.maxStack = hwm.getHwm();
    return copy;
  }

  public static void copyArg(IAbstract arg, ISpec argSpec, ClassNode conNode, boolean shallow, InsnList ins, int ix,
                             HWM hwm) {
    String javaSig = argSpec.getJavaSig();

    String id = CafeSyntax.isTypedTerm(arg) ? Utils.javaIdentifierOf(((Name) CafeSyntax.typedTerm(arg)).getId())
        : conargId(ix);

    hwm.bump(1);
    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    ins.add(new FieldInsnNode(Opcodes.GETFIELD, conNode.name, id, javaSig));

    if (!shallow && !TypeUtils.isRawType(argSpec.getType())) {
      hwm.probe(1);
      ins.add(new InsnNode(Opcodes.DUP));

      LabelNode nullLabel = new LabelNode();
      ins.add(new JumpInsnNode(Opcodes.IFNULL, nullLabel));
      ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.IVALUE, "copy", "()" + Types.IVALUE_SIG));
      ins.add(nullLabel);
    }
  }

  static MethodNode genCopyBridge(ClassNode conNode, String retSig, boolean shallow) {
    MethodNode copy = new MethodNode(Opcodes.ACC_PUBLIC, shallow ? "shallowCopy" : "copy", "()" + retSig, null,
        new String[]{Types.EVALUATION_EXCEPTION});

    InsnList ins = copy.instructions;
    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, conNode.name, shallow ? "shallowCopy" : "copy", "()"
        + conNode.signature));
    ins.add(new InsnNode(Opcodes.ARETURN));

    copy.maxLocals = 1;
    copy.maxStack = 1;
    return copy;
  }

  private static void genConstructorVisit(ClassNode typeNode) {
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, "accept", "(" + Types.IVALUEVISITOR_SIG + ")V", null,
        new String[]{});

    LabelNode firstLbl = new LabelNode();
    LabelNode endLbl = new LabelNode();
    InsnList ins = mtd.instructions;

    ins.add(firstLbl);
    int visitorOffset = 1;
    mtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, typeNode.signature, null, firstLbl, endLbl,
        Theta.THIS_OFFSET));
    mtd.localVariables.add(new LocalVariableNode("visitor", Types.IVALUEVISITOR_SIG, null, firstLbl, endLbl,
        visitorOffset));

    // switch(XX) ...
    ins.add(new VarInsnNode(Opcodes.ALOAD, visitorOffset));
    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.IVALUEVISITOR, "visitConstructor", "("
        + Types.ICONSTRUCTOR_SIG + ")V"));
    ins.add(new InsnNode(Opcodes.RETURN));

    ins.add(endLbl);

    mtd.maxLocals = 2;
    mtd.maxStack = 2;
    typeNode.methods.add(mtd);
  }

  public static void genRecordVisit(ClassNode typeNode) {
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, "accept", "(" + Types.IVALUEVISITOR_SIG + ")V", null,
        new String[]{});

    LabelNode firstLbl = new LabelNode();
    LabelNode endLbl = new LabelNode();
    InsnList ins = mtd.instructions;

    ins.add(firstLbl);
    int visitorOffset = 1;
    mtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, typeNode.signature, null, firstLbl, endLbl,
        Theta.THIS_OFFSET));
    mtd.localVariables.add(new LocalVariableNode("visitor", Types.IVALUEVISITOR_SIG, null, firstLbl, endLbl,
        visitorOffset));

    // switch(XX) ...
    ins.add(new VarInsnNode(Opcodes.ALOAD, visitorOffset));
    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.IVALUEVISITOR, "visitRecord", "(" + Types.IRECORD_SIG
        + ")V"));
    ins.add(new InsnNode(Opcodes.RETURN));

    ins.add(endLbl);

    mtd.maxLocals = 2;
    mtd.maxStack = 2;
    typeNode.methods.add(mtd);
  }

  public static void genToString(ClassNode node) {
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, Types.TOSTRING, Types.TOSTRING_SIG, null, new String[]{});

    LabelNode firstLbl = new LabelNode();
    LabelNode endLbl = new LabelNode();
    InsnList ins = mtd.instructions;

    ins.add(firstLbl);
    mtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, node.signature, null, firstLbl, endLbl,
        Theta.THIS_OFFSET));

    //
    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Types.VALUEDISPLAY, "display", "(" + Types.IVALUE_SIG + ")"
        + Types.JAVA_STRING_SIG));
    ins.add(new InsnNode(Opcodes.ARETURN));

    ins.add(endLbl);

    mtd.maxLocals = 2;
    mtd.maxStack = 2;
    node.methods.add(mtd);
  }

  public static void genFunctionVisit(ClassNode node) {
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, "accept", "(" + Types.IVALUEVISITOR_SIG + ")V", null,
        new String[]{});

    LabelNode firstLbl = new LabelNode();
    LabelNode endLbl = new LabelNode();
    InsnList ins = mtd.instructions;

    ins.add(firstLbl);
    int visitorOffset = 1;
    mtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, node.signature, null, firstLbl, endLbl,
        Theta.THIS_OFFSET));
    mtd.localVariables.add(new LocalVariableNode("visitor", Types.IVALUEVISITOR_SIG, null, firstLbl, endLbl,
        visitorOffset));

    // switch(XX) ...
    ins.add(new VarInsnNode(Opcodes.ALOAD, visitorOffset));
    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.IVALUEVISITOR, "visitFunction", "(" + Types.IFUNCTION_SIG
        + ")V"));
    ins.add(new InsnNode(Opcodes.RETURN));

    ins.add(endLbl);

    mtd.maxLocals = 2;
    mtd.maxStack = 2;
    node.methods.add(mtd);
  }

  public static void genPatternVisit(ClassNode node) {
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, "accept", "(" + Types.IVALUEVISITOR_SIG + ")V", null,
        new String[]{});

    LabelNode firstLbl = new LabelNode();
    LabelNode endLbl = new LabelNode();
    InsnList ins = mtd.instructions;

    ins.add(firstLbl);
    int visitorOffset = 1;
    mtd.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, node.signature, null, firstLbl, endLbl,
        Theta.THIS_OFFSET));
    mtd.localVariables.add(new LocalVariableNode("visitor", Types.IVALUEVISITOR_SIG, null, firstLbl, endLbl,
        visitorOffset));

    // switch(XX) ...
    ins.add(new VarInsnNode(Opcodes.ALOAD, visitorOffset));
    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.IVALUEVISITOR, "visitPattern", "(" + Types.IPATTERN_SIG
        + ")V"));
    ins.add(new InsnNode(Opcodes.RETURN));

    ins.add(endLbl);

    mtd.maxLocals = 2;
    mtd.maxStack = 2;
    node.methods.add(mtd);
  }

  private static void genSetter(ClassNode typeNode, VarInfo var, TypeDescription desc, ErrorReport errors) {
    String javaSig = var.getJavaSig();
    String field = var.getName();
    String setterSig = "(" + javaSig + ")V";
    String javaName = var.getJavaSafeName();
    String setterName = Types.setterName(javaName);

    MethodNode setter = new MethodNode(Opcodes.ACC_PUBLIC, setterName, setterSig, null, new String[]{});
    InsnList ins = setter.instructions;
    LabelNode firstLabel = new LabelNode();
    LabelNode endLabel = new LabelNode();
    ins.add(firstLabel);
    setter.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, typeNode.signature, null, firstLabel, endLabel,
        Theta.THIS_OFFSET));
    setter.localVariables.add(new LocalVariableNode("arg", javaSig, null, firstLabel, endLabel, 1));

    int localsSize = Types.stackAmnt(Types.varType(var.getType())) + 1;
    HWM hwm = new HWM();

    Actions.doLineNumber(var.getLoc(), setter);

    if (desc.getValueSpecifiers().size() > 1) {
      int maxIx = desc.maxConIx();

      LabelNode labels[] = new LabelNode[maxIx + 1];
      for (int ix = 0; ix < labels.length; ix++)
        labels[ix] = new LabelNode();

      LabelNode defltLbl = new LabelNode();

      int mark = hwm.bump(2);
      ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.ICONSTRUCTOR));
      ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.ICONSTRUCTOR, Constructors.CONIX, "()I"));
      ins.add(new TableSwitchInsnNode(0, maxIx, defltLbl, labels));
      hwm.reset(mark);

      for (IValueSpecifier spec : desc.getValueSpecifiers()) {
        ICafeConstructorSpecifier con = (ICafeConstructorSpecifier) spec;
        int conIx = con.getConIx();
        LabelNode caseLbl = labels[conIx];
        ins.add(caseLbl);
        if (con.hasMember(field)) {
          hwm.bump(1);
          ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
          String conJavaType = con.getJavaType();
          ins.add(new TypeInsnNode(Opcodes.CHECKCAST, conJavaType));

          switch (var.getKind()) {
            case general:
            case constructor:
            case rawBinary:
            case rawString:
            case rawDecimal:
              ins.add(new VarInsnNode(Opcodes.ALOAD, 1));
              hwm.bump(1);
              break;
            case rawBool:
            case rawChar:
            case rawInt:
              ins.add(new VarInsnNode(Opcodes.ILOAD, 1));
              hwm.bump(1);
              break;
            case rawLong:
              ins.add(new VarInsnNode(Opcodes.LLOAD, 1));
              hwm.bump(2);
              break;
            case rawFloat:
              ins.add(new VarInsnNode(Opcodes.DLOAD, 1));
              hwm.bump(2);
              break;
            case builtin:
            case userJava:
              errors.reportError("invalid variable: " + field, var.getLoc());
          }

          ins.add(new FieldInsnNode(Opcodes.PUTFIELD, conJavaType, javaName, javaSig));
          ins.add(new InsnNode(Opcodes.RETURN));
        } else
          ins.add(new JumpInsnNode(Opcodes.GOTO, defltLbl));
      }

      ins.add(defltLbl);

      hwm.reset(mark);
      hwm.bump(3);
      ins.add(new TypeInsnNode(Opcodes.NEW, ILLEGAL_ARGUMENT_EXCEPTION));
      ins.add(new InsnNode(Opcodes.DUP));
      ins.add(new LdcInsnNode(var.getLoc().toString()));
      ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ILLEGAL_ARGUMENT_EXCEPTION, Types.INIT, JAVA_STRING_VOID_SIG));
      ins.add(new InsnNode(Opcodes.ATHROW));
    } else if (desc.getValueSpecifiers().size() == 1) {
      ICafeConstructorSpecifier con = (ICafeConstructorSpecifier) desc.getOnlyValueSpecifier();

      if (con.hasMember(field)) {
        hwm.bump(1);
        ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
        String conJavaType = con.getJavaType();
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, conJavaType));

        switch (var.getKind()) {
          case general:
          case constructor:
          case rawBinary:
          case rawString:
          case rawDecimal:
            ins.add(new VarInsnNode(Opcodes.ALOAD, 1));
            ins.add(new TypeInsnNode(Opcodes.CHECKCAST, var.getJavaType()));
            hwm.bump(1);
            break;
          case rawBool:
          case rawChar:
          case rawInt:
            ins.add(new VarInsnNode(Opcodes.ILOAD, 1));
            hwm.bump(1);
            break;
          case rawLong:
            ins.add(new VarInsnNode(Opcodes.LLOAD, 1));
            hwm.bump(2);
            break;
          case rawFloat:
            ins.add(new VarInsnNode(Opcodes.DLOAD, 1));
            hwm.bump(2);
            break;
          case builtin:
          case userJava:
            errors.reportError("invalid variable: " + field, var.getLoc());
        }

        ins.add(new FieldInsnNode(Opcodes.PUTFIELD, conJavaType, javaName, javaSig));
        ins.add(new InsnNode(Opcodes.RETURN));
      } else {
        ins.add(new TypeInsnNode(Opcodes.NEW, ILLEGAL_ARGUMENT_EXCEPTION));
        ins.add(new InsnNode(Opcodes.DUP));
        ins.add(new LdcInsnNode(var.getLoc().toString()));
        ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, ILLEGAL_ARGUMENT_EXCEPTION, Types.INIT, JAVA_STRING_VOID_SIG));
        ins.add(new InsnNode(Opcodes.ATHROW));
        hwm.probe(3);
      }
    } else
      errors.reportError("no known value specifiers for " + desc.getTypeLabel(), desc.getLoc());

    ins.add(endLabel);

    setter.maxLocals = localsSize;
    setter.maxStack = hwm.getHwm();

    typeNode.methods.add(setter);

    if (!javaSig.equals(Types.IVALUE_SIG))
      genIValueSetter(typeNode, var, setterName, setterSig);
  }

  private static void genIValueSetter(ClassNode conNode, VarInfo var, String setterName, String setterSig) {
    MethodNode setter = new MethodNode(Opcodes.ACC_PUBLIC, setterName, "(" + Types.IVALUE_SIG + ")V", null,
        new String[]{});
    InsnList ins = setter.instructions;
    LabelNode firstLabel = new LabelNode();
    LabelNode endLabel = new LabelNode();
    ins.add(firstLabel);
    setter.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, firstLabel, endLabel,
        Theta.THIS_OFFSET));
    setter.localVariables.add(new LocalVariableNode("X", conNode.signature, null, firstLabel, endLabel,
        Theta.FIRST_OFFSET));
    HWM hwm = new HWM();

    Actions.doLineNumber(var.getLoc(), setter);

    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.FIRST_OFFSET));
    hwm.bump(2);

    AutoBoxing.unboxValue(setter, hwm, var.getType());
    if (Types.varType(var.getType()) == JavaKind.general)
      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, var.getJavaType()));
    ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, conNode.name, setterName, setterSig));

    ins.add(new InsnNode(Opcodes.RETURN));

    ins.add(endLabel);

    setter.maxLocals = 2;
    setter.maxStack = hwm.getHwm();

    conNode.methods.add(setter);
  }

  public static void genNullTest(Location loc, HWM hwm, MethodNode mtd) {
    InsnList ins = mtd.instructions;

    String exceptionType = Type.getInternalName(NullPointerException.class);

    LabelNode nxLabel = new LabelNode();

    Actions.doLineNumber(loc, mtd);

    hwm.probe(3);
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new JumpInsnNode(Opcodes.IFNONNULL, nxLabel));
    ins.add(new TypeInsnNode(Opcodes.NEW, exceptionType));
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, exceptionType, Types.INIT, Types.VOID_SIG));
    ins.add(new InsnNode(Opcodes.ATHROW));

    Utils.jumpTarget(ins, nxLabel);
  }

  public static ISpec constructorCall(Location loc, VarInfo var, IAbstract args, ErrorReport errors,
                                      CafeDictionary dict, CafeDictionary outer, IContinuation cont, CodeContext ccxt) {
    assert var.getKind() == JavaKind.constructor;

    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();

    InsnList ins = mtd.instructions;

    IType varType = Freshen.freshenForUse(var.getType());

    assert TypeUtils.isConstructorType(varType);

    ISpec[] argSpecs = SrcSpec.genericConstructorSpecs(varType, dict, bldCat, ccxt.getRepository(), errors, loc);

    if (isEnum(var))
      ins.add(new FieldInsnNode(Opcodes.GETSTATIC, var.getJavaOwner(), var.getJavaSafeName(), var.getJavaSig()));
    else {
      int mark = hwm.getDepth();
      // pre-amble to access the appropriate constructor
      ins.add(new TypeInsnNode(Opcodes.NEW, var.getJavaType()));
      ins.add(new InsnNode(Opcodes.DUP));
      hwm.bump(2);

      Expressions.compileArgs(args, argSpecs, ccxt);

      ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, var.getJavaType(), Types.INIT, var.getJavaInvokeSig()));

      hwm.reset(mark);
    }
    hwm.bump(Types.stackAmnt(Types.varType(TypeUtils.getConstructorResultType(varType))));
    return cont.cont(argSpecs[argSpecs.length - 1], dict, loc, ccxt);
  }

  public static ISpec conFunCall(Location loc, VarInfo var, IAbstract call, ErrorReport errors, CafeDictionary dict,
                                 CafeDictionary outer, IContinuation cont, CodeContext ccxt) {
    IType varType = Freshen.freshenForUse(var.getType());
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();

    InsnList ins = mtd.instructions;

    assert CafeSyntax.isConstructor(call);

    ISpec[] argSpecs = SrcSpec.genericConstructorSpecs(varType, dict, bldCat, ccxt.getRepository(), errors, loc);
    IList args = CafeSyntax.constructorArgs(call);

    assert args.size() + 1 == argSpecs.length;

    int mark = hwm.bump(1);

    var.loadValue(mtd, hwm, dict);

    ISpec funSpec = SrcSpec.generic(loc, var.getType(), dict, ccxt.getRepository(), errors);
    String methodType = funSpec.getJavaType();

    if (!methodType.equals(var.getJavaType()))
      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, methodType));

    int arity = Expressions.compileArgs(args, argSpecs, ccxt);

    // actually invoke the constructor function
    Actions.doLineNumber(loc, mtd);

    if (arity >= 0) {
      // This is awful, but the JVM made me do it.
      if (methodType.startsWith(Types.CON_PREFIX))
        ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, methodType, Names.ENTER, var.getJavaInvokeSig()));
      else
        ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, methodType, Names.ENTER, var.getJavaInvokeSig()));
    } else {
      ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.IFUNC, Names.ENTERFUNCTION,
          IFuncImplementation.IFUNCTION_INVOKE_SIG));
    }

    hwm.reset(mark);
    ISpec resltSpec = argSpecs[argSpecs.length - 1];
    hwm.bump(Types.stackAmnt(Types.varType(resltSpec.getType())));
    Expressions.checkType(var, resltSpec, mtd, dict, hwm);
    return cont.cont(resltSpec, dict, loc, ccxt);
  }

  public static ISpec buildTuple(Location loc, IAbstract con, IContinuation cont, CodeContext ccxt) {
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();

    CafeDictionary dict = ccxt.getDict();
    InsnList ins = mtd.instructions;

    IList args = CafeSyntax.constructorArgs(con);
    int arity = args.size();

    if (arity == 0) {
      ins.add(new FieldInsnNode(Opcodes.GETSTATIC, Utils.javaInternalClassName(NTuple.class), "$0Enum", Utils
          .javaTypeSig(NTpl.class)));
    } else {
      String tupleJavaType = Utils.javaInternalClassName(NTpl.class);

      int mark = hwm.getDepth();
      hwm.bump(1);

      // preamble to access the appropriate constructor
      ins.add(new TypeInsnNode(Opcodes.NEW, tupleJavaType));
      ins.add(new InsnNode(Opcodes.DUP));
      hwm.bump(2);

      Expressions.genIntConst(ins, hwm, arity);
      ins.add(new TypeInsnNode(Opcodes.ANEWARRAY, Types.IVALUE));

      for (int ix = 0; ix < arity; ix++) {
        IAbstract arg = (IAbstract) args.getCell(ix);

        LabelNode nxLbl = new LabelNode();
        int mark2 = hwm.bump(1);
        ins.add(new InsnNode(Opcodes.DUP));
        Expressions.genIntConst(ins, hwm, ix);

        ISpec actual = Expressions.compileExp(arg, new JumpCont(nxLbl), ccxt);
        Utils.jumpTarget(mtd.instructions, nxLbl);
        Expressions.checkType(actual, SrcSpec.generalSrc, mtd, dict, hwm);
        ins.add(new InsnNode(Opcodes.AASTORE));
        hwm.reset(mark2);
      }

      Actions.doLineNumber(loc, mtd);

      ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, tupleJavaType, Types.INIT, "(" + Types.IVALUE_ARRAY + ")V"));

      hwm.reset(mark);
    }
    hwm.bump(1);
    return cont.cont(SrcSpec.generalSrc, dict, loc, ccxt);
  }

  public static ISpec recordCall(Location loc, VarInfo var, IAbstract call, IContinuation cont, CodeContext ccxt) {
    assert var.getKind() == JavaKind.constructor;

    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();
    CafeDictionary dict = ccxt.getDict();
    InsnList ins = mtd.instructions;

    IType varType = Freshen.freshenForUse(var.getType());

    assert TypeUtils.isConstructorType(varType);
    assert CafeSyntax.isRecord(call);

    IList args = CafeSyntax.recordArgs(call);

    ISpec[] argSpecs = SrcSpec.genericConstructorSpecs(varType, dict, bldCat, ccxt.getRepository(), ccxt.getErrors(), loc);

    if (isEnum(var))
      ins.add(new FieldInsnNode(Opcodes.GETSTATIC, var.getJavaOwner(), var.getJavaSafeName(), var.getJavaSig()));
    else {
      int mark = hwm.getDepth();
      // pre-amble to access the appropriate constructor
      ins.add(new TypeInsnNode(Opcodes.NEW, var.getJavaType()));
      ins.add(new InsnNode(Opcodes.DUP));
      hwm.bump(2);

      Expressions.compileRecordArgs(args, argSpecs, ccxt);

      ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, var.getJavaType(), Types.INIT, var.getJavaInvokeSig()));

      hwm.reset(mark);
    }
    hwm.bump(Types.stackAmnt(Types.varType(TypeUtils.getConstructorResultType(varType))));
    return cont.cont(argSpecs[argSpecs.length - 1], dict, loc, ccxt);
  }

  public static ISpec recordFunCall(Location loc, VarInfo var, IAbstract call, ErrorReport errors,
                                    IContinuation cont, CodeContext ccxt) {
    IType varType = Freshen.freshenForUse(var.getType());
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();
    CafeDictionary dict = ccxt.getDict();

    InsnList ins = mtd.instructions;

    assert CafeSyntax.isRecord(call);

    ISpec[] argSpecs = SrcSpec.genericConstructorSpecs(varType, dict, bldCat, ccxt.getRepository(), errors, loc);
    IList args = CafeSyntax.recordArgs(call);

    assert args.size() + 1 == argSpecs.length;

    int mark = hwm.bump(1);

    var.loadValue(mtd, hwm, dict);

    ISpec funSpec = SrcSpec.generic(loc, var.getType(), dict, ccxt.getRepository(), errors);
    String methodType = funSpec.getJavaType();

    if (!methodType.equals(var.getJavaType()))
      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, methodType));

    int arity = Expressions.compileRecordArgs(args, argSpecs, ccxt);

    // actually invoke the constructor function
    Actions.doLineNumber(loc, mtd);

    if (arity >= 0) {
      // This is awful, but the JVM made me do it.
      if (methodType.startsWith(Types.CON_PREFIX))
        ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, methodType, Names.ENTER, var.getJavaInvokeSig()));
      else
        ins.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, methodType, Names.ENTER, var.getJavaInvokeSig()));
    } else {
      ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.IFUNC, Names.ENTERFUNCTION,
          IFuncImplementation.IFUNCTION_INVOKE_SIG));
    }

    hwm.reset(mark);
    ISpec resltSpec = argSpecs[argSpecs.length - 1];
    hwm.bump(Types.stackAmnt(Types.varType(resltSpec.getType())));
    Expressions.checkType(var, resltSpec, mtd, dict, hwm);
    return cont.cont(resltSpec, dict, loc, ccxt);
  }
}
