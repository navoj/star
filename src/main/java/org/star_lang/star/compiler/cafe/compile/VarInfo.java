package org.star_lang.star.compiler.cafe.compile;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.star_lang.star.compiler.cafe.Names;
import org.star_lang.star.compiler.canonical.compile.CompileContext;
import org.star_lang.star.compiler.canonical.compile.ExpressionCompile;
import org.star_lang.star.compiler.canonical.compile.FrameState;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.Validate;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.operators.ICafeBuiltin;
import org.star_lang.star.operators.Intrinsics;

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

@SuppressWarnings("serial")
public class VarInfo implements ISpec, Validate
{
  private final Location loc;
  private final String name;
  private final VarSource where;
  private final VarInfo record;
  private final JavaKind kind;
  private boolean isInited;
  private final int offset;
  private final Object literal;
  private final AccessMode access;
  private final IType type;
  private final String javaOwner;
  private final String javaSafeName;
  private final String javaType;
  private final String javaSig;
  private final String javaInvokeSig;
  private final String javaInvokeName;
  private final String javaGetterName;

  public VarInfo(Location loc, String name, boolean isInited, VarSource where, VarInfo record, JavaKind kind,
      int offset, AccessMode access, IType type, String javaSafeName, String javaGetterName, String javaType,
      String javaSig, String javaInvokeSig, String javaInvokeName, String javaOwner)
  {
    this.loc = loc;
    this.name = name;
    this.isInited = isInited;
    this.where = where;
    this.record = record;
    this.literal = null;
    this.javaOwner = javaOwner;
    this.kind = kind;
    this.offset = offset;
    this.access = access;
    this.type = type;
    this.javaType = javaType;
    this.javaSig = javaSig;
    this.javaInvokeSig = javaInvokeSig;
    this.javaInvokeName = javaInvokeName;
    this.javaSafeName = javaSafeName;
    this.javaGetterName = javaGetterName;
  }

  @Override
  public Location getLoc()
  {
    return loc;
  }

  public String getName()
  {
    return name;
  }

  public boolean isInited()
  {
    return isInited;
  }

  public void setInited(boolean isInited)
  {
    this.isInited = isInited;
  }

  public String getJavaSafeName()
  {
    return javaSafeName;
  }

  public String getJavaGetterName()
  {
    return javaGetterName;
  }

  public VarSource getWhere()
  {
    return where;
  }

  public boolean isStatic()
  {
    return getWhere() == VarSource.staticMethod;
  }

  public JavaKind getKind()
  {
    return kind;
  }

  public VarInfo getRecord()
  {
    return record;
  }

  public int getOffset()
  {
    return offset;
  }

  public Object getLiteral()
  {
    return literal;
  }

  public String getJavaOwner()
  {
    return javaOwner;
  }

  public AccessMode getAccess()
  {
    return access;
  }

  @Override
  public IType getType()
  {
    return type;
  }

  @Override
  public String getJavaType()
  {
    return javaType;
  }

  @Override
  public String getJavaSig()
  {
    return javaSig;
  }

  @Override
  public Object getFrameCode()
  {
    return SrcSpec.frameRep(javaSig);
  }

  @Override
  public String getJavaInvokeSig()
  {
    return javaInvokeSig;
  }

  @Override
  public String getJavaInvokeName()
  {
    return javaInvokeName;
  }

  @Override
  public int slotSize()
  {
    return Types.stackAmnt(kind);
  }

  @Override
  public boolean validate()
  {
    if (javaSig != null && javaType != null) {
      if (javaInvokeName != null || javaInvokeSig != null)
        return javaInvokeName != null && javaInvokeSig != null;
    }
    return false;
  }

