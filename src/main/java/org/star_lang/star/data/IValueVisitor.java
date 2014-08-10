package org.star_lang.star.data;

/**
 * Visitor interface for {@link IValue}s. Part of a visitor pattern.
 * 
 * Implementing an {@link IValueVisitor} will allow traversal of an {@link IValue} for such purposes
 * as serialization and pretty printing.
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
public interface IValueVisitor
{
  void visitScalar(IScalar<?> scalar);

  void visitRecord(IRecord agg);

  void visitList(IList list);

  void visitFunction(IFunction fn);

  void visitPattern(IPattern ptn);

  void visitConstructor(IConstructor con);

  void visitMap(IMap map);
}
