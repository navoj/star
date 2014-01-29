package org.star_lang.star.compiler.canonical;

import org.star_lang.star.compiler.canonical.EnvironmentEntry.ContractEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImplementationEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.ImportEntry;
import org.star_lang.star.compiler.canonical.EnvironmentEntry.TypeAliasEntry;

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
public interface TransformStatement<A, E, P, C, D, T>
{
  D transformContractDefn(ContractEntry con, T context);

  D transformContractImplementation(ImplementationEntry entry, T context);

  D transformImportEntry(ImportEntry entry, T context);

  D transformJavaEntry(JavaEntry entry, T context);

  D transformTypeAliasEntry(TypeAliasEntry entry, T context);

  D transformTypeEntry(TypeDefinition entry, T context);

  D transformVarEntry(VarEntry entry, T context);

  D transformOpenStatement(OpenStatement open, T context);

  D transformWitness(TypeWitness stmt, T context);
}
