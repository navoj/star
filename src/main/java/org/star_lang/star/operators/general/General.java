package org.star_lang.star.operators.general;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.general.runtime.Assert;
import org.star_lang.star.operators.general.runtime.GeneralEq;
import org.star_lang.star.operators.general.runtime.Raise;

/**
 * Some general primitive functions
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

public abstract class General
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(GeneralEq.name, GeneralEq.type(), GeneralEq.class));
    cxt.declareBuiltin(new Builtin(Assert.name, Assert.type(), Assert.class));
    cxt.declareBuiltin(new Builtin(Raise.name, Raise.type(), Raise.class));
  }
}
