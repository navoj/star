package org.star_lang.star.compiler.cafe.compile;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.cafe.type.ICafeTypeDescription;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeContext;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;

/**
 * Copyright (c) 2015. Francis G. McCabe
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
public class AutoBoxing {
  static final String dataFactory = Type.getInternalName(Factory.class);

  static void unboxValue(MethodNode mtd, HWM hwm, IType type) {
    String isvSig = Type.getDescriptor(IValue.class);
    InsnList ins = mtd.instructions;

    switch (Types.varType(type)) {
      case rawBool:
        ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "boolValue", "(" + isvSig + ")Z"));
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

  public static ISpec boxValue(IType type, InsnList ins, ITypeContext cxt) {

    Location loc = Location.nullLoc;

    switch (Types.varType(type)) {
      case rawBool:
        ins.add(new MethodInsnNode(Opcodes.INVOKESTATIC, dataFactory, "newBool", Utils.javaInvokeSig(Factory.class,
            "newBool")));
        return new SrcSpec(StandardTypes.booleanType, loc, Types.BOOLEAN_TYPE, Types.BOOLEAN_SIG, null, null);
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
