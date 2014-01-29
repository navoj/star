package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.Types;

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;

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
public class CallCont implements IContinuation
{
  private final InsnList ins;
  private final IContinuation cont;

  public CallCont(InsnList ins, IContinuation cont)
  {
    this.ins = ins;
    this.cont = cont;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, ErrorReport errors, CodeContext ccxt)
  {
    IType resType = src.getType();
    switch (Types.varType(resType)) {
    default:
      // if (!TypeUtils.isProcedureReturnType(resType))
      ins.add(new InsnNode(Opcodes.POP));
      break;
    case rawLong:
    case rawFloat:
      ins.add(new InsnNode(Opcodes.POP2));
      break;
    }
    return cont.cont(src, cxt, loc, errors, ccxt);
  }

  @Override
  public boolean isJump()
  {
    return cont.isJump();
  }
}