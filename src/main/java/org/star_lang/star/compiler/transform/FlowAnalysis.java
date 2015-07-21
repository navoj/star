package org.star_lang.star.compiler.transform;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.canonical.CanonicalVisitor;
import org.star_lang.star.compiler.canonical.ConditionCondition;
import org.star_lang.star.compiler.canonical.Conjunction;
import org.star_lang.star.compiler.canonical.DefaultVisitor;
import org.star_lang.star.compiler.canonical.Disjunction;
import org.star_lang.star.compiler.canonical.FalseCondition;
import org.star_lang.star.compiler.canonical.FunctionLiteral;
import org.star_lang.star.compiler.canonical.ICondition;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.IContentPattern;
import org.star_lang.star.compiler.canonical.Implies;
import org.star_lang.star.compiler.canonical.IsTrue;
import org.star_lang.star.compiler.canonical.ListSearch;
import org.star_lang.star.compiler.canonical.Matches;
import org.star_lang.star.compiler.canonical.Negation;
import org.star_lang.star.compiler.canonical.Otherwise;
import org.star_lang.star.compiler.canonical.PatternAbstraction;
import org.star_lang.star.compiler.canonical.PatternApplication;
import org.star_lang.star.compiler.canonical.Search;
import org.star_lang.star.compiler.canonical.TrueCondition;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.canonical.WherePattern;
import org.star_lang.star.compiler.transform.VarAnalysis.VarChecker;
import org.star_lang.star.compiler.util.TopologySort;
import org.star_lang.star.compiler.util.Wrapper;
import org.star_lang.star.compiler.util.TopologySort.IDefinition;

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
public class FlowAnalysis
{

  private static IContentPattern extractPtn(IContentPattern ptn, Wrapper<ICondition> extra)
  {
    while (ptn instanceof WherePattern) {
      WherePattern where = (WherePattern) ptn;
      CompilerUtils.extendCondition(extra, where.getCond());
      ptn = where.getPtn();
    }
    return ptn;
  }

  private static void mergeDefined(List<Variable> leftDefined, List<Variable> rightDefined, List<Variable> defined,
                                   List<Variable> required)
  {
    for (Variable left : leftDefined) {
      if (rightDefined.contains(left)) {
        if (!defined.contains(left))
          defined.add(left);
      } else if (!required.contains(left)) // If it is not in both, then put it in
        // required?
        required.add(left);
    }
  }

  private static void analyseExp(IContentExpression exp, final List<Variable> predDefined, final List<Variable> predRequired,
                                 final List<Variable> definedVars, final List<Variable> candidates)
  {
    final CanonicalVisitor analyser = new ExpressionAnalyser(predRequired, candidates);

    exp.accept(analyser);
  }

  private static void analysePtn(IContentPattern ptn, final List<Variable> predDefined, final List<Variable> predRequired,
                                 final List<Variable> definedVars, final List<Variable> candidates)
  {
    final CanonicalVisitor analyser = new DefaultVisitor(true) {
      @Override
      public void visitVariable(Variable var)
      {
        if (candidates.contains(var)) {
          if (!predDefined.contains(var))
            predDefined.add(var);
        }
      }

      @Override
      public void visitWherePattern(WherePattern where)
      {
        analyseCondition(where.getCond(), definedVars, predDefined, predRequired, candidates);
        analysePtn(where.getPtn(), predDefined, predRequired, definedVars, candidates);
      }

      @Override
      public void visitPatternApplication(PatternApplication apply)
      {
        analyseExp(apply.getAbstraction(), predDefined, predRequired, definedVars, candidates);
        analysePtn(apply.getArg(), predDefined, predRequired, definedVars, candidates);
      }
    };

    ptn.accept(analyser);
  }

  private static class ExpressionAnalyser extends DefaultVisitor
  {
    final List<Variable> predRequired;
    final List<Variable> candidates;

    private ExpressionAnalyser(List<Variable> predRequired, List<Variable> candidates)
    {
      super(true);
      this.predRequired = predRequired;
      this.candidates = candidates;
    }

    @Override
    public void visitVariable(Variable var)
    {
      if (candidates.contains(var) && !predRequired.contains(var))
        predRequired.add(var);
    }

    @Override
    public void visitPatternAbstraction(PatternAbstraction pattern)
    {
      for (Variable fr : pattern.getFreeVars())
        fr.accept(this);
    }

    @Override
    public void visitFunctionLiteral(FunctionLiteral f)
    {
      for (Variable fr : f.getFreeVars())
        fr.accept(this);
    }
  }

