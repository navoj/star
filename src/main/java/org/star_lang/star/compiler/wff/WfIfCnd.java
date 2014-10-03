package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.wff.WffOp.applyMode;
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
public class WfIfCnd implements WffCond
{
  private final WffCond test, thOp, elOp;

  public WfIfCnd(WffCond test, WffCond thOp, WffCond elOp)
  {
    this.test = test;
    this.thOp = thOp;
    this.elOp = elOp;
  }

  @Override
  public applyMode satisfied(IAbstract[] env, Location loc, WffEngine engine)
  {

    if (test.satisfied(env, loc, engine) == applyMode.validates)
      return thOp.satisfied(env, loc, engine);
    else
      return elOp.satisfied(env, loc, engine);
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("(");
    test.prettyPrint(disp);
    disp.append(":?");
    thOp.prettyPrint(disp);
    disp.append(":|");
    elOp.prettyPrint(disp);
    disp.append(")");
  }

}
