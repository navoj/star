package org.star_lang.star.compiler.wff;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.Location;

/**
 * A macro operator applies itself to an abstract syntax term.
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
public interface WffOp extends PrettyPrintable
{
  enum applyMode {
    validates, notValidates, notApply
  }

  applyMode apply(IAbstract term, IAbstract env[], Location loc, WffEngine engine);

  long specificity();
}