  public static void analyseCondition(ICondition qTerm, List<Variable> definedVars, List<Variable> defined,
      List<Variable> required, List<Variable> candidates)
  {
    if (qTerm instanceof Search) {
      Search pred = (Search) qTerm;

      analysePtn(pred.getPtn(), defined, required, definedVars, candidates);
      analyseExp(pred.getSource(), defined, required, definedVars, candidates);
    } else if (qTerm instanceof ListSearch) {
      ListSearch search = (ListSearch) qTerm;
      analysePtn(search.getPtn(), defined, required, definedVars, candidates);
      analysePtn(search.getIx(), defined, required, definedVars, candidates);
      analyseExp(search.getSource(), defined, required, definedVars, candidates);
    } else if (qTerm instanceof Matches) {
      Matches test = (Matches) qTerm;

      analyseExp(test.getExp(), defined, required, definedVars, candidates);
      analysePtn(test.getPtn(), defined, required, definedVars, candidates);
    } else if (qTerm instanceof Conjunction) {
      Conjunction conj = (Conjunction) qTerm;
      analyseCondition(conj.getLhs(), definedVars, defined, required, candidates);
      analyseCondition(conj.getRhs(), definedVars, defined, required, candidates);
    } else if (qTerm instanceof Disjunction) {
      Disjunction disj = (Disjunction) qTerm;
      List<Variable> leftDefined = new ArrayList<>();
      List<Variable> rightDefined = new ArrayList<>();

      analyseCondition(disj.getLhs(), definedVars, leftDefined, required, candidates);
      analyseCondition(disj.getRhs(), definedVars, rightDefined, required, candidates);

      mergeDefined(leftDefined, rightDefined, defined, required);
    } else if (qTerm instanceof Negation) {
      Negation neg = (Negation) qTerm;
      List<Variable> rightDefined = new ArrayList<>();

      analyseCondition(neg.getNegated(), definedVars, rightDefined, required, candidates);
    } else if (qTerm instanceof Otherwise) {
      Otherwise other = (Otherwise) qTerm;
      List<Variable> leftDefined = new ArrayList<>();
      List<Variable> rightDefined = new ArrayList<>();

      analyseCondition(other.getLhs(), definedVars, leftDefined, required, candidates);
      analyseCondition(other.getRhs(), definedVars, rightDefined, required, candidates);

      mergeDefined(leftDefined, rightDefined, defined, required);
    } else if (qTerm instanceof ConditionCondition) {
      ConditionCondition cond = (ConditionCondition) qTerm;
      analyseCondition(cond.getTest(), definedVars, defined, required, candidates);
      List<Variable> leftDefined = new ArrayList<>();
      List<Variable> rightDefined = new ArrayList<>();

      analyseCondition(cond.getLhs(), definedVars, leftDefined, required, candidates);
      analyseCondition(cond.getRhs(), definedVars, rightDefined, required, candidates);

      mergeDefined(leftDefined, rightDefined, defined, required);
    } else if (qTerm instanceof IsTrue)
      analyseExp(((IsTrue) qTerm).getExp(), defined, required, definedVars, candidates);
    else if (qTerm instanceof TrueCondition)
      ;
    else if (qTerm instanceof FalseCondition)
      ;
    else if (qTerm instanceof Implies) {
      Implies implies = (Implies) qTerm;
      List<Variable> rightDefined = new ArrayList<>();

      analyseCondition(implies.getGenerate(), definedVars, rightDefined, required, candidates);
      analyseCondition(implies.getTest(), definedVars, rightDefined, required, candidates);
    } else
      assert false : "cannot deal with condition";
  }

  private static FlowInfo analyseQTerm(ICondition qTerm, List<Variable> definedVars, List<Variable> candidates)
  {
    List<Variable> defined = new ArrayList<>();
    List<Variable> required = new ArrayList<>();

    analyseCondition(qTerm, definedVars, defined, required, candidates);
    return new FlowInfo(qTerm, required, defined);
  }

