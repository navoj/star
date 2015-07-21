package org.star_lang.star.compiler.grammar;

import org.star_lang.star.compiler.grammar.Token.TokenType;
import org.star_lang.star.compiler.standard.StandardNames;

/**
 * Look for operator declarations so that the character map can be updated with the new token.
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
public class OperatorListener implements TokenListener
{
  private enum State {
    idle, gotHash, gotDecl, gotOpenParen
  }

  private State listenerState = State.idle;
  private final TokenChar charMap;

  public OperatorListener(TokenChar charMap)
  {
    this.charMap = charMap;
  }

  // look for
  // #infix("oper" ...

  @Override
  public void newToken(Token token)
  {
    switch (listenerState) {
    case idle:
      if (token.getType() == TokenType.identifier && token.getImage().equals(StandardNames.META_HASH))
        listenerState = State.gotHash;
      break;
    case gotHash:
      if (token.getType() == TokenType.identifier) {
        switch (token.getImage()) {
        case StandardNames.PREFIX:
        case StandardNames.PREFIXA:
        case StandardNames.INFIX:
        case StandardNames.LEFT:
        case StandardNames.RIGHT:
        case StandardNames.POSTFIX:
        case StandardNames.POSTFIXA:
        case StandardNames.TOKEN:
          listenerState = State.gotDecl;
          break;
        default:
          listenerState = State.idle;
        }
      } else
        listenerState = State.idle;
      break;
    case gotDecl:
      if (token.getType() == TokenType.identifier && token.getImage().equals(OpGrammar.LPAR))
        listenerState = State.gotOpenParen;
      else
        listenerState = State.idle;
      break;
    case gotOpenParen:
      if (token.getType() == TokenType.string) {
        String op = token.getImage();
        if (!Tokenizer.isIdentifierStart(op))
          TokenChar.recordToken(charMap, op);
      }
      listenerState = State.idle;
      break;
    default:
      listenerState = State.idle;
      break;
    }
  }

}
