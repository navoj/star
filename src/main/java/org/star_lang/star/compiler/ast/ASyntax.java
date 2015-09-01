package org.star_lang.star.compiler.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.star_lang.star.compiler.type.TypeUtils;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IConstructor;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

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
public abstract class ASyntax implements IAbstract
{
  private Location loc;
  private Map<String, IAttribute> attributes;
  private List<String> categories;

  public static final IType type = TypeUtils.typeExp(StandardTypes.QUOTED);

  protected static final int nameIx = 0;
  protected static final int boolIx = 1;
  protected static final int charIx = 2;
  protected static final int stringIx = 3;
  protected static final int intIx = 4;
  protected static final int longIx = 5;
  protected static final int floatIx = 6;
  protected static final int decimalIx = 7;
  protected static final int applyIx = 8;

  protected ASyntax(Location loc)
  {
    this.loc = loc;
    assert loc != null;
  }

  protected ASyntax(Location loc, Map<String, IAttribute> attributes)
  {
    this.loc = loc;
    assert loc != null;
    this.attributes = new HashMap<>();
    if (attributes != null)
      this.attributes.putAll(attributes);

    this.categories = new ArrayList<>();
  }

  protected ASyntax(Location loc, List<String> categories, Map<String, IAttribute> attributes)
  {
    this.loc = loc;
    assert loc != null;
    this.attributes = new HashMap<>();
    if (attributes != null)
      this.attributes.putAll(attributes);
    this.categories = new ArrayList<>();
    if (categories != null)
      this.categories.addAll(categories);
  }

  @Override
  public final IType getType()
  {
    return type;
  }

  @Override
  public Location getLoc()
  {
    return loc;
  }

  @Override
  public void setLoc(Location loc)
  {
    this.loc = loc;
  }

  @Override
  public IAttribute getAttribute(String att)
  {
    if (attributes == null)
      return null;
    else
      return attributes.get(att);
  }

  @Override
  public boolean hasAttribute(String att)
  {
    return attributes != null && attributes.containsKey(att);
  }

  @Override
  public IAttribute setAttribute(String att, IAttribute attribute)
  {
    if (attributes == null)
      attributes = new HashMap<>();

    IAttribute old = attributes.get(att);
    attributes.put(att, attribute);
    return old;
  }

  @Override
  public Map<String, IAttribute> getAttributes()
  {
    return attributes;
  }

  @Override
  public List<String> getCategories()
  {
    return categories;
  }

  @Override
  public void setCategory(String category)
  {
    if (categories == null)
      categories = new ArrayList<>();
    categories.add(category);
  }

  @Override
  public boolean isCategory(String category)
  {
    if (categories == null)
      return false;
    else
      return categories.contains(category);
  }

  @Override
  public void setCell(int index, IValue value) throws EvaluationException
  {
    throw new IllegalArgumentException("not permitted");
  }

  public IValue get___0()
  {
    return loc;
  }

  @Override
  public IConstructor copy() throws EvaluationException
  {
    return shallowCopy();
  }

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitConstructor(this);
  }

  public abstract boolean isTernaryOperator(String op);

  public abstract boolean isBinaryOperator(String op);

  public abstract boolean isUnaryOperator(String op);

  public abstract boolean isApply(String op);

  public boolean isIdentifier(String name)
  {
    return false;
  }

  @Override
  public String toString()
  {
    return DisplayAst.display(this);
  }

}
