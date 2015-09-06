package org.star_lang.star.compiler.grammar;

import org.star_lang.star.compiler.grammar.Token.TokenType;
import org.star_lang.star.compiler.standard.StandardNames;


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
