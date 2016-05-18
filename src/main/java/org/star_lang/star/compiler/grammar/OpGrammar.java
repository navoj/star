package org.star_lang.star.compiler.grammar;

import org.star_lang.star.compiler.CompilerUtils;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.ast.*;
import org.star_lang.star.compiler.grammar.Token.TokenType;
import org.star_lang.star.compiler.operator.*;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.PrettyPrintDisplay;
import org.star_lang.star.compiler.util.PrettyPrintable;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.type.StandardTypes;
import org.star_lang.star.data.value.ResourceURI;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
public class OpGrammar implements PrettyPrintable {
  private Tokenizer tokenizer;

  final static String LPAR = "(";
  final static String RPAR = ")";
  final static String MLPAR = "#(";
  final static String MRPAR = ")#";

  private final static int MAX_PRIORITY = 2000;

  private final static int ARG_PRIORITY = 999;

  private final static int MIN_PRIORITY = 0;

  private Stack<IAbstract> termStack = new Stack<>();

  private final Operators operators;

  private static final boolean DEBUG_PARSER = false;

  private final ErrorReport errors;

  private final List<TermListener<IAbstract>> listeners = new ArrayList<>();

  public OpGrammar(Operators operators, ErrorReport errors) {
    this.errors = errors;
    this.operators = operators;
  }

  public static IAbstract parse(Location loc, Reader rdr, Operators operators, ErrorReport errors) {
    OpGrammar parser = new OpGrammar(operators, errors);
    return parser.parse(loc.getUri(), rdr, loc);
  }

  public IAbstract parse(ResourceURI uri, Reader input, Location loc) {
    tokenizer = new Tokenizer(uri, errors, input, loc);
    // tokenizer.addListener(new DumpToken());

    termStack.clear();
    int wrapCount = 0;
    Token hed = tokenizer.headToken();

    while (!isTerminal(hed)) {
      term(Operators.STATEMENT_PRIORITY - 1, "");

      if (!termStack.isEmpty()) {
        IAbstract peek = termStack.peek();
        checkForOperators(peek);
        pushToListeners(peek);
      }

      hed = tokenizer.headToken();
      if (hed.isIdentifier(StandardNames.TERM)) {
        tokenizer.commitToken();
        hed = tokenizer.headToken();
      }

      wrapCount++;
    }

    while (wrapCount > 1) {
      if (termStack.size() > 1) {
        IAbstract right = termStack.pop();
        IAbstract left = termStack.pop();
        termStack.push(Abstract.binary(left.getLoc().extendWith(right.getLoc()), StandardNames.TERM, left, right,
            OpFormAttribute.name, new OpFormAttribute(Operators.STATEMENT_PRIORITY, OperatorForm.infix)));
      }
      wrapCount--;
    }

    if (termStack.isEmpty()) {
      reportError("no data", tokenizer.getLocation());
      return null;
    } else if (termStack.size() != 1)
      reportError("unfinished text", termStack.peek().getLoc());
    else if (!isTerminal(tokenizer.headToken()))
      reportError("incomplete parse", tokenizer.headToken().getLoc());

    return termStack.pop();
  }

  public IAbstract parse(Reader input, Location loc) {
    tokenizer = new Tokenizer(errors, input, loc);

    termStack.clear();

    term(Operators.STATEMENT_PRIORITY, "");

    if (termStack.isEmpty()) {
      reportError("no data", null);
      return null;
    } else if (termStack.size() != 1)
      reportError("unfinished text", termStack.peek().getLoc());
    else if (!isTerminal(tokenizer.headToken()))
      reportError("incomplete parse", tokenizer.headToken().getLoc());

    return termStack.pop();
  }

  private int term(int priority, String bracket) {
    if (DEBUG_PARSER)
      System.out.println("looking for term of priority: " + priority);

    return termRight(termLeft(priority, bracket), priority, bracket);
  }

