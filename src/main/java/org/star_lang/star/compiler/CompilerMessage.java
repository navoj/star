package org.star_lang.star.compiler;

import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.Location;

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
public class CompilerMessage implements PrettyPrintable
{
  private final MsgType msgType;
  private final String msg;
  private final Location[] locs;

  public CompilerMessage(MsgType msgType, String msg, Location... locs)
  {
    assert msgType != null && msg != null && locs != null;
    this.msgType = msgType;
    this.msg = msg;
    this.locs = locs;
  }

  public MsgType getMsgType()
  {
    return msgType;
  }

  public String getMsg()
  {
    return msg;
  }

  public Location[] getLocs()
  {
    return locs;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof CompilerMessage) {
      CompilerMessage other = (CompilerMessage) obj;
      if (other.msg.equals(msg) && other.locs.length == locs.length) {
        for (int ix = 0; ix < locs.length; ix++)
          if (!locs[ix].equals(other.locs[ix]))
            return false;
        return true;
      }
    }
    return false;
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp)
  {
    disp.append(msgType.name());
    disp.append(": ");
    disp.append(msg);
    for (Location loc : locs) {
      disp.append("\n at ");
      loc.prettyPrint(disp);
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  @Override
  public int hashCode()
  {
    int hash = 37 * msg.hashCode();
    for (Location loc : locs)
      hash = hash * 37 + loc.hashCode();
    return hash;
  }
}
