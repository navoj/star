package org.star_lang.star.compiler.format.rules;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.Location;

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
public class FmtVarPtn implements FmtPtnOp
{
  private final int offset;
  private final FmtPtnOp other;

  FmtVarPtn(int offset)
  {
    this(offset, new FmtPtnNull());
  }

  public FmtVarPtn(int offset, FmtPtnOp other)
  {
    this.offset = offset;
    this.other = other;
  }

  @Override
  public formatCode apply(IAbstract term, IAbstract env[], Location loc)
  {
    formatCode mode = other.apply(term, env, loc);
    if (env[offset] == null) {
      env[offset] = term;
      return mode;

    } else if (env[offset].equals(term))
      return mode;
    else
      return formatCode.notApply;
  }

  @Override
  public int getSpecificity()
  {
    return 0;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    other.prettyPrint(disp);
    disp.append(" ");
    disp.append(StandardNames.WFF_VAR);
    disp.append(Long.toString(offset));
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
