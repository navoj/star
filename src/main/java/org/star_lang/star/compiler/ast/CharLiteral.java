package org.star_lang.star.compiler.ast;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Factory;

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
public class CharLiteral extends Literal
{
  public static final String name = "charAst";
  private static final int locIndex = 0;
  private static final int charIndex = 1;
  private static final IType conType = TypeUtils.tupleConstructorType(Location.type, TypeUtils
      .typeExp(StandardTypes.CHAR), ASyntax.type);

  private final int ch;

  public CharLiteral(Location loc, int ch)
  {
    super(loc);
    this.ch = ch;
  }

  public CharLiteral(IValue loc, IValue val) throws EvaluationException
  {
    super((Location) loc);
    this.ch = Factory.charValue(val);
  }

  @Override
  public astType astType()
  {
    return astType.Char;
  }

  public static IType conType()
  {
    return conType;
  }

  public static ConstructorSpecifier spec()
  {
    return new ConstructorSpecifier(Location.nullLoc, null, name, charIx, conType, CharLiteral.class, ASyntax.class);
  }

  @Override
  public void accept(IAbstractVisitor visitor)
  {
    visitor.visitCharLiteral(this);
  }

  @Override
  public Integer getLit()
  {
    return ch;
  }

  @Override
  public int conIx()
  {
    return charIx;
  }

  @Override
  public String getLabel()
  {
    return name;
  }

  @Override
  public int size()
  {
    return 2;
  }

  @Override
  public IValue getCell(int index)
  {
    switch (index) {
    case locIndex:
      return getLoc();
    case charIndex:
      return Factory.newChar(ch);
    default:
      throw new IllegalArgumentException("index out of range");
    }
  }

  public IValue get___1()
  {
    return getCell(charIndex);
  }

  @Override
  public IValue[] getCells()
  {
    return new IValue[] { getLoc(), Factory.newChar(ch) };
  }

  @Override
  public IConstructor shallowCopy() throws EvaluationException
  {
    return new CharLiteral(getLoc(), ch);
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof CharLiteral && ((CharLiteral) obj).ch == ch;
  }

  @Override
  public int hashCode()
  {
    return ch;
  }
}
