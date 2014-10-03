package org.star_lang.star.compiler.type;

import static org.star_lang.star.data.type.StandardTypes.astType;
import static org.star_lang.star.data.type.StandardTypes.booleanType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ast.Abstract;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.BigDecimalLiteral;
import org.star_lang.star.compiler.ast.BooleanLiteral;
import org.star_lang.star.compiler.ast.CharLiteral;
import org.star_lang.star.compiler.ast.FloatLiteral;
import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.IAbstractVisitor;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.LongLiteral;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.canonical.Application;
import org.star_lang.star.compiler.canonical.ConstructorTerm;
import org.star_lang.star.compiler.canonical.IContentAction;
import org.star_lang.star.compiler.canonical.IContentExpression;
import org.star_lang.star.compiler.canonical.Scalar;
import org.star_lang.star.compiler.canonical.ValisAction;
import org.star_lang.star.compiler.canonical.ValofExp;
import org.star_lang.star.compiler.canonical.VarDeclaration;
import org.star_lang.star.compiler.canonical.Variable;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.AccessMode;
import org.star_lang.star.compiler.util.GenSym;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.IList;
import org.star_lang.star.data.type.IType;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.Factory;
import org.star_lang.star.operators.arrays.runtime.ArrayOps.ArrayConcatenate;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.ArrayNil;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.BinaryArray;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.TernaryArray;
import org.star_lang.star.operators.arrays.runtime.ArraySequenceOps.UnaryArray;

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
public class Quoter implements IAbstractVisitor
{
  private final Stack<IContentExpression> stack = new Stack<>();
  private final Map<Location, Variable> locations = new HashMap<>();
  private final List<IContentAction> locDefs = new ArrayList<>();
  private final Dictionary cxt;
  private final Dictionary outer;
  private final TypeChecker checker;

  private static final IType astArrayType = TypeUtils.arrayType(astType);

  private static final boolean NOWHERE = true;

  Quoter(Dictionary cxt, Dictionary outer, TypeChecker checker)
  {
    this.cxt = cxt;
    this.outer = outer;
    this.checker = checker;
  }

  IContentExpression quoted(IAbstract term)
  {
    term.accept(this);
    assert stack.size() == 1;

    if (!locDefs.isEmpty()) {
      locDefs.add(new ValisAction(term.getLoc(), stack.pop()));
      return new ValofExp(term.getLoc(), astType, locDefs);
    } else
      return stack.pop();
  }

  private IContentExpression genLocation(Location loc)
  {
    if (NOWHERE)
      return new ConstructorTerm(loc, Location.nowhere, Location.type);
    if (locations.containsKey(loc))
      return locations.get(loc);
    else {
      Variable locVar = new Variable(loc, Location.type, GenSym.genSym("__loc"));

      IContentExpression locExp = generateLocation(loc);

      locDefs.add(new VarDeclaration(loc, locVar, AccessMode.readOnly, locExp));
      locations.put(loc, locVar);
      return locVar;
    }
  }

  public static IContentExpression generateLocation(Location loc)
  {
    IContentExpression uri = TypeCheckerUtils.stringLiteral(loc, loc.getSrc());
    IContentExpression charCount = TypeCheckerUtils.integerLiteral(loc, loc.getCharCnt());
    IContentExpression lineCount = TypeCheckerUtils.integerLiteral(loc, loc.getLineCnt());
    IContentExpression lineOffset = TypeCheckerUtils.integerLiteral(loc, loc.getLineOff());
    IContentExpression length = TypeCheckerUtils.integerLiteral(loc, loc.getLen());

    // must be in alphabetical order
    IContentExpression args[] = new IContentExpression[] { charCount, length, lineCount, lineOffset, uri };

    return new ConstructorTerm(loc, Location.somewhere, Location.type, args);
  }

  @Override
  public void visitApply(Apply app)
  {
    if (CompilerUtils.isUnQuoted(app) || CompilerUtils.isQuestion(app))
      stack.push(checker.typeOfExp(CompilerUtils.unquotedExp(app), astType, cxt, outer));
    else if (Abstract.isBinary(app, StandardNames.MACRO_APPLY)) {
      Abstract.binaryLhs(app).accept(this);

      IAbstract rhs = Abstract.binaryRhs(app);
      if (!Abstract.isUnary(rhs, StandardNames.QUESTION)) {
        checker.getErrorReport().reportError(StringUtils.msg("expecting a ? after ", StandardNames.MACRO_APPLY),
            rhs.getLoc());
        return;
      } else {
        IContentExpression args = checker.typeOfExp(Abstract.binaryRhs(app), astArrayType, cxt, outer);
        IContentExpression op = stack.pop();
        IContentExpression loc = genLocation(app.getLoc());
        stack.push(new ConstructorTerm(app.getLoc(), Apply.name, astType, loc, op, args));
      }
    } else {
      app.getOperator().accept(this);
      IContentExpression loc = genLocation(app.getLoc());
      IContentExpression op = stack.pop();

      IList argList = app.getArgs();

      IContentExpression args = partition(app.getLoc(), argList, 0, argList.size());

      stack.push(new ConstructorTerm(app.getLoc(), Apply.name, astType, loc, op, args));
    }
  }

