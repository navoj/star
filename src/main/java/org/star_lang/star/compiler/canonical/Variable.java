package org.star_lang.star.compiler.canonical;

import static org.star_lang.star.data.type.Location.merge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.cafe.compile.Utils;
import org.star_lang.star.compiler.operator.StandardNames;
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
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.Quantifier;
import org.star_lang.star.data.type.TypeConstraintException;

/**
 * The Variable references a local variable. A variable that is part of a content expression. It can
 * denote either an expression or a pattern.
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
        Subsume.subsume(f.left, TypeUtils.referenceType(expectedType), loc, dict);

        if (checkForRaw)
          checkForRawBindings(loc, f.right);

      } catch (TypeConstraintException e) {
        errors.reportError(StringUtils.msg(getName(), " has type ", getType(), "\nwhich is not consistent with ",
            expectedType, "\nbecause ", e.getWords()), merge(loc, e.getLocs()));
      }
      return new Shriek(loc, new Variable(loc, TypeUtils.referenceType(expectedType), getName()));
    } else {
      try {
        Subsume.subsume(f.left, expectedType, loc, dict);

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