  private int termLeft(int priority, String bracket) {
    Token hed = tokenizer.headToken();

    switch (hed.getType()) {
      case string:
      case blob:
      case regexp:
      case integer:
      case longint:
      case floating:
      case decimal:
        return term0();
      case identifier: {
        if (operators.isRightBracket(hed.getImage()))
          reportError("not expecting a `" + hed.toString() + "' here" + bracket, hed.getLoc());

        Operator prefix = operators.isPrefixOperator(hed.getImage(), priority);
        Location leftLoc = hed.getLoc();

        if (prefix != null) {
          tokenizer.commitToken();
          termStack.push(new Name(hed.getLoc(), hed.getImage()));

          hed = tokenizer.headToken();
          if ((hed.getType() == TokenType.identifier) && operators.isRightBracket(hed.getImage()))
            return MIN_PRIORITY;
          else if (prefix.getPriority() <= priority) {
            if (DEBUG_PARSER)
              System.out.println("using prefix op: " + hed.getImage() + " at " + hed.getLoc());
            int mark = termStack.size();
            term(prefix.rightPriority(), bracket);
            if (termStack.size() == mark + 1) {
              Location opLoc = termStack.peek().getLoc().extendWith(leftLoc);

              buildApply(opLoc, 1, prefix.getForm(), prefix.getPriority());

              if (prefix.getOperator().equals(StandardNames.MINUS)) {
                IAbstract arg = Abstract.unaryArg(termStack.peek());
                boolean isRaw = false;
                if (Abstract.isUnary(arg, StandardNames.RAW)) {
                  isRaw = true;
                  arg = Abstract.unaryArg(arg);
                }
                if (arg instanceof IntegerLiteral) {
                  IntegerLiteral intArg = (IntegerLiteral) arg;
                  termStack.pop();
                  termStack.push(rawWrap(new IntegerLiteral(arg.getLoc(), -intArg.getLit()), isRaw));
                } else if (arg instanceof LongLiteral) {
                  LongLiteral lngArg = (LongLiteral) arg;
                  termStack.pop();
                  termStack.push(rawWrap(new LongLiteral(arg.getLoc(), -lngArg.getLit()), isRaw));
                } else if (arg instanceof FloatLiteral) {
                  FloatLiteral fltArg = (FloatLiteral) arg;
                  termStack.pop();
                  termStack.push(rawWrap(new FloatLiteral(arg.getLoc(), -fltArg.getLit()), isRaw));
                } else if (arg instanceof BigDecimalLiteral) {
                  BigDecimalLiteral decArg = (BigDecimalLiteral) arg;
                  termStack.pop();
                  termStack.push(rawWrap(new BigDecimalLiteral(arg.getLoc(), decArg.getLit().negate()), isRaw));
                }
              }
            }
            return prefix.getPriority();
          } else {
            reportError("prefix operator '" + prefix.getOperator() + "' of priority " + prefix.getPriority()
                + " not permitted here (expecting " + priority + ")", leftLoc);
            return MIN_PRIORITY;
          }
        } else
          return term0();
      }

      case terminal:
        reportError("unexpected end of input", hed.getLoc());
        termStack.push(Abstract.tupleTerm(hed.getLoc()));
        return MIN_PRIORITY;

      default:
        reportError("unexpected token: '" + hed.toString() + "'", hed.getLoc());
        return MIN_PRIORITY;
    }
  }

  private int termRight(int leftPrior, int priority, String bracket) {
    Token hed = tokenizer.headToken();

    switch (hed.getType()) {
      case terminal:
        return leftPrior;

      case identifier: {
        if (operators.isRightBracket(hed.getImage()))
          return leftPrior;

        Operator postfix = operators.isPostfixOperator(hed.getImage(), leftPrior);
        Operator infix = operators.isInfixOperator(hed.getImage(), leftPrior);

        if (postfix != null && postfix.leftPriority() >= leftPrior && postfix.getPriority() <= priority) {
          Token token = tokenizer.commitToken();

          if (infix != null && infix.leftPriority() >= leftPrior && infix.getPriority() <= priority) {
            // Look ahead, the operator is both infix and postfix ...
            Token next = tokenizer.headToken();

            // We want to see if the token is a legitimate start to an
            // expression
            // to see how to disambiguate

            boolean treatAsPostfix = true;
            switch (next.getType()) {
              case integer:
              case longint:
              case floating:
              case decimal:
              case string:
              case regexp:
              case blob:
                treatAsPostfix = false;
                break;
              case identifier: { // This may be a prefix operator ...
                if (operators.isRightBracket(next.getImage())) {
                  treatAsPostfix = true;
                  break;
                } else {
                  Operator prefix = operators.isPrefixOperator(next.getImage(), priority);
                  if (prefix != null) {
                    treatAsPostfix = prefix.getPriority() > infix.rightPriority();
                    break;
                  }
                }

                Operator nxtInfix = operators.isInfixOperator(next.getImage(), priority);
                if (nxtInfix != null) {
                  treatAsPostfix = nxtInfix.getPriority() > infix.rightPriority();
                  break;
                }

                treatAsPostfix = false; // identifier can be infix
                break;
              }
              default:
            }
            if (treatAsPostfix) {
              if (DEBUG_PARSER)
                System.out.println("using postfix op: " + token.getImage() + " at " + token.getLoc());

              IAbstract arg = termStack.pop();
              IAbstract op = new Name(token.getLoc(), token.getImage());
              IAbstract term = Abstract.unary(arg.getLoc().extendWith(token.getLoc()), op, arg);
              setOpForm(postfix, term);
              termStack.push(term);

              return termRight(postfix.getPriority(), priority, bracket);
            } else {
              if (DEBUG_PARSER)
                System.out.println("using infix op: " + token.getImage() + " at " + token.getLoc());
              IAbstract leftArg = termStack.pop();
              IAbstract op = new Name(token.getLoc(), token.getImage());

              int mark = termStack.size();
              term(infix.rightPriority(), bracket);
              if (termStack.size() == mark + 1) {
                IAbstract term = Abstract.binary(leftArg.getLoc().extendWith(tokenizer.headToken().getLoc()), op,
                    leftArg, termStack.pop());
                setOpForm(infix, term);
                termStack.push(term);
              }
              return termRight(infix.getPriority(), priority, bracket);
            }
          } else {// treat as postfix ...
            if (DEBUG_PARSER)
              System.out.println("using postfix op: " + token.getImage() + " at " + token.getLoc());
            IAbstract arg = termStack.pop();
            IAbstract op = new Name(token.getLoc(), token.getImage());
            IAbstract term = Abstract.unary(arg.getLoc().extendWith(token.getLoc()), op, arg);
            setOpForm(postfix, term);
            termStack.push(term);

            return termRight(postfix.getPriority(), priority, bracket);
          }
        } else if (infix != null && infix.leftPriority() >= leftPrior && infix.getPriority() <= priority) {
          Token token = tokenizer.commitToken();
          if (DEBUG_PARSER)
            System.out.println("using infix op: " + token.getImage() + " at " + token.getLoc());

          IAbstract left = termStack.pop();
          IAbstract op = new Name(token.getLoc(), token.getImage());

          term(infix.rightPriority(), bracket);
          if (!termStack.isEmpty()) // only false in case of syntax errors
          {
            IAbstract right = termStack.pop();
            termStack.push(Abstract.binary(left.getLoc().extendWith(right.getLoc()), op, left, right,
                OpFormAttribute.name, new OpFormAttribute(infix.getPriority(), infix.getForm())));
          }
          return termRight(infix.getPriority(), priority, bracket);
        } else
          return leftPrior;
      }
      case integer:
      case longint:
      case floating:
      case decimal:
      case string:
      case regexp:
      case blob:
      default:
        return leftPrior;
    }
  }

