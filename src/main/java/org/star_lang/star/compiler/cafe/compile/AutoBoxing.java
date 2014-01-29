package org.star_lang.star.compiler.cafe.compile;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.type.ICafeTypeDescription;
import org.star_lang.star.compiler.canonical.compile.FrameState;

import com.starview.platform.data.IValue;
import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.ITypeContext;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.StandardTypes;
import com.starview.platform.data.value.Factory;

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
public class AutoBoxing
{
  public static final String dataFactory = Type.getInternalName(Factory.class);

  public static void unboxValue(MethodNode mtd, HWM hwm, IType type)
  {
    String isvSig = Type.getDescriptor(IValue.class);
    InsnList ins = mtd.instructions;

    switch (Types.varType(type)) {
    case rawBool:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "boolValue", "(" + isvSig + ")Z"));
      break;
    case rawChar:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "charValue", "(" + isvSig + ")C"));
      break;
    case rawInt:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "intValue", "(" + isvSig + ")I"));
      break;
    case rawLong:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "lngValue", "(" + isvSig + ")J"));
      hwm.bump(1);
      break;
    case rawFloat:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "fltValue", "(" + isvSig + ")D"));
      hwm.bump(1);
      break;
    case rawDecimal:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "decimalValue", "(" + isvSig + ")"
          + Types.JAVA_DECIMAL_SIG));
      hwm.bump(1);
      break;
    case rawBinary:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "binaryValue", "(" + isvSig + ")"
          + Types.JAVA_OBJECT_SIG));
      hwm.bump(1);
      break;
    case rawString:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "stringValue", "(" + isvSig + ")L"
          + Types.JAVA_STRING_TYPE + ";"));
      break;
    case general:
      // FIXME: Why is it commented out? See Constructors.genSetMember.
      // ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Theta.javaType(dict,
      // type, bldCat, loc, errors)));
      break;
    case builtin:
    case constructor:
    case userJava:
      throw new IllegalArgumentException("internal: invalid value to unbox");
    }
  }

  public static FrameState unboxValue(MethodNode mtd, FrameState frame, IType type)
  {
    String isvSig = Type.getDescriptor(IValue.class);
    InsnList ins = mtd.instructions;

    switch (Types.varType(type)) {
    case rawBool:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "boolValue", "(" + isvSig + ")Z"));
      return frame.pushStack(SrcSpec.rawBoolSrc);
    case rawChar:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "charValue", "(" + isvSig + ")C"));
      return frame.pushStack(SrcSpec.rawIntSrc);
    case rawInt:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "intValue", "(" + isvSig + ")I"));
      return frame.pushStack(SrcSpec.rawIntSrc);
    case rawLong:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "lngValue", "(" + isvSig + ")J"));
      return frame.pushStack(SrcSpec.rawLngSrc);
    case rawFloat:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "fltValue", "(" + isvSig + ")D"));
      return frame.pushStack(SrcSpec.rawDblSrc);
    case rawDecimal:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "decimalValue", "(" + isvSig + ")"
          + Types.JAVA_DECIMAL_SIG));
      return frame.pushStack(SrcSpec.rawDecimalSrc);
    case rawBinary:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "binaryValue", "(" + isvSig + ")"
          + Types.JAVA_OBJECT_SIG));
      return frame.pushStack(SrcSpec.generalSrc);
    case rawString:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "stringValue", "(" + isvSig + ")L"
          + Types.JAVA_STRING_TYPE + ";"));
      return frame.pushStack(SrcSpec.rawStringSrc);
    case general:
      return frame.pushStack(SrcSpec.generalSrc);
      // FIXME: Why is it commented out? See Constructors.genSetMember.
      // ins.add(new TypeInsnNode(Opcodes.CHECKCAST, Theta.javaType(dict,
      // type, bldCat, loc, errors)));
    case builtin:
    case constructor:
    case userJava:
    default:
      throw new IllegalArgumentException("internal: invalid value to unbox");
    }
  }

  public static ISpec boxValue(IType type, InsnList ins, ITypeContext cxt)
  {

    Location loc = Location.nullLoc;

    switch (Types.varType(type)) {
    case rawBool:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "newBool", Utils.javaInvokeSig(Factory.class,
          "newBool")));
      return new SrcSpec(StandardTypes.booleanType, loc, Types.BOOLEAN_TYPE, Types.BOOLEAN_SIG, null, null);
    case rawChar:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "newChar", Utils.javaInvokeSig(Factory.class,
          "newChar")));
      return new SrcSpec(StandardTypes.charType, loc, Types.CHAR_TYPE, Types.CHAR_SIG, null, null);
    case rawInt:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "newInt", Utils.javaInvokeSig(Factory.class,
          "newInt")));
      return new SrcSpec(StandardTypes.integerType, loc, Types.INTEGER_TYPE, Types.INTEGER_SIG, null, null);
    case rawLong:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "newLng", Utils.javaInvokeSig(Factory.class,
          "newLng")));
      return new SrcSpec(StandardTypes.longType, loc, Types.LONG_TYPE, Types.LONG_SIG, null, null);
    case rawFloat:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "newFlt", Utils.javaInvokeSig(Factory.class,
          "newFlt")));
      return new SrcSpec(StandardTypes.floatType, loc, Types.FLOAT_TYPE, Types.FLOAT_SIG, null, null);
    case rawDecimal:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "newDecimal", Utils.javaInvokeSig(Factory.class,
          "newDecimal")));
      return new SrcSpec(StandardTypes.decimalType, loc, Types.DECIMAL_TYPE, Types.DECIMAL_SIG, null, null);
    case rawString:
      ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "newString", Utils.javaInvokeSig(Factory.class,
          "newString")));
      return new SrcSpec(StandardTypes.stringType, loc, Types.STRING_TYPE, Types.STRING_SIG, null, null);
    case general:
    default: {
      ICafeTypeDescription desc = (ICafeTypeDescription) cxt.getTypeDescription(type.typeLabel());
      if (desc != null)
        return new SrcSpec(type, loc, desc.getJavaName(), desc.getJavaSig(), null, null);
      else
        return new SrcSpec(type, loc, Types.IVALUE, Types.IVALUE_SIG, null, null);
    }
    }
  }
}
