package org.star_lang.star.compiler.cafe;

import java.util.List;

import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.Display;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintFormatProperties;
import org.star_lang.star.data.IArray;

/**
 * 
 * Copyright (C) 2013 Starview Inc
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
public class CafeDisplay extends Display
{

  private CafeDisplay(PrettyPrintDisplay disp)
  {
    super(disp);
  }

  public static String display(IAbstract trm)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    PrettyPrintFormatProperties props = disp.getProperties();
    props.setRelativeTabs(false);

    trm.accept(new CafeDisplay(disp));
    return disp.toString();
  }

  public static String display(List<IAbstract> trms)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    CafeDisplay display = new CafeDisplay(disp);
    display.display(trms, "", ";\n", "");
    return disp.toString();
  }

  public static String display(IArray trms)
  {
    PrettyPrintDisplay disp = new PrettyPrintDisplay();
    CafeDisplay display = new CafeDisplay(disp);
    display.display(trms, "", ";\n", "");
    return disp.toString();
  }

  public static void display(PrettyPrintDisplay disp, IAbstract trm)
  {
    CafeDisplay cafe = new CafeDisplay(disp);
    trm.accept(cafe);
  }

  @Override
  public void visitApply(Apply trm)
  {
    if (CafeSyntax.isFunctionDefn(trm)) {
      Abstract.argPath(trm, 0, 0).accept(this);
      appendWord(Names.IS);
      Abstract.argPath(trm, 1, 0).accept(this);
      appendWord(Names.ARROW);
      Abstract.argPath(trm, 1, 1).accept(this);
    } else if (CafeSyntax.isPattern(trm)) {
      Abstract.argPath(trm, 0).accept(this);
      appendWord(Names.LARROW);
      Abstract.argPath(trm, 1).accept(this);
    } else if (CafeSyntax.isLetExp(trm)) {
      appendWord(Names.LET);
      display(CafeSyntax.letDefs(trm), "{\n  ", ";\n", "\n}", 2);
      disp.append(Names.IN);
      CafeSyntax.letBound(trm).accept(this);
    } else if (CafeSyntax.isIsDeclaration(trm)) {
      CafeSyntax.isDeclLval(trm).accept(this);
      disp.append(" is ");
      CafeSyntax.isDeclValue(trm).accept(this);
    } else if (CafeSyntax.isVarDeclaration(trm)) {
      disp.appendWord(Names.VAR);
      CafeSyntax.varDeclLval(trm).accept(this);
      disp.appendWord(" := ");
      CafeSyntax.varDeclValue(trm).accept(this);
    } else if (CafeSyntax.isTypedTerm(trm)) {
      CafeSyntax.typedTerm(trm).accept(this);
      append(Names.COLON);
      CafeSyntax.typedType(trm).accept(new CafeTypeDisplay(disp));
    } else if (CafeSyntax.isDot(trm)) {
      CafeSyntax.dotRecord(trm).accept(this);
      append(".");
      CafeSyntax.dotField(trm).accept(this);
    } else if (CafeSyntax.isMatch(trm)) {
      CafeSyntax.matchExp(trm).accept(this);
      appendWord(Names.MATCH);
      CafeSyntax.matchPtn(trm).accept(this);
    } else if (CafeSyntax.isBlock(trm)) {
      display(CafeSyntax.blockContents(trm), "{ ", ";\n", "\n}", 2);
    } else if (CafeSyntax.isLabeled(trm)) {
      disp.append(CafeSyntax.labeledLabel(trm));
      disp.append("::");
      CafeSyntax.labeledAction(trm).accept(this);
    } else if (CafeSyntax.isSync(trm)) {
      disp.append(Names.SYNC);
      disp.append("(");
      CafeSyntax.syncObject(trm).accept(this);
      disp.append("){");
      CafeSyntax.syncAction(trm).accept(this);
      disp.append("}");
    } else if (CafeSyntax.isWhile(trm)) {
      int mark = disp.markIndent(2);
      disp.appendWord(Names.WHILE);
      CafeSyntax.whileTest(trm).accept(this);
      disp.appendWord(Names.DO);
      CafeSyntax.whileBody(trm).accept(this);
      disp.popIndent(mark);
    } else if (CafeSyntax.isEscape(trm)) {
      appendName(CafeSyntax.escapeOp(trm));
      display(CafeSyntax.escapeArgs(trm), "(", ", ", ")");
    } else if (CafeSyntax.isConstructor(trm)) {
      appendName(CafeSyntax.constructorOp(trm));
      append("¢");
      display(CafeSyntax.constructorArgs(trm), "(", ", ", ")");
    } else if (CafeSyntax.isSwitch(trm)) {
      appendName(Names.SWITCH);
      CafeSyntax.switchSel(trm).accept(this);
      appendName(Names.IN);
      display(CafeSyntax.blockContents(Abstract.argPath(trm, 1)), "{ ", ";\n", "\n} ", 2);
      if (!CafeSyntax.isNothing(CafeSyntax.switchDeflt(trm))) {
        appendName(Names.DEFLT);
        CafeSyntax.switchDeflt(trm).accept(this);
      }
    } else if (CafeSyntax.isCaseRule(trm)) {
      CafeSyntax.caseRulePtn(trm).accept(this);
      disp.appendWord(Names.THIN_ARROW);
      CafeSyntax.caseRuleBody(trm).accept(this);
    } else if (CafeSyntax.isConditional(trm)) {
      int mark = disp.markIndent(2);
      appendName(Names.IF);
      CafeSyntax.conditionalTest(trm).accept(this);
      appendName(Names.THEN);
      disp.append("\n");
      CafeSyntax.conditionalThen(trm).accept(this);
      appendName(Names.ELSE);
      disp.append("\n");
      CafeSyntax.conditionalElse(trm).accept(this);
      disp.popIndent(mark);
    } else if (CafeSyntax.isValof(trm)) {
      appendName(Names.VALOF);
      CafeSyntax.valofAction(trm).accept(this);
    } else if (CafeSyntax.isValis(trm)) {
      appendName(Names.VALIS);
      CafeSyntax.valisExp(trm).accept(this);
    } else if (CafeSyntax.isImport(trm)) {
      appendName(Names.IMPORT);
      Abstract.unaryArg(trm).accept(this);
    } else if (CafeSyntax.isJavaImport(trm)) {
      appendName(Names.JAVA);
      appendName(CafeSyntax.javaImportClass(trm));
    } else if (CafeSyntax.isTypeDef(trm)) {
      appendName(Names.TYPE);
      CafeSyntax.typeDefType(trm).accept(new CafeTypeDisplay(disp));
      appendName(Names.IS);
      display(CafeSyntax.typeDefSpecs(trm), "", "or\n", "", 2);
    } else if (CafeSyntax.isConstructorSpec(trm)) {
      disp.appendId(CafeSyntax.constructorSpecLabel(trm));
      display(CafeSyntax.constructorSpecArgs(trm), "(", ", ", ")");
    } else if (CafeSyntax.isRecord(trm)) {
      disp.appendId(CafeSyntax.recordLabel(trm));
      display(CafeSyntax.recordArgs(trm), "{", ", ", "}", 2);
    } else if (CafeSyntax.isExistentialType(trm)) {
      disp.appendWord(StandardNames.EXISTS);
      String sep = "";
      IAbstract eType = trm;
      while (CafeSyntax.isExistentialType(eType)) {
        disp.append(sep);
        sep = ", ";
        display(CafeSyntax.existentialTypeVar(eType));
        eType = CafeSyntax.existentialBoundType(eType);
      }
      disp.appendWord(StandardNames.S_T);
      display(eType);
    } else if (Abstract.isBinary(trm, Names.AND))
      displayConjunction(trm);
    else if (CafeSyntax.isAssignment(trm)) {
      CafeSyntax.assignmentLval(trm).accept(this);
      disp.append(Names.ASSIGN);
      CafeSyntax.assignmentRval(trm).accept(this);
    } else
      super.visitApply(trm);
  }

  private void displayConjunction(IAbstract trm)
  {
    if (Abstract.isBinary(trm, Names.AND)) {
      displayConjunction(Abstract.binaryLhs(trm));
      disp.appendWord(Names.AND);
      displayConjunction(Abstract.binaryRhs(trm));
    } else
      trm.accept(this);
  }
}
