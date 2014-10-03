package org.star_lang.star.compiler.generate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.type.BindingKind;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.ContinueFlag;
import org.star_lang.star.compiler.util.EntryVisitor;
import org.star_lang.star.compiler.util.LayeredHash;
import org.star_lang.star.compiler.util.LayeredMap;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.UndoManager;
import org.star_lang.star.compiler.util.UpdateEntry;
import org.star_lang.star.compiler.util.Wrapper;
import org.star_lang.star.data.type.IAlgebraicType;
import org.star_lang.star.data.type.IType;
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
public class CContext implements PrettyPrintable
{
  private final UndoManager undoMgr = new UndoManager();
  private final LayeredMap<String, DictEntry> dict;
  private final LayeredMap<String, IContentExpression> rewrites;
  private final Map<String, IAlgebraicType> introducedTypes;
  private final Wrapper<ICondition> cond;
  private final ErrorReport errors;
  private final CContext outer;
  private final Location loc;
  private final boolean isDeep;
  private final List<IAbstract> extra;
  private final AccessMode access;

  private CContext(LayeredMap<String, DictEntry> dict, LayeredMap<String, IContentExpression> rewrites,
      Map<String, IAlgebraicType> introducedTypes, List<IAbstract> extra, Wrapper<ICondition> cond, ErrorReport errors,
      CContext outer, Location loc, boolean isDeep, AccessMode access)
  {
    super();
    this.dict = dict;
    this.rewrites = rewrites;
    this.introducedTypes = introducedTypes;
    this.extra = extra;
    this.errors = errors;
    this.outer = outer;
    this.loc = loc;
    this.isDeep = isDeep;
    this.cond = cond;
    this.access = access;
  }

  public CContext(CContext template)
  {
    this(new LayeredHash<String, DictEntry>(), template.rewrites.fork(), template.introducedTypes, template.extra,
        template.cond, template.errors, template, template.loc, template.isDeep, template.access);
  }

  public CContext(Location loc, ErrorReport errors)
  {
    this(new LayeredHash<String, DictEntry>(), new LayeredHash<String, IContentExpression>(),
        new HashMap<String, IAlgebraicType>(), new ArrayList<IAbstract>(), Wrapper.create(CompilerUtils.truth), errors,
        null, loc, false, AccessMode.readOnly);
  }

  public CContext fork(boolean isDeep)
  {
    return new CContext(dict, rewrites, introducedTypes, extra, cond, errors, this, loc, isDeep, access);
  }

  public CContext fork(Wrapper<ICondition> cond)
  {
    return new CContext(dict, rewrites, introducedTypes, extra, cond, errors, this, loc, isDeep, access);
  }

  public CContext fork(AccessMode access)
  {
    return new CContext(dict, rewrites, introducedTypes, extra, cond, errors, this, loc, isDeep, access);
  }

  public CContext fork(List<IAbstract> extra)
  {
    return new CContext(dict, rewrites, introducedTypes, extra, cond, errors, this, loc, isDeep, access);
  }
  
  public CContext fork(Location loc,LayeredMap<String, DictEntry> dict)
  {
    return new CContext(dict,rewrites,introducedTypes,extra,cond,errors,this,loc,false,AccessMode.readOnly);
  }

  public ErrorReport getErrors()
  {
    return errors;
  }

  public LayeredMap<String, DictEntry> getDict()
  {
    return dict;
  }

  public Wrapper<ICondition> getCond()
  {
    return cond;
  }

  public boolean isDeep()
  {
    return isDeep;
  }

  public List<IAbstract> getExtra()
  {
    return extra;
  }

  public AccessMode getAccess()
  {
    return access;
  }

  public Map<String, IAlgebraicType> getIntroducedTypes()
  {
    return introducedTypes;
  }

  public void introduceType(IAlgebraicType desc)
  {
    if (!introducedTypes.containsKey(desc.getName()))
      introducedTypes.put(desc.getName(), desc);
  }

