package org.star_lang.star.compiler.cafe.compile.cont;

import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.Patterns;
import org.star_lang.star.compiler.util.AccessMode;

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

public class PatternCont implements IContinuation
{
  private final IAbstract ptn;
  private final CafeDictionary dict, outer;
  private final AccessMode access;
  private final LabelNode endLabel;
  private final IContinuation succ, fail;

  public PatternCont(IAbstract ptn, CafeDictionary dict, CafeDictionary outer, AccessMode access, MethodNode mtd,
      LabelNode endLabel, ErrorReport errors, IContinuation succ, IContinuation fail)
  {
    this.ptn = ptn;
    this.dict = dict;
    this.outer = outer;
    this.access = access;
    this.endLabel = endLabel;
    this.succ = succ;
    this.fail = fail;
  }

  @Override
  public ISpec cont(ISpec src, CafeDictionary cxt, Location loc, ErrorReport errors, CodeContext ccxt)
  {
    Patterns.compilePttrn(ptn, access, src, dict, outer, endLabel, errors, succ, fail, ccxt);
    return src;
  }

  @Override
  public boolean isJump()
  {
    return succ.isJump() && fail.isJump();
  }

}
