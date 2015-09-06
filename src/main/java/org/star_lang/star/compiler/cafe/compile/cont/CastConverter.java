package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.AutoBoxing;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
/*
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

public final class CastConverter implements IContinuation
{
  private final IType castType;
  private final HWM stackHWM;
  private final ISpec castSpec;

  public CastConverter(IType castType, HWM stackHWM, ISpec castSpec)
  {
    this.castType = castType;
    this.stackHWM = stackHWM;
    this.castSpec = castSpec;
  }

  @Override
  public boolean isJump()
  {
    return false;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary dict, Location loc, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    ErrorReport errors = ccxt.getErrors();
    InsnList ins = mtd.instructions;
    
    switch (Types.varType(castType)) {
    case rawBool:
    case rawChar:
    case rawInt:
      switch (Types.varType(src.getType())) {
      case rawBool:
      case rawChar:
      case rawInt:
        return castSpec;
      case rawLong:
        ins.add(new InsnNode(Opcodes.L2I));
        stackHWM.bump(-1);
        return castSpec;
      case rawFloat:
        ins.add(new InsnNode(Opcodes.D2I));
        stackHWM.bump(-1);
        return castSpec;
      default:
        errors.reportError("cannot convert to " + castType, loc);
        return castSpec;
      }

    case rawLong:
      switch (Types.varType(src.getType())) {
      case rawBool:
      case rawChar:
      case rawInt:
        ins.add(new InsnNode(Opcodes.I2L));
        stackHWM.bump(1);
        return castSpec;
      case rawLong:
        return castSpec;
      case rawFloat:
        ins.add(new InsnNode(Opcodes.D2L));
        return castSpec;
      default:
        errors.reportError("cannot convert to " + castType, loc);
        return castSpec;
      }
    case rawFloat:
      switch (Types.varType(src.getType())) {
      case rawBool:
      case rawChar:
      case rawInt:
        ins.add(new InsnNode(Opcodes.I2D));
        stackHWM.bump(1);
        return castSpec;
      case rawLong:
        ins.add(new InsnNode(Opcodes.L2D));
        return castSpec;
      case rawFloat:
        return castSpec;
      default:
        errors.reportError("cannot convert  to " + castType, loc);
        return castSpec;
      }
    default: {
      ISpec boxed = AutoBoxing.boxValue(src.getType(), ins, dict);
      if (!boxed.getType().equals(castType))
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, castSpec.getJavaType()));

      return castSpec;
    }
    }
  }
}