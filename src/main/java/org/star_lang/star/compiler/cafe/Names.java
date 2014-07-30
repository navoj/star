package org.star_lang.star.compiler.cafe;

import java.util.Set;
import java.util.TreeSet;

import org.star_lang.star.compiler.ast.IAbstract;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.util.StringIterator;
import org.star_lang.star.operators.general.runtime.Assert;

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
public class Names
{
  public static final Set<String> keywords = new TreeSet<String>();
  public static final Set<String> graphic = new TreeSet<String>();

  public static final String FROM = keyword("from");
  public static final String IMPORT = keyword("import");
  public static final String JAVA = keyword("java");
  public static final String CAFE = keyword("cafe");

  public static final String TYPE = keyword("type");
  public static final String CONSPEC = keyword("constructor");
  public static final String VARCON = keyword("varcon");
  public static final String RECORD = keyword("record");
  public static final String LET = keyword("let");
  public static final String CASE = keyword("case");
  public static final String SWITCH = keyword("switch");
  public static final String DEFLT = keyword("default");
  public static final String IN = keyword("in");
  public static final String VALOF = keyword("valof");
  public static final String VALIS = keyword("valis");
  public static final String LAMBDA = keyword("lambda");
  public static final String VAR = keyword("var");
  public static final String REF = keyword("ref");
  public static final String LABELED = graphic("::");
  public static final String LEAVE = keyword("leave");
  public static final String GOTO = keyword("goto");
  public static final String IS = keyword("is");
  public static final String DO = keyword("do");
  public static final String LOOP = keyword("loop");
  public static final String WHILE = keyword("while");
  public static final String IF = keyword("if");
  public static final String THEN = keyword("then");
  public static final String ELSE = keyword("else");
  public static final String MATCH = keyword("match");

  public static final String TRY = keyword("try");
  public static final String CATCH = keyword("catch");
  public static final String EXCEPTION_VAR = "__exception";
  public static final String THROW = keyword("throw");

  public static final String REQUIRING = keyword("requiring");
  public static final String WITH = keyword("with");
  public static final String SYNC = keyword("sync");
  public static final String WAIT = "_wait";

  public static final String NOTHING = keyword("nothing");
  public static final String VOID = keyword("void");

  public static final String PRCENT = graphic("%");
  public static final String TILDA = graphic("~");
  public static final String COLON = graphic(":");
  public static final String DCOLON = graphic("::");
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

  public static final String DOLLAR = graphic("$");
  public static final String FACE = "__face";

  public static final String ANONYMOUS = "_";

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
  public static final String STRING_TYPE = keyword("string");
  public static final String RAW_FILE_TYPE = keyword("file_");

  public static final String BOOLEAN = "boolean";

  public static final String THIS = keyword("this");
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
  public static final String CONTINUE = keyword("continue");
  public static final String FCALL = keyword("funcall");
  public static final String ESCAPE = keyword("escape");
  public static final String CONSTRUCT = keyword("construct");
  public static final String REGEXP = keyword("regexp");

  public static final String APPLY = keyword("apply");

  public static final String BUILTIN = keyword("builtin");
  public static final String TYPEVAR = graphic("%");
  public static final String EXISTS = keyword("exists");
  public static final String FORALL = keyword("forall");
  public static final String ACTION = keyword("action");

  public static final String COPY = keyword("copy");

  public static final String YIELD = keyword("yield");

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
