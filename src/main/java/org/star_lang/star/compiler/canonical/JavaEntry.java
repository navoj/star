package org.star_lang.star.compiler.canonical;

import java.util.Collection;

import org.star_lang.star.compiler.sources.JavaInfo;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.type.Visibility;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

/**
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
@SuppressWarnings("serial")
public class JavaEntry extends EnvironmentEntry
{
  private final String className;
  private final JavaInfo pkg;

  public JavaEntry(String className, Location loc, JavaInfo pkg, Visibility visibility)
  {
    super(loc, visibility);
    this.className = className;
    this.pkg = pkg;
  }

  public String getClassName()
  {
    return className;
  }

  public JavaInfo getJava()
  {
    return pkg;
  }

  @Override
  public boolean defines(String name)
  {
    return false;
  }

  @Override
  public Collection<String> definedFields()
  {
    return FixedList.create();
  }

  @Override
  public Collection<String> definedTypes()
  {
    return FixedList.create();
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitJavaEntry(this);
  }

  @Override
  public <A, E, P, C, D, T> D transform(TransformStatement<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformJavaEntry(this, context);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendWord(StandardNames.JAVA);
    disp.appendWord(className);
  }
}