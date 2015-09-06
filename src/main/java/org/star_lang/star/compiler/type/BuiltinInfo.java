package org.star_lang.star.compiler.type;

import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.operators.ICafeBuiltin;

/*
 * Copyright (c) 2015. Francis G. McCabe
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

@SuppressWarnings("serial")
public class BuiltinInfo extends VarInfo
{
  private final ICafeBuiltin escape;

  public BuiltinInfo(ICafeBuiltin escape)
  {
    super(new Variable(Location.nullLoc, escape.getType(), escape.getName()), AccessMode.readOnly, true);
    this.escape = escape;
  }

  public ICafeBuiltin getEscape()
  {
    return escape;
  }
}