  private void setOpForm(Operator op, IAbstract term) {
    IAttribute opform = new OpFormAttribute(op.getPriority(), op.getForm());
    term.setAttribute(OpFormAttribute.name, opform);
  }

  private int term0() {
    Token hed = tokenizer.headToken();
    final Location loc = hed.getLoc();

    switch (hed.getType()) {
      case string: {
        boolean isRaw = hed.isRaw();
        tokenizer.commitToken();

        return stringParse(loc, hed.getImage(), isRaw);
      }
      case blob:
        tokenizer.commitToken();
        termStack.push(rawWrap(new StringLiteral(loc, hed.getImage()), hed.isRaw()));
        return MIN_PRIORITY;
      case regexp:
        tokenizer.commitToken();
        termStack.push(CompilerUtils.regexp(loc, hed.getImage()));
        return MIN_PRIORITY;
      case integer:
        tokenizer.commitToken();
        termStack.push(rawWrap(new IntegerLiteral(loc, (int) hed.getIntVal()), hed.isRaw()));
        return MIN_PRIORITY;
      case longint:
        tokenizer.commitToken();
        termStack.push(rawWrap(Abstract.newLong(loc, hed.getIntVal()), hed.isRaw()));
        return MIN_PRIORITY;
      case floating:
        tokenizer.commitToken();
        termStack.push(rawWrap(Abstract.newFloat(loc, hed.getFloatingValue()), hed.isRaw()));
        return MIN_PRIORITY;
      case decimal:
        tokenizer.commitToken();
        termStack.push(rawWrap(Abstract.newBigdecimal(loc, (BigDecimal) hed.getValue()), hed.isRaw()));
        return MIN_PRIORITY;
      default: {
        return termArgs(term00());
      }
    }
  }

  private static IAbstract rawWrap(IAbstract term, boolean isRaw) {
    if (isRaw)
      return Abstract.unary(term.getLoc(), StandardNames.RAW, term);
    else
      return term;
  }

  private interface Interpolator {
    IAbstract interpolate(Location loc, IAbstract term);

    IAbstract format(Location loc, IAbstract term, IAbstract format);
  }

