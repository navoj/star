package org.star_lang.star.compiler.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.data.type.ConstructorSpecifier;
import org.star_lang.star.data.type.ContractImplementation;
import org.star_lang.star.data.type.IAlgebraicType;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.ITypeAlias;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.TypeContract;
import org.star_lang.star.data.type.TypeExists;
import org.star_lang.star.data.type.TypeVar;
import org.star_lang.star.operators.Intrinsics;

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
public class Dict implements Dictionary
{
  private final Map<String, DictInfo> names = new HashMap<>();
  private final Map<String, Set<ContractImplementation>> implementations = new HashMap<>();
  private final Collection<Variable> freeVars = new ArrayList<>();

  private final Map<String, IValueSpecifier> constructors = new HashMap<>();
  private final Map<String, TypeContract> contracts = new HashMap<>();
  private final Map<String, ITypeDescription> definedTypes = new HashMap<>();
  private final Dict outer;

  public Dict(Dict outer)
  {
    this.outer = outer;
  }

  public static Dict baseDict()
  {
    return Intrinsics.intrinsics().fork();
  }

  @Override
  public ITypeDescription getTypeDescription(String name)
  {
    ITypeDescription desc = definedTypes.get(name);
    if (desc == null && outer != null)
      desc = outer.getTypeDescription(name);
    return desc;
  }

  @Override
  public void defineType(ITypeDescription desc)
  {
    definedTypes.put(desc.getName(), desc);
    if (desc instanceof IAlgebraicType) {
      for (IValueSpecifier con : ((IAlgebraicType) desc).getValueSpecifiers()) {
        if (con instanceof ConstructorSpecifier)
          declareConstructor((ConstructorSpecifier) con);
      }
    }
  }

  @Override
  public boolean typeExists(String name)
  {
    return getTypeDescription(name) != null;
  }

  @Override
  public Map<String, ITypeDescription> getAllTypes()
  {
    Map<String, ITypeDescription> allTypes = new HashMap<>();
    Dict dict = this;
    while (dict != null) {
      allTypes.putAll(dict.definedTypes);
      dict = dict.outer;
    }
    return allTypes;
  }

  @Override
  public boolean isConstructor(String name)
  {
    return getConstructor(name) != null;
  }

  @Override
  public IValueSpecifier getConstructor(String name)
  {
    IValueSpecifier con = constructors.get(name);
    if (con == null && outer != null)
      con = outer.getConstructor(name);
    return con;
  }

  @Override
  public void declareConstructor(ConstructorSpecifier con)
  {
    String name = con.getLabel();
    constructors.put(name, con);
    Location loc = con.getLoc();
    declareVar(name, new VarInfo(new Variable(loc, con.getConType(), name), AccessMode.readOnly, true));
  }

  @Override
  public void defineTypeAlias(Location loc, ITypeAlias alias)
  {
    definedTypes.put(alias.getName(), alias);
  }

  @Override
  public TypeContract getContract(String name)
  {
    TypeContract con = contracts.get(name);
    if (con == null && outer != null)
      con = outer.getContract(name);
    return con;
  }

  @Override
  public void defineTypeContract(TypeContract contract)
  {
    contracts.put(contract.getName(), contract);
  }

