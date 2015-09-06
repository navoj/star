package org.star_lang.star.compiler.wff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

/**
 * Compile a macro rule into a sequence of operations
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

public class WffCompile
{
  private Map<String, Integer> dict = new HashMap<>();
  private int varCount = 0;

  public WffCompile()
  {
  }

  public WffRule compileRule(IAbstract rule, WffProgram ruleSet, ErrorReport errors)
  {
    if (Abstract.isUnary(rule, StandardNames.META_HASH))
      return compileRule(Abstract.getArg(rule, 0), ruleSet, errors);
    if (Abstract.isBinary(rule, StandardNames.WFF_RULE)) {
      Apply rl = ((Apply) rule);

      dict.clear();

      final IAbstract ptnArg = Abstract.getArg(rl, 0);

      if (Abstract.isBinary(ptnArg, StandardNames.WFF_DEFINES)) {
        Apply head = (Apply) ptnArg;
        String category = identifierOf(Abstract.getArg(head, 1));
        if (category == null) {
          errors.reportError("missing category", head.getLoc());
          return null;
        } else {
          WffOp ptnOp = compilePattern(Abstract.getArg(ptnArg, 0), errors);

          return new WffRule(rl.getLoc(), dict.size(), category, ptnOp, compileBody(Abstract.getArg(rl, 1), ruleSet,
              errors));
        }
      } else {
        errors.reportError("invalid head of validation rule", ptnArg.getLoc());
        return null;
      }
    } else if (Abstract.isBinary(rule, StandardNames.WFF_DEFINES)) {
      Apply rl = ((Apply) rule);

      dict.clear();

      if (Abstract.isBinary(rl, StandardNames.WFF_DEFINES)) {
        String category = identifierOf(Abstract.getArg(rl, 1));
        if (category == null)
          errors.reportError("missing category", rl.getLoc());
        else {
          WffOp ptnOp = compilePattern(Abstract.getArg(rl, 0), errors);

          return new WffRule(rl.getLoc(), dict.size(), category, ptnOp, new WffPtnNull());
        }
      } else
        errors.reportError("invalid validation rule", rl.getLoc());
    } else
      errors.reportError("not properly formed validation rule", rule.getLoc());
    return null;
  }

  private WffOp compilePattern(IAbstract ptn, ErrorReport errors)
  {
    if (ptn instanceof Apply) {
      Apply app = (Apply) ptn;

      if (Abstract.isUnary(app, StandardNames.QUESTION)) {
        String var = identifierOf(Abstract.getArg(app, 0));
        int offset;

        if (var != null) {
          if (!dict.containsKey(var)) {
            offset = varCount++;
            dict.put(var, offset);
          } else
            offset = dict.get(var);
          return new WffVarPtn(offset);
        }
        errors.reportError("missing variable", ptn.getLoc());
        return new WffPtnNull();
      } else if (Abstract.isBinary(app, StandardNames.QUESTION) && Abstract.getArg(app, 0) instanceof Name) {
        IAbstract other = Abstract.getArg(app, 0);
        String var = identifierOf(Abstract.getArg(app, 1));
        int offset;

        if (var != null) {
          if (!dict.containsKey(var)) {
            offset = varCount++;
            dict.put(var, offset);
          } else
            offset = dict.get(var);
          return new WffVarPtn(offset, compilePattern(other, errors));
        }
        errors.reportError("missing variable", ptn.getLoc());
        return new WffPtnNull();
      } else if (Abstract.isBinary(app, StandardNames.MACRO_APPLY)) {
        WffOp opPtn = compilePattern(Abstract.getArg(app, 0), errors);
        WffOp argPtn = compilePattern(Abstract.getArg(app, 1), errors);

        return new WffApplyApplyPtn(opPtn, argPtn);
      } else {
        WffOp opPtn = compilePattern(app.getOperator(), errors);
        IList args = app.getArgs();
        WffOp ops[] = new WffOp[args.size()];
        for (int ix = 0; ix < args.size(); ix++)
          ops[ix] = compilePattern((IAbstract) args.getCell(ix), errors);

        return new WffApplyPtn(opPtn, ops);
      }
    } else if (Abstract.isTupleTerm(ptn)) {
      IList tuple = Abstract.tupleArgs(ptn);
      WffOp ops[] = new WffOp[tuple.size()];
      for (int ix = 0; ix < tuple.size(); ix++)
        ops[ix] = compilePattern((IAbstract) tuple.getCell(ix), errors);

      return new WffTuplePtn(ops);
    } else if (ptn instanceof Name) {
      String sym = ((Name) ptn).getId();

      if (sym.equals(StandardTypes.CHAR))
        return new WffCharPtn();
      else if (sym.equals(StandardTypes.STRING))
        return new WffStringPtn();
      else if (sym.equals(StandardNames.WFF_REGEXP))
        return new WffRegexpPtn();
      else if (sym.equals(StandardTypes.INTEGER))
        return new WffIntegerPtn();
      else if (sym.equals(StandardNames.NUMBER))
        return new WffNumberOp();
      else if (sym.equals(StandardNames.IDENTIFIER))
        return new WffIdentifierPtn();
      else if (sym.equals(StandardNames.WFF_SYMBOL))
        return new WffSymbolPtn();
      else if (sym.equals(StandardNames.WFF_KEYWORD))
        return new WffKeywordPtn();
      else if (sym.equals(StandardNames.WFF_TUPLE))
        return new WffTuple();
      else
        return new WffLitPtn(ptn);
    } else
      return new WffLitPtn(ptn);
  }

  private WffCond compileBody(IAbstract bdy, WffProgram ruleSet, ErrorReport errors)
  {
    if (bdy instanceof Apply) {
      Apply body = ((Apply) bdy);

      if (Abstract.isBinary(body, StandardNames.WFF_DEFINES)) {
        IAbstract category = Abstract.binaryRhs(body);
        WffBuildOp tgt = compileBuilder(Abstract.binaryLhs(body), errors);

        if (Abstract.isIdentifier(category)) {
          String categoryId = Abstract.getId(category);

          final WffOp ptn;

          switch (categoryId) {
          case StandardNames.WFF_KEYWORD:
            ptn = new WffKeywordPtn();
            break;
          case StandardNames.WFF_IDENTIFIER:
            ptn = new WffIdentifierPtn();
            break;
          case StandardNames.WFF_SYMBOL:
            ptn = new WffSymbolPtn();
            break;
          case "integer":
            ptn = new WffIntegerPtn();
            break;
          case "string":
            ptn = new WffStringPtn();
            break;
          default:
            ptn = null;
          }

          if (ptn != null)
            return new WffPrimCategory(body.getLoc(), tgt, ptn);
          else
            return new WffCategory(body.getLoc(), tgt, categoryId);
        } else {
          errors.reportError(category + " should be an identifier", bdy.getLoc());
          return new WffPtnNull();
        }
      } else if (Abstract.isBinary(body, StandardNames.WFF_STAR)) {
        IAbstract category = Abstract.getArg(body, 1);
        IAbstract ptn = Abstract.getArg(body, 0);
        if (Abstract.isUnary(ptn, StandardNames.QUESTION)) {
          String var = identifierOf(((Apply) ptn).getArg(0));
          if (var == null)
            errors.reportError("missing variable name", bdy.getLoc());
          else {
            if (dict.containsKey(var)) {
              return new WffStarPtn(dict.get(var), identifierOf(category));
            } else
              errors.reportError("validation variable " + var + " not defined in lhs", ptn.getLoc());
          }
          return new WffPtnNull();
        } else if (ptn instanceof Name) {
          String var = identifierOf(ptn);
          if (var == null)
            errors.reportError("missing validation variable name", bdy.getLoc());
          else {
            if (dict.containsKey(var)) {
              return new WffStarPtn(dict.get(var), identifierOf(category));
            } else
              errors.reportError("validation variable " + var + " not defined in lhs", ptn.getLoc());
          }
        } else
          errors.reportError("lhs must be a variable spec", bdy.getLoc());
      } else if (Abstract.isBinary(body, StandardNames.WFF_TERM)) {
        IAbstract category = Abstract.getArg(body, 1);
        IAbstract ptn = Abstract.getArg(body, 0);
        if (Abstract.isUnary(ptn, StandardNames.QUESTION)) {
          String var = identifierOf(((Apply) ptn).getArg(0));
          if (var == null)
            errors.reportError("missing variable name", bdy.getLoc());
          else {
            if (dict.containsKey(var)) {
              return new WffSequencePtn(dict.get(var), StandardNames.TERM, identifierOf(category));
            } else
              errors.reportError("validation variable " + var + " not defined in lhs", ptn.getLoc());
          }
          return new WffPtnNull();
        } else if (ptn instanceof Name) {
          String var = identifierOf(ptn);
          if (var == null)
            errors.reportError("missing validation variable name", bdy.getLoc());
          else {
            if (dict.containsKey(var)) {
              return new WffSequencePtn(dict.get(var), StandardNames.TERM, identifierOf(category));
            } else
              errors.reportError("validation variable " + var + " not defined in lhs", ptn.getLoc());
          }
        } else
          errors.reportError("lhs must be a variable spec", bdy.getLoc());
      } else if (Abstract.isBinary(body, StandardNames.WFF_RULES)) {
        IAbstract category = Abstract.getArg(body, 1);
        IAbstract ptn = Abstract.getArg(body, 0);
        if (Abstract.isUnary(ptn, StandardNames.QUESTION)) {
          String var = identifierOf(((Apply) ptn).getArg(0));
          if (var == null)
            errors.reportError("missing variable name", bdy.getLoc());
          else {
            if (dict.containsKey(var)) {
              return new WffSequencePtn(dict.get(var), StandardNames.PIPE, identifierOf(category));
            } else
              errors.reportError("validation variable " + var + " not defined in lhs", ptn.getLoc());
          }
          return new WffPtnNull();
        } else if (ptn instanceof Name) {
          String var = identifierOf(ptn);
          if (var == null)
            errors.reportError("missing validation variable name", bdy.getLoc());
          else {
            if (dict.containsKey(var)) {
              return new WffSequencePtn(dict.get(var), StandardNames.PIPE, identifierOf(category));
            } else
              errors.reportError("validation variable " + var + " not defined in lhs", ptn.getLoc());
          }
        } else
          errors.reportError("lhs must be a variable spec", bdy.getLoc());
      } else if (Abstract.isBinary(body, StandardNames.WFF_AND)) {
        IAbstract left = Abstract.getArg(body, 0);
        IAbstract right = Abstract.getArg(body, 1);
        return new WffAndCnd(compileBody(left, ruleSet, errors), compileBody(right, ruleSet, errors));
      } else if (Abstract.isBinary(body, StandardNames.WFF_OR)) {
        IAbstract left = Abstract.getArg(body, 0);
        IAbstract right = Abstract.getArg(body, 1);
        return new WffOrCnd(compileBody(left, ruleSet, errors), compileBody(right, ruleSet, errors));
      } else if (Abstract.isUnary(body, StandardNames.WFF_NOT)) {
        IAbstract right = Abstract.getArg(body, 0);
        return new WffNotCnd(compileBody(right, ruleSet, errors));
      } else if (Abstract.isBinary(body, StandardNames.MACRO_WHERE)
          && Abstract.isUnary(Abstract.getArg(body, 1), StandardNames.BRACES)) {
        WffProgram subRules = new WffProgram();
        for (IAbstract rule : CompilerUtils.unWrap(Abstract.argPath(body, 1, 0), StandardNames.TERM))
          subRules.defineValidationRule(new WffCompile().compileRule(rule, subRules, errors));

        return new WffWhere(body.getLoc(), compileBody(Abstract.getArg(body, 0), ruleSet, errors), subRules);
      } else if (Abstract.isUnary(body, StandardNames.ERROR)) {
        IAbstract rgt = Abstract.getArg(body, 0);

        return new WffError(compileBuilder(rgt, errors));
      } else if (Abstract.isUnary(body, StandardNames.WARNING)) {
        IAbstract rgt = Abstract.getArg(body, 0);

        return new WffWarning(compileBuilder(rgt, errors));
      } else if (Abstract.isUnary(body, StandardNames.INFO)) {
        IAbstract rgt = Abstract.getArg(body, 0);

        return new WffInfo(compileBuilder(rgt, errors));
      } else if (Abstract.isParenTerm(body))
        return compileBody(Abstract.getArg(body, 0), ruleSet, errors);
      else
        errors.reportError("invalid validation condition", body.getLoc());
    } else
      errors.reportError("invalid validation condition", bdy.getLoc());
    return new WffPtnNull();
  }

  private WffBuildOp compileBuilder(IAbstract term, ErrorReport errors)
  {
    Location loc = term.getLoc();

    if (Abstract.isUnary(term, StandardNames.QUESTION)) {
      String var = identifierOf(((Apply) term).getArg(0));
      if (var == null) {
        errors.reportError("missing variable name", loc);
        return new WffLiteral(term);
      } else {
        if (dict.containsKey(var))
          return new WffVar(dict.get(var), var);
        else {
          errors.reportError("validation variable " + var + " not defined in lhs", loc);
          return new WffLiteral(term);
        }
      }
    } else if (term instanceof Name) {
      String var = identifierOf(term);
      if (var == null) {
        errors.reportError("missing validation variable name", loc);
        return new WffLiteral(term);
      } else if (dict.containsKey(var))
        return new WffVar(dict.get(var), var);
      else
        return new WffLiteral(term);
    } else if (Abstract.isBinary(term, StandardNames.STRING_CATENATE)) {
      List<WffBuildOp> elements = new ArrayList<>();

      while (Abstract.isBinary(term, StandardNames.STRING_CATENATE)) {
        elements.add(compileBuilder(Abstract.getArg(term, 0), errors));
        term = Abstract.getArg(term, 1);
      }
      elements.add(compileBuilder(term, errors));
      return new WffString(elements);
    } else if (Abstract.isUnary(term, StandardNames.DISPLAY)) {
      return compileBuilder(Abstract.getArg(term, 0), errors);
    } else if (Abstract.isBinary(term, StandardNames.MACRO_APPLY)) {
      return new WffApplyApply(compileBuilder(Abstract.getArg(term, 0), errors), compileBuilder(Abstract
          .getArg(term, 1), errors));
    } else if (term instanceof Apply) {
      Apply apply = (Apply) term;
      IList tpl = apply.getArgs();
      WffBuildOp tplOps[] = new WffBuildOp[tpl.size()];
      for (int ix = 0; ix < tpl.size(); ix++)
        tplOps[ix] = compileBuilder((IAbstract) tpl.getCell(ix), errors);

      return new WffApply(compileBuilder(apply.getOperator(), errors), tplOps);
    } else
      return new WffLiteral(term);
  }

  private static String identifierOf(IAbstract trm)
  {
    if (trm instanceof Name)
      return ((Name) trm).getId();
    else
      return null;
  }
}