  public void loadValue(MethodNode mtd, HWM hwm, CafeDictionary dict)
  {
    InsnList ins = mtd.instructions;
    switch (getKind()) {
    case builtin: {
      ICafeBuiltin builtin = Intrinsics.getBuiltin(getName());
      String classSig = Type.getInternalName(builtin.getClass());

      ins.add(new FieldInsnNode(Opcodes.GETSTATIC, dict.getOwnerName(), builtin.getJavaName(), "L" + classSig + ";"));
      hwm.bump(1);
      break;
    }
    case constructor: {
      ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
      hwm.bump(1);
      break;
    }
    case rawBool:
      switch (getWhere()) {
      case localVar:
        ins.add(new VarInsnNode(Opcodes.ILOAD, getOffset()));
        break;
      case freeVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), getJavaSafeName(), getJavaSig()));
        break;
      case field:
        record.loadValue(mtd, hwm, dict);
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, record.getJavaType(), getJavaSafeName(), getJavaSig()));
        break;
      case arrayArg:
        record.loadValue(mtd, hwm, dict);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE_ARRAY));
        Expressions.genIntConst(ins, hwm, getOffset());
        ins.add(new InsnNode(Opcodes.AALOAD));
        AutoBoxing.unboxValue(mtd, hwm, getType());
        break;
      case literal:
        if ((Boolean) getLiteral())
          ins.add(new InsnNode(Opcodes.ICONST_1));
        else
          ins.add(new InsnNode(Opcodes.ICONST_0));
        break;
      case staticField:
        ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
        break;
      default:
        assert false : "invalid location of boolean variable";
      }
      hwm.bump(1);
      break;
    case rawChar:
      switch (getWhere()) {
      case localVar:
        ins.add(new VarInsnNode(Opcodes.ILOAD, getOffset()));
        break;
      case freeVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), getJavaSafeName(), getJavaSig()));
        break;
      case field:
        record.loadValue(mtd, hwm, dict);
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, record.getJavaType(), getJavaSafeName(), getJavaSig()));
        break;
      case arrayArg:
        record.loadValue(mtd, hwm, dict);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE_ARRAY));
        Expressions.genIntConst(ins, hwm, getOffset());
        ins.add(new InsnNode(Opcodes.AALOAD));
        AutoBoxing.unboxValue(mtd, hwm, getType());
        break;
      case literal:
        ins.add(new LdcInsnNode(getLiteral()));
        break;
      case staticField:
        ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
        break;
      default:
        assert false : "invalid location of variable";
      }
      hwm.bump(1);
      break;
    case rawInt:
      switch (getWhere()) {
      case localVar:
        ins.add(new VarInsnNode(Opcodes.ILOAD, getOffset()));
        break;
      case freeVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), getJavaSafeName(), getJavaSig()));
        break;
      case field:
        record.loadValue(mtd, hwm, dict);
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, record.getJavaType(), getJavaSafeName(), getJavaSig()));
        break;
      case arrayArg:
        record.loadValue(mtd, hwm, dict);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE_ARRAY));
        Expressions.genIntConst(ins, hwm, getOffset());
        ins.add(new InsnNode(Opcodes.AALOAD));
        AutoBoxing.unboxValue(mtd, hwm, getType());
        break;
      case literal:
        ins.add(new LdcInsnNode(getLiteral()));
        break;
      case staticField:
        ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
        break;
      default:
        assert false : "invalid location of variable";
      }
      hwm.bump(1);
      break;
    case rawLong:
      switch (getWhere()) {
      case localVar:
        ins.add(new VarInsnNode(Opcodes.LLOAD, getOffset()));
        break;
      case freeVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), getJavaSafeName(), getJavaSig()));
        break;
      case field:
        record.loadValue(mtd, hwm, dict);
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, record.getJavaType(), getJavaSafeName(), getJavaSig()));
        break;
      case arrayArg:
        record.loadValue(mtd, hwm, dict);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE_ARRAY));
        Expressions.genIntConst(ins, hwm, getOffset());
        ins.add(new InsnNode(Opcodes.AALOAD));
        AutoBoxing.unboxValue(mtd, hwm, getType());
        break;
      case literal:
        ins.add(new LdcInsnNode(getLiteral()));
        break;
      case staticField:
        ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
        break;
      default:
        assert false : "invalid location of variable";
      }
      hwm.bump(2);
      break;
    case rawFloat:
      switch (getWhere()) {
      case localVar:
        ins.add(new VarInsnNode(Opcodes.DLOAD, getOffset()));
        break;
      case freeVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), getJavaSafeName(), getJavaSig()));
        break;
      case field:
        record.loadValue(mtd, hwm, dict);
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, record.getJavaType(), getJavaSafeName(), getJavaSig()));
        break;
      case arrayArg:
        record.loadValue(mtd, hwm, dict);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE_ARRAY));
        Expressions.genIntConst(ins, hwm, getOffset());
        ins.add(new InsnNode(Opcodes.AALOAD));
        AutoBoxing.unboxValue(mtd, hwm, getType());
        break;
      case literal:
        ins.add(new LdcInsnNode(getLiteral()));
        break;
      case staticField:
        ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
        break;
      default:
        assert false : "invalid location of variable";
      }
      hwm.bump(2);
      break;
    case rawBinary:
    case rawString:
    case rawDecimal:
    case general:
      switch (getWhere()) {
      case localVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, getOffset()));
        break;
      case freeVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), getJavaSafeName(), getJavaSig()));
        break;
      case field:
        record.loadValue(mtd, hwm, dict);
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, record.getJavaType(), getJavaSafeName(), getJavaSig()));
        break;
      case arrayArg:
        record.loadValue(mtd, hwm, dict);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE_ARRAY));
        Expressions.genIntConst(ins, hwm, getOffset());
        ins.add(new InsnNode(Opcodes.AALOAD));
        break;
      case literal:
        ins.add(new LdcInsnNode(getLiteral()));
        break;
      case staticField:
        ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
        break;
      default:
        assert false : "invalid location of variable";
      }
      hwm.bump(1);
      break;
    default:
      assert false : "invalid kind of variable";
    }
  }

  public FrameState loadValue(FrameState frame, CompileContext cxt)
  {
    MethodNode mtd = cxt.getMtd();
    InsnList ins = mtd.instructions;
    CafeDictionary dict = cxt.getDict();

    switch (getKind()) {
    case builtin: {
      ICafeBuiltin builtin = Intrinsics.getBuiltin(getName());
      String classSig = Type.getInternalName(builtin.getClass());

      ins.add(new FieldInsnNode(Opcodes.GETSTATIC, dict.getOwnerName(), builtin.getJavaName(), "L" + classSig + ";"));
      return frame.pushStack(this);
    }
    case constructor: {
      ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
      return frame.pushStack(this);
    }
    case rawBool:
      switch (getWhere()) {
      case localVar:
        ins.add(new VarInsnNode(Opcodes.ILOAD, getOffset()));
        return frame.pushStack(SrcSpec.rawBoolSrc);
      case freeVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawBoolSrc);
      case field:
        record.loadValue(frame, cxt);
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, record.getJavaType(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawBoolSrc);
      case arrayArg:
        FrameState rState = record.loadValue(frame, cxt);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE_ARRAY));
        ExpressionCompile.genIntConst(cxt, rState, getOffset());
        ins.add(new InsnNode(Opcodes.AALOAD));
        return AutoBoxing.unboxValue(mtd, frame, getType());
      case literal:
        if ((Boolean) getLiteral())
          ins.add(new InsnNode(Opcodes.ICONST_1));
        else
          ins.add(new InsnNode(Opcodes.ICONST_0));
        return frame.pushStack(SrcSpec.rawBoolSrc);
      case staticField:
        ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawBoolSrc);
      default:
        throw new IllegalStateException("invalid location of boolean variable");
      }

    case rawChar:
      switch (getWhere()) {
      case localVar:
        ins.add(new VarInsnNode(Opcodes.ILOAD, getOffset()));
        return frame.pushStack(SrcSpec.rawCharSrc);
      case freeVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawCharSrc);
      case field:
        record.loadValue(frame, cxt);
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, record.getJavaType(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawCharSrc);
      case arrayArg:
        record.loadValue(frame, cxt);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE_ARRAY));
        ExpressionCompile.genIntConst(cxt, frame, getOffset());
        ins.add(new InsnNode(Opcodes.AALOAD));
        return AutoBoxing.unboxValue(mtd, frame, getType());
      case literal:
        ins.add(new LdcInsnNode(getLiteral()));
        return frame.pushStack(SrcSpec.rawCharSrc);
      case staticField:
        ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawCharSrc);
      default:
        throw new IllegalStateException("invalid location of char variable");
      }

    case rawInt:
      switch (getWhere()) {
      case localVar:
        ins.add(new VarInsnNode(Opcodes.ILOAD, getOffset()));
        return frame.pushStack(SrcSpec.rawIntSrc);
      case freeVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawIntSrc);
      case field:
        record.loadValue(frame, cxt);
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, record.getJavaType(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawIntSrc);
      case arrayArg:
        record.loadValue(frame, cxt);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE_ARRAY));
        ExpressionCompile.genIntConst(cxt, frame, getOffset());
        ins.add(new InsnNode(Opcodes.AALOAD));
        return AutoBoxing.unboxValue(mtd, frame, getType());
      case literal:
        ins.add(new LdcInsnNode(getLiteral()));
        return frame.pushStack(SrcSpec.rawIntSrc);
      case staticField:
        ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawIntSrc);
      default:
        throw new IllegalStateException("invalid location of int variable");
      }

    case rawLong:
      switch (getWhere()) {
      case localVar:
        ins.add(new VarInsnNode(Opcodes.LLOAD, getOffset()));
        return frame.pushStack(SrcSpec.rawLngSrc);
      case freeVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawLngSrc);
      case field:
        record.loadValue(frame, cxt);
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, record.getJavaType(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawLngSrc);
      case arrayArg:
        record.loadValue(frame, cxt);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE_ARRAY));
        ExpressionCompile.genIntConst(cxt, frame, getOffset());
        ins.add(new InsnNode(Opcodes.AALOAD));
        return AutoBoxing.unboxValue(mtd, frame, getType());
      case literal:
        ins.add(new LdcInsnNode(getLiteral()));
        return frame.pushStack(SrcSpec.rawLngSrc);
      case staticField:
        ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawLngSrc);
      default:
        throw new IllegalStateException("invalid location of long variable");
      }

    case rawFloat:
      switch (getWhere()) {
      case localVar:
        ins.add(new VarInsnNode(Opcodes.DLOAD, getOffset()));
        return frame.pushStack(SrcSpec.rawDblSrc);
      case freeVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawDblSrc);
      case field:
        record.loadValue(frame, cxt);
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, record.getJavaType(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawDblSrc);
      case arrayArg:
        record.loadValue(frame, cxt);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE_ARRAY));
        ExpressionCompile.genIntConst(cxt, frame, getOffset());
        ins.add(new InsnNode(Opcodes.AALOAD));
        return AutoBoxing.unboxValue(mtd, frame, getType());
      case literal:
        ins.add(new LdcInsnNode(getLiteral()));
        return frame.pushStack(SrcSpec.rawDblSrc);
      case staticField:
        ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(SrcSpec.rawDblSrc);
      default:
        throw new IllegalStateException("invalid location of float variable");
      }

    case rawBinary:
    case rawString:
    case rawDecimal:
    case general:
      switch (getWhere()) {
      case localVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, getOffset()));
        return frame.pushStack(this);
      case freeVar:
        ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, dict.getOwnerName(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(this);
      case field:
        record.loadValue(frame, cxt);
        ins.add(new FieldInsnNode(Opcodes.GETFIELD, record.getJavaType(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(this);
      case arrayArg:
        record.loadValue(frame, cxt);
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE_ARRAY));
        ExpressionCompile.genIntConst(cxt, frame, getOffset());
        ins.add(new InsnNode(Opcodes.AALOAD));
        return frame.pushStack(this);
      case literal:
        ins.add(new LdcInsnNode(getLiteral()));
        return frame.pushStack(this);
      case staticField:
        ins.add(new FieldInsnNode(Opcodes.GETSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
        return frame.pushStack(this);
      default:
        throw new IllegalStateException("invalid location of variable");
      }
    default:
      throw new IllegalStateException("invalid kind of variable");
    }
  }

  public void storeValue(MethodNode mtd, HWM hwm, CafeDictionary dict)
  {
    InsnList ins = mtd.instructions;

    switch (getWhere()) {
    case freeVar:
      ins.add(new VarInsnNode(Opcodes.ALOAD, dict.find(Names.PRIVATE_THIS).getOffset()));
      ins.add(new InsnNode(Opcodes.SWAP));
      ins.add(new FieldInsnNode(Opcodes.PUTFIELD, dict.getOwnerName(), getJavaSafeName(), getJavaSig()));
      break;
    case field: {
      record.loadValue(mtd, hwm, dict);
      ins.add(new InsnNode(Opcodes.SWAP));
      ins.add(new FieldInsnNode(Opcodes.PUTFIELD, record.getJavaType(), getJavaSafeName(), getJavaSig()));
      break;
    }
    case arrayArg:
      record.loadValue(mtd, hwm, dict);
      ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Types.IVALUE_ARRAY));
      ins.add(new InsnNode(Opcodes.SWAP));
      Expressions.genIntConst(ins, hwm, getOffset());
      ins.add(new InsnNode(Opcodes.AASTORE));
      break;
    case localVar: {
      int offset = getOffset();

      switch (getKind()) {
      case rawBool:
      case rawChar:
        ins.add(new VarInsnNode(Opcodes.ISTORE, offset));
        break;
      case rawInt:
        ins.add(new VarInsnNode(Opcodes.ISTORE, offset));
        break;
      case rawLong:
        ins.add(new VarInsnNode(Opcodes.LSTORE, offset));
        break;
      case rawFloat:
        ins.add(new VarInsnNode(Opcodes.DSTORE, offset));
        break;
      case rawBinary:
      case rawString:
      case rawDecimal:
      case general:
        ins.add(new VarInsnNode(Opcodes.ASTORE, offset));
        break;
      default:
        assert false : "invalid kind of variable";
      }
      break;
    }
    case staticField:
      ins.add(new FieldInsnNode(Opcodes.PUTSTATIC, getJavaOwner(), getJavaSafeName(), getJavaSig()));
      break;
    default:
      assert false : "(internal) invalid target for store of " + this;
    }
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendId(name);
    disp.append(":");
    DisplayType.display(disp, type);
    disp.append("@");

    disp.append(getWhere().toString());
    switch (getWhere()) {
    case freeVar:
    case localVar:
      disp.append("[");
      disp.append(offset);
      disp.append("]");
      break;
    case field:
      if (record != null) {
        disp.append("ƒ[");
        record.prettyPrint(disp);
        disp.append("]");
      } else
        disp.append("ƒ");
      break;
    case arrayArg:
      assert record != null;
      record.prettyPrint(disp);
      disp.append("[");
      disp.append(getOffset());
      disp.append("]");
      break;
    case literal:
    case staticField:
      disp.append(" ");
      break;
    case staticMethod:
      break;
    }
    disp.append(isInited ? "" : "(U)");
    disp.append(javaType);
  }

  public void display(PrettyPrintDisplay disp)
  {
    disp.appendId(name);
    int mark = disp.markIndent(2);
    disp.append("{\n");
    disp.appendWord("type is ");
    DisplayType.display(disp, type);
    disp.append(";\n");
    disp.append("javaType is ");
    disp.appendQuoted(javaType);
    disp.append(";\n");
    if (javaInvokeName != null || javaInvokeSig != null) {
      assert javaInvokeName != null && javaInvokeSig != null;
      disp.append("entry is ");
      disp.appendQuoted(javaInvokeName);
      disp.append(";\n");
      disp.append("javaSig is ");
      disp.appendQuoted(javaSig);
      disp.append(";\n");
      disp.append("javaInvoke is ");
      disp.appendQuoted(javaInvokeName);
    }
    disp.append("javaName is ");
    disp.appendQuoted(javaSafeName);
    disp.append(";\n");
    disp.popIndent(mark);
    disp.append(";\n");
    disp.append("}");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public int hashCode()
  {
    int hash = name.hashCode();

    hash = hash * 37 + where.hashCode();
    hash = hash * 37 + offset;
    hash = hash * 37 + kind.hashCode();
    hash = hash * 37 + type.hashCode();

    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof VarInfo) {
      VarInfo other = (VarInfo) obj;

      return other.name.equals(name) && other.getWhere() == getWhere() && other.getOffset() == getOffset()
          && other.getKind() == getKind() && other.getType().equals(getType());
    } else
      return false;
  }
}
