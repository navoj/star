package org.star_lang.star.compiler.cafe.compile;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.cafe.CafeSyntax;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.cafe.compile.cont.JumpCont;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.ExistentialType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeVisitor;
import org.star_lang.star.data.type.Kind;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeDescription;
import org.star_lang.star.data.type.TypeExp;
import org.star_lang.star.data.type.TypeInterface;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.data.type.UniversalType;

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

public class TypeGen implements ITypeVisitor<Void>
{
  private final CafeDictionary dict;
  private final CafeDictionary outer;
  private final HWM hwm;
  private final LiveMap locals;
  private final PathToType resolveTypeVar;
  private final Location loc;
  private final ErrorReport errors;
  private final CodeContext ccxt;
  private final LabelNode end;
  private final Map<String, Integer> vars = new HashMap<>();

  public static final String TYPE_UTILS = Utils.javaInternalClassName(TypeUtils.class);

  TypeGen(LiveMap locals, CafeDictionary dict, CafeDictionary outer, ErrorReport errors, Location loc,
      CodeContext ccxt, PathToType resolver, LabelNode end)
  {
    this.dict = dict;
    this.outer = outer;
    this.hwm = ccxt.getMtdHwm();
    this.loc = loc;
    this.errors = errors;
    this.ccxt = ccxt;
    this.resolveTypeVar = resolver;
    this.locals = locals;
    this.end = end;
  }

  @Override
  public void visitSimpleType(Type t, Void cxt)
  {
    String javaTypeName = Utils.javaInternalClassName(Type.class);

    InsnList ins = ccxt.getIns();
    hwm.bump(1);
    ins.add(new TypeInsnNode(Opcodes.NEW, javaTypeName));
    int mark = hwm.bump(2);
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new LdcInsnNode(t.typeLabel()));

    Kind kind = t.kind();

