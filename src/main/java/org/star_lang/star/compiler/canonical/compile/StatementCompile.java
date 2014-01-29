package org.star_lang.star.compiler.canonical.compile;

import org.star_lang.star.compiler.cafe.compile.CafeDictionary;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.JavaKind;
import org.star_lang.star.compiler.cafe.compile.VarInfo;
import org.star_lang.star.compiler.canonical.JavaEntry;
import org.star_lang.star.compiler.canonical.OpenStatement;
import org.star_lang.star.compiler.canonical.TransformStatement;
import org.star_lang.star.compiler.canonical.TypeDefinition;
import org.star_lang.star.compiler.canonical.TypeWitness;
import org.star_lang.star.compiler.canonical.VarEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ContractEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImplementationEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImportEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.TypeAliasEntry;

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
public class StatementCompile implements TransformStatement<ISpec, ISpec, ISpec, ISpec, ISpec, CompileContext>
{

  @Override
  public ISpec transformContractDefn(ContractEntry con, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ISpec transformContractImplementation(ImplementationEntry entry, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ISpec transformImportEntry(ImportEntry entry, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ISpec transformJavaEntry(JavaEntry entry, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ISpec transformTypeAliasEntry(TypeAliasEntry entry, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ISpec transformTypeEntry(TypeDefinition entry, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ISpec transformVarEntry(VarEntry entry, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ISpec transformOpenStatement(OpenStatement open, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ISpec transformWitness(TypeWitness stmt, CompileContext context)
  {
    // TODO Auto-generated method stub
    return null;
  }// Convert a reference to a free reference

  public static VarInfo varReference(CompileContext cxt, String name, Location loc)
  {
    return varReference(cxt, name, cxt.getDict(), loc);

  }

  private static VarInfo varReference(CompileContext cxt, String name, CafeDictionary dict, Location loc)
  {
    VarInfo var = dict.find(name);

    if (var == null && dict.getParent() != null) {
      VarInfo ref = varReference(cxt, name, dict.getParent(), loc);

      if (ref != null) {
        if (ref.getKind() == JavaKind.builtin || ref.getKind() == JavaKind.constructor)
          return ref;

        if (!ref.isInited())
          cxt.reportError("accessing uninitialized free variable: " + ref + "@" + ref.getLoc(), loc, ref.getLoc());
        return dict.declareFree(ref.getAccess().downGrade(), ref);
      }
    }

    return var;
  }
}
