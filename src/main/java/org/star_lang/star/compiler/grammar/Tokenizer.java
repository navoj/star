package org.star_lang.star.compiler.grammar;

import static org.star_lang.star.compiler.grammar.Token.TokenType.blob;
import static org.star_lang.star.compiler.grammar.Token.TokenType.character;
import static org.star_lang.star.compiler.grammar.Token.TokenType.decimal;
import static org.star_lang.star.compiler.grammar.Token.TokenType.floating;
import static org.star_lang.star.compiler.grammar.Token.TokenType.identifier;
import static org.star_lang.star.compiler.grammar.Token.TokenType.integer;
import static org.star_lang.star.compiler.grammar.Token.TokenType.longint;
import static org.star_lang.star.compiler.grammar.Token.TokenType.regexp;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.grammar.Token.TokenType;
import org.star_lang.star.compiler.operator.Operators;
import org.star_lang.star.compiler.standard.StandardNames;
import org.star_lang.star.compiler.util.Pair;
import org.star_lang.star.compiler.util.StringUtils;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.data.value.ResourceURI;

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

public class Tokenizer
{
  private static final char EOF = Character.MAX_VALUE;
  public static final char QUOTE = '\1';

  private final Reader rdr;
  private final ResourceURI uri;

  private int lineCount = 1;
  private int lineOffset = 1;
  private int charCount = 0;
  private int lineMark = 1;
  private int offsetMark = 1;
  private int charMark = 0;

  StringBuilder image = new StringBuilder();

  private final ErrorReport errors;

  private static final boolean DEBUG_TOKENIZER = false;

  private static final Map<String, List<Pair<String[], String>>> multiTokens = new HashMap<>();
  private static TokenChar stdTokens = new TokenChar(-1, true);

  private final TokenListener operatorListener = new OperatorListener(stdTokens);

  static {
    Operators.operatorRoot(); // force the operators
  }

  public Tokenizer(ResourceURI uri, ErrorReport errors, Reader rdr, Location loc)
  {
    this.rdr = rdr;
    this.uri = uri;
    this.errors = errors;

    if (loc != null) {
      this.charMark = loc.getCharCnt();
      this.lineMark = loc.getLineCnt();
      this.offsetMark = loc.getLineOff();
    }
  }

  public Tokenizer(ErrorReport errors, Reader rdr, Location loc)
  {
    this.rdr = rdr;
    this.errors = errors;

    this.uri = loc.getUri();
    this.charMark = this.charCount = loc.getCharCnt();
    this.lineMark = this.lineCount = loc.getLineCnt();
    this.offsetMark = this.lineOffset = loc.getLineOff();
  }

  public static void declareMultiToken(String[] token, String full)
  {
    assert token.length > 0;
    List<Pair<String[], String>> L = multiTokens.get(token[0]);
    if (L == null) {
      L = new ArrayList<>();
      multiTokens.put(token[0], L);
    }
    L.add(Pair.pair(token, full));
  }

  public static void recordStdToken(String tok)
  {
    if (!isAlphaIdentifier(tok))
      TokenChar.recordToken(stdTokens, tok);
  }

  public static String[] multiTokens()
  {
    Collection<String> multi = new HashSet<>();

    for (Entry<String, List<Pair<String[], String>>> entry : multiTokens.entrySet()) {
      for (Pair<String[], String> e : entry.getValue()) {
        multi.add(e.right);
      }
    }
    return multi.toArray(new String[multi.size()]);
  }

  private static boolean isAlphaIdentifier(String tok)
  {
    int first = tok.codePointAt(0);
    if (isIdentifierChar(first)) {
      int pos = 0;
      while (pos < tok.length()) {
        int ch = tok.codePointAt(pos);
        if (isIdentifierChar(ch))
          pos = tok.offsetByCodePoints(pos, 1);
        else
          return false;
      }
      return true;
    }
    return false;
  }

  public static TokenChar stdTokens()
  {
    return stdTokens;
  }

  private List<Token> tokenBuffer = new ArrayList<>();