  // Implements the syntactic sugar for string interpolation
  private int stringParse(Location loc, String str, boolean isRaw) {
    int mark = termStack.size();
    StringBuilder buffer = new StringBuilder();

    for (int ix = 0; ix < str.length(); ) {
      int ch = str.codePointAt(ix);

      if (ch == Tokenizer.QUOTE) {
        assert ix < str.length() - 1;
        ix = str.offsetByCodePoints(ix, 1);
        buffer.appendCodePoint(str.codePointAt(ix));
        ix = str.offsetByCodePoints(ix, 1);
      } else if (ch == '#' && ix < str.length() - 1) {
        Interpolator hasher = new Interpolator() {
          @Override
          public IAbstract interpolate(Location loc, IAbstract term) {
            return Abstract.binary(loc, StandardNames.AS, term, Abstract.name(loc, StandardTypes.STRING));
          }

          @Override
          public IAbstract format(Location loc, IAbstract term, IAbstract format) {
            return Abstract.binary(loc, StandardNames.FORMAT, term, format);
          }
        };
        ix = parseInterpolation(loc, buffer, str, ix, hasher);
      } else if (ch == '$' && ix < str.length() - 2 && str.charAt(ix + 1) == '$') {
        buffer.append("$");
        ix += 2;
      } else if (ch == '$' && ix < str.length() - 1) {
        Interpolator hasher = new Interpolator() {
          @Override
          public IAbstract interpolate(Location loc, IAbstract term) {
            return Abstract.unary(loc, StandardNames.DISPLAY, term);
          }

          @Override
          public IAbstract format(Location loc, IAbstract term, IAbstract format) {
            return Abstract.binary(loc, StandardNames.FORMAT, term, format);
          }
        };
        ix = parseInterpolation(loc, buffer, str, ix, hasher);
      } else {
        buffer.appendCodePoint(str.codePointAt(ix));
        ix = str.offsetByCodePoints(ix, 1);
      }
    }

    int fragmentCount = termStack.size() - mark;

    if (buffer.length() > 0 || fragmentCount == 0) {
      termStack.push(rawWrap(new StringLiteral(loc.offset(1, buffer.length()), buffer.toString()), isRaw));
      fragmentCount++;
    }

    while (fragmentCount > 1) {
      IAbstract rgt = termStack.pop();
      IAbstract lft = termStack.pop();
      if (CompilerUtils.isEmptyString(lft))
        termStack.push(rgt);
      else if (CompilerUtils.isEmptyString(rgt))
        termStack.push(lft);
      else {
        IAbstract join = Abstract.binary(lft.getLoc().extendWith(rgt.getLoc()), StandardNames.STRING_CATENATE, lft,
            rgt, OpFormAttribute.name, new OpFormAttribute(Operators.CATENATE_PRIORITY, OperatorForm.infix));
        termStack.push(join);
      }
      fragmentCount--;
    }
    return MIN_PRIORITY;
  }

  private int parseInterpolation(Location loc, StringBuilder buffer, String str, int ix, Interpolator interpolator) {
    if (buffer.length() > 0) {
      termStack.push(new StringLiteral(loc.offset(str.offsetByCodePoints(ix, 1), buffer.length()), buffer.toString()));
      buffer.setLength(0);
    }

    ix = str.offsetByCodePoints(ix, 1);

    int ch = str.codePointAt(ix);

    if (ch == '(') {
      int nx = StringUtils.countParens(str, ix, '(', ')');
      String interpolString = str.substring(ix + 1, nx - 1);
      if (!interpolString.isEmpty()) {
        StringReader rdr = new StringReader(interpolString);
        OpGrammar parser = new OpGrammar(operators, errors);

        // adjust location by applying offset
        IAbstract term = parser.parse(rdr, loc.offset(ix + 2, nx - 1));
        if (Abstract.isParenTerm(term))
          term = Abstract.deParen(term);

        if (nx < str.length() && str.codePointAt(nx) == ':') {
          int formatEnd = str.indexOf(';', nx);
          if (formatEnd > nx) {
            Location formLoc = loc.offset(nx, formatEnd - nx);
            IAbstract formSpec = new StringLiteral(formLoc, str.substring(nx + 1, formatEnd));
            nx = formatEnd + 1;
            Location offsetLoc = loc.offset(ix + 1, nx - ix);
            term = interpolator.format(offsetLoc, term, formSpec);
          } else if (!(term instanceof StringLiteral)) {
            Location offsetLoc = loc.offset(ix + 1, nx - ix);
            term = interpolator.interpolate(offsetLoc, term);
          }
        } else if (!(term instanceof StringLiteral)) {
          Location offsetLoc = loc.offset(ix + 1, nx - ix);
          term = interpolator.interpolate(offsetLoc, term);
        }
        termStack.push(term);
      }

      return nx;
    } else {
      int nx = ix;
      while (nx < str.length() && Tokenizer.isIdentifierChar(ch)) {
        nx = str.offsetByCodePoints(nx, 1);
        if (nx < str.length())
          ch = str.codePointAt(nx);
      }

      Location subLoc = loc.offset(ix + 1, nx - ix);

      IAbstract term = Abstract.name(subLoc, str.substring(ix, nx));

      if (nx < str.length() && str.codePointAt(nx) == ':') {
        int formatEnd = str.indexOf(';', nx);
        if (formatEnd > nx) {
          Location formLoc = loc.offset(nx + 2, formatEnd - nx);
          IAbstract formSpec = new StringLiteral(formLoc, str.substring(nx + 1, formatEnd));
          Location offsetLoc = loc.offset(ix + 1, formatEnd - ix);
          term = interpolator.format(offsetLoc, term, formSpec);
          nx = formatEnd + 1;
        } else {
          Location offsetLoc = loc.offset(ix + 1, nx - ix);
          term = interpolator.interpolate(offsetLoc, term);
        }
      } else {
        Location offsetLoc = loc.offset(ix + 1, nx - ix);
        term = interpolator.interpolate(offsetLoc, term);
      }

      termStack.push(term);
      return nx;
    }
  }

