package org.star_lang.star.compiler.type;

import java.util.Stack;

import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.Type;
import org.star_lang.star.data.type.TypeConstraintException;
import org.star_lang.star.data.type.TypeExp;

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
public class TypeRewriter extends AbstractTypeTransformer<Void>
{
  private final ITypeAlias rule;
  private final Dictionary dict;

  protected TypeRewriter(ITypeAlias rule, Dictionary dict)
  {
    super(new Stack<String>());
    this.rule = rule;
    this.dict = dict;
  }

  @Override
  public IType transformSimpleType(Type t, Void cxt)
  {
    try {
      IType reslt = rule.apply(t, rule.getLoc(), dict);
      if (reslt != null)
        return reslt;
    } catch (TypeConstraintException e) {
    }

    return super.transformSimpleType(t, cxt);
  }

  @Override
  public IType transformTypeExp(TypeExp t, Void cxt)
  {
    try {
      IType reslt = rule.apply(t, rule.getLoc(), dict);
      if (reslt != null)
        return reslt;
    } catch (TypeConstraintException e) {
    }
    return super.transformTypeExp(t, cxt);
  }
}