  public static void introduceGlobalType(CContext cxt, IAlgebraicType desc)
  {
    if (cxt != null) {
      while (cxt.outer != null)
        cxt = cxt.outer;
      cxt.introduceType(desc);
    }
  }

  public IAlgebraicType introducedType(String name)
  {
    return introducedTypes.get(name);
  }

  public Location getLoc()
  {
    return loc;
  }

  public void defineRewrite(String var, IContentExpression rewrite)
  {
    undoMgr.pushUndo(new RewriteUndo(var, rewrites.get(var)));

    rewrites.put(var, rewrite);
  }

  public IContentExpression rewriteVar(String var)
  {
    return rewrites.get(var);
  }

  public void defineFree(String name, IType type, AccessMode readOnly)
  {
    DictEntry entry = new DictEntry(name, Variable.create(loc, type, name), loc, readOnly, BindingKind.free);
    undoMgr.pushUndo(new DictUndo(name, dict.get(name)));
    dict.put(name, entry);
  }

  public void defineLocal(String name, Variable var, AccessMode readOnly)
  {
    undoMgr.pushUndo(new DictUndo(name, dict.get(name)));
    DictEntry entry = new DictEntry(name, var, var.getLoc(), readOnly, BindingKind.local);
    dict.put(name, entry);
  }

  public List<Variable> definedVariables()
  {
    List<Variable> vars = new ArrayList<Variable>();
    for (DictEntry entry : dict.values()) {
      Variable v = entry.getVariable();
      if (!vars.contains(v))
        vars.add(v);
    }
    return vars;
  }

  public boolean isDefined(String name)
  {
    return dict.containsKey(name);
  }

  public DictEntry getDictInfo(String name)
  {
    return dict.get(name);
  }

  public IType getTypeOf(String name)
  {
    assert dict.containsKey(name);

    return dict.get(name).getType();
  }

  public AccessMode isReadOnly(String name)
  {
    DictEntry entry = dict.get(name);
    assert entry != null;
    return entry.access;
  }

  public void resetDict(int reset)
  {
    undoMgr.resetStack(reset);
  }

  public int getMark()
  {
    return undoMgr.getCurrentState();
  }

  public BindingKind varLocation(String name)
  {
    DictEntry entry = dict.get(name);
    if (entry != null)
      return entry.getBindingKind();
    else
      return null;
  }

  public IType typeOf(String name)
  {
    DictEntry entry = dict.get(name);

    if (entry != null)
      return entry.getType();
    else
      return null;
  }

  @Override
  public String toString()
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();

    prettyPrint(disp);

    return disp.toString();
  }

  @Override
  public void prettyPrint(final PrettyPrintDisplay disp)
  {
    int mark = disp.markIndent(2);
    disp.append("vars{");

    dict.visit(new EntryVisitor<String, DictEntry>() {

      @Override
      public ContinueFlag visit(String key, DictEntry var)
      {
        disp.appendWord(key);
        disp.append("/");
        disp.append(var.getBindingKind().toString());

        disp.append(" has type ");
        DisplayType.display(disp, var.getType());
        disp.append("\n");

        return ContinueFlag.cont;
      }
    });

    disp.append("}\n");

    disp.popIndent(mark);
  }

  public ContinueFlag visitCContext(EntryVisitor<String, DictEntry> visitor)
  {
    return dict.visit(visitor);
  }

  private class DictUndo extends UpdateEntry
  {
    String name;
    DictEntry old;

    DictUndo(String name, DictEntry old)
    {
      this.name = name;
      this.old = old;
    }

    @Override
    public void reset()
    {
      dict.remove(name);
      if (old != null)
        dict.put(name, old);
    }
  }

  private class RewriteUndo extends UpdateEntry
  {
    String name;
    IContentExpression old;

    RewriteUndo(String name, IContentExpression old)
    {
      this.name = name;
      this.old = old;
    }

    @Override
    public void reset()
    {
      if (old != null)
        rewrites.put(name, old);
      else
        rewrites.remove(name);
    }
  }
}