  private Location term00() {
    Token token = tokenizer.nextToken();

    final Location loc = token.getLoc();

    switch (token.getType()) {
      case identifier: {
        String image = token.getImage();

        if (image.equals(MLPAR)) {
          term(Operators.STATEMENT_PRIORITY, MRPAR);

          token = tokenizer.nextToken();
          if (!token.isIdentifier(MRPAR))
            reportError("missing a )#, got [" + token.toString() + "]\nleft #( at " + loc, token.getLoc());

          extendTopLocation(loc, token.getLoc());
          return token.getLoc();
        } else if (image.equals(LPAR)) {
          Token hed = tokenizer.headToken();
          if (hed.isIdentifier(RPAR)) {
            tokenizer.commitToken();
            termStack.push(Abstract.tupleTerm(loc.extendWith(hed.getLoc())));
            return hed.getLoc();
          } else {
            BracketPair brackets = operators.getBracketPair(token.getImage());

            term(brackets.innerPriority, RPAR);
            token = tokenizer.nextToken();
            if (!token.isIdentifier(RPAR))
              reportError("missing a ), got [" + token.toString() + "]\nleft ( at " + loc, token.getLoc());
            else
              termStack.push(tupleize(loc, termStack.pop()));
            extendTopLocation(loc, token.getLoc());
            return token.getLoc();
          }
        } else if (operators.isLeftBracket(token.getImage())) {
          BracketPair brackets = operators.getBracketPair(token.getImage());

          if (DEBUG_PARSER)
            System.out.println("Left bracket: " + token.toString() + " at " + loc);

          Token hed = tokenizer.headToken();
          if (isRightBracket(hed, brackets)) {
            tokenizer.commitToken();
            termStack.push(new Name(loc.extendWith(hed.getLoc()), brackets.operator));
            return hed.getLoc();
          } else
            termStack.push(new Name(loc, brackets.operator));

          int mark = termStack.size();

          if (brackets.innerPriority < Operators.STATEMENT_PRIORITY)
            term(brackets.innerPriority, brackets.rightBracket);
          else { // We process this level specially
            int wrapCount = 0;
            Location hedLoc = hed.getLoc();

            while (!isTerminal(hed) && !isRightBracket(hed, brackets)) {
              term(brackets.innerPriority - 1, brackets.rightBracket);
              IAbstract peek = termStack.peek();

              hed = tokenizer.headToken();
              if (hed.isIdentifier(StandardNames.TERM)) {
                tokenizer.commitToken();
                peek.setLoc(peek.getLoc().extendWith(hed.getLoc()));

                hed = tokenizer.headToken();
              } else if (hed.getType() == TokenType.terminal)
                reportError("expecting a `" + StandardNames.TERM + "', or `" + brackets.rightBracket, hed.getLoc());

              checkForOperators(peek);
              pushToListeners(peek);
              wrapCount++;
              hedLoc = hed.getLoc();
            }
            if (!isRightBracket(hed, brackets))
              reportError("missing a " + brackets.rightBracket + "\nleft " + brackets.leftBracket + " at " + loc, hedLoc);

            while (wrapCount > 1) {
              if (termStack.size() > 1) {
                IAbstract right = termStack.pop();
                IAbstract left = termStack.pop();
                termStack.push(Abstract.binary(left.getLoc().extendWith(right.getLoc()), StandardNames.TERM, left, right,
                    OpFormAttribute.name, new OpFormAttribute(Operators.STATEMENT_PRIORITY, OperatorForm.infix)));
              }
              wrapCount--;
            }
          }
          token = tokenizer.nextToken();
          if (!isRightBracket(token, brackets))
            reportError("'" + token.toString() + "' not permitted here, expecting '" + brackets.rightBracket + "', '"
                + brackets.leftBracket + "' at " + loc, token.getLoc());
          else if (DEBUG_PARSER)
            System.out.print("Right bracket from " + brackets.leftBracket + ":" + token.toString() + " at " + loc);

          if (termStack.size() == mark + 1)
            buildApply(loc.extendWith(token.getLoc()), 1, OperatorForm.none, 0);
          return token.getLoc();
        } else if (operators.isRightBracket(token.getImage())) {
          reportError("not expecting '" + token.getImage() + "' here", loc);
          return token.getLoc();
        } else if (operators.isOperator(image, 0)) {
          Token hed = tokenizer.headToken();
          if (!hed.isIdentifier(RPAR)) {
            errors.reportError("operator '" + token.toString() + "' not valid here", loc);
            return hed.getLoc();
          }
        }
        termStack.push(new Name(loc, token.getImage()));
        return loc;
      }
      default:
        reportError("expecting an identifier, got '" + token.toString() + "'", loc);
        return loc;
    }
  }

