package org.star_lang.star.grammar;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.star_lang.star.compiler.ErrorReport;
import org.star_lang.star.compiler.grammar.Token;
import org.star_lang.star.compiler.grammar.Tokenizer;
import org.star_lang.star.compiler.grammar.Token.TokenType;
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
public class TokenizerTest
{

  @Before
  public void setUp() throws Exception
  {
  }

  // @Test
  public void testBlanks() throws IOException
  {
    try (Reader strReader = new StringReader("--  line com/*  ment  \n--\n/*thisi s-- a com\nment /*/\n /**//*a*/  \n")) {

      ErrorReport errors = new ErrorReport();
      Tokenizer tok = new Tokenizer(ResourceURI.noUriEnum, errors, strReader, null);

      assert (tok.headToken().getType() == TokenType.terminal);
      if (!errors.isErrorFree())
        Assert.fail(errors.toString());
    }
  }

  @Test
  public void testTokens() throws IOException
  {
    String str = "-- a test\n123.45 123 123.45f name ;..;;.; ;; ..; $$. $$ f .. # #$ [ [: [:] [:=] [::=] [:::] [:: _var _ nihon\u3000\u3053\u3068\u3048\u308a  `a`  `A.*[a-z]` \"\\u23; \" { } \n( ) begin 'n 's 's' 'y' 'm' 'b' '\\n' '\\'' \"a string\\n\" #[ ]# ]## [ ] #()# #(())# #())# end ==> # ## == is $$ #$ != = ** /**/*/**/";
    try (Reader strReader = new StringReader(str)) {
      ErrorReport errors = new ErrorReport();
      Tokenizer tok = new Tokenizer(ResourceURI.noUriEnum, errors, strReader, null);

      Token token;
      do {
        token = tok.nextToken();
        if (token != null)
          token.display();
        else
          break;
      } while (token.getType() != TokenType.terminal);

      if (!errors.isErrorFree()) {
        Assert.fail(errors.toString());
      }
    }
  }
}
