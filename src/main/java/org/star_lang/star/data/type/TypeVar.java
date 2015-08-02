package org.star_lang.star.data.type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.DisplayType;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.FixedList;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.NullIterator;
import org.star_lang.star.compiler.util.Pair;

/**
 * A type variable is a type expression that denotes an arbitrary type. A free type variable in a
 * type expression denotes an arbitrary type -- meaning that the type variable may be replaced by
 * another type in a systematic fashion. A type variable that is bound -- defined by -- a quantified
 * type expression has semantics depending on the form of the quantifier: a UniversalType denotes a
 * universally quantified type expression.
 * 
 * Every type variable has a name -- used mostly in displaying the variable -- a list of type
 * contracts that it depends on; and a potential binding for the type variable.
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
public class TypeVar implements IType, Iterable<ITypeConstraint>
{
  private IType value;
  private Location bindLocation;
  private AccessMode access;
  private int version = 0;
  private List<Pair<ITypeConstraint, Integer>> constraints;
  private final String originalName;
  private final String name;

  /**
   * Construct a type variable with an explicit name, read-only status and list of required type
   * contracts.
   * 
   * @param varname
   *          the name of the variable.
   * @param access
   *          if true, then not permitted to bind the variable. Generally set to true only for
   *          variables that are also bound in a quantified type expression.
   * @param constraints
   *          the type constraints for the type variable. This list is used when attempting to bind
   *          the type variable to a type expression: if the type in the type expression does not
   *          satisfy the required constraints then the binding cannot be permitted.
   */
  public TypeVar(String varname, String originalName, AccessMode access, ITypeConstraint... constraints)
  {
    assert varname != null;
    this.name = varname;
    this.value = null;
    this.constraints = new ArrayList<>();
    for (ITypeConstraint constraint : constraints)
      this.constraints.add(Pair.pair(constraint, version));
    this.access = access;
    this.originalName = originalName;
  }

  /**
   * Construct a type variable with an explicit name, read-only status and list of required type
   * contracts.
   * 
   * @param varname
   *          the name of the variable.
   * @param access
   *          if true, then not permitted to bind the variable. Generally set to true only for
   *          variables that are also bound in a quantified type expression.
   * @param constraints
   *          the type constraints for the type variable. This list is used when attempting to bind
   *          the type variable to a type expression: if the type in the type expression does not
   *          satisfy the required constraints then the binding cannot be permitted.
   */
  public TypeVar(String varname, AccessMode access, ITypeConstraint... constraints)
  {
    this(varname, varname, access, constraints);
  }

  /**
   * Create a new type variable with an automatically generated name and with no contract
   * dependencies.
   */
  public TypeVar()
  {
    this(GenSym.genSym("_"), AccessMode.readWrite);
  }

  public TypeVar(String name)
  {
    this(name, AccessMode.readWrite);
  }

  public String getVarName()
  {
    return typeLabel();
  }

  public String getOriginalName()
  {
    return originalName;
  }

  @Override
  public Kind kind()
  {
    if (value == null) {
      for (Pair<ITypeConstraint, Integer> entry : constraints) {
        ITypeConstraint con = entry.getKey();
        if (con instanceof HasKind) {
          HasKind hasKind = (HasKind) con;
          return hasKind.getKind();
        }
      }
      return Kind.unknown;
    } else
      return value.kind();
  }

  public int typeArity()
  {
    IType tp = deRef();
    if (tp instanceof TypeVar) {
      TypeVar v = (TypeVar) tp;
      for (Pair<ITypeConstraint, Integer> entry : v.constraints) {
        ITypeConstraint con = entry.left;
        if (con instanceof HasKind)
          return ((HasKind) con).getKind().arity();
      }
    }
    return -1;
  }

  public boolean canbeTypeFunction(int arity)
  {
    IType tp = deRef();
    if (tp instanceof TypeVar) {
      TypeVar v = (TypeVar) tp;
      for (Pair<ITypeConstraint, Integer> con : v.constraints) {
        if (con.left instanceof HasKind)
          return ((HasKind) con.left).getKind().arity() == arity;
      }
    }
    return true;
  }

  public boolean checkKind(Kind kind)
  {
    if (kind.mode() != Kind.Mode.unknown)
      switch (kind().mode()) {
      case unknown:
        setConstraint(new HasKind(this, kind));
        return true;
      default:
        return kind.equals(kind());
      }
    else
      return true;
  }

  @Override
  public String typeLabel()
  {
    if (value == null)
      return name;
    else
      return value.typeLabel();
  }

  public Location getBindingLocation()
  {
    return bindLocation;
  }

  public IType getBoundValue()
  {
    return value;
  }

  public boolean isBound()
  {
    return value != null;
  }

  public IType deRef()
  {
    if (value == null)
      return this;
    else {
      IType t = value;
      while (t instanceof TypeVar) {
        TypeVar tv = (TypeVar) t;
        assert tv != this;
        if (tv.value == null)
          return t;
        t = tv.value;
      }
      return t;
    }
  }

  @Override
  public <C> void accept(ITypeVisitor<C> visitor, C cxt)
  {
    IType t = deRef();

    if (t instanceof TypeVar)
      visitor.visitTypeVar((TypeVar) t, cxt);
    else
      t.accept(visitor, cxt);
  }

  @Override
  public <T, C, X> T transform(TypeTransformer<T, C, X> trans, X cxt)
  {
    if (value == null)
      return trans.transformTypeVar(this, cxt);
    else
      return value.transform(trans, cxt);
  }

  public boolean isReadOnly()
  {
    return access == AccessMode.readOnly;
  }

  public void markReadOnly()
  {
    this.access = AccessMode.readOnly;
  }

  public AccessMode getAccess()
  {
    return access;
  }

  /**
   * Attempt to bind the type variable to another type expression. This will only be permitted if
   * (a) the type variable is not itself referenced within the type expression and (b) any contracts
   * that this type variable requires are known to be implemented by the type expression being bound
   * to this type variable.
   * 
   * It is possible (common) to bind a type variable to another type variable. In this case, the
   * type contracts of the binding type variable are merged in with the existing type contract
   * dependencies.
   * 
   * @param type
   *          the type expression that this type variable is being bound to.
   * @param loc
   *          the source location that gives rise to this attempt to bind the variable
   * @param cxt
   *          the type context of the binding operation. This context is used to establish which
   *          type contracts the binding type has.
   * @param allow
   *          whether type constraints may be added to the type variable
   * @return the state to which one might wish to reset should you need to undo this binding
   * @throws TypeConstraintException
   *           is thrown if the binding operation is not permitted.
   */
  public int bind(IType type, Location loc, Dictionary cxt, boolean allow) throws TypeConstraintException
  {
    type = TypeUtils.deRef(type);

    if (!type.equals(this)) {
      if (isReadOnly() && !allow)
        throw new TypeConstraintException(FixedList.create(this, " cannot be constrained to ", type, "\nbecause ",
            this, " is read only"), loc);

      try {
        int state = this.version++;
        value = type; // Actually bind the variable. To enable good constraint checking
        bindLocation = loc;
        checkConstraints(type, loc, cxt, allow); // This will throw an exception if
        // something goes wrong.
        return state;
      } catch (TypeConstraintException e) {
        value = null; // undo the binding
        bindLocation = null;
        throw e;
      }
    }
    return version;
  }

  public void reset(int state)
  {
    value = null;
    bindLocation = null;
    if (constraints != null)
      for (Iterator<Pair<ITypeConstraint, Integer>> it = constraints.iterator(); it.hasNext();) {
        Entry<ITypeConstraint, Integer> entry = it.next();
        if (entry.getValue() > state)
          it.remove();
      }
    this.version = state;
  }

  public void addContractRequirement(TypeExp contract, Location loc, Dictionary dict) throws TypeConstraintException
  {
    addConstraint(new ContractConstraint(contract), false, loc, dict);
  }

  public void addConstraint(ITypeConstraint con, boolean allow, Location loc, Dictionary dict)
      throws TypeConstraintException
  {
    assert getBoundValue() == null;

    for (ITypeConstraint c : this) {
      if (c.sameConstraint(con, loc, dict))
        return;
    }

    if (access == AccessMode.readOnly && !allow)
      throw new TypeConstraintException(FixedList.create("cannot further constrain ", this, " with ", con), loc);

    appendConstraint(con);
  }

  public void addConstraint(ITypeConstraint con, Location loc, Dictionary dict) throws TypeConstraintException
  {
    addConstraint(con, false, loc, dict);
  }

  public void setConstraint(ITypeConstraint con)
  {
    assert getBoundValue() == null;
    if (constraints != null)
      for (Pair<ITypeConstraint, Integer> entry : constraints)
        if (entry.left.equals(con))
          return;
    appendConstraint(con);
  }

  private void appendConstraint(ITypeConstraint con)
  {
    if (constraints == null)
      constraints = new ArrayList<>();
    constraints.add(Pair.pair(con, version++));
  }

  @Override
  public Iterator<ITypeConstraint> iterator()
  {
    if (constraints != null)
      return new Iterator<ITypeConstraint>() {
        Iterator<Pair<ITypeConstraint, Integer>> it = constraints.iterator();

        @Override
        public boolean hasNext()
        {
          return it.hasNext();
        }

        @Override
        public ITypeConstraint next()
        {
          return it.next().left;
        }

        @Override
        public void remove()
        {
          it.remove();
        }
      };
    else
      return new NullIterator<>();
  }

  public void clearConstraints()
  {
    if (constraints != null)
      constraints.clear();
  }

  public boolean hasConstraints()
  {
    return constraints != null && !constraints.isEmpty();
  }

  public int numConstraints()
  {
    if (constraints == null)
      return 0;
    else
      return constraints.size();
  }

  public boolean hasContractContraint(String contract)
  {
    for (Pair<ITypeConstraint, Integer> constraint : constraints) {
      if (constraint.left instanceof ContractConstraint
          && ((ContractConstraint) constraint.left).getContractName().equals(contract))
        return true;
    }
    return false;
  }

  private void checkConstraints(IType candidate, Location loc, Dictionary dict, boolean allow)
      throws TypeConstraintException
  {
    if (constraints != null) {
      if (candidate instanceof TypeVar) {
        TypeVar var = (TypeVar) candidate;

        assert var.value == null;

        if (var.constraints != null) {
          ConLoop: for (ITypeConstraint con : this) {
            for (ITypeConstraint oCon : var) {
              if (con.sameConstraint(oCon, loc, dict))
                continue ConLoop; // Will exit in a TypeConstraintException if
              // it is valid but false
            }
            var.addConstraint(con, allow, loc, dict);
          }
        } else {
          for (Pair<ITypeConstraint, Integer> con : constraints)
            var.addConstraint(con.left, allow, loc, dict);
        }
      } else if (candidate instanceof TypeExp && TypeUtils.isTypeVar(((TypeExp) candidate).getTypeCon())) {
        TypeVar var = (TypeVar) TypeUtils.deRef(((TypeExp) candidate).getTypeCon());
        if (var.constraints != null) {
          ConLoop: for (Pair<ITypeConstraint, Integer> con : constraints) {
            ITypeConstraint leftCon = con.left;
            for (Pair<ITypeConstraint, Integer> oCon : var.constraints) {
              if (leftCon.sameConstraint(oCon.left, loc, dict))
                continue ConLoop; // Will exit in a TypeConstraintException if
              // it is valid but false
            }
            var.addConstraint(leftCon, allow, loc, dict);
          }
        } else {
          for (Pair<ITypeConstraint, Integer> con : constraints)
            var.addConstraint(con.left, allow, loc, dict);
        }
      } else {
        for (Pair<ITypeConstraint, Integer> con : constraints) {
          try {
            con.left.checkBinding(candidate, loc, dict);
          } catch (TypeConstraintException e) {
            value = null;
            throw e;
          }
        }
      }
    }
  }

  @Override
  public boolean equals(Object obj)
  {
    if (value != null)
      return value.equals(obj);
    if (obj == null)
      return false;
    if (this == obj)
      return true;

    if (obj instanceof TypeVar) {
      TypeVar var = (TypeVar) obj;
      if (var.value != null)
        return equals(var.value);
      else
        return var.typeLabel().equals(typeLabel());
    } else
      return false;
  }

  @Override
  public int hashCode()
  {
    IType type = this.deRef();

    if (type instanceof TypeVar) {
      TypeVar tVar = (TypeVar) type;
      return tVar.getVarName().hashCode() * 49;
    } else
      return type.hashCode();
  }

  @Override
  public String toString()
  {
    return DisplayType.toString(this);
  }

  public static TypeVar var(String name, AccessMode access)
  {
    return new TypeVar(name, access);
  }

  public static TypeVar var(String name, int arity, AccessMode access)
  {
    TypeVar var = new TypeVar(name, access);
    var.setConstraint(new HasKind(var, Kind.kind(arity)));
    return var;
  }

  public static TypeVar var(String name, Kind kind, AccessMode access)
  {
    TypeVar var = new TypeVar(name, access);
    if (kind != Kind.unknown)
      var.setConstraint(new HasKind(var, kind));
    return var;
  }

  public static TypeVar var(Kind kind, AccessMode access)
  {
    TypeVar var = new TypeVar(GenSym.genSym("_"), access);
    if (kind != Kind.unknown)
      var.setConstraint(new HasKind(var, kind));
    return var;
  }
}