  public Token nextToken()
  {
    headToken();
    Token first = tokenBuffer.remove(0);
    operatorListener.newToken(first);
    return first;
  }

  public Token commitToken()
  {
    assert!tokenBuffer.isEmpty();

    Token first = tokenBuffer.remove(0);
    operatorListener.newToken(first);
    return first;
  }

  public Token headToken()
  {
    if (tokenBuffer.isEmpty()) {
      Token token = nxToken();
      if (DEBUG_TOKENIZER)
        token.display();
      tokenBuffer.add(token);
    }

    checkForMultiTokens();

    return tokenBuffer.get(0);
  }

  private void checkForMultiTokens()
  {
    Token token = tokenBuffer.get(0);
    if (token.getType() == TokenType.identifier) {
      List<Pair<String[], String>> multis = multiTokens.get(token.getImage());
      if (multis != null) {
        multiLoop: for (Pair<String[], String> multi : multis) {
          String frags[] = multi.left();
          Location lastLoc = token.getLoc();
          for (int ix = 1; ix < frags.length; ix++) {
            if (tokenBuffer.size() <= ix)
              tokenBuffer.add(nxToken());

            Token frag = tokenBuffer.get(ix);
            if (frag.getType() != TokenType.identifier || !frag.getImage().equals(frags[ix]))
              continue multiLoop;
            else
              lastLoc = frag.getLoc();
          }
          assert tokenBuffer.size() >= frags.length;
          for (int ix = 0; ix < frags.length; ix++)
            tokenBuffer.remove(0);
          Location extendLoc = token.getLoc().extendWith(lastLoc);
          tokenBuffer.add(new Token(identifier, multi.right(), extendLoc, false));
          return;
        }
      }
    }
  }

  private Token nxToken()
  {
    image.setLength(0);

    try {
      skipComments();
      markLocation();

      int ch = hedChar(); // read a character
      if (ch == EOF)
        return reportToken(TokenType.terminal, "<EOF>");

      switch (ch) {
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        return readNumber();

      case '\'': // look for 's
        switch (nextHedChar()) {
        case 's':
          image.appendCodePoint(nextChar());
          if (hedChar() == '\'') {
            nextChar();
            return reportToken(character, "s");
          } else
            return reportToken(identifier, StandardNames.EXPORTS);
        case 'n':
          image.appendCodePoint(nextChar());
          if (hedChar() == '\'') {
            nextChar();
            return reportToken(character, "n");
          } else
            return reportToken(identifier, StandardNames.ALSO);

        default:
          image.setLength(0);
          grabChar(image, false);
          if (hedChar() == '\'') {
            nextChar(); // consume the closing quote
            return reportToken(character, image.toString());
          } else {
            errors.reportError("missing character close quote", thisLocation());
            return reportToken(character, image.toString());
          }
        }
      case '"': // begin a string definition
        switch (ch = nextHedChar()) {
        case '"':
          switch (ch = nextHedChar()) {
          case '"':
            nextChar();
            grabBlock(image);
            return reportBlob(image.toString(), isRaw());

          default:
            return reportString("", isRaw());
          }
        default:
          image.setLength(0);
          grabString(image);

          if (hedChar() != '\"')
            errors.reportError("missing close quote of string", thisLocation());

          nextChar(); // Commit to the quote
          return reportString(image.toString(), isRaw());
        }

      case '\u201c': // Unicode open double quote
        image.setLength(0);
        nextChar();
        grabString(image, '\u201d');

        if (hedChar() != '\u201d')
          errors.reportError("missing close quote of string", thisLocation());

        nextChar(); // Commit to the quote
        return reportString(image.toString(), isRaw());

      case '`': // Regular expression
        image.setLength(0);
        nextChar();

        while ((ch = hedChar()) != '`' && ch != EOF)
          image.appendCodePoint(nextChar());

        if (hedChar() != '`')
          errors.reportError("missing close quote of regular expression", thisLocation());

        nextChar(); // Commit to the quote
        return reportToken(regexp, image.toString());

      case '\\': // start of escaped identifier
        image.setLength(0);
        return reportToken(identifier, grabIdentifier(image));

      case '.': // This case must be allowed to fall through to default
        switch (nextHedChar()) { // special handling for degenerate floating point numbers .34
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          image.setLength(0);
          unChar('.');
          return readNumber();
        default:
          unChar('.');
          ch = '.';
          // fall through
        }

      default:
        TokenChar tree = stdTokens.follows(ch);

        if (tree != null) {
          TokenChar lastFinal = null;
          int lastPos = 0;

          while (tree != null) {
            image.appendCodePoint(ch);

            if (tree.isTerm()) {
              lastFinal = tree;
              lastPos = image.length();
            }
            ch = nextHedChar();
            tree = tree.follows(ch);
          }
          if (lastFinal != null) {
            while (image.length() > lastPos) { // back off invalid characters
              int len = image.length() - 1;
              char last = image.charAt(len);
              unChar(last);
              image.setLength(len);
            }
            return reportToken(identifier, image.toString());
          } else {
            errors.reportError(StringUtils.msg("not a valid token: ", image), thisLocation());
            return reportToken(identifier, image.toString());
          }
        } else {
          // look for identifiers
          grabIdentifier(image);

          String id = image.toString();
          return reportToken(identifier, id);
        }
      }
    } catch (IOException e) {
      errors.reportError("IO error: " + e.getMessage(), thisLocation());
      return reportToken(TokenType.terminal, image.toString());
    }
  }

