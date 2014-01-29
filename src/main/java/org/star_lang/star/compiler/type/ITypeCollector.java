package org.star_lang.star.compiler.type;

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;

public interface ITypeCollector
{
  void fieldAnnotation(Location loc, String name, IType type);

  void kindAnnotation(Location loc, String name, IType type);

  void completeInterface(Location loc);
}