  private int termArgs(Location lastLoc) {
    Token hed = tokenizer.headToken();
    while (hed.getLoc().sameLine(lastLoc)) {
      Location leftLoc = hed.getLoc();

      if (LPAR.equals(hed.getImage())) {
        int argCount = 0;
        int mark = termStack.size();
        Location opLoc = termStack.peek().getLoc();

        tokenizer.commitToken();

        hed = tokenizer.headToken();

        if (!hed.isIdentifier(RPAR)) {
          do {
            term(ARG_PRIORITY, RPAR);
            argCount++;
            hed = tokenizer.headToken();
            if (hed.isIdentifier(StandardNames.COMMA)) {
              tokenizer.commitToken();
            } else
              break;
          } while (true);
        }
        if (!hed.isIdentifier(RPAR))
          reportError("expecting a comma or close paren, got '" + hed.toString() + "'", hed.getLoc());
        else
          tokenizer.commitToken();
        lastLoc = hed.getLoc();

        if (termStack.size() == mark + argCount)
          buildApply(opLoc.extendWith(hed.getLoc()), argCount, OperatorForm.none, 0); // construct
        // F(a1,a2,..,an)
      } else if (hed.getType() == TokenType.identifier && operators.isLeftBracket(hed.getImage())) {// begin
        // term
        // end
        // = begin(term)
        BracketPair pair = operators.getBracketPair(hed.getImage());

        IAbstract label = termStack.pop();
        Location opLoc = label.getLoc();

        IAbstract bkOp = new Name(hed.getLoc(), pair.operator);
        tokenizer.commitToken();

        hed = tokenizer.headToken();
        if (isRightBracket(hed, pair)) {
          tokenizer.commitToken();
          termStack.push(new Apply(opLoc.extendWith(hed.getLoc()), label, new IAbstract[]{bkOp}));
          lastLoc = hed.getLoc();
        } else if (pair.innerPriority == Operators.STATEMENT_PRIORITY) {
          // We process this level specially
          int wrapCount = 0;

          while (!isTerminal(hed) && !isRightBracket(hed, pair)) {
            term(pair.innerPriority - 1, pair.rightBracket);
            IAbstract peek = termStack.peek();

            checkForOperators(peek);
            pushToListeners(peek);

            hed = tokenizer.headToken();
            if (hed.isIdentifier(StandardNames.TERM)) {
              tokenizer.commitToken();
              peek.setLoc(peek.getLoc().extendWith(hed.getLoc()));
              hed = tokenizer.headToken();
            }

            wrapCount++;
          }
          if (!isRightBracket(hed, pair))
            reportError("missing a " + pair.rightBracket + "\nleft " + pair.leftBracket + " at " + leftLoc, hed
                .getLoc());

          hed = tokenizer.commitToken();
          lastLoc = hed.getLoc();
          while (wrapCount > 1) {
            if (termStack.size() > 1) {
              IAbstract right = termStack.pop();
              IAbstract left = termStack.pop();
              termStack.push(Abstract.binary(left.getLoc().extendWith(right.getLoc()), StandardNames.TERM, left, right,
                  OpFormAttribute.name, new OpFormAttribute(Operators.STATEMENT_PRIORITY, OperatorForm.infix)));
            }
            wrapCount--;
          }
          Location termLoc = opLoc.extendWith(hed.getLoc());
          // build <bk>(label,args)
          termStack.push(Abstract.binary(termLoc, bkOp, label, termStack.pop()));
        } else {
          term(pair.innerPriority, pair.rightBracket);
          Token token = tokenizer.nextToken();
          if (!isRightBracket(token, pair))
            reportError("expecting " + pair.rightBracket + ", got '" + token.toString() + "', left " + pair.leftBracket
                + " at " + hed.getLoc(), token.getLoc());
          Location termLoc = opLoc.extendWith(hed.getLoc());
          // build <bk>(label,args)
          termStack.push(Abstract.binary(termLoc, bkOp, label, termStack.pop()));
          lastLoc = hed.getLoc();
        }
      } else
        break;

      hed = tokenizer.headToken();
    }
    return MIN_PRIORITY;
  }

  private boolean isRightBracket(Token token, BracketPair pair) {
    TokenType tokType = token.getType();
    return (tokType == TokenType.identifier && token.getImage().equals(pair.rightBracket));
  }

  private boolean isTerminal(Token token) {
    return token.getType() == TokenType.terminal;
  }

  private void buildApply(Location loc, int argCount, OperatorForm form, int priority) {
    if (termStack.size() >= argCount + 1) {
      IAbstract args[] = new IAbstract[argCount];

      for (int ix = argCount - 1; ix >= 0; ix--)
        args[ix] = termStack.pop();

      IAbstract op = termStack.pop();

      Apply apply = new Apply(loc, op, args);
      IAttribute opform = new OpFormAttribute(priority, form);
      apply.setAttribute(OpFormAttribute.name, opform);

      termStack.push(apply);

      if (DEBUG_PARSER)
        System.out.println("building: " + apply);
    }
  }