  @Override
  public Map<String, TypeContract> allContracts()
  {
    throw new UnsupportedOperationException("not implemented!");
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    Dict dict = this;

    while (dict != null) {
      int mark = disp.markIndent(2);
      disp.append("{\n");

      if (!dict.names.isEmpty()) {
        int m2 = disp.markIndent(2);
        disp.append("names{\n");
        for (Entry<String, DictInfo> entry : dict.names.entrySet()) {
          entry.getValue().prettyPrint(disp);
          disp.append("\n");
        }
        disp.popIndent(m2);
        disp.append("}\n");
      }

      if (!dict.definedTypes.isEmpty()) {
        disp.append("types{\n");
        int m2 = disp.markIndent(2);
        for (Entry<String, ITypeDescription> entry : dict.definedTypes.entrySet()) {
          entry.getValue().prettyPrint(disp);
          disp.append("\n");
        }
        disp.popIndent(m2);
        disp.append("}\n");
      }
      if (!dict.contracts.isEmpty()) {
        int m2 = disp.markIndent(2);
        disp.append("contracts{\n");
        for (Entry<String, TypeContract> entry : dict.contracts.entrySet()) {
          entry.getValue().prettyPrint(disp);
          disp.append("\n");
        }
        disp.popIndent(m2);
        disp.append("}\n");
      }

      if (!dict.implementations.isEmpty()) {
        disp.append("implementations{\n");
        int m2 = disp.markIndent(2);
        for (Entry<String, Set<ContractImplementation>> entry : dict.implementations.entrySet()) {
          for (ContractImplementation impl : entry.getValue()) {
            impl.prettyPrint(disp);
            disp.append("\n");
          }
        }
        disp.popIndent(m2);
        disp.append("}\n");
      }

      disp.popIndent(mark);
      disp.append("}\n");

      dict = dict.outer;
      if (dict != null)
        disp.append("===\n");
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public boolean isDeclaredVar(String name)
  {
    return names.containsKey(name);
  }

  @Override
  public boolean isDefinedVar(String name)
  {
    return getVar(name) != null;
  }

  @Override
  public DictInfo getVar(String name)
  {
    DictInfo con = names.get(name);
    if (con == null && outer != null)
      con = outer.getVar(name);
    return con;
  }

  @Override
  public IType getVarType(String name)
  {
    DictInfo var = getVar(name);
    if (var != null)
      return var.getType();
    return null;
  }

  @Override
  public DictInfo varReference(String name)
  {
    DictInfo info = names.get(name);

    if (info == null && outer != null) {
      info = outer.varReference(name);

      if (info != null) {
        declareVar(name, info); // may not be the same name as the original
        freeVars.add(info.getVariable());
        return info;
      }
    }

    return info;
  }

  @Override
  public void declareVar(String name, DictInfo var)
  {
    names.put(name, var);
  }

  @Override
  public void declareVar(String name, Variable var, AccessMode access, Visibility visibility, boolean initialized)
  {
    declareVar(name, new VarInfo(var, access, initialized));
  }

  @Override
  public Iterator<DictInfo> iterator()
  {
    return new Iterator<DictInfo>() {
      Dict dict = Dict.this;
      Iterator<DictInfo> it = dict.names.values().iterator();

      {
        probe();
      }

      private void probe()
      {
        while (it != null && !it.hasNext()) {
          dict = dict.outer;
          if (dict != null)
            it = dict.names.values().iterator();
          else
            it = null;
        }
      }

      @Override
      public boolean hasNext()
      {
        return it != null && it.hasNext();
      }

      @Override
      public DictInfo next()
      {
        DictInfo next = it.next();
        probe();
        return next;
      }

      @Override
      public void remove()
      {
        throw new UnsupportedOperationException("not permitted");
      }
    };
  }

  @Override
  public boolean isFreeVar(Variable var)
  {
    return freeVars.contains(var);
  }

  @Override
  public Variable[] getFreeVars()
  {
    return freeVars.toArray(new Variable[freeVars.size()]);
  }

  @Override
  public void declareImplementation(Variable var, String contractName, boolean isDefault)
  {
    Set<ContractImplementation> conImpls = implementations.get(contractName);
    if (conImpls == null) {
      conImpls = new HashSet<>();
      implementations.put(contractName, conImpls);
    }
    conImpls.add(new ContractImplementation(contractName, var, isDefault));
  }

  @Override
  public Map<String, Set<ContractImplementation>> allImplementations()
  {
    return implementations;
  }

  @Override
  public Dict fork()
  {
    return new Dict(this);
  }

  @Override
  public boolean isLocallyDeclared(String name, Dictionary limit)
  {
    DictInfo var = names.get(name);
    if (var == null && outer != null && outer != limit)
      return outer.isLocallyDeclared(name, limit);

    return var != null;
  }

  @Override
  public boolean isTypeVarInScope(final TypeVar var)
  {
    Dict dict = this;

    while (dict != null) {
      for (Entry<String, DictInfo> entry : dict.names.entrySet()) {
        if(entry.getValue().isTypeVarInScope(var))
          return true;
      }
      for (Entry<String, ITypeDescription> entry : dict.definedTypes.entrySet()) {
        ITypeDescription desc = entry.getValue();
        if (desc instanceof TypeExists) {
          if (OccursCheck.occursIn(desc.getType(), var))
            return true;
        }
      }
      dict = dict.outer;
    }

    return false;
  }

  @Override
  public Dictionary outerDict()
  {
    return outer;
  }
}
