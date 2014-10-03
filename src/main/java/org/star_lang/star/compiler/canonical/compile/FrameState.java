package org.star_lang.star.compiler.canonical.compile;

import java.util.Map.Entry;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FrameNode;
import org.star_lang.star.compiler.cafe.compile.HWM;
import org.star_lang.star.compiler.cafe.compile.ISpec;
import org.star_lang.star.compiler.cafe.compile.LiveMap;
import org.star_lang.star.compiler.cafe.compile.SrcSpec;
import org.star_lang.star.compiler.util.ConsList;
import org.star_lang.star.data.indextree.Fold;
import org.star_lang.star.data.indextree.Mapping;

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
public class FrameState
{
  private final HWM stackHwm;
  private final ConsList<ISpec> stackMap;

  private final LiveMap localAvail;
  private final Mapping<Integer, ISpec> localMap;

  public FrameState(HWM stackHwm, LiveMap localAvail, ConsList<ISpec> stackMap, Mapping<Integer, ISpec> localMap)
  {
    super();
    this.stackHwm = stackHwm;
    this.localAvail = localAvail;
    this.stackMap = stackMap;
    this.localMap = localMap;
  }

  public ConsList<ISpec> getStackMap()
  {
    return stackMap;
  }

  public Mapping<Integer, ISpec> getLocalMap()
  {
    return localMap;
  }

  public HWM getStackHwm()
  {
    return stackHwm;
  }

  public LiveMap getLocalAvail()
  {
    return localAvail;
  }

  public FrameState pushStack(ISpec var)
  {
    stackHwm.bump(var.slotSize());
    return new FrameState(stackHwm, localAvail, stackMap.cons(var), localMap);
  }

  public FrameState dropStack(int count)
  {
    stackHwm.bump(-count);
    return new FrameState(stackHwm, localAvail, stackMap.tail(), localMap);
  }

  public FrameState pushLocal(ISpec var)
  {
    int offset = localAvail.reserve(var.slotSize());
    return new FrameState(stackHwm, localAvail, stackMap, localMap.insrt(offset, var));
  }

  public ISpec tos()
  {
    if (!stackMap.isNil())
      return stackMap.head();
    else
      return null;
  }

  public FrameState merge(FrameState other)
  {
    assert other.stackMap.length() == stackMap.length();
    return new FrameState(stackHwm, localAvail, merge(stackMap, other.stackMap), merge(localMap, other.localMap));
  }

  private Mapping<Integer, ISpec> merge(Mapping<Integer, ISpec> left, Mapping<Integer, ISpec> right)
  {
    Mapping<Integer, ISpec> result = left;
    for (Entry<Integer, ISpec> entry : left) {
      if (!right.contains(entry.getKey()))
        result = result.delete(entry.getKey());
    }
    for (Entry<Integer, ISpec> entry : right)
      if (!left.contains(entry.getKey()))
        result = result.delete(entry.getKey());
    return result;
  }

  private static ConsList<ISpec> merge(ConsList<ISpec> left, ConsList<ISpec> right)
  {
    if (left.isNil()) {
      assert right.isNil();
      return left;
    } else {
      ConsList<ISpec> tl = merge(left.tail(), right.tail());
      return tl.cons(merge(left.head(), right.head()));
    }
  }

  private static ISpec merge(ISpec left, ISpec right)
  {
    if (left.getJavaType().equals(right.getJavaType()))
      return left;
    else
      return SrcSpec.voidSrc;
  }

  public FrameNode synthesizeFrame(FrameState prior)
  {
    int stackPrefix = 0;
    int framePrefix = 0;
    ConsList<ISpec> stackState = stackMap.reverse();
    ConsList<ISpec> priorStack = prior.stackMap.reverse();
    int stackSize = stackMap.length();
    Object stackData[] = new Object[stackSize];

    while (!stackState.isNil() && !priorStack.isNil()) {
      String javaType = stackState.head().getJavaSig();
      if (javaType.equals(priorStack.head().getJavaSig())) {
        stackData[stackPrefix] = stackState.head().getFrameCode();
        stackPrefix++;
      }
      stackState = stackState.tail();
      priorStack = priorStack.tail();
    }

    int sx = stackPrefix;
    while (!stackState.isNil()) {
      stackData[sx++] = stackState.head().getFrameCode();
      stackState = stackState.tail();
    }

    Fold<Entry<Integer, ISpec>, Integer> maxFold = new Fold<Entry<Integer, ISpec>, Integer>() {
      @Override
      public Integer apply(Entry<Integer, ISpec> entry, Integer soFar)
      {
        if (entry.getKey() > soFar)
          return entry.getKey();
        else
          return soFar;
      }
    };

    Integer maxLx = localMap.fold(maxFold, 0);
    Object frame[] = new Object[maxLx];
    for (Entry<Integer, ISpec> entry : localMap) {
      int ix = entry.getKey();
      frame[ix] = entry.getValue().getFrameCode();
    }
    for (int ix = 0; ix < maxLx; ix++)
      if (frame[ix] == null)
        frame[ix] = Opcodes.TOP;

    Integer priorLx = prior.localMap.fold(maxFold, 0);
    Object priorFrame[] = new Object[priorLx];

    for (Entry<Integer, ISpec> entry : prior.localMap) {
      int ix = entry.getKey();
      priorFrame[ix] = entry.getValue().getFrameCode();
    }
    for (int ix = 0; ix < priorLx; ix++)
      if (priorFrame[ix] == null)
        priorFrame[ix] = Opcodes.TOP;

    for (int ix = 0; ix < Math.min(priorLx, maxLx); ix++)
      if (frame[ix].equals(priorFrame[ix]))
        framePrefix = ix;
      else
        break;

    if (stackPrefix == stackSize && stackPrefix == prior.stackMap.length() && maxLx == priorLx && maxLx.equals(framePrefix)) {
      if (framePrefix > 0 || stackPrefix > 0)
        return new FrameNode(Opcodes.F_SAME, maxLx, frame, stackData.length, stackData);
      else
        return new FrameNode(Opcodes.F_NEW, maxLx, frame, stackData.length, stackData);
    } else
      return new FrameNode(Opcodes.F_FULL, maxLx, frame, stackData.length, stackData);
  }
}