  /**
   * Move equality conditions into the pattern of a search condition. THis makes it easier for the
   * later stages to generate indexing structures.
   * 
   * @param conjuncts
   * @param defined
   */
  private static void mergeEqualities(List<ICondition> conjuncts, List<Variable> defined)
  {
    boolean done = false;

    doneLoop: while (!done) {
      done = true;

      for (int ix = 0; ix < conjuncts.size(); ix++) {
        ICondition cond = conjuncts.get(ix);
        Wrapper<ICondition> extra = Wrapper.create(CompilerUtils.truth);

        if (cond instanceof Search) {
          Search pred = (Search) cond;

          List<Variable> newlyDefined = new ArrayList<>(defined);

          IContentPattern ptn = extractPtn(pred.getPtn(), extra);
          VarAnalysis.findDefinedVars(ptn, newlyDefined);

          searchLoop: for (int jx = 0; jx < conjuncts.size();) {
            ICondition test = conjuncts.get(jx);
            if (CompilerUtils.isEquality(test)) {
              IContentExpression[] args = Indexing.equalArgs(test);
              for (IContentExpression arg : args) {
                if (!VarAnalysis.allDefined(arg, newlyDefined)) {
                  jx++;
                  continue searchLoop;
                }
              }
              conjuncts.remove(jx);
              if (jx < ix)
                ix--; // twiddle the outer index, because we have removed a
              // prior entry
              CompilerUtils.extendCondition(extra, test);
              done = false;
              continue;
            }
            jx++;
          }
          if (!done) {
            conjuncts.set(ix, new Search(pred.getLoc(), new WherePattern(pred.getLoc(), ptn, extra.get()), pred
                .getSource()));
            continue doneLoop;
          }
        } else if (cond instanceof ListSearch) {
          ListSearch pred = (ListSearch) cond;
          List<Variable> newlyDefined = new ArrayList<>(defined);

          IContentPattern ptn = extractPtn(pred.getPtn(), extra);
          VarAnalysis.findDefinedVars(ptn, newlyDefined);

          searchLoop: for (int jx = 0; jx < conjuncts.size();) {
            ICondition test = conjuncts.get(jx);
            if (CompilerUtils.isEquality(test)) {
              if (!VarAnalysis.allDefined(CompilerUtils.equalityLhs(test), newlyDefined)) {
                jx++;
                continue;
              }
              if (!VarAnalysis.allDefined(CompilerUtils.equalityRhs(test), newlyDefined)) {
                jx++;
                continue;
              }

              conjuncts.remove(jx);
              if (jx < ix)
                ix--; // twiddle the outer index, because we have removed a
              // prior entry
              CompilerUtils.extendCondition(extra, test);
              done = false;
              continue;
            }
            jx++;
          }
          if (!done) {
            conjuncts.set(ix, new ListSearch(pred.getLoc(), new WherePattern(pred.getLoc(), ptn, extra.get()), pred
                .getIx(), pred.getSource()));
            continue doneLoop;
          }
        }
      }
    }
  }

  private static void deConj(ICondition qury, List<ICondition> conjuncts)
  {
    if (qury instanceof Conjunction) {
      Conjunction conj = (Conjunction) qury;
      deConj(conj.getLhs(), conjuncts);
      deConj(conj.getRhs(), conjuncts);
    } else
      conjuncts.add(qury);
  }

  private static List<FlowInfo> parseConjuncts(ICondition query, List<Variable> definedVars, List<Variable> candidates)
  {
    List<ICondition> conjuncts = new ArrayList<>();
    deConj(query, conjuncts);

    mergeEqualities(conjuncts, definedVars);

    List<FlowInfo> flow = new ArrayList<>();
    for (ICondition qTerm : conjuncts)
      flow.add(analyseQTerm(qTerm, definedVars, candidates));

    return flow;
  }

  private static List<FlowInfo> sortConjuncts(List<FlowInfo> conjuncts)
  {
    if (conjuncts.isEmpty() || conjuncts.size() == 1)
      return conjuncts;
    else {
      List<List<IDefinition<Variable>>> groups = TopologySort.sort(conjuncts);

      List<FlowInfo> sorted = new ArrayList<>();

      for (List<IDefinition<Variable>> group : groups) {
        for (IDefinition<Variable> item : group) {
          sorted.add((FlowInfo) item);
        }
      }
      return sorted;
    }
  }

  public static ICondition analyseFlow(ICondition query, List<Variable> freeVars, VarChecker checker)
  {
    List<Variable> candidates = VarAnalysis.findDefinedVars(query, checker);

    List<FlowInfo> conjuncts = parseConjuncts(query, freeVars, candidates);

    conjuncts = sortConjuncts(conjuncts);

    ICondition result = conjuncts.get(conjuncts.size() - 1).term;
    for (int ix = conjuncts.size() - 2; ix >= 0; ix--)
      result = new Conjunction(query.getLoc(), conjuncts.get(ix).term, result);
    return result;
  }
}
