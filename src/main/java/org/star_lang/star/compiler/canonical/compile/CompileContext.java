package org.star_lang.star.compiler.canonical.compile;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.star_lang.star.code.repository.CodeCatalog;
import org.star_lang.star.code.repository.CodeRepository;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.CodeContext;

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
public class CompileContext
{
  protected final ErrorReport errors;
  protected final ClassNode klass;
  protected final MethodNode mtd;
  protected final Continue cont;
  protected final CodeCatalog bldCat;
  protected final CafeDictionary dict;
  protected final CodeRepository repository;
  protected final CodeContext ccxt;
  protected final FrameState frame;
  protected Location lastLoc;

  public CompileContext(ErrorReport errors, ClassNode klass, MethodNode mtd, Continue cont, CafeDictionary dict,
      CodeCatalog bldCat, FrameState frame, Location loc, CodeRepository repository)
  {
    this.errors = errors;
    this.klass = klass;
    this.mtd = mtd;
    this.cont = cont;
    this.dict = dict;
    this.bldCat = bldCat;
    this.ccxt = null;
    this.frame = frame;
    this.lastLoc = loc;
    this.repository = repository;
  }

  public FrameState getFrame()
  {
    return frame;
  }

  public void reportError(String msg, Location... locs)
  {
    errors.reportError(msg, locs);
  }

  public ErrorReport getErrors()
  {
    return errors;
  }

  public MethodNode getMtd()
  {
    return mtd;
  }

  public InsnList getIns()
  {
    return mtd.instructions;
  }

  public CodeCatalog getBldCat()
  {
    return bldCat;
  }

  public CodeRepository getRepository()
  {
    return repository;
  }

  public Continue getCont()
  {
    return cont;
  }

  public CafeDictionary getDict()
  {
    return dict;
  }

  public FrameState cont(FrameState src, Location loc)
  {
    return cont.cont(src, loc);
  }

  public Location getLastLoc()
  {
    return lastLoc;
  }

  public void setLastLoc(Location lastLoc)
  {
    this.lastLoc = lastLoc;
  }

  public CompileContext fork(Continue cont)
  {
    return new CompileContext(errors, klass, mtd, cont, dict, bldCat, frame, lastLoc, repository);
  }

  public CompileContext fork(Continue cont, FrameState frame)
  {
    return new CompileContext(errors, klass, mtd, cont, dict, bldCat, frame, lastLoc, repository);
  }

  public CompileContext fork(FrameState frame)
  {
    return new CompileContext(errors, klass, mtd, cont, dict, bldCat, frame, lastLoc, repository);
  }

  public CompileContext fork(CafeDictionary dict)
  {
    return new CompileContext(errors, klass, mtd, cont, dict, bldCat, frame, lastLoc, repository);
  }
}
