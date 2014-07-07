package org.star_lang.star.compiler.cafe.compile;

import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.transform.Over;
import org.star_lang.star.compiler.type.ITypeCollector;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.ContractConstraint;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeConstraint;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeVar;

/*
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
/**
 * Special version of the type collector for parsing cafe-style type expressions
 */
public class CafeTypeCollector implements ITypeCollector
{
  private final Map<String, IType> elements;
  private final Map<String, IType> exists;

  public CafeTypeCollector(Map<String, IType> elements, Map<String, IType> types)
  {
    this.elements = elements;
    this.exists = types;
  }

  @Override
  public void fieldAnnotation(Location loc, String name, IType type)
  {
    elements.put(name, type);
  }

  @Override
  public void kindAnnotation(Location loc, String name, IType type)
  {
    IType t = exists.get(name);
    if (t == null) {
      exists.put(name, type);
    }
  }

  @Override
  public void completeInterface(Location loc)
  {
    for (Entry<String, IType> e : exists.entrySet()) {
      if (e.getValue() instanceof TypeVar) {
        TypeVar v = (TypeVar) e.getValue();
        for (ITypeConstraint con : v) {
          if (con instanceof ContractConstraint) {
            ContractConstraint contract = (ContractConstraint) con;
            String instanceName = Over.instanceFunName(contract.getContract());
            IType instanceType = Over.computeDictionaryType(contract.getContract(), loc, AccessMode.readWrite);
            fieldAnnotation(loc, instanceName, instanceType);
          }
        }
      }
    }
  }
}