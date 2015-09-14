package org.star_lang.star.compiler.canonical;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.star_lang.star.compiler.canonical.CharSet.AnyChar;
import org.star_lang.star.compiler.util.ArrayIterator;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.data.type.Location;

/**
 * Regular expressions are converted to a non-deterministic Finite Automaton form
 *
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
public abstract class NFA implements PrettyPrintable
{
  public static final String floatRegexp = "[-+]?\\d+[.]\\d+([eE][-+]?\\d+)?";
  private final Location loc;
  private int stateNo = -1;

  private NFA(Location loc, int stateNo)
  {
    this.loc = loc;
    this.stateNo = stateNo;
  }

  private NFA(Location loc)
  {
    this.loc = loc;
  }

  public abstract NFAKind nfaKind();

  public abstract boolean isNullable();

  private static int[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
  private static int[] white = { ' ', '\t', '\n', '\f', '\r' };
  private static final int[] word;

  static {
    word = new int[26 + 26 + 10];
    int ix = 0;
    for (int ch = 'a'; ch <= 'z'; ch++)
      word[ix++] = ch;
    for (int ch = 'A'; ch <= 'Z'; ch++)
      word[ix++] = ch;
    for (int ch = '0'; ch <= '9'; ch++)
      word[ix++] = ch;
  }

  public enum NFAKind {
    emptyNFA, charSetNFA, disjunctNFA, starNFA, seqNFA, bindNFA, boundNFA, varNFA, endVarNFA
  }

  public interface NFAVisitor
  {
    void visitEmptyNFA(EmptyNFA empty);

    void visitBindNFA(BindNFA bind);

    void visitBoundNFA(BoundNFA bound);

    void visitVarNFA(VarNFA var);

    void visitCharClassNFA(CharClassNFA charClass);

    void visitSequence(Sequence seq);

    void visitDisjunct(Disjunct disj);

    void visitStar(StarNFA star);
  }

  public abstract void accept(NFAVisitor visitor);

  public Location getLoc()
  {
    return loc;
  }

  public int getStateNo()
  {
    return stateNo;
  }

  public int setStateNo(int stateNo)
  {
    assert this.stateNo == -1;

    this.stateNo = stateNo;
    return stateNo;
  }

  public static class CharClassNFA extends NFA
  {
    private final boolean negated;
    private final CharSet chars;

    private CharClassNFA(Location loc, int stateNo, boolean negated, CharSet chars)
    {
      super(loc, stateNo);
      this.negated = negated;
      this.chars = chars;
    }

    public boolean isNegated()
    {
      return negated;
    }

    public CharSet getChars()
    {
      return chars;
    }

    @Override
    public NFAKind nfaKind()
    {
      return NFAKind.charSetNFA;
    }

    @Override
    public boolean isNullable()
    {
      return false;
    }

    @Override
    public void accept(NFAVisitor visitor)
    {
      visitor.visitCharClassNFA(this);
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof CharClassNFA) {
        CharClassNFA chars = (CharClassNFA) obj;
        return chars.negated == negated && chars.chars.equals(this.chars);
      }
      return false;
    }

    @Override
    public int hashCode()
    {
      return ((chars.hashCode() * 37) + (negated ? 1 : 0)) * 37 + "[]".hashCode();
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      showState(disp);
      if (chars instanceof AnyChar)
        disp.append(".");
      else {
        disp.append("[");
        if (negated)
          disp.appendChar('^');
        chars.prettyPrint(disp);
        disp.append("]");
      }
    }
  }

  public static class VarNFA extends NFA
  {
    private final Variable var;

    private VarNFA(Location loc, int stateNo, Variable var)
    {
      super(loc, stateNo);
      this.var = var;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      showState(disp);
      openState(disp);
      disp.append("$");
      disp.appendId(var.getName());
      closeState(disp);
    }

    @Override
    public NFAKind nfaKind()
    {
      return NFAKind.varNFA;
    }

    @Override
    public boolean isNullable()
    {
      return false;
    }

    @Override
    public void accept(NFAVisitor visitor)
    {
      visitor.visitVarNFA(this);
    }

    public Variable getVar()
    {
      return var;
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof VarNFA && ((VarNFA) obj).getVar().equals(var);
    }

    @Override
    public int hashCode()
    {
      return (var.hashCode() * 37) + "$".hashCode();
    }
  }

  public static class EmptyNFA extends NFA
  {
    private EmptyNFA(Location loc, int stateNo)
    {
      super(loc, stateNo);
    }

    @Override
    public NFAKind nfaKind()
    {
      return NFAKind.emptyNFA;
    }

    @Override
    public boolean isNullable()
    {
      return true;
    }

    @Override
    public void accept(NFAVisitor visitor)
    {
      visitor.visitEmptyNFA(this);
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof EmptyNFA;
    }

    @Override
    public int hashCode()
    {
      return "€".hashCode();
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      showState(disp);
      disp.append("€");
    }
  }

  public static class BindNFA extends NFA
  {
    private final Variable var;

    private BindNFA(Location loc, int stateNo, Variable var)
    {
      super(loc, stateNo);
      this.var = var;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      showState(disp);
      disp.append("^");
      var.prettyPrint(disp);
    }

    @Override
    public NFAKind nfaKind()
    {
      return NFAKind.bindNFA;
    }

    @Override
    public boolean isNullable()
    {
      return true;
    }

    @Override
    public void accept(NFAVisitor visitor)
    {
      visitor.visitBindNFA(this);
    }

    public Variable getVar()
    {
      return var;
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof BindNFA && ((BindNFA) obj).getVar().equals(getVar());
    }

    @Override
    public int hashCode()
    {
      return var.hashCode() * 37 + "^".hashCode();
    }
  }

  public static class BoundNFA extends NFA
  {
    private final Variable var;

    private BoundNFA(Location loc, int stateNo, Variable var)
    {
      super(loc, stateNo);
      this.var = var;
    }

    @Override
    public NFAKind nfaKind()
    {
      return NFAKind.boundNFA;
    }

    @Override
    public void accept(NFAVisitor visitor)
    {
      visitor.visitBoundNFA(this);
    }

    @Override
    public boolean isNullable()
    {
      return true;
    }

    public Variable getVar()
    {
      return var;
    }

    @Override
    public boolean equals(Object obj)
    {
      return obj instanceof BoundNFA && ((BoundNFA) obj).getVar().equals(getVar());
    }

    @Override
    public int hashCode()
    {
      return var.hashCode() * 37 + "$".hashCode();
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      showState(disp);
      disp.appendChar(':');
      var.prettyPrint(disp);
    }
  }

  public static class Sequence extends NFA implements Iterable<NFA>
  {
    private final NFA[] els;

    private Sequence(Location loc, int stateNo, NFA... els)
    {
      super(loc, stateNo);
      this.els = els;
      assert els.length > 1;
    }

    @Override
    public NFAKind nfaKind()
    {
      return NFAKind.seqNFA;
    }

    @Override
    public void accept(NFAVisitor visitor)
    {
      visitor.visitSequence(this);
    }

    public NFA getEl(int ix)
    {
      return els[ix];
    }

    public NFA[] getEls()
    {
      return els;
    }

    @Override
    public boolean isNullable()
    {
      for (NFA el : els)
        if (!el.isNullable())
          return false;
      return true;
    }

    @Override
    public Iterator<NFA> iterator()
    {
      return new ArrayIterator<>(els);
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof Sequence) {
        Sequence seq = (Sequence) obj;
        if (seq.els.length == els.length) {
          for (int ix = 0; ix < els.length; ix++)
            if (!els[ix].equals(seq.els[ix]))
              return false;
          return true;
        }
      }
      return false;
    }

    @Override
    public int hashCode()
    {
      int hash = ";".hashCode();
      for (NFA nfa : els)
        hash = hash * 37 + nfa.hashCode();
      return hash;
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      showState(disp);
      openState(disp);
      disp.prettyPrint(els, "; ");
      closeState(disp);
    }
  }

  public static class Disjunct extends NFA
  {
    private final NFA left;
    private final NFA right;

    private Disjunct(Location loc, int stateNo, NFA left, NFA right)
    {
      super(loc, stateNo);
      this.left = left;
      this.right = right;
    }

    @Override
    public NFAKind nfaKind()
    {
      return NFAKind.disjunctNFA;
    }

    public NFA getLeft()
    {
      return left;
    }

    public NFA getRight()
    {
      return right;
    }

    @Override
    public void accept(NFAVisitor visitor)
    {
      visitor.visitDisjunct(this);
    }

    @Override
    public boolean isNullable()
    {
      return left.isNullable() || right.isNullable();
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof Disjunct) {
        Disjunct dis = (Disjunct) obj;
        return dis.left.equals(left) && dis.right.equals(right);
      }
      return false;
    }

    @Override
    public int hashCode()
    {
      return (left.hashCode() * 37 + right.hashCode()) * 37 + "|".hashCode();
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      NFA nfa = this;
      showState(disp);
      openState(disp);
      while (nfa instanceof Disjunct) {
        Disjunct disjunct = (Disjunct) nfa;
        disjunct.left.prettyPrint(disp);
        disp.appendChar('|');
        nfa = disjunct.right;
      }
      nfa.prettyPrint(disp);
      closeState(disp);
    }
  }

  public static class StarNFA extends NFA
  {
    private final NFA repeat;

    private StarNFA(Location loc, int stateNo, NFA left)
    {
      super(loc, stateNo);
      this.repeat = left;
    }

    @Override
    public NFAKind nfaKind()
    {
      return NFAKind.starNFA;
    }

    public NFA getRepeat()
    {
      return repeat;
    }

    @Override
    public boolean isNullable()
    {
      return true;
    }

    @Override
    public void accept(NFAVisitor visitor)
    {
      visitor.visitStar(this);
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof StarNFA) {
        StarNFA star = (StarNFA) obj;
        return star.repeat.equals(repeat);
      }
      return false;
    }

    @Override
    public int hashCode()
    {
      return (repeat.hashCode() * 37) + "*".hashCode();
    }

    @Override
    public void prettyPrint(PrettyPrintDisplay disp)
    {
      showState(disp);
      repeat.prettyPrint(disp);
      disp.appendChar('*');
    }
  }

  protected void showState(PrettyPrintDisplay disp)
  {
    if (stateNo >= 0) {
      disp.appendWord(stateNo);
      disp.append(":");
    }
  }

  protected void openState(PrettyPrintDisplay disp)
  {
    if (stateNo >= 0) {
      disp.append("(");
    }
  }

  protected void closeState(PrettyPrintDisplay disp)
  {
    if (stateNo >= 0) {
      disp.append(")");
    }
  }

  @Override
  public String toString()
  {
    return PrettyPrintDisplay.toString(this);
  }

  // Factory methods
  public static NFA emptyNFA(Location loc, int stateNo)
  {
    return new EmptyNFA(loc, stateNo);
  }

  public static NFA emptyNFA(Location loc)
  {
    return emptyNFA(loc, -1);
  }

  public static NFA anyNFA(Location loc)
  {
    return anyNFA(loc, -1);
  }

  public static NFA anyNFA(Location loc, int stateNo)
  {
    return new CharClassNFA(loc, stateNo, false, new CharSet.AnyChar());
  }

  public static NFA charNFA(Location loc, boolean negated, CharSet chars)
  {
    return charNFA(loc, -1, negated, chars);
  }

  public static NFA charNFA(Location loc, int stateNo, boolean negated, CharSet chars)
  {
    return new CharClassNFA(loc, stateNo, negated, chars);
  }

  public static NFA charNFA(Location loc, int ch)
  {
    return charNFA(loc, -1, ch);
  }

  public static NFA charNFA(Location loc, int stateNo, int ch)
  {
    return new CharClassNFA(loc, stateNo, false, new CharSet.CharClass(new int[] { ch }));
  }

  public static NFA charClass(Location loc, boolean negated, int chars[])
  {
    return charClass(loc, -1, negated, chars);
  }

  public static NFA charClass(Location loc, int stateNo, boolean negated, int chars[])
  {
    return new CharClassNFA(loc, stateNo, negated, new CharSet.CharClass(chars));
  }

  public static NFA digit(Location loc, boolean negated)
  {
    return digit(loc, -1, negated);
  }

  public static NFA digit(Location loc, int stateNo, boolean negated)
  {
    return new CharClassNFA(loc, stateNo, negated, new CharSet.CharClass(digits));
  }

  public static NFA word(Location loc, boolean negated)
  {
    return word(loc, -1, negated);
  }

  public static NFA word(Location loc, int stateNo, boolean negated)
  {
    return new CharClassNFA(loc, stateNo, negated, new CharSet.CharClass(word));
  }

  public static NFA white(Location loc, boolean negated)
  {
    return white(loc, -1, negated);
  }

  public static NFA white(Location loc, int stateNo, boolean negated)
  {
    return new CharClassNFA(loc, stateNo, negated, new CharSet.CharClass(white));
  }

  public static NFA sequence(Location loc, NFA... els)
  {
    return sequence(loc, -1, els);
  }

  public static NFA sequence(Location loc, int stateNo, NFA... els)
  {
    return new Sequence(loc, stateNo, els);
  }

  public static NFA disjunct(Location loc, NFA left, NFA right)
  {
    return disjunct(loc, -1, left, right);
  }

  public static NFA disjunct(Location loc, int stateNo, NFA left, NFA right)
  {
    return new Disjunct(loc, stateNo, left, right);
  }

  public static NFA variable(Location loc, Variable var)
  {
    return variable(loc, -1, var);
  }

  public static NFA variable(Location loc, int stateNo, Variable var)
  {
    return new VarNFA(loc, stateNo, var);
  }

  public static NFA optional(Location loc, NFA left)
  {
    return optional(loc, -1, left);
  }

  public static NFA optional(Location loc, int stateNo, NFA left)
  {
    return new Disjunct(loc, stateNo, left, new EmptyNFA(loc, stateNo));
  }

  public static NFA star(Location loc, NFA left)
  {
    return star(loc, -1, left);
  }

  public static NFA star(Location loc, int stateNo, NFA left)
  {
    return new StarNFA(loc, stateNo, left);
  }

  public static NFA plus(Location loc, NFA left)
  {
    return plus(loc, -1, left);
  }

  public static NFA plus(Location loc, int stateNo, NFA left)
  {
    return new Sequence(loc, stateNo, left, new StarNFA(loc, stateNo, left));
  }

  public static NFA bound(Location loc, Variable var, NFA regexp)
  {
    return sequence(loc, new BindNFA(loc, -1, var), regexp, new BoundNFA(loc, -1, var));
  }

  @SuppressWarnings("unused")
  private static CharSet merge(CharSet left, CharSet right)
  {
    if (left instanceof CharSet.AnyChar)
      return left;
    else if (left instanceof CharSet.CharClass) {
      if (right instanceof CharSet.AnyChar)
        return right;
      else if (right instanceof CharSet.CharClass) {
        int leftChars[] = ((CharSet.CharClass) left).chars;
        int rightChars[] = ((CharSet.CharClass) right).chars;
        int concat[] = new int[leftChars.length + rightChars.length];
        int ix = 0;
        for (int ch : leftChars)
          concat[ix++] = ch;
        for (int ch : rightChars)
          concat[ix++] = ch;
        Arrays.sort(concat);
        return new CharSet.CharClass(elimDuplicates(concat));
      } else
        return new CharSet.CharUnion(left, right);

    } else if (left instanceof CharSet.CharUnion) {
      CharSet.CharUnion leftUnion = (CharSet.CharUnion) left;
      CharSet leftMerge = merge(leftUnion.getLeft(), right);
      CharSet rightMerge = merge(leftUnion.getRight(), right);
      if (!(leftMerge instanceof CharSet.CharUnion && rightMerge instanceof CharSet.CharUnion))
        return merge(leftMerge, rightMerge);
      else
        return new CharSet.CharUnion(leftMerge, rightMerge);
    } else
      return new CharSet.CharUnion(left, right);
  }

  private static int[] elimDuplicates(int[] concat)
  {
    int dupl = 0;

    {
      int ix = 0;
      while (ix < concat.length) {
        int mark = ix++;
        while (ix < concat.length && concat[ix] == concat[mark])
          ix++;
        dupl += ix - mark - 1;
      }
    }

    if (dupl > 0) {
      int result[] = new int[concat.length - dupl];
      int ix = 0;
      int tx = 0;
      while (ix < concat.length) {
        result[tx] = concat[ix++];

        while (ix < concat.length && concat[ix] == result[tx])
          ix++;

        tx++;
      }
      assert ix == concat.length && tx == result.length;
      return result;
    } else
      return concat; // no duplicates
  }

  public static int numberStates(NFA nfa, int firstStateNo, Map<Integer, NFA> states)
  {
    NumberStateVisitor visitor = new NumberStateVisitor(firstStateNo, states);
    nfa.accept(visitor);
    return visitor.stateNo;
  }

  private static class NumberStateVisitor implements NFAVisitor
  {
    private int stateNo;
    private final Map<Integer, NFA> states;

    NumberStateVisitor(int stateNo, Map<Integer, NFA> states)
    {
      this.stateNo = stateNo;
      this.states = states;
    }

    private void incState(NFA nfa)
    {
      nfa.setStateNo(stateNo++);
      states.put(nfa.getStateNo(), nfa);
    }

    @Override
    public void visitEmptyNFA(EmptyNFA empty)
    {
      incState(empty);
    }

    @Override
    public void visitBindNFA(BindNFA bind)
    {
      incState(bind);
    }

    @Override
    public void visitBoundNFA(BoundNFA bound)
    {
      incState(bound);
    }

    @Override
    public void visitVarNFA(VarNFA var)
    {
      incState(var);
    }

    @Override
    public void visitCharClassNFA(CharClassNFA charClass)
    {
      incState(charClass);
    }

    @Override
    public void visitSequence(Sequence seq)
    {
      for (NFA nfa : seq) {
        nfa.accept(this);
      }
      incState(seq);
    }

    @Override
    public void visitDisjunct(Disjunct disj)
    {
      disj.getLeft().accept(this);
      disj.getRight().accept(this);
      incState(disj);
    }

    @Override
    public void visitStar(StarNFA star)
    {
      star.getRepeat().accept(this);
      incState(star);
    }
  }
}