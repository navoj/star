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
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.Validate;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.operators.ICafeBuiltin;
import org.star_lang.star.operators.Intrinsics;

/*
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
