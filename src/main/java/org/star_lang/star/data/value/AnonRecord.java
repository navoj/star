package org.star_lang.star.data.value;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.EvaluationException;
import org.star_lang.star.data.IRecord;
import org.star_lang.star.data.IValue;
import org.star_lang.star.data.IValueVisitor;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.TypeInterfaceType;
import org.star_lang.star.operators.string.runtime.ValueDisplay;

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
public class AnonRecord implements IRecord, PrettyPrintable
{
  private final String label;
  private final IValue[] els;
  private final String[] fields;
  private final int size;
  private Map<String, Integer> index;

  public static final String ELS = "els"; // IMPORTANT: the value of this field must equal the NAME
                                          // of the element array els

  protected AnonRecord(String label, String[] fields, IValue[] els)
  {
    this.label = label;
    this.fields = fields;
    this.size = fields.length;
    this.els = els;
  }

  @Override
  public String getLabel()
  {
    return label;
  }

  @Override
  public int size()
  {
    return size;
  }

  @Override
  public int conIx()
  {
    return 0;
  }

  @Override
  public IValue getCell(int index)
  {
    return els[index];
  }

  @Override
  public IValue[] getCells()
  {
    return els;
  }

  @Override
  public void setCell(int index, IValue value) throws EvaluationException
  {
    els[index] = value;
  }

  @Override
  public IType getType()
  {
    SortedMap<String, IType> fieldTypes = new TreeMap<>();
    for (int ix = 0; ix < size; ix++)
      fieldTypes.put(fields[ix], els[ix].getType());
    return new TypeInterfaceType(fieldTypes);
  }

  @Override
  public void accept(IValueVisitor visitor)
  {
    visitor.visitRecord(this);
  }

  @Override
  public IValue getMember(String memberName)
  {
    if (index == null)
      buildIndex();
    Integer ix = index.get(memberName);
    if (ix == null)
      throw new IllegalArgumentException("member " + memberName + " not present in " + this);
    else
      return els[ix];
  }

  @Override
  public void setMember(String memberName, IValue value) throws EvaluationException
  {
    if (index == null)
      buildIndex();
    Integer ix = index.get(memberName);
    if (ix == null)
      throw new IllegalArgumentException("member " + memberName + " not present");
    else
      els[ix] = value;
  }

  private synchronized void buildIndex()
  {
    if (index == null) {
      Map<String, Integer> index = new HashMap<>();
      for (int ix = 0; ix < size; ix++)
        index.put(fields[ix], ix);
      this.index = index;
    }
  }

  @Override
  public String[] getMembers()
  {
    return fields;
  }

  @Override
  public IRecord copy() throws EvaluationException
  {
    IValue[] copyEls = new IValue[size];
    for (int ix = 0; ix < size; ix++)
      copyEls[ix] = els[ix].copy();

    return new AnonRecord(label, fields, copyEls);
  }

  @Override
  public IRecord shallowCopy() throws EvaluationException
  {
    IValue[] copyEls = new IValue[size];
    for (int ix = 0; ix < size; ix++)
      copyEls[ix] = els[ix];

    return new AnonRecord(label, fields, copyEls);
  }

  @Override
  public int hashCode()
  {
    int hash = label.hashCode();
    for (int ix = 0; ix < size; ix++)
      hash = hash * 37 + els[ix].hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof AnonRecord) {
      AnonRecord other = (AnonRecord) obj;
      if (other.size == size && other.label.equals(label)) {
        for (int ix = 0; ix < size; ix++)
          if (!other.fields[ix].equals(fields[ix]) || !other.els[ix].equals(els[ix]))
            return false;
        return true;
      }
    }
    return false;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    ValueDisplay.display(disp, this);
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

}
