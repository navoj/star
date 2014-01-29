package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.Expressions;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.SrcSpec;

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

public class CheckCont implements IContinuation
{
  private final LabelNode jmp;
  private final ISpec expect;
  private final CafeDictionary dict;

  public CheckCont(LabelNode jmp, ISpec expect, CafeDictionary dict)
  {
    this.jmp = jmp;
    this.expect = expect;
    this.dict = dict;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, ErrorReport errors, CodeContext ccxt)
  {
    MethodNode mtd = ccxt.getMtd();
    HWM hwm = ccxt.getMtdHwm();
    CodeCatalog bldCat = ccxt.getBldCat();
    
    Expressions.checkType(src, expect, mtd, dict, hwm, loc, errors, bldCat);
    InsnList ins = mtd.instructions;
    if (!JumpCont.isUnconditionalJump(ins.getLast().getOpcode()))
      ins.add(new JumpInsnNode(Opcodes.GOTO, jmp));
    return SrcSpec.prcSrc;
  }

  @Override
  public boolean isJump()
  {
    return true;
  }

}
