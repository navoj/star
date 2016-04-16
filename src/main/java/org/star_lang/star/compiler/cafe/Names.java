package org.star_lang.star.compiler.cafe;

import java.util.Set;
import java.util.TreeSet;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.util.StringIterator;
import org.star_lang.star.operators.general.runtime.Assert;
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
public class Names
{
  public static final Set<String> keywords = new TreeSet<>();
  public static final Set<String> graphic = new TreeSet<>();

  public static final String IMPORT = keyword("import");
  public static final String JAVA = keyword("java");

  public static final String TYPE = keyword("type");
  public static final String CONSPEC = keyword("constructor");
  public static final String RECORD = keyword("record");
  public static final String LET = keyword("let");
  public static final String CASE = keyword("case");
  public static final String SWITCH = keyword("switch");
  public static final String DEFLT = keyword("default");
  public static final String IN = keyword("in");
  public static final String VALOF = keyword("valof");
  public static final String VALIS = keyword("valis");
  public static final String VAR = keyword("var");
  public static final String REF = keyword("ref");
  public static final String IS = keyword("is");
  public static final String DO = keyword("do");
  public static final String LOOP = keyword("loop");
  public static final String WHILE = keyword("while");
  public static final String IF = keyword("if");
  public static final String THEN = keyword("then");
  public static final String ELSE = keyword("else");
  public static final String MATCH = keyword("match");

  public static final String CATCH = keyword("catch");
  public static final String EXCEPTION_VAR = "__exception";
  public static final String THROW = keyword("throw");

  public static final String REQUIRING = keyword("requiring");

  public static final String NOTHING = keyword("nothing");
  public static final String VOID = keyword("void");

  public static final String TILDA = graphic("~");
  public static final String COLON = graphic(":");
  public static final String QQUERY = graphic("??");
  public static final String ASSIGN = graphic(":=");
  public static final String ASSERT = Assert.name;
  public static final String IGNORE = keyword("ignore");
  public static final String ARROW = graphic("=>");
  public static final String MEMO = keyword("memo");
  public static final String LARROW = graphic("<=");
  public static final String BIARROW = graphic("<=>");
  public static final String THIN_ARROW = graphic("->");
  public static final String AND = keyword("and");
  public static final String OR = keyword("or");
  public static final String NOT = keyword("not");
  public static final String PERIOD = graphic(".");

  public static final String FACE = "__face";

  public static final String TRUE = keyword("true");
  public static final String FALSE = keyword("false");

  public static final String RAW_BOOL_TYPE = keyword("_bool");
  public static final String RAW_CHAR_TYPE = keyword("char_");
  public static final String RAW_INT_TYPE = keyword("integer_");
  public static final String RAW_LONG_TYPE = keyword("long_");
  public static final String RAW_FLOAT_TYPE = keyword("float_");
  public static final String RAW_DECIMAL_TYPE = keyword("decimal_");
  public static final String RAW_BINARY_TYPE = keyword("binary_");
  public static final String RAW_STRING_TYPE = keyword("string_");
  public static final String RAW_FILE_TYPE = keyword("file_");

  public static final String PRIVATE_THIS = keyword("this$");
  public static final String ARG_ARRAY = keyword("args$");
  public static final String PTN_ARG = keyword("arg$");
  public static final String ENTER = keyword("enter");
  public static final String MAIN = "main";
  public static final String ENTERFUNCTION = "enter";

  public static final String CAFE_MANIFEST = "cafeManifest";
  public static final String CLASS_ROOT = "classRoot";
  public static final String PKG = "$pkg$";

  public static final String BLOCK = keyword("{}");
  public static final String FUNCTION = keyword("function");
  public static final String PATTERN = keyword("pattern");
  public static final String NULL_PTN = keyword("$null");
  public static final String FCALL = keyword("funcall");
  public static final String ESCAPE = keyword("escape");
  public static final String CONSTRUCT = keyword("construct");
  public static final String TUPLE = keyword("tuple");
  public static final String REGEXP = keyword("regexp");
  public static final String VALUEPTN = keyword("value");

  public static final String BUILTIN = keyword("builtin");
  public static final String TYPEVAR = graphic("%");
  public static final String EXISTS = keyword("exists");
  public static final String FORALL = keyword("forall");

  public static final String COPY = keyword("copy");

  protected static String keyword(String word)
  {
    word = word.intern();
    assert !keywords.contains(word);
    keywords.add(word);
    return word;
  }

  public static boolean isKeyword(IAbstract term)
  {
    if (term instanceof Name) {
      return isKeyword(((Name) term).getId());
    } else
      return false;
  }

  public static boolean isKeyword(String word)
  {
    return keywords.contains(word) || graphic.contains(word);
  }

  protected static String graphic(String word)
  {
    graphic.add(word);
    return word;
  }

  public static String capName(String prefix, String name)
  {
    StringBuilder blder = new StringBuilder();

    StringIterator it = new StringIterator(name);
    if (it.hasNext()) {
      int first = it.next();

      if (Character.isLowerCase(first)) {
        blder.append(prefix);
        blder.appendCodePoint(Character.toUpperCase(first));

        while (it.hasNext())
          blder.appendCodePoint(it.next());
      } else {
        blder.append(prefix);
        blder.append("_");
        blder.append(name);
      }
    }

    return blder.toString();
  }
}
