package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.Expressions;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.SrcSpec;
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
public class ReturnCont implements IContinuation
{
  private final IType tipe;
  private final ISpec spec;
  private final CafeDictionary dict;

  public ReturnCont(IType tipe, ISpec spec, CafeDictionary dict)
  {
    this.tipe = tipe;
    this.spec = spec;
    this.dict = dict;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, ErrorReport errors, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();
    
    InsnList ins = mtd.instructions;

    Expressions.checkType(src, spec, mtd, dict, hwm, loc, errors, bldCat);

    switch (Types.varType(tipe)) {
    case rawBool:
    case rawChar:
      ins.add(new InsnNode(Opcodes.IRETURN));
      break;
    case rawInt:
      ins.add(new InsnNode(Opcodes.IRETURN));
      break;
    case rawLong:
      ins.add(new InsnNode(Opcodes.LRETURN));
      break;
    case rawFloat:
      ins.add(new InsnNode(Opcodes.DRETURN));
      break;
    case rawBinary:
    case rawString:
    case rawDecimal:
    case general:
      ins.add(new InsnNode(Opcodes.ARETURN));
      break;
    default:
      assert false : "invalid kind of variable";
    }
    return SrcSpec.prcSrc;
  }

  @Override
  public boolean isJump()
  {
    return true;
  }
}