    switch (kind.mode()) {
    case typefunction:
      Expressions.genIntConst(ins, hwm, kind.arity());
      ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, javaTypeName, Types.INIT, "(" + Types.JAVA_STRING_SIG + "I)V"));
      break;
    case type:
    case unknown:
      ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, javaTypeName, Types.INIT, "(" + Types.JAVA_STRING_SIG + ")V"));
      break;
    default:
      break;

    }

    hwm.reset(mark);
  }

  @Override
  public void visitTypeExp(TypeExp t, Void cxt)
  {
    InsnList ins = ccxt.getIns();
    hwm.bump(1);
    int mark = hwm.bump(4);

    t.getTypeCon().accept(this, cxt); // type constructor

    IType typeArgs[] = t.getTypeArgs();
    Expressions.genIntConst(ins, hwm, typeArgs.length);
    ins.add(new TypeInsnNode(Opcodes.ANEWARRAY, Types.ITYPE));

    for (int ix = 0; ix < typeArgs.length; ix++) {
      int m2 = hwm.bump(1);
      ins.add(new InsnNode(Opcodes.DUP));
      Expressions.genIntConst(ins, hwm, ix);
      typeArgs[ix].accept(this, cxt);
      ins.add(new InsnNode(Opcodes.AASTORE));
      hwm.reset(m2);
    }

    ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, TYPE_UTILS, "typeExp", "(" + Types.ITYPE_SIG
        + ("[" + Types.ITYPE_SIG) + ")" + Types.ITYPE_SIG));

    hwm.reset(mark);
  }

  @Override
  public void visitTypeInterface(TypeInterfaceType t, Void cxt)
  {
    Map<String, IType> members = t.getAllFields();
    Map<String, IType> types = t.getAllTypes();

    String javaTypeName = Utils.javaInternalClassName(TypeInterfaceType.class);

    InsnList ins = ccxt.getIns();
    hwm.bump(1);
    ins.add(new TypeInsnNode(Opcodes.NEW, javaTypeName));
    int mark = hwm.bump(2);
    ins.add(new InsnNode(Opcodes.DUP));

    hwm.bump(1);
    ins.add(new TypeInsnNode(Opcodes.NEW, Types.TYPEMAP));
    hwm.probe(1);
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Types.TYPEMAP, Types.INIT, "()V"));

    for (Entry<String, IType> entry : types.entrySet()) {
      int m2 = hwm.bump(2);
      ins.add(new InsnNode(Opcodes.DUP));
      ins.add(new LdcInsnNode(t.typeLabel()));
      entry.getValue().accept(this, cxt);
      ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.MAP, "put", Types.MAP_PUTSIG));
      ins.add(new InsnNode(Opcodes.POP)); // throw away the return value
      hwm.reset(m2);
    }

    hwm.bump(1);
    ins.add(new TypeInsnNode(Opcodes.NEW, Types.TYPEMAP));
    hwm.probe(1);
    ins.add(new InsnNode(Opcodes.DUP));
    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Types.TYPEMAP, Types.INIT, "()V"));

    for (Entry<String, IType> entry : members.entrySet()) {
      int m2 = hwm.bump(2);
      ins.add(new InsnNode(Opcodes.DUP));
      ins.add(new LdcInsnNode(t.typeLabel()));
      entry.getValue().accept(this, cxt);
      ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.MAP, "put", Types.MAP_PUTSIG));
      ins.add(new InsnNode(Opcodes.POP));
      hwm.reset(m2);
    }

    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, javaTypeName, Types.INIT, "(" + Types.SORTEDMAP_SIG
        + Types.SORTEDMAP_SIG + ")V"));
    hwm.reset(mark);
  }

  @Override
  public void visitTypeVar(TypeVar v, Void cxt)
  {
    InsnList ins = ccxt.getIns();

    Integer offset = vars.get(v.typeLabel());

    if (offset != null) {
      ins.add(new VarInsnNode(Opcodes.ALOAD, offset));
    } else {
      IAbstract pth = resolveTypeVar.pathToType(v, loc);
      if (pth != null) {
        int mark = hwm.getDepth();
        LabelNode nx = new LabelNode();
        Expressions.compileExp(pth, errors, dict, outer, null, new JumpCont(nx), null, ccxt);
        Utils.jumpTarget(ins, nx);
        ins.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, Types.IVALUE, "getType", "()" + Types.ITYPE_SIG));
        hwm.reset(mark);
        hwm.bump(1);
      } else {
        hwm.bump(1);
        ins.add(new TypeInsnNode(Opcodes.NEW, Types.TYPEVAR));
        int mark = hwm.probe(2);
        ins.add(new InsnNode(Opcodes.DUP));
        ins.add(new LdcInsnNode(v.getVarName()));
        ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, Types.TYPEVAR, Types.INIT, "(" + Types.JAVA_STRING_SIG + ")V"));
        offset = locals.reserve(1);
        vars.put(v.typeLabel(), offset);

        LabelNode start = new LabelNode();
        ins.add(new InsnNode(Opcodes.DUP));
        ins.add(start);
        ccxt.getMtd().localVariables.add(new LocalVariableNode(v.getVarName(), Types.ITYPE_SIG, null, start, end,
            offset));
        ins.add(new VarInsnNode(Opcodes.ASTORE, offset));
        hwm.reset(mark);
      }
    }
  }

  @Override
  public void visitExistentialType(ExistentialType t, Void cxt)
  {
    String javaTypeName = Utils.javaInternalClassName(ExistentialType.class);

    InsnList ins = ccxt.getIns();

    TypeVar tVar = t.getBoundVar();

    hwm.bump(1);
    int mark = hwm.bump(5);

    ins.add(new TypeInsnNode(Opcodes.NEW, javaTypeName));
    ins.add(new InsnNode(Opcodes.DUP));

    tVar.accept(this, cxt);

    t.getBoundType().accept(this, cxt);

    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, javaTypeName, Types.INIT, "(" + Types.TYPEVAR_SIG
        + Types.ITYPE_SIG + ")V"));
    hwm.reset(mark);
  }

  @Override
  public void visitUniversalType(UniversalType t, Void cxt)
  {
    String javaTypeName = Utils.javaInternalClassName(UniversalType.class);

    InsnList ins = ccxt.getIns();

    TypeVar tVar = t.getBoundVar();

    hwm.bump(1);
    int mark = hwm.bump(5);

    ins.add(new TypeInsnNode(Opcodes.NEW, javaTypeName));
    ins.add(new InsnNode(Opcodes.DUP));

    tVar.accept(this, cxt);

    t.getBoundType().accept(this, cxt);

    ins.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, javaTypeName, Types.INIT, "(" + Types.TYPEVAR_SIG
        + Types.ITYPE_SIG + ")V"));
    hwm.reset(mark);
  }

  public interface PathToType
  {
    public IAbstract pathToType(TypeVar tv, Location loc);
  }

  public static MethodNode genType(Location loc, IType type, ClassNode klass, CafeDictionary dict, ErrorReport errors,
      CodeRepository repository, CodeCatalog bldCat)
  {
    MethodNode getType = new MethodNode(Opcodes.ACC_PUBLIC, "getType", "()" + Types.ITYPE_SIG, null, new String[] {});
    LabelNode fLabel = new LabelNode();
    LabelNode eLabel = new LabelNode();

    CafeDictionary funDict = dict.funDict(klass);
    getType.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, klass.signature, null, fLabel, eLabel,
        Theta.THIS_OFFSET));
    funDict.declareLocal(loc, Names.PRIVATE_THIS, true, type, klass.name, null, null, null, AccessMode.readOnly);
    HWM hwm = new HWM();

    LiveMap locals = new LiveMap();

    locals.reserve(1);

    CodeContext ccxt = new CodeContext(repository, klass, getType, hwm, null, null, funDict.getLocalAvail(), bldCat);

    PathToType resolver = new PathToType() {

      @Override
      public IAbstract pathToType(TypeVar tv, Location loc)
      {
        return null;
      }
    };
    TypeGen typeGen = new TypeGen(locals, funDict, dict, errors, loc, ccxt, resolver, eLabel);
    type.accept(typeGen, null);
    InsnList ins = getType.instructions;
    ins.add(fLabel);

    ins.add(new InsnNode(Opcodes.ARETURN));
    ins.add(eLabel);
    getType.maxLocals = locals.getHwm();
    getType.maxStack = hwm.getHwm();
    return getType;
  }

  public static MethodNode genType(final TypeDescription desc, final String lbl, ClassNode conNode,
      CafeDictionary dict, ErrorReport errors, CodeRepository repository, CodeCatalog bldCat)
  {
    MethodNode getType = new MethodNode(Opcodes.ACC_PUBLIC, "getType", "()" + Types.ITYPE_SIG, null, new String[] {});
    LabelNode fLabel = new LabelNode();
    LabelNode eLabel = new LabelNode();

    CafeDictionary funDict = dict.funDict(/* dict.getPath(), */conNode);
    getType.localVariables.add(new LocalVariableNode(Names.PRIVATE_THIS, conNode.signature, null, fLabel, eLabel,
        Theta.THIS_OFFSET));
    funDict.declareLocal(desc.getLoc(), Names.PRIVATE_THIS, true, desc.getType(), conNode.name, null, null, null,
        AccessMode.readOnly);
    HWM hwm = new HWM();
    LiveMap locals = new LiveMap();
    locals.reserve(1);
    CodeContext ccxt = new CodeContext(repository, conNode, getType, hwm, null, null, funDict.getLocalAvail(), bldCat);

    PathToType resolver = new PathToType() {

      @Override
      public IAbstract pathToType(TypeVar tv, Location loc)
      {
        ConstructorSpecifier con = (ConstructorSpecifier) desc.getValueSpecifier(lbl);
        IType conType = TypeUtils.unwrap(con.getConType());
        IType argsType = TypeUtils.unwrap(TypeUtils.getConstructorArgType(conType));
        if (TypeUtils.isTupleType(argsType)) {
          IType argTypes[] = TypeUtils.tupleTypes(argsType);
          for (int ix = 0; ix < argTypes.length; ix++)
            if (argTypes[ix].equals(tv))
              return CafeSyntax.dot(loc, Abstract.name(loc, Names.PRIVATE_THIS), ix);
        } else if (TypeUtils.isTypeInterface(argsType)) {
          TypeInterface face = (TypeInterface) argsType;
          for (Entry<String, IType> entry : face.getAllFields().entrySet()) {
            if (entry.getValue().equals(tv))
              return CafeSyntax.dot(loc, Abstract.name(loc, Names.PRIVATE_THIS), entry.getKey());
          }
        }
        return null;
      }
    };
    TypeGen typeGen = new TypeGen(locals, funDict, dict, errors, desc.getLoc(), ccxt, resolver, eLabel);
    TypeUtils.unwrap(desc.getType()).accept(typeGen, null);
    InsnList ins = getType.instructions;
    ins.add(fLabel);

    ins.add(new InsnNode(Opcodes.ARETURN));
    ins.add(eLabel);
    getType.maxLocals = locals.getHwm();
    getType.maxStack = hwm.getHwm();
    return getType;
  }
}