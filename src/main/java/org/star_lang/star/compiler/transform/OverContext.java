package org.star_lang.star.compiler.transform;

import java.util.Stack;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.TypeVar;

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
    this(new Stack<Pair<IType, IContentExpression>>(), localCxt, errors, varNo);
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
