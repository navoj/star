package org.star_lang.star.compiler.transform;

import java.util.Stack;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.TypeVar;
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

public class OverContext
{
  private final ErrorReport errors;
  private int varNo;
  private final Stack<Pair<IType, IContentExpression>> dict;
  private final Dictionary localCxt;

  public OverContext(Stack<Pair<IType, IContentExpression>> dict, Dictionary localCxt, ErrorReport errors, int varNo)
  {
    this.errors = errors;
    this.dict = dict;
    this.localCxt = localCxt;
    this.varNo = varNo;
  }

  public OverContext(Dictionary localCxt, ErrorReport errors, int varNo)
  {
    this(new Stack<>(), localCxt, errors, varNo);
  }

  public int getVarNo()
  {
    return varNo;
  }

  public void setVarNo(int varNo)
  {
    this.varNo = varNo;
  }
  
  public int nextVarNo()
  {
    return varNo++;
  }

  public Stack<Pair<IType, IContentExpression>> getDict()
  {
    return dict;
  }

  public int markDict()
  {
    return dict.size();
  }

  public void resetDict(int mark)
  {
    dict.setSize(mark);
  }

  public OverContext fork()
  {
    Stack<Pair<IType, IContentExpression>> subStack = new Stack<>();
    for (int ix = 0; ix < dict.size(); ix++)
      subStack.push(this.dict.get(ix));
    return new OverContext(subStack, localCxt.fork(), errors, varNo);
  }

  public void define(IType type, IContentExpression term)
  {
    dict.push(Pair.pair(type, term));
  }

  public IContentExpression locateDictVar(IType type)
  {
    // Need to process the stack in top-down order
    for (int ix = dict.size(); ix > 0; ix--) {
      Pair<IType, IContentExpression> entry = dict.get(ix - 1);
      if (entry.left().equals(type))
        return entry.right();
    }

    return null;
  }

  public boolean isDefinedVar(String name)
  {
    return localCxt.isDefinedVar(name);
  }

  public boolean varsInScope(IType tp)
  {
    for (IType arg : TypeUtils.typeArgs(tp)) {
      arg = TypeUtils.deRef(arg);
      if (arg instanceof TypeVar) {
        if (localCxt.typeExists(((TypeVar) arg).getVarName()))
          return true;
      }
    }
    return false;
  }

  public Dictionary getLocalCxt()
  {
    return localCxt;
  }

  public ErrorReport getErrors()
  {
    return errors;
  }

}