  private String getOperator(IAbstract term) {
    if (term instanceof Name)
      return ((Name) term).getId();
    else if (Abstract.isParenTerm(term))
      return getOperator(Abstract.getArg(term, 0));
    else if (term instanceof StringLiteral)
      return ((StringLiteral) term).getLit();
    else {
      reportError("expecting an operator NAME", term.getLoc());
      return null;
    }
  }

  private void reportError(String msg, Location loc) {
    errors.reportError(msg, loc);
  }

  private boolean checkForOperators(IAbstract term) {
    if (Abstract.isUnary(term, StandardNames.META_HASH)) {
      IAbstract stmt = Abstract.getArg(term, 0);
      Location loc = stmt.getLoc();
      final boolean force;
      if (Abstract.isUnary(stmt, StandardNames.FORCE)) {
        force = true;
        stmt = Abstract.unaryArg(stmt);
      } else
        force = false;

      if (Abstract.isBinary(stmt, StandardNames.INFIX)) {
        IAbstract o1 = Abstract.getArg(stmt, 1);
        String op = getOperator(Abstract.getArg(stmt, 0));
        int priority = getPriority(o1);

        if (op != null && priority >= 0 && priority < Operators.STATEMENT_PRIORITY) {
          operators.defineInfixOperator(op, priority, force, errors, loc);
          return true;
        } else
          reportError("infix operator must have priority in range 0..1999", term.getLoc());
      } else if (Abstract.isTernary(stmt, StandardNames.INFIX)) {
        IAbstract o1 = Abstract.ternaryMid(stmt);
        String op = getOperator(Abstract.ternaryLhs(stmt));
        int priority = getPriority(o1);
        IAbstract o2 = Abstract.ternaryRhs(stmt);
        int min = getPriority(o2);

        if (op != null && priority >= 0 && min >= 0 && priority < Operators.STATEMENT_PRIORITY) {
          operators.defineInfixOperator(op, priority, min, force, errors, loc);
          return true;
        } else
          reportError("infix operator must have priority in range 0..1999", term.getLoc());
      } else if (Abstract.isBinary(stmt, StandardNames.LEFT)) {
        IAbstract o1 = Abstract.getArg(stmt, 1);
        String op = getOperator(Abstract.getArg(stmt, 0));

        int priority = getPriority(o1);

        if (op != null && priority >= 0 && priority < Operators.STATEMENT_PRIORITY) {
          operators.defineLeftOperator(op, priority, force, errors, loc);
        } else
          errors.reportError("left operator must have priority in range 0..1999", term.getLoc());
        return true;
      } else if (Abstract.isBinary(stmt, StandardNames.RIGHT)) {
        IAbstract o1 = Abstract.getArg(stmt, 1);
        String op = getOperator(Abstract.getArg(stmt, 0));
        int priority = getPriority(o1);

        if (op != null && priority >= 0 && priority < Operators.STATEMENT_PRIORITY) {
          operators.defineRightOperator(op, priority, force, errors, loc);
        } else
          reportError("right operator must have priority in range 0..1999", term.getLoc());
        return true;
      } else if (Abstract.isUnary(stmt, StandardNames.TOKEN)) {
        String op = getOperator(Abstract.unaryArg(stmt));

        if (op != null) {
          operators.defineToken(op);
        } else
          reportError("missing token specification", term.getLoc());
      } else if (Abstract.isBinary(stmt, StandardNames.PREFIX)) {
        IAbstract o1 = Abstract.getArg(stmt, 1);
        String op = getOperator(Abstract.getArg(stmt, 0));
        int priority = getPriority(o1);

        if (op != null && priority >= 0 && priority < Operators.STATEMENT_PRIORITY) {
          operators.definePrefixOperator(op, priority, force, errors, loc);
        } else
          reportError("prefix operator must have priority in range 0..1999", term.getLoc());
      } else if (Abstract.isTernary(stmt, StandardNames.PREFIX)) {
        String op = getOperator(Abstract.getArg(stmt, 0));
        int priority = getPriority(Abstract.getArg(stmt, 1));
        int minPrior = getPriority(Abstract.getArg(stmt, 2));

        if (op != null && priority >= 0 && minPrior >= 0 && priority < Operators.STATEMENT_PRIORITY) {
          operators.definePrefixOperator(op, priority, minPrior, force, errors, loc);
        } else
          reportError("prefix operator must have priority in range 0..1999", term.getLoc());
      } else if (Abstract.isBinary(stmt, StandardNames.POSTFIX)) {
        IAbstract o1 = Abstract.getArg(stmt, 1);
        String op = getOperator(Abstract.getArg(stmt, 0));
        int priority = getPriority(o1);

        if (op != null && priority >= 0 && priority < Operators.STATEMENT_PRIORITY) {
          operators.definePostfixOperator(op, priority, force, errors, loc);
          return true;
        } else
          reportError("postfix operator must have priority in range 0..1999", term.getLoc());
      } else if (Abstract.isBinary(stmt, StandardNames.PREFIXA)) {
        IAbstract o1 = Abstract.getArg(stmt, 1);
        String op = getOperator(Abstract.getArg(stmt, 0));
        int priority = getPriority(o1);

        if (op != null && priority >= 0 && priority < Operators.STATEMENT_PRIORITY) {
          operators.definePrefixAssocOperator(op, priority, force, errors, loc);
          return true;
        } else
          reportError("associative prefix operator must have priority in range 0..1999", term.getLoc());
      } else if (Abstract.isBinary(stmt, StandardNames.POSTFIXA)) {
        IAbstract o1 = Abstract.getArg(stmt, 1);
        String op = getOperator(Abstract.getArg(stmt, 0));
        int priority = getPriority(o1);

        if (op != null && priority >= 0 && priority < Operators.STATEMENT_PRIORITY) {
          operators.definePostfixAssocOperator(op, priority, force, errors, loc);
          return true;
        } else
          errors.reportError("associative postfix operator must have priority in range 0..1999", term.getLoc());
      } else if (Abstract.isApply(stmt, StandardNames.BRACKETS, 3)) {
        IAbstract o2 = Abstract.getArg(stmt, 2);
        String left = getOperator(Abstract.getArg(stmt, 0));
        String right = getOperator(Abstract.getArg(stmt, 1));
        int priority = getPriority(o2);

        if (left != null && right != null && priority >= 0 && priority <= Operators.STATEMENT_PRIORITY) {
          try {
            operators.defineBracketPair(priority, left, right, left + right);
            return true;
          } catch (OperatorException e) {
            reportError(e.getMessage(), term.getLoc());
          }
        } else
          reportError("bracket pair priority must be between 0 and " + MAX_PRIORITY, term.getLoc());
      } else if (Abstract.isApply(stmt, StandardNames.BRACKETS, 4)) {
        IAbstract o2 = Abstract.getArg(stmt, 3);
        String left = getOperator(Abstract.getArg(stmt, 0));
        String right = getOperator(Abstract.getArg(stmt, 1));
        String op = getOperator(Abstract.getArg(term, 2));
        int priority = getPriority(o2);

        if (left != null && right != null && priority >= 0 && priority <= Operators.STATEMENT_PRIORITY) {
          try {
            operators.defineBracketPair(priority, left, right, op);
            return true;
          } catch (OperatorException e) {
            reportError(e.getMessage(), term.getLoc());
          }
        } else
          reportError("bracket pair priority must be between 0 and " + MAX_PRIORITY, term.getLoc());
      } else if (Abstract.isBinary(stmt, StandardNames.MACRORULE) || Abstract.isBinary(stmt, StandardNames.WFF_RULE)
          || Abstract.isBinary(stmt, StandardNames.WFF_DEFINES) || Abstract.isBinary(stmt, StandardNames.FMT_RULE)
          || CompilerUtils.isFunctionStatement(stmt) || CompilerUtils.isIsStatement(stmt))
        return true;
      else
        reportError("cannot understand meta statement:: " + term, term.getLoc());
    }
    return false;
  }

