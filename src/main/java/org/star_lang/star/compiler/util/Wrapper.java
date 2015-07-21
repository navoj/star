package org.star_lang.star.compiler.util;

/*
 * To force a way around a restriction in Java's anonymous classes
 *
 * @param <T>

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
public class Wrapper<T> implements PrettyPrintable
{
  private T el;

  public Wrapper(T el)
  {
    this.set(el);
  }

  public static <T> Wrapper<T> create(T el)
  {
    return new Wrapper<>(el);
  }

  public void set(T el)
  {
    this.el = el;
  }

  public T get()
  {
    return el;
  }

  public boolean isEmpty()
  {
    return el == null;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append("{");
    if (el instanceof PrettyPrintable) {
      ((PrettyPrintable) el).prettyPrint(disp);
    } else if (el != null)
      disp.append(el.toString());
    else
      disp.append("null");
    disp.append("}");
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }
}
