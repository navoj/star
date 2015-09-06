package org.star_lang.star.data.type;

import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;

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
public class Kind implements PrettyPrintable
{
  public enum Mode {
    unknown, type, typefunction
  }

  private final Mode mode;

  public static Kind unknown = new Kind(Mode.unknown);
  public static Kind type = new Kind(Mode.type);

  private final int arity;

  private Kind(int arity)
  {
    this.arity = arity;
    this.mode = Mode.typefunction;
  }

  private Kind(Mode mode)
  {
    this.arity = 0;
    this.mode = mode;
  }

  public Mode mode()
  {
    return mode;
  }

  public int arity()
  {
    return arity;
  }

  public boolean checkKind(Kind other)
  {
    switch (mode) {
    case unknown:
      return true;
    case type:
      switch (other.mode) {
      case typefunction:
        return false;
      case unknown:
      case type:
      default:
        return true;
      }
    case typefunction:
      switch (other.mode) {
      case unknown:
        return true;
      case typefunction:
      default:
        return other.arity() == arity;
      case type:
        return false;
      }
    default:
      return false;
    }
  }

  public boolean check(int arity)
  {
    switch (mode) {
    case unknown:
      return true;
    case type:
      return arity == 0;
    case typefunction:
    default:
      return arity == this.arity;
    }
  }

  @Override
  public int hashCode()
  {
    return mode.name().hashCode() * 37 + arity;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Kind) {
      Kind k = (Kind) obj;
      return k.mode == mode && k.arity == arity;
    }
    return false;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    switch (mode) {
    case type:
      disp.appendWord(StandardNames.TYPE);
      return;
    case unknown:
      disp.appendWord("unknown kind");
      return;
    case typefunction:
      switch (arity) {
      case 0:
        disp.appendWord(StandardNames.TYPE);
        return;
      case 1:
        disp.appendWord(StandardNames.TYPE);
        disp.appendWord(StandardNames.OF);
        disp.appendWord(StandardNames.TYPE);
        return;
      default:
        disp.appendWord(StandardNames.TYPE);
        disp.appendWord(StandardNames.OF);
        disp.append("(");
        String sep = "";
        for (int ix = 0; ix < arity; ix++) {
          disp.append(sep);
          sep = ",";
          disp.appendWord(StandardNames.TYPE);
        }
        disp.append(")");
      }
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  public static Kind kind(int arity)
  {
    if (arity == 0)
      return type;
    else
      return new Kind(arity);
  }
}