  private Token readNumber() throws IOException
  {
    char ch = hedChar();
    long sign = image.length() > 0 && image.charAt(0) == '-' ? -1 : 1;

    if (ch == '0') {
      image.append("0");

      nextChar(); // Commit to the leading 0

      ch = hedChar();
      if (ch == 'x' || ch == 'X') { // Read a hexadecimal number
        nextChar();
        long X = sign * grabHex();

        ch = hedChar();
        if (ch == 'l' || ch == 'L') {
          nextChar();
          return reportToken(X, isRaw());
        }
        return reportToken((int) X, isRaw());
      } else if (ch == 'c' || ch == 'C') { // read a character code
        nextChar();
        ch = grabChar(image, false); // Pick up a character reference
        return reportToken((int) sign * ch, isRaw());
      } else if (ch == '\'') { // read a string blob 0'len"Text
        long len = grabDigits();
        ch = nextChar();
        if (ch != '"')
          errors.reportError("badly formed string blob", thisLocation());

        image.setLength(0);
        while (len-- > 0) {
          image.appendCodePoint(nextChar());
        }
        return reportBlob(image.toString(), isRaw());
      }
    }

    // Pick up the leading sequence of decimal digits
    while (Character.isDigit(ch)) {
      image.appendCodePoint(ch);
      ch = nextHedChar();
    }

    if (ch == '.') {
      ch = nextHedChar();

      if (Character.isDigit(ch)) {
        image.appendCodePoint('.');
        while (Character.isDigit(ch)) {
          image.appendCodePoint(ch);
          ch = nextHedChar();
        }

        switch (ch) {
        case 'e':
        case 'E': // A floating point number
          image.appendCodePoint(ch);
          ch = nextHedChar();
          if (ch == '-' || ch == '+') {
            image.appendCodePoint(ch);
            ch = nextHedChar();
          }
          while (Character.isDigit(ch)) {
            image.appendCodePoint(ch);
            ch = nextHedChar();
          }
          if (ch == 'd' || ch == 'D') {
            nextChar();
            return new Token(floating, Double.parseDouble(image.toString()), getLocation(), isRaw());
          } else
            return new Token(floating, Double.parseDouble(image.toString()), getLocation(), isRaw());

        default:
          return new Token(floating, Double.parseDouble(image.toString()), getLocation(), isRaw());
        case 'd':
        case 'D':
          nextChar();
          return new Token(floating, Double.parseDouble(image.toString()), getLocation(), isRaw());
        case 'a':
        case 'A':
          nextChar();
          return new Token(decimal, new BigDecimal(image.toString()), getLocation(), isRaw());
        }
      } else {
        unChar('.');
        return new Token(integer, Long.parseLong(image.toString()), getLocation(), false);
      }
    }

    // We fell through here deliberately
    long result;

    try {
      result = image.length() > 0 ? Long.parseLong(image.toString()) : 0;
    } catch (NumberFormatException e) {
      errors.reportError("cannot handle " + image.toString()
          + " since it does not have the appropriate numeric format.", thisLocation());
      // proceed to finish this parsing
      return new Token(integer, new Long(0), getLocation(), isRaw());
    }

    switch (hedChar()) {
    case 'a':
    case 'A':
      nextChar(); // consume the decimal number marker
      return new Token(decimal, new BigDecimal(image.toString()), getLocation(), isRaw());
    case 'l':
    case 'L':
      nextChar(); // explicit long value
      return new Token(longint, result, getLocation(), isRaw());
    case 'd':
    case 'D':
      nextChar();
      return new Token(floating, Double.parseDouble(image.toString()), getLocation(), isRaw());
    default: // end of numeric value
      return new Token(integer, result, getLocation(), isRaw());
    }
  }

