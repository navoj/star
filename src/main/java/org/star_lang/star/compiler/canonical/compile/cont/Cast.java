package org.star_lang.star.compiler.canonical.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.star_lang.star.compiler.cafe.compile.AutoBoxing;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.Types;
import org.star_lang.star.compiler.canonical.compile.CompileContext;
import org.star_lang.star.compiler.canonical.compile.Continue;
import org.star_lang.star.compiler.canonical.compile.FrameState;
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
public final class Cast implements Continue
{
  private final IType castType;
  private final ISpec castSpec;
  private final CompileContext cxt;

  public Cast(IType castType, CompileContext cxt, ISpec castSpec)
  {
    this.castType = castType;
    this.cxt = cxt;
    this.castSpec = castSpec;
  }

  @Override
  public boolean isJump()
  {
    return false;
  }

  @Override
  public FrameState cont(FrameState src, Location loc)
  {
    MethodNode mtd = cxt.getMtd();
    InsnList ins = mtd.instructions;
    ISpec tos = src.tos();
    FrameState rest = src.dropStack(1);
    

    switch (Types.varType(castType)) {
    case rawBool:
    case rawChar:
    case rawInt:
      switch (Types.varType(tos.getType())) {
      case rawBool:
      case rawChar:
      case rawInt:
        return rest.pushStack(castSpec);
      case rawLong:
        ins.add(new InsnNode(Opcodes.L2I));
        return rest.pushStack(castSpec);
      case rawFloat:
        ins.add(new InsnNode(Opcodes.D2I));
        return rest.pushStack(castSpec);
      default:
        cxt.reportError("cannot convert to " + castType, loc);
        return rest.pushStack(castSpec);
      }

    case rawLong:
      switch (Types.varType(tos.getType())) {
      case rawBool:
      case rawChar:
      case rawInt:
        ins.add(new InsnNode(Opcodes.I2L));
        return rest.pushStack(castSpec);
      case rawLong:
        return rest.pushStack(castSpec);
      case rawFloat:
        ins.add(new InsnNode(Opcodes.D2L));
        return rest.pushStack(castSpec);
      default:
        cxt.reportError("cannot convert to " + castType, loc);
        return rest.pushStack(castSpec);
      }
    case rawFloat:
      switch (Types.varType(tos.getType())) {
      case rawBool:
      case rawChar:
      case rawInt:
        ins.add(new InsnNode(Opcodes.I2D));
        return rest.pushStack(castSpec);
      case rawLong:
        ins.add(new InsnNode(Opcodes.L2D));
        return rest.pushStack(castSpec);
      case rawFloat:
        return rest.pushStack(castSpec);
      default:
        cxt.reportError("cannot convert  to " + castType, loc);
        return rest.pushStack(castSpec);
      }
    default: {
      ISpec boxed = AutoBoxing.boxValue(tos.getType(), ins, cxt.getDict());
      if (!boxed.getType().equals(castType))
        ins.add(new TypeInsnNode(Opcodes.CHECKCAST, castSpec.getJavaType()));

      return rest.pushStack(castSpec);
    }
    }
  }
}