  private IContentExpression partition(Location loc, IList args, int from, int to)
  {
    assert to >= from;

    if (to == from)
      return new Application(loc, astArrayType, new Variable(loc, ArrayNil.type(), ArrayNil.name));
    else if (to == from + 1) {
      ((IAbstract) args.getCell(from)).accept(this);
      IContentExpression argTpl[] = new IContentExpression[] { stack.pop() };
      return new Application(loc, astArrayType, new Variable(loc, UnaryArray.type(), UnaryArray.name), argTpl);
    } else if (to == from + 2) {
      ((IAbstract) args.getCell(from)).accept(this);
      IContentExpression a1 = stack.pop();
      ((IAbstract) args.getCell(from + 1)).accept(this);
      IContentExpression a2 = stack.pop();
      IContentExpression argTpl[] = new IContentExpression[] { a1, a2 };
      return new Application(loc, astArrayType, new Variable(loc, BinaryArray.type(), BinaryArray.name), argTpl);
    } else if (to == from + 3) {
      ((IAbstract) args.getCell(from)).accept(this);
      IContentExpression a1 = stack.pop();
      ((IAbstract) args.getCell(from + 1)).accept(this);
      IContentExpression a2 = stack.pop();
      ((IAbstract) args.getCell(from + 2)).accept(this);
      IContentExpression a3 = stack.pop();
      IContentExpression argTpl[] = new IContentExpression[] { a1, a2, a3 };
      return new Application(loc, astArrayType, new Variable(loc, TernaryArray.type(), TernaryArray.name), argTpl);
    } else {
      int s = (from + to) / 2;
      IContentExpression low = partition(loc, args, from, s);
      IContentExpression high = partition(loc, args, s, to);
      IContentExpression argTpl[] = new IContentExpression[] { low, high };
      return new Application(loc, astArrayType, new Variable(loc, ArrayConcatenate.type(), ArrayConcatenate.name),
          argTpl);
    }
  }

  @Override
  public void visitBooleanLiteral(BooleanLiteral lit)
  {
    IContentExpression locExp = genLocation(lit.getLoc());
    IContentExpression bool = new Scalar(lit.getLoc(), booleanType, Factory.newBool(lit.getLit()));
    stack.push(new ConstructorTerm(lit.getLoc(), BooleanLiteral.name, astType, locExp, bool));
  }

  @Override
  public void visitCharLiteral(CharLiteral lit)
  {
    IContentExpression locExp = genLocation(lit.getLoc());
    IContentExpression ch = TypeCheckerUtils.charLiteral(lit.getLoc(), lit.getLit());
    stack.push(new ConstructorTerm(lit.getLoc(), CharLiteral.name, astType, locExp, ch));
  }

  @Override
  public void visitFloatLiteral(FloatLiteral lit)
  {
    IContentExpression locExp = genLocation(lit.getLoc());
    IContentExpression flt = TypeCheckerUtils.floatLiteral(lit.getLoc(), lit.getLit());
    stack.push(new ConstructorTerm(lit.getLoc(), FloatLiteral.name, astType, locExp, flt));
  }

  @Override
  public void visitStringLiteral(StringLiteral lit)
  {
    IContentExpression locExp = genLocation(lit.getLoc());
    IContentExpression str = TypeCheckerUtils.stringLiteral(lit.getLoc(), lit.getLit());
    stack.push(new ConstructorTerm(lit.getLoc(), StringLiteral.name, astType, locExp, str));
  }

  @Override
  public void visitIntegerLiteral(IntegerLiteral lit)
  {
    IContentExpression locExp = genLocation(lit.getLoc());
    IContentExpression ix = TypeCheckerUtils.integerLiteral(lit.getLoc(), lit.getLit());
    stack.push(new ConstructorTerm(lit.getLoc(), IntegerLiteral.name, astType, locExp, ix));
  }

  @Override
  public void visitLongLiteral(LongLiteral lit)
  {
    IContentExpression locExp = genLocation(lit.getLoc());
    IContentExpression lx = TypeCheckerUtils.longLiteral(lit.getLoc(), lit.getLit());
    stack.push(new ConstructorTerm(lit.getLoc(), LongLiteral.name, astType, locExp, lx));
  }

  @Override
  public void visitBigDecimal(BigDecimalLiteral lit)
  {
    IContentExpression locExp = genLocation(lit.getLoc());
    IContentExpression big = TypeCheckerUtils.decimalLiteral(lit.getLoc(), lit.getLit());
    stack.push(new ConstructorTerm(lit.getLoc(), BigDecimalLiteral.name, astType, locExp, big));
  }

  @Override
  public void visitName(Name name)
  {
    IContentExpression locExp = genLocation(name.getLoc());
    IContentExpression str = TypeCheckerUtils.stringLiteral(name.getLoc(), name.getId());
    stack.push(new ConstructorTerm(name.getLoc(), Name.name, astType, locExp, str));
  }
}