  private boolean isRaw() throws IOException
  {
    if (hedChar() == '_') {
      nextChar();
      return true;
    } else
      return false;
  }

  private long grabDigits() throws IOException
  {
    long X = 0;
    char ch = nextHedChar();

    while (Character.getType(ch) == Character.DECIMAL_DIGIT_NUMBER) {
      X = X * 10 + Character.digit(ch, 10);
      ch = nextHedChar();
    }
    return X;
  }

  private long grabHex() throws IOException
  {
    long X = 0;
    char ch = hedChar();
    while (Character.getType(ch) == Character.DECIMAL_DIGIT_NUMBER || (ch >= 'a' && ch <= 'f') || (ch >= 'A'
        && ch <= 'F')) {
      X = X * 16 + Character.digit(ch, 16);
      ch = nextHedChar();
    }
    return X;
  }

  private void grabString(StringBuilder str) throws IOException
  {
    char ch = hedChar();

    while (ch != EOF && ch != '"') {
      switch (ch) {
      case '\\':
        grabChar(str, true);
        break;
      case '$':
      case '#': {
        str.append(ch);
        if ((ch = nextHedChar()) == '(') {
          str.append('(');
          nextChar();
          grabString(str, ')');
          if (hedChar() == ')')
            str.append(nextChar());
          else
            errors.reportWarning("incomplete string", thisLocation());
          break;
        } else {
          while (isIdentifierChar(ch)) {
            str.append(ch);
            ch = nextHedChar();
          }
          break;
        }
      }
      case QUOTE:
        str.append(QUOTE);
        nextChar();
        str.append(nextChar());
        break;
      default:
        str.append(ch);
        nextChar();
        break;
      }
      ch = hedChar();
    }
  }

  private char grabChar(StringBuilder str, boolean quoteDollars) throws IOException
  {
    char ch = nextChar();
    if (ch == '\\')
      ch = grabQtChar(str, quoteDollars);
    else if (ch == '\n') {
      errors.reportError("unexpected new line", thisLocation());
      str.append(ch);
    } else
      str.append(ch);
    return ch;
  }

  private char grabQtChar(StringBuilder str, boolean quoteDollars) throws IOException
  {
    char ch;
    switch (ch = nextChar()) {
    case 'b':
      str.append('\b');
      return '\b';
    case 'd':
      str.append('\377'); // The delete character
      return '\377';
    case 'e': // The escape character
      str.append('\33');
      return '\33';
    case 'f': // Form feed
      str.append('\f');
      return '\f';
    case 'n': // New line
      str.append('\n');
      return '\n';
    case 'r': // Carriage return
      str.append('\r');
      return '\r';
    case 't': // Tab
      str.append('\t');
      return '\t';
    case '"': // Quote
      str.append('"');
      return '"';
    case '$':
      if (quoteDollars)
        str.append(QUOTE); // The parse will strip this out later
      str.append('$');
      return '$';
    case '#':
      if (quoteDollars)
        str.append(QUOTE); // The parse will strip this out later
      str.append('#');
      return '#';
    case '\\': // Backslash itself
      str.append('\\');
      return '\\';
    case 'u':
    case '+': { // Start a hex sequence
      long hex = grabHex();
      if (nextChar() != ';') {
        errors.reportError("invalid Unicode sequence", thisLocation());
      }
      if (hex == QUOTE)
        str.append(QUOTE);
      str.append((char) hex);
      return (char) hex;
    }
    default:
      str.append(ch);
      return ch;
    }
  }

  /*
   * Grab text until we see the """ sequence
   */
  private void grabBlock(StringBuilder str) throws IOException
  {
    char ch = nextChar();

    while (ch != EOF) {
      switch (ch) {
      case '"':
        switch (ch = nextChar()) {
        case '"':
          switch (nextChar()) {
          case '"':
            return; // we are done
          default:
            str.append("\"\""); // append the "" we saw
            continue;
          }
        default:
          str.append('"');
          str.appendCodePoint(ch);
          ch = nextChar();
          continue;
        }
      default:
        str.appendCodePoint(ch);
        ch = nextChar();
      }
    }
  }

  private final static Map<Character, Character> brackets = new HashMap<>();

  static {
    brackets.put('(', ')');
    brackets.put('[', ']');
    brackets.put('{', '}');
    brackets.put('\u201c', '\u201d');
  }

  private void grabString(StringBuilder str, char nesting) throws IOException
  {
    char ch = hedChar();

    while (ch != EOF && ch != nesting) {
      if (brackets.containsKey(ch)) {
        char rgt = brackets.get(ch);
        str.append(ch);
        nextChar();
        grabString(str, rgt);
        if (hedChar() == rgt)
          str.append(nextChar());
        else
          errors.reportError("unbalanced " + ch + ", expecting a " + rgt, thisLocation());
      } else
        switch (ch) {
        case '"':
          str.append(nextChar());
          grabString(str);
          if (hedChar() == '"')
            str.append(nextChar());
          break;
        case '`':
          str.append(nextChar());
          while ((ch = hedChar()) != '`' && ch != EOF)
            grabChar(str, false);
          if (hedChar() != '`')
            errors.reportError("missing close quote of regular expression", thisLocation());
          str.append(nextChar()); // Commit to the quote
          break;
        case '\\':
          grabChar(str, true);
          break;
        default:
          str.append(ch);
          nextChar();
          break;
        }
      ch = hedChar();
    }
  }

  public static boolean isIdentifierStart(String str)
  {
    return isIdentifierChar(str.codePointAt(0));
  }

  private String grabIdentifier(StringBuilder str) throws IOException
  {
    char ch = hedChar();
    switch (Character.getType(ch)) {
    case Character.CONNECTOR_PUNCTUATION:
    case Character.LETTER_NUMBER:
    case Character.LOWERCASE_LETTER:
    case Character.TITLECASE_LETTER:
    case Character.UPPERCASE_LETTER:
    case Character.OTHER_LETTER:
      str.append(ch);
      ch = nextHedChar();
      break;
    default:
      if (ch == '\\') {
        nextChar();
        grabQtChar(str, false);
        ch = hedChar();
      } else {
        errors.reportError(StringUtils.msg("invalid char: `" + ch + "'"), thisLocation());
        nextChar();
      }
    }

    while (ch != EOF) {
      switch (Character.getType(ch)) {
      case Character.CONNECTOR_PUNCTUATION:
      case Character.LETTER_NUMBER:
      case Character.LOWERCASE_LETTER:
      case Character.TITLECASE_LETTER:
      case Character.UPPERCASE_LETTER:
      case Character.OTHER_LETTER:
      case Character.OTHER_NUMBER:
      case Character.DECIMAL_DIGIT_NUMBER:
        str.append(ch);
        ch = nextHedChar();
        continue;
      default:
        if (ch == '\\') {
          nextChar();
          grabQtChar(str, false);
          ch = hedChar();
        } else
          return str.toString();
      }
    }
    return str.toString();
  }