  private static int getPriority(IAbstract term) {
    if (Abstract.isUnary(term, StandardTypes.INTEGER))
      term = Abstract.unaryArg(term);
    if (term instanceof IntegerLiteral)
      return ((IntegerLiteral) term).getLit();
    else
      return -1;
  }

  private IAbstract tupleize(Location loc, IAbstract term) {
    List<IAbstract> els = new ArrayList<>();

    while (Abstract.isBinary(term, StandardNames.COMMA)) {
      IAbstract el = Abstract.getArg(term, 0);

      els.add(el);
      term = Abstract.getArg(term, 1);
    }
    els.add(term);
    return Abstract.tupleTerm(loc, els);
  }

  private void extendTopLocation(Location start, Location end) {
    if (!termStack.isEmpty())
      termStack.peek().setLoc(start.extendWith(end));
  }

  @Override
  public void prettyPrint(PrettyPrintDisplay disp) {
    int depth = termStack.size();
    for (IAbstract el : termStack) {
      disp.append(--depth);
      disp.append(": ");
      Display.display(disp, el);
      disp.append("\n");
    }
    errors.prettyPrint(disp);
  }

  @Override
  public String toString() {
    return PrettyPrintDisplay.toString(this);
  }

  public void addListener(TermListener<IAbstract> listener) {
    listeners.add(listener);
  }

  private void pushToListeners(IAbstract term) {
    for (TermListener<IAbstract> listener : listeners)
      listener.processTerm(term);
  }
}
