package org.star_lang.star.compiler.canonical;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;

/**
 * An action that denotes a synchronization request on a value
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
@SuppressWarnings("serial")
public class SyncAction extends Action
{
  private final IContentExpression sel;
  private final Map<ICondition, IContentAction> body;

  public SyncAction(Location loc, IType type, IContentExpression sel, IContentAction body)
  {
    super(loc, type);
    this.sel = sel;
    this.body = new HashMap<ICondition, IContentAction>();
    this.body.put(CompilerUtils.truth, body);
  }

  public SyncAction(Location loc, IType type, IContentExpression sel, Map<ICondition, IContentAction> body)
  {
    super(loc, type);
    this.sel = sel;
    this.body = body;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.append(StandardNames.SYNC);
    disp.append("(");
    sel.prettyPrint(disp);
    disp.append("){\n");
    String sep = "";
    for (Entry<ICondition, IContentAction> entry : body.entrySet()) {
      disp.append(sep);
      disp.appendWord(StandardNames.WHEN);
      entry.getKey().prettyPrint(disp);
      disp.appendWord(StandardNames.DO);
      entry.getValue().prettyPrint(disp);
      sep = ";\n";
    }
    disp.popIndent(mark);
    disp.append("}");
  }

  public IContentExpression getSel()
  {
    return sel;
  }

  public Map<ICondition, IContentAction> getBody()
  {
    return body;
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitSyncAction(this);
  }

  @Override
  public void accept(ActionVisitor visitor)
  {
    visitor.visitSyncAction(this);
  }

  @Override
  public <A, E, P, C, D, T> A transform(TransformAction<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformSyncAction(this, context);
  }
}