  private void skipComments() throws IOException
  {
    char ch = hedChar();

    while (ch != EOF) {
      while (Character.isWhitespace(ch)) {
        ch = nextHedChar();
      }
      if (ch == '-') {
        markLocation();
        ch = nextHedChar();
        if (ch == '-') {
          ch = nextHedChar(); // Look for a white space character

          if (Character.isWhitespace(ch)) {
            while (ch != EOF && ch != '\n') {
              ch = nextHedChar();
            }
          } else if (ch != '\n') {
            unChar('-'); // step back two characters
            unChar('-');
            return;
          }
        } else {
          unChar('-');
          return;
        }
      } else if (ch == '/') {
        markLocation();
        ch = nextHedChar();
        if (ch == '*') {
          nextChar(); // commit to the first *

          char ch1 = nextChar();
          char ch2 = hedChar();

          while (ch1 != EOF && !(ch1 == '*' && ch2 == '/')) {
            ch1 = nextChar();
            ch2 = hedChar();
          }

          ch = nextHedChar();
        } else {
          unChar('/');
          return;
        }
      } else
        return;
    }
  }

  public static boolean isIdentifierChar(int ch)
  {
    int chType = Character.getType(ch);

    return chType == Character.LETTER_NUMBER || chType == Character.LOWERCASE_LETTER
        || chType == Character.TITLECASE_LETTER || chType == Character.UPPERCASE_LETTER
        || chType == Character.DECIMAL_DIGIT_NUMBER || chType == Character.MODIFIER_LETTER
        || chType == Character.OTHER_LETTER || chType == Character.OTHER_NUMBER
        || chType == Character.CONNECTOR_PUNCTUATION;
  }

  private char hedChar() throws IOException
  {
    if (pos == len) {
      if (!refill())
        return EOF;
    }

    return buffer[pos];
  }

  private char nextChar() throws IOException
  {
    if (pos == len) {
      if (!refill())
        return EOF;
    }
    if (buffer[pos] == '\n') {
      lineCount++;
      lineOffset = 1;
    } else
      lineOffset++;
    charCount++;
    return buffer[pos++];
  }

  private char nextHedChar() throws IOException
  {
    nextChar();
    return hedChar();
  }

  private void unChar(char ch) throws IOException
  {
    if (pos == 0) {
      char nbuffer[] = new char[buffer.length + 10];
      for (int ix = 0; ix < buffer.length; ix++)
        nbuffer[ix + 10] = buffer[ix];
      buffer = nbuffer;
      pos = 10;
      len = len + 10;
    }

    if (ch == '\n')
      lineCount--;

    charCount--;
    lineOffset--;
    buffer[--pos] = ch;
  }

  private char buffer[] = new char[1024];
  private int pos = 0;
  private int len = 0;
  private boolean foundEof = false;

  private boolean refill() throws IOException
  {
    if (foundEof)
      return false;
    else {
      try {
        int count = rdr.read(buffer, 0, buffer.length);

        if (count < 0) {
          pos = 0;
          len = 0;
          foundEof = true;
          return false;
        } else {
          pos = 0;
          len = count;
          return true;
        }
      } catch (Exception e) {
        throw new IOException("problem with input", e);
      }
    }
  }

  private void markLocation()
  {
    lineMark = lineCount;
    offsetMark = lineOffset;
    charMark = charCount;
  }

  public Location getLocation()
  {
    return Location.location(uri, charMark, lineMark, offsetMark, charCount - charMark);
  }

  private Location thisLocation()
  {
    return Location.location(uri, charMark, lineCount, lineOffset, charCount - charMark);
  }

  private Token reportToken(Token.TokenType type, String image)
  {
    return new Token(type, image, getLocation(), false);
  }

  private Token reportString(String str, boolean raw)
  {
    return new Token(TokenType.string, str, getLocation(), raw);
  }

  private Token reportBlob(String str, boolean raw)
  {
    return new Token(blob, str, getLocation(), raw);
  }

  private Token reportToken(int ix, boolean raw)
  {
    return new Token(integer, ix, getLocation(), raw);
  }

  private Token reportToken(long ix, boolean raw)
  {
    return new Token(longint, ix, getLocation(), raw);
  }
}