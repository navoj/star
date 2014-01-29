package org.star_lang.star.compiler.canonical;

import static com.starview.platform.data.type.Location.merge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.transform.Over;
import org.star_lang.star.compiler.type.Dictionary;
import org.star_lang.star.compiler.type.Freshen;
import org.star_lang.star.compiler.type.Subsume;
import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.StringUtils;

import com.starview.platform.data.type.IType;
import com.starview.platform.data.type.Location;
import com.starview.platform.data.type.Quantifier;
import com.starview.platform.data.type.TypeConstraintException;

/**
 * The Variable references a local variable. A variable that is part of a content expression. It can
 * denote either an expression or a pattern.
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
public class Variable extends BaseExpression implements IContentPattern, Comparable<Variable>
{
  private final String name;

  public Variable(Location loc, IType type, String name)
  {
    super(loc, type);
    this.name = name;
  }

  public static Variable create(Location loc, IType type, String name)
  {
    if (TypeUtils.hasContractDependencies(type) && !TypeUtils.isConstructorType(TypeUtils.unwrap(type)))
      return new OverloadedVariable(loc, type, Over.computeDictionaryType(type, loc, AccessMode.readOnly), name);
    else
      return new Variable(loc, type, name);
  }

  // create a new variable with a different type but everything else the same.
  public Variable copy()
  {
    return new Variable(getLoc(), getType(), GenSym.genSym(getName()));
  }

  // opposite of an overload
  public Variable underLoad()
  {
    return this;
  }

  public String getName()
  {
    return name;
  }

  public IContentExpression verifyType(Location loc, ErrorReport errors, IType expectedType, Dictionary dict,
      boolean checkForRaw)
  {
    Pair<IType, Map<String, Quantifier>> f = Freshen.freshen(getType(), AccessMode.readOnly, AccessMode.readWrite);
    if (TypeUtils.isReferenceType(f.left) && !TypeUtils.isReferenceType(expectedType)) {
      try {
        Subsume.subsume(TypeUtils.referenceType(expectedType), f.left, loc, dict);

        if (checkForRaw)
          checkForRawBindings(loc, f.right);

      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg(getName(), " has type ", getType(), "\nwhich is not consistent with ",
            expectedType, "\nbecause ", e.getWords()), merge(loc, e.getLocs()));
      }
      return new Shriek(loc, new Variable(loc, TypeUtils.referenceType(expectedType), getName()));
    } else {
      try {
        Subsume.subsume(expectedType, f.left, loc, dict);

        if (checkForRaw)
          checkForRawBindings(loc, f.right);

      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg(getName(), " has type ", getType(), "\nwhich is not consistent with ",
            expectedType, "\nbecause ", e.getWords()), merge(loc, e.getLocs()));
      }
      return new Variable(loc, expectedType, getName());
    }
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.appendId(name);
  }

  @Override
  public void accept(CanonicalVisitor visitor)
  {
    visitor.visitVariable(this);
  }

  @Override
  public <A, E, P, C, D, T> E transform(TransformExpression<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformVariable(this, context);
  }

  @Override
  public <A, E, P, C, D, T> P transformPattern(TransformPattern<A, E, P, C, D, T> transform, T context)
  {
    return transform.transformVariablePtn(this, context);
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj instanceof Variable && ((Variable) obj).getName().equals(getName()) && ((Variable) obj).isRealVariable();
  }

  @Override
  public int hashCode()
  {
    return getName().hashCode();
  }

  @Override
  public int compareTo(Variable o)
  {
    return getName().compareTo(o.getName());
  }

  public static Variable anonymous(Location loc, IType type)
  {
    return new Variable(loc, type, GenSym.genSym(StandardNames.ANONYMOUS_PREFIX));
  }

  public static boolean isAnonymous(Variable var)
  {
    String label = var.getName();
    return Utils.isAnonymous(label);
  }

  public boolean isRealVariable()
  {
    return true;
  }

  public static List<String> varNames(Collection<Variable> defined)
  {
    List<String> names = new ArrayList<>(defined.size());
    for (Variable v : defined)
      names.add(v.getName());
    return names;
  }

  protected void checkForRawBindings(Location loc, Map<String, Quantifier> bound) throws TypeConstraintException
  {
    for (Entry<String, Quantifier> e : bound.entrySet()) {
      IType tp = TypeUtils.deRef(e.getValue().getVar());

      if (TypeUtils.isRawType(tp))
        throw new TypeConstraintException(StringUtils.msg("not permitted to constrain ", e.getKey(), " with raw type ",
            tp), loc);
    }
  }
}
