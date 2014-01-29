package org.star_lang.star.operators.string;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.string.runtime.StringOps.*;

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
public class StringOps
{
  public static void declare(Intrinsics cxt)
  {
    cxt.declareBuiltin(new Builtin(StrLength.name, StrLength.type(), StrLength.class));
    cxt.declareBuiltin(new Builtin(StringConcat.name, StringConcat.funType(), StringConcat.class));
    cxt.declareBuiltin(new Builtin(SplitString.name, SplitString.type(), SplitString.class));
    cxt.declareBuiltin(new Builtin(StringExplode.name, StringExplode.type(), StringExplode.class));
    cxt.declareBuiltin(new Builtin(String2Array.name, String2Array.type(), String2Array.class));
    cxt.declareBuiltin(new Builtin(StringImplode.name, StringImplode.type(), StringImplode.class));
    cxt.declareBuiltin(new Builtin(StringRevImplode.name, StringRevImplode.type(), StringRevImplode.class));
    cxt.declareBuiltin(new Builtin(StringReplace.name, StringReplace.type(), StringReplace.class));
    cxt.declareBuiltin(new Builtin(StringReverse.name, StringReverse.type(), StringReverse.class));
    cxt.declareBuiltin(new Builtin(Spaces.name, Spaces.type(), Spaces.class));
    cxt.declareBuiltin(new Builtin(StringQuote.name, StringQuote.type(), StringQuote.class));

    cxt.declareBuiltin(new Builtin(StringPair.name, StringPair.type(), StringPair.class));
    cxt.declareBuiltin(new Builtin(StringBack.name, StringBack.type(), StringBack.class));
    cxt.declareBuiltin(new Builtin(StringCons.name, StringCons.type(), StringCons.class));
    cxt.declareBuiltin(new Builtin(StringAppend.name, StringAppend.type(), StringAppend.class));
    cxt.declareBuiltin(new Builtin(StringFilter.name, StringFilter.type(), StringFilter.class));
    cxt.declareBuiltin(new Builtin(StringIterate.name, StringIterate.type(), StringIterate.class));
    cxt.declareBuiltin(new Builtin(StringIxIterate.name, StringIxIterate.type(), StringIxIterate.class));
    cxt.declareBuiltin(new Builtin(StringChar.name, StringChar.type(), StringChar.class));
    cxt.declareBuiltin(new Builtin(SubstituteChar.name, SubstituteChar.type(), SubstituteChar.class));
    cxt.declareBuiltin(new Builtin(CharPresent.name, CharPresent.type(), CharPresent.class));
    cxt.declareBuiltin(new Builtin(SubString.name, SubString.type(), SubString.class));
    cxt.declareBuiltin(new Builtin(DeleteChar.name, DeleteChar.programType(), DeleteChar.class));
    cxt.declareBuiltin(new Builtin(StringSplice.name, StringSplice.programType(), StringSplice.class));
    cxt.declareBuiltin(new Builtin(StringSlice.name, StringSlice.type(), StringSlice.class));
    cxt.declareBuiltin(new Builtin(StringConcatenate.name, StringConcatenate.funType(), StringConcatenate.class));
    cxt.declareBuiltin(new Builtin(StringFind.name, StringFind.funType(), StringFind.class));

    cxt.declareBuiltin(new Builtin(StringFormat.name, StringFormat.type(), StringFormat.class));

    cxt.declareBuiltin(new Builtin(GenerateSym.name, GenerateSym.programType(), GenerateSym.class));
    cxt.declareBuiltin(new Builtin(ToUpperCase.name, ToUpperCase.type(), ToUpperCase.class));
    cxt.declareBuiltin(new Builtin(ToLowerCase.name, ToLowerCase.type(), ToLowerCase.class));

  }
}
