package org.star_lang.star.compiler.format.rules;

import java.util.HashMap;
import java.util.Map;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IAttribute;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.format.Absolute;
import org.star_lang.star.compiler.format.BooleanAttribute;
import org.star_lang.star.compiler.format.NumericAttribute;
import org.star_lang.star.compiler.format.StringAttribute;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;

/**
 * Compile a formatting rule
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
public class FmtCompile
{
  public FmtCompile()
  {
  }

  /**
   * A format rule looks like:
   * 
   * # [ptn ::] category --> attribute_specs
   * 
   * @param rule
   * @param ruleSet
   * @param errors
   * @return
   */
  public static FmtRule compileRule(IAbstract rule, FmtProgram ruleSet, ErrorReport errors)
  {
    if (Abstract.isUnary(rule, StandardNames.META_HASH))
      return compileRule(Abstract.getArg(rule, 0), ruleSet, errors);
    if (Abstract.isBinary(rule, StandardNames.FMT_RULE)) {
      Map<String, Integer> dict = new HashMap<String, Integer>();

      final IAbstract ptnArg = Abstract.getArg(rule, 0);

      if (Abstract.isBinary(ptnArg, StandardNames.WFF_DEFINES)) {
        Apply head = (Apply) ptnArg;
        String category = identifierOf(Abstract.getArg(head, 1));
        if (category == null) {
          errors.reportError("missing category", head.getLoc());
          return null;
        } else {
          FmtPtnOp ptnOp = compilePattern(Abstract.getArg(ptnArg, 0), errors, dict);

          return new FmtRule(rule.getLoc(), dict.size(), category, ptnOp, compileBody(Abstract.getArg(rule, 1),
              ruleSet, errors, dict, ptnOp.getSpecificity()));
        }
      } else {
        errors.reportError(StringUtils.msg("invalid head of format rule: ", ptnArg), ptnArg.getLoc());
        return null;
      }
    } else
      errors.reportError(StringUtils.msg("not properly formed formatting rule: ", rule), rule.getLoc());
    return null;
  }

  private static FmtPtnOp compilePattern(IAbstract ptn, ErrorReport errors, Map<String, Integer> dict)
  {
    if (ptn instanceof Apply) {
      Apply app = (Apply) ptn;

      if (Abstract.isUnary(app, StandardNames.QUESTION)) {
        String var = identifierOf(Abstract.getArg(app, 0));
        int offset;

        if (var != null) {
          if (!dict.containsKey(var)) {
            offset = dict.size();
            dict.put(var, offset);
          } else
            offset = dict.get(var);
          return new FmtVarPtn(offset);
        }
        errors.reportError("missing variable", ptn.getLoc());
        return new FmtPtnNull();
      } else if (Abstract.isBinary(app, StandardNames.QUESTION) && Abstract.getArg(app, 0) instanceof Name) {
        IAbstract other = Abstract.getArg(app, 0);
        String var = identifierOf(Abstract.getArg(app, 1));
        int offset;

        if (var != null) {
          if (!dict.containsKey(var)) {
            offset = dict.size();
            dict.put(var, offset);
          } else
            offset = dict.get(var);
          return new FmtVarPtn(offset, compilePattern(other, errors, dict));
        }
        errors.reportError("missing variable", ptn.getLoc());
        return new FmtPtnNull();
      } else if (Abstract.isBinary(app, StandardNames.MACRO_APPLY)) {
        FmtPtnOp opPtn = compilePattern(Abstract.getArg(app, 0), errors, dict);
        FmtPtnOp argPtn = compilePattern(Abstract.getArg(app, 1), errors, dict);

        return new FmtApplyApplyPtn(opPtn, argPtn);
      } else {
        FmtPtnOp opPtn = compilePattern(app.getOperator(), errors, dict);
        IList args = app.getArgs();
        FmtPtnOp ops[] = new FmtPtnOp[args.size()];
        for (int ix = 0; ix < args.size(); ix++) {
          ops[ix] = compilePattern(Abstract.getArg(app, ix), errors, dict);
        }

        return new FmtApplyPtn(opPtn, ops);
      }
    } else if (ptn instanceof Name) {
      String sym = ((Name) ptn).getId();

      if (sym.equals(StandardTypes.STRING))
        return new FmtStringPtn();
      else if (sym.equals(StandardNames.WFF_REGEXP))
        return new FmtRegexpPtn();
      else if (sym.equals(StandardTypes.INTEGER))
        return new FmtIntegerPtn();
      else if (sym.equals(StandardNames.NUMBER))
        return new FmtNumberOp();
      else if (sym.equals(StandardNames.IDENTIFIER))
        return new FmtIdentifierPtn();
      else if (sym.equals(StandardNames.WFF_SYMBOL))
        return new FmtSymbolPtn();
      else
        return new FmtLitPtn(ptn);
    } else
      return new FmtLitPtn(ptn);
  }

  // The body of a formatting rule is a combination of attribute values with
  // optional assignment to macro variable
  //
  // E.g.
  //
  // ?A when ?C :: action --> A :: { indent:+2} :& {indent:23}

  private static FmtFormatOp compileBody(IAbstract bdy, FmtProgram ruleSet, ErrorReport errors,
      Map<String, Integer> dict, int specificity)
  {
    Location loc = bdy.getLoc();
    if (CompilerUtils.isAttributeSpec(bdy)) {
      Map<String, IAttribute> attributes = new HashMap<String, IAttribute>();
      for (IAbstract el : CompilerUtils.unWrap(CompilerUtils.attributes(bdy), StandardNames.TERM))
        parseFmtAttribute(el, attributes, specificity, errors);

      return new FmtAttributes(loc, attributes);
    } else if (Abstract.isBinary(bdy, StandardNames.WFF_AND)) {
      FmtFormatOp lhs = compileBody(Abstract.getArg(bdy, 0), ruleSet, errors, dict, specificity);
      FmtFormatOp rhs = compileBody(Abstract.getArg(bdy, 1), ruleSet, errors, dict, specificity);
      return new FmtConjunction(loc, lhs, rhs);
    } else if (Abstract.isBinary(bdy, StandardNames.WFF_DEFINES)) {
      FmtFormatOp atts = compileBody(Abstract.getArg(bdy, 1), ruleSet, errors, dict, specificity);
      FmtBuildOp trm = compileBuilder(Abstract.getArg(bdy, 0), errors, dict);
      return new FmtApplyFormat(loc, trm, atts);
    } else {
      errors.reportError("invalid format specification: " + bdy, loc);
      return new FmtEmpty();
    }
  }

  private static void parseFmtAttribute(IAbstract el, Map<String, IAttribute> attributes, int specificity,
      ErrorReport errors)
  {
    String att = "";
    IAbstract spec = null;
    Absolute mode = Absolute.absolute;

    if (Abstract.isBinary(el, StandardNames.COLON) && CompilerUtils.isIdentifier(Abstract.getArg(el, 0))) {
      att = Abstract.getId(Abstract.getArg(el, 0));
      spec = Abstract.getArg(el, 1);
    } else if (Abstract.isBinary(el, StandardNames.FMT_INCREMENT) && CompilerUtils.isIdentifier(Abstract.getArg(el, 0))) {
      att = Abstract.getId(Abstract.getArg(el, 0));
      spec = Abstract.getArg(el, 1);

      mode = Absolute.increasing;
    } else if (Abstract.isBinary(el, StandardNames.FMT_DECREMENT) && CompilerUtils.isIdentifier(Abstract.getArg(el, 0))) {
      att = Abstract.getId(Abstract.getArg(el, 0));
      spec = Abstract.getArg(el, 1);

      mode = Absolute.decreasing;
    }

    switch (att) {
    case StandardNames.FMT_INDENT:
      if (CompilerUtils.isInteger(spec))
        attributes.put(att, new NumericAttribute(mode, CompilerUtils.getInteger(spec), specificity));
      else
        errors.reportError("invalid indent specification: " + spec, spec.getLoc());
      break;
    case StandardNames.FMT_LINES:
      if (CompilerUtils.isInteger(spec))
        attributes.put(att, new NumericAttribute(mode, CompilerUtils.getInteger(spec), specificity));
      else
        errors.reportError("invalid blank lines specification: " + spec, spec.getLoc());
      break;
    case StandardNames.FMT_COMMENT_COLUMN:
      if (CompilerUtils.isInteger(spec))
        attributes.put(att, new NumericAttribute(mode, CompilerUtils.getInteger(spec), specificity));
      else
        errors.reportError("invalid comment column specification: " + spec, spec.getLoc());
      break;
    case StandardNames.FMT_COMMENT_WRAP:
      if (CompilerUtils.isIdentifier(spec, StandardNames.TRUE) || CompilerUtils.isIdentifier(spec, StandardNames.FALSE))
        attributes.put(att, new BooleanAttribute(CompilerUtils.isIdentifier(spec, StandardNames.TRUE), specificity));
      else
        errors.reportError("invalid comment wrap specification: " + spec, spec.getLoc());
      break;
    case StandardNames.FMT_WRAP_COLUMN:
      if (CompilerUtils.isInteger(spec))
        attributes.put(att, new NumericAttribute(mode, CompilerUtils.getInteger(spec), specificity));
      else
        errors.reportError("invalid comment column specification: " + spec, spec.getLoc());
      break;
    case StandardNames.FMT_BREAK_BEFORE:
      if (CompilerUtils.isIdentifier(spec, StandardNames.TRUE) || CompilerUtils.isIdentifier(spec, StandardNames.FALSE))
        attributes.put(att, new BooleanAttribute(CompilerUtils.isIdentifier(spec, StandardNames.TRUE), specificity,
            false));
      else
        errors.reportError("invalid comment wrap specification: " + spec, spec.getLoc());
      break;
    case StandardNames.FMT_BREAK_AFTER:
      if (CompilerUtils.isIdentifier(spec, StandardNames.TRUE) || CompilerUtils.isIdentifier(spec, StandardNames.FALSE)) {
        attributes.put(att, new BooleanAttribute(CompilerUtils.isIdentifier(spec, StandardNames.TRUE), specificity,
            false));
      } else if (CompilerUtils.isString(spec))
        attributes.put(att, new StringAttribute(Abstract.getString(spec), specificity));
      else if (Abstract.isIdentifier(spec))
        attributes.put(att, new StringAttribute(Abstract.getId(spec), specificity));
      else
        errors.reportError("invalid comment wrap specification: " + spec, spec.getLoc());
      break;
    default:
      errors.reportWarning("unknown formatting attribute: " + att, spec.getLoc());
    }
  }

  private static FmtBuildOp compileBuilder(IAbstract term, ErrorReport errors, Map<String, Integer> dict)
  {
    Location loc = term.getLoc();

    if (Abstract.isUnary(term, StandardNames.QUESTION)) {
      String var = identifierOf(((Apply) term).getArg(0));
      if (var == null) {
        errors.reportError("missing variable name", loc);
        return new FmtLiteral(term);
      } else {
        if (dict.containsKey(var))
          return new FmtVar(dict.get(var), var);
        else {
          errors.reportError("validation variable " + var + " not defined in lhs", loc);
          return new FmtLiteral(term);
        }
      }
    } else if (term instanceof Name) {
      String var = identifierOf(term);
      if (var == null) {
        errors.reportError("missing validation variable name", loc);
        return new FmtLiteral(term);
      } else {
        if (dict.containsKey(var))
          return new FmtVar(dict.get(var), var);
        else
          return new FmtLiteral(term);
      }
    } else if (Abstract.isBinary(term, StandardNames.MACRO_APPLY))
      return new FmtApplyApplyBuild(compileBuilder(Abstract.getArg(term, 0), errors, dict), compileBuilder(Abstract
          .getArg(term, 1), errors, dict));
    else if (term instanceof Apply) {
      Apply apply = (Apply) term;
      FmtBuildOp op = compileBuilder(apply.getOperator(), errors, dict);
      IList args = apply.getArgs();
      FmtBuildOp argOps[] = new FmtBuildOp[args.size()];
      for (int ix = 0; ix < args.size(); ix++)
        argOps[ix] = compileBuilder((IAbstract) args.getCell(ix), errors, dict);

      return new FmtApply(op, argOps);
    } else
      return new FmtLiteral(term);
  }

  private static String identifierOf(IAbstract trm)
  {
    if (trm instanceof Name)
      return ((Name) trm).getId();
    else
      return null;
  }
}
