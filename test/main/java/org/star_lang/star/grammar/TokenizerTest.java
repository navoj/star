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
    String str = "-- a test\n123.45 123 123.45f NAME ,..;;., ;; .., $$. $$ f .. # #$ [ [: [:] [:=] [::=] [:::] [:: _var _ nihon\u3000\u3053\u3068\u3048\u308a  `a`  `A.*[a-z]` \"\\u23; \" { } \n( ) begin 'n 's 's' 'y' 'm' 'b' '\\n' '\\'' \"a string\\n\" #[ ]# ]## [ ] #()# #(())# #())# end ==> # ## == is $$ #$ != = ** /**/*/**/";
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
