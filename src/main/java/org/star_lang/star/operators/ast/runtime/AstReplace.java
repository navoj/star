package org.star_lang.star.operators.ast.runtime;

import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IFunction;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.Cons;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.data.value.Cons.ConsCons;
import org.star_lang.star.data.value.Cons.Nil;
import org.star_lang.star.operators.CafeEnter;

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
public class AstReplace implements IFunction
{
  public static final String name = "__astMacroReplace";

  @CafeEnter
  public static IAbstract enter(IAbstract term, Cons path, IAbstract repl) throws EvaluationException
  {
    if (path.equals(Nil.nilEnum))
      return repl;
    else {
      assert path instanceof ConsCons && term instanceof Apply;
      ConsCons consPath = (ConsCons) path;
      Cons tail = (Cons) consPath.get___1();
      Apply app = (Apply) term;
      IAbstract op = app.getOperator();
      IList args = app.getArgs();
      int ix = Factory.intValue(consPath.get___0());
      if (ix < 0)
        op = enter(op, tail, repl);
      else
        args = args.substituteCell(ix, enter((IAbstract) args.getCell(ix), tail, repl));

      return new Apply(app.getLoc(), op, args, app.getCategories(), app.getAttributes());
    }
  }

  @Override
  public IValue enter(IValue... args) throws EvaluationException
  {
    return enter((IAbstract) args[0], (Cons) args[1], (IAbstract) args[2]);
  }

  @Override
  public IType getType()
  {
    return type();
  }

  public static IType type()
  {
    return TypeUtils.functionType(StandardTypes.astType, TypeUtils.consType(StandardTypes.integerType),
        StandardTypes.astType, StandardTypes.astType);
  }
}
