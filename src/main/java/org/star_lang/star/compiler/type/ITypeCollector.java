package org.star_lang.star.compiler.type;

import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;

public interface ITypeCollector
{
  void fieldAnnotation(Location loc, String name, IType type);

  void kindAnnotation(Location loc, String name, IType type);

  void completeInterface(Location loc);
}