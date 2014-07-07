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
  public ISpec cont(ISpec src, CafeDictionary dict, Location loc, ErrorReport errors, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
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