package org.star_lang.star.compiler.cafe.compile;

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
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.star_lang.star.code.CafeCode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.ASyntax;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.TypeAttribute;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.cont.IContinuation;
import org.star_lang.star.compiler.cafe.compile.cont.JumpCont;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.value.AnonRecord;

public class Faces
{
  private static final String FaceInitSIG = "(" + Types.IVALUE_ARRAY + ")V";
  private static final String MEMBER_FIELD = "members";

  /**
   * Generate an instance of an anonymous record. This involves sub-classing a standard template and
   * implementing a type-specific interface
   * 
   * This is analogous to the way that lambdas are implemented.
   * 
   * in addition, a special function is created as a factory for the constructor and which can test
   * values to verify that they are instances of the constructor
   */
  private static String genFaceNode(IAbstract con, TypeInterfaceType type, String javaOwner, CafeDictionary dict,
      ErrorReport errors, CodeContext ccxt)
  {
    assert CafeSyntax.isFace(con);

    CodeCatalog synCat = ccxt.getSynthCode();

    String faceLabel = CafeSyntax.faceLabel(con);
    String javaName = Utils.javaIdentifierOf(faceLabel);

    if (!synCat.isPresent(javaName, CafeCode.EXTENSION)) {
      Location loc = con.getLoc();

      ClassNode faceNode = new ClassNode();
      faceNode.version = Opcodes.V1_6;
      faceNode.access = Opcodes.ACC_PUBLIC;
      faceNode.name = javaName;
      faceNode.sourceFile = loc.getSrc();
      faceNode.signature = "L" + javaName + ";";
      faceNode.superName = Types.ANON_RECORD_TYPE;

      IList args = CafeSyntax.faceContents(con);
      Map<String, VarInfo> fields = new HashMap<>();
      String[] fieldNames = new String[args.size()];

      // Sort out the indices of individual fields
      Map<String, Integer> index = new HashMap<>();

      for (int ix = 0; ix < args.size(); ix++) {
        IAbstract arg = (IAbstract) args.getCell(ix);

        if (CafeSyntax.isField(arg)) {
          String fieldId = CafeSyntax.fieldName(arg);

          IType argType = type.getFieldType(fieldId);
          ISpec argSpec = SrcSpec.generalSrc;

          String javaFieldName = Utils.javaIdentifierOf(fieldId);
          fieldNames[ix] = fieldId;

          String javaType = argSpec.getJavaType();
          String javaSig = argSpec.getJavaSig();
          String javaInvokeSig = argSpec.getJavaInvokeSig();
          String javaInvokeName = argSpec.getJavaInvokeName();
          String javaGetterName = Types.getterName(javaFieldName);

          index.put(fieldId, ix);

          fields.put(fieldId, new VarInfo(arg.getLoc(), fieldId, false, VarSource.field, null, Types.varType(argType),
              ix, AccessMode.readOnly, argType, javaFieldName, javaGetterName, javaType, javaSig, javaInvokeSig,
              javaInvokeName, javaOwner));
        } else
          errors.reportError("expecting a field, not " + arg, arg.getLoc());
      }

      List<MethodNode> methods = faceNode.methods;

      // Zero-argument constructor
      methods.add(genInit(faceNode, faceLabel, fieldNames));
      methods.add(genClInit(faceNode, fieldNames));

      // Special methods to access named fields
      genGetters(faceNode, fields, index);

      CompileCafe.genByteCode(javaName, loc, faceNode, synCat, errors);
    }
    return javaName;
  }

  public static ISpec buildRecord(IAbstract exp, ErrorReport errors, CafeDictionary dict, CafeDictionary outer,
      String inFunction, IContinuation cont, Exit exit, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();
    Location loc = exp.getLoc();
    InsnList ins = mtd.instructions;

    IList args = CafeSyntax.faceContents(exp);

    TypeAttribute typeAtt = (TypeAttribute) exp.getAttribute(Names.TYPE);

    assert typeAtt != null;
    IType type = TypeUtils.unwrap(typeAtt.getType());

    String faceLabel = genFaceNode(exp, (TypeInterfaceType) type, dict.getOwnerName(), dict, errors, ccxt);
    Map<String, Integer> index = TypeUtils.getMemberIndex(type);

    int mark = hwm.bump(2);

    ins.add(new TypeInsnNode(Opcodes.NEW, faceLabel));
    ins.add(new InsnNode(Opcodes.DUP));

    int arity = args.size();

    Expressions.genIntConst(ins, hwm, arity);
    ins.add(new TypeInsnNode(Opcodes.ANEWARRAY, Types.IVALUE));

    for (int ix = 0; ix < arity; ix++) {
      ASyntax field = (ASyntax) args.getCell(ix);
      assert CafeSyntax.isField(field);
      String fieldName = CafeSyntax.fieldName(field);

      IAbstract arg = CafeSyntax.fieldValue(field);

      LabelNode nxLbl = new LabelNode();
      int mark2 = hwm.bump(1);
      ins.add(new InsnNode(Opcodes.DUP)); // get the record element array

      Expressions.genIntConst(ins, hwm, index.get(fieldName));

      ISpec actual = Expressions.compileExp(arg, errors, dict, outer, inFunction, new JumpCont(nxLbl), exit, ccxt);
      Utils.jumpTarget(mtd.instructions, nxLbl);
      Expressions.checkType(actual, SrcSpec.generalSrc, mtd, dict, hwm);

      ins.add(new InsnNode(Opcodes.AASTORE));
      hwm.reset(mark2);
    }

    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, faceLabel, Types.INIT, FaceInitSIG));

    hwm.reset(mark);

    hwm.bump(1);
    return cont.cont(SrcSpec.generalSrc, dict, loc, errors, ccxt);
  }

  private static MethodNode genInit(ClassNode conNode, String label, String[] fields)
  {
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC, Types.INIT, FaceInitSIG, null, new String[] {});

    InsnList ins = mtd.instructions;
    List<LocalVariableNode> localVariables = mtd.localVariables;

    LabelNode firstLabel = new LabelNode();
    LabelNode endLabel = new LabelNode();

    localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, firstLabel, endLabel,
        Theta.THIS_OFFSET));
    localVariables
        .add(new LocalVariableNode("els", Types.IVALUE_ARRAY, null, firstLabel, endLabel, Theta.FIRST_OFFSET));

    ins.add(firstLabel);

    HWM hwm = new HWM();
    hwm.probe(1);

    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));

    ins.add(new LdcInsnNode(label));

    ins.add(new FieldInsnNode(Opcodes.GETSTATIC, conNode.name, MEMBER_FIELD, Types.JAVA_STRING_ARRAY_TYPE));

    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.FIRST_OFFSET));

    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, conNode.superName, Types.INIT, "(" + Types.JAVA_STRING_SIG
        + Types.JAVA_STRING_ARRAY_TYPE + Types.IVALUE_ARRAY + ")V"));

    ins.add(new InsnNode(Opcodes.RETURN));
    ins.add(endLabel);

    mtd.maxLocals = 2;
    mtd.maxStack = hwm.getHwm();
    return mtd;
  }

  private static MethodNode genClInit(ClassNode faceNode, String[] members)
  {
    MethodNode mtd = new MethodNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, Types.CLASS_INIT, "()V", null,
        new String[] {});
    HWM hwm = new HWM();
    LabelNode endInit = new LabelNode();

    InsnList ins = mtd.instructions;

    // Build the array of member names to pass in to the anonymous record constructor
    Expressions.genIntConst(ins, hwm, members.length);
    hwm.bump(1);
    ins.add(new TypeInsnNode(Opcodes.ANEWARRAY, Types.JAVA_STRING_TYPE));
    for (int ix = 0; ix < members.length; ix++) {
      int mark = hwm.bump(2);
      ins.add(new InsnNode(Opcodes.DUP));
      Expressions.genIntConst(ins, hwm, ix);
      ins.add(new LdcInsnNode(members[ix]));
      ins.add(new InsnNode(Opcodes.AASTORE));
      hwm.reset(mark);
    }

    ins.add(new FieldInsnNode(Opcodes.PUTSTATIC, faceNode.name, MEMBER_FIELD, Types.JAVA_STRING_ARRAY_TYPE));
    ins.add(endInit);
    ins.add(new InsnNode(Opcodes.RETURN));

    mtd.maxLocals = 0;
    mtd.maxStack = hwm.getHwm();

    FieldNode fieldNode = new FieldNode(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, MEMBER_FIELD,
        Types.JAVA_STRING_ARRAY_TYPE, null, null);
    faceNode.fields.add(fieldNode);

    return mtd;
  }

  private static void genGetters(ClassNode faceNode, Map<String, VarInfo> fields, Map<String, Integer> index)
  {
    for (Entry<String, Integer> entry : index.entrySet())
      genGetter(faceNode, entry.getValue(), fields.get(entry.getKey()));

  }

  private static void genGetter(ClassNode faceNode, int ix, VarInfo var)
  {
    String javaSig = var.getJavaSig();
    String javaName = var.getJavaSafeName();
    String getterName = Types.getterName(javaName);

    MethodNode getter = new MethodNode(Opcodes.ACC_PUBLIC, getterName, "()" + javaSig, null, new String[] {});
    HWM hwm = new HWM();
    InsnList ins = getter.instructions;
    LabelNode firstLabel = new LabelNode();
    LabelNode endLabel = new LabelNode();
    ins.add(firstLabel);
    getter.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, faceNode.signature, null, firstLabel, endLabel,
        Theta.THIS_OFFSET));

    Actions.doLineNumber(var.getLoc(), getter);

    hwm.probe(3);
    ins.add(new VarInsnNode(Opcodes.ALOAD, Theta.THIS_OFFSET));
    ins.add(new FieldInsnNode(Opcodes.GETFIELD, Types.ANON_RECORD_TYPE, AnonRecord.ELS, Types.IVALUE_ARRAY));
    Expressions.genIntConst(ins, hwm, ix);
    ins.add(new InsnNode(Opcodes.AALOAD));
    ins.add(new InsnNode(Opcodes.ARETURN));

    ins.add(endLabel);

    getter.maxLocals = 1;
    getter.maxStack = hwm.getHwm();

    faceNode.methods.add(getter);
  }
}
