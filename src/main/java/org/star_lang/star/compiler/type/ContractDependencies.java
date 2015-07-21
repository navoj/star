package org.star_lang.star.compiler.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.star_lang.star.compiler.canonical.DefaultVisitor;
import org.star_lang.star.compiler.canonical.IStatement;
import org.star_lang.star.compiler.canonical.MethodVariable;
import org.star_lang.star.compiler.canonical.OverloadedVariable;
import org.star_lang.star.compiler.canonical.VarEntry;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.transform.Over;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.TopologySort;
import org.star_lang.star.compiler.util.TopologySort.IDefinition;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.TypeExp;

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
public class ContractDependencies
{
  public static List<List<IStatement>> dependencySort(List<IStatement> stmts)
  {
    if (stmts.isEmpty() || stmts.size() == 1)
      return FixedList.create(stmts);
    else {
      Collection<String> allList = pickList(stmts);
      List<Implementation> initGroup = new ArrayList<>();
      for (IStatement def : stmts)
        initGroup.add(new Implementation(def, allList));
      List<List<IDefinition<String>>> groups = TopologySort.sort(initGroup);
      List<List<IStatement>> sortd = new ArrayList<>();
      for (List<IDefinition<String>> entry : groups) {
        List<IStatement> group = new ArrayList<>();
        sortd.add(group);
        for (IDefinition<String> def : entry)
          group.add(((Implementation) def).getStmt());
      }

      return sortd;
    }
  }

  private static Collection<String> pickList(List<IStatement> stmts)
  {
    Set<String> pick = new HashSet<>();

    for (IStatement stmt : stmts) {
      if (stmt instanceof VarEntry) {
        VarEntry var = (VarEntry) stmt;
        for (Variable n : var.getDefined())
          pick.add(n.getName());
      }
    }
    return pick;
  }

  @SuppressWarnings("unused")
  private static <T> boolean setEqual(Collection<T> S1, Collection<T> S2)
  {
    if (S1.size() != S2.size())
      return false;
    else {
      outer: for (T s1 : S1) {
        for (T s2 : S2)
          if (s1.equals(s2))
            continue outer;
        return false;
      }
      return true;
    }
  }

  private static class Implementation implements IDefinition<String>, PrettyPrintable
  {
    private final IStatement stmt;
    private final Collection<String> refs;
    private final Collection<String> defs;

    public Implementation(IStatement stmt, Collection<String> pick)
    {
      this.stmt = stmt;
      if (stmt instanceof VarEntry) {
        this.defs = new HashSet<>(Variable.varNames(((VarEntry) stmt).getDefined()));
        this.refs = allOverloadRefs(stmt, pick);
      } else {
        this.defs = new HashSet<>();
        this.refs = new HashSet<>();
      }
    }

    public IStatement getStmt()
    {
      return stmt;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      disp.append(defs.toString());
      disp.append(":");
      disp.append(refs.toString());
    }

    @Override
    public String toString()
    {
      return PrettyPrintDisplay.toString(this);
    }

    @Override
    public boolean defines(String obj)
    {
      return defs.contains(obj);
    }

    @Override
    public Collection<String> definitions()
    {
      return defs;
    }

    @Override
    public Collection<String> references()
    {
      return refs;
    }
  }

  private static Collection<String> allOverloadRefs(IStatement stmt, Collection<String> pick)
  {
    OverloadFinder finder = new OverloadFinder(pick);

    stmt.accept(finder);
    return finder.refs;
  }

  private static class OverloadFinder extends DefaultVisitor
  {
    private final Collection<String> pick;
    private Set<String> refs = new HashSet<>();

    public OverloadFinder(Collection<String> pick)
    {
      super(true);
      this.pick = pick;
    }

    @Override
    public void visitOverloadedVariable(OverloadedVariable over)
    {
      visitVariable(over);
    }

    @Override
    public void visitMethodVariable(MethodVariable method)
    {
      if (isNotExcluded(method.getName())) {
        IType contract = TypeUtils.deRef(method.getContract());
        if (contract instanceof TypeExp) {
          String funName = Over.instanceFunName(contract);

          if (pick.contains(funName) && !refs.contains(funName))
            refs.add(funName);
        }
      }
    }

    @Override
    public void visitVariable(Variable variable)
    {
      if (isNotExcluded(variable.getName())) {
        String name = variable.getName();
        if (pick.contains(name) && !refs.contains(name))
          refs.add(name);
      }
    }

    @Override
    public void visitVarEntry(VarEntry entry)
    {
      entry.getValue().accept(this);
    }
  }
}
