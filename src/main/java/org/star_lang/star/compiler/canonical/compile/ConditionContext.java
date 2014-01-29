package org.star_lang.star.compiler.canonical.compile;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.Sense;

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
public class ConditionContext extends CompileContext
{

  private final Sense sense;
  private final LabelNode lf;
  private final LabelNode lx;

  public ConditionContext(ErrorReport errors, ClassNode klass, MethodNode mtd, Continue cont, CafeDictionary dict,
      CodeCatalog bldCat, CodeRepository repository, Sense sense, LabelNode lf, LabelNode lx, FrameState frame,
      Location loc)
  {
    super(errors, klass, mtd, cont, dict, bldCat, frame, loc, repository);
    this.sense = sense;
    this.lf = lf;
    this.lx = lx;
  }

  public LabelNode getLf()
  {
    return lf;
  }

  public LabelNode getLx()
  {
    return lx;
  }

  public Sense getSense()
  {
    return sense;
  }

  public static ConditionContext fork(CompileContext cxt, Sense sense, LabelNode lf, LabelNode lx)
  {
    return new ConditionContext(cxt.errors, cxt.klass, cxt.mtd, cxt.cont, cxt.dict, cxt.bldCat, cxt.getRepository(), sense, lf, lx,
        cxt.getFrame(), cxt.getLastLoc());
  }

  public ConditionContext fork(Sense sense, LabelNode lf, LabelNode lx)
  {
    return new ConditionContext(errors, klass, mtd, cont, dict, bldCat, repository, sense, lf, lx, frame, lastLoc);
  }

  @Override
  public CompileContext fork(Continue cont)
  {
    return new ConditionContext(errors, klass, mtd, cont, dict, bldCat, repository, sense, lf, lx, frame, lastLoc);
  }

  @Override
  public CompileContext fork(CafeDictionary dict)
  {
    return new ConditionContext(errors, klass, mtd, cont, dict, bldCat, repository, sense, lf, lx, frame, lastLoc);
  }
}
