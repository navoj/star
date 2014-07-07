package org.star_lang.star.operators.ast;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.ast.ASyntax;
import org.star_lang.star.compiler.ast.Apply;
import org.star_lang.star.compiler.ast.BigDecimalLiteral;
import org.star_lang.star.compiler.ast.BooleanLiteral;
import org.star_lang.star.compiler.ast.CharLiteral;
import org.star_lang.star.compiler.ast.FloatLiteral;
import org.star_lang.star.compiler.ast.IntegerLiteral;
import org.star_lang.star.compiler.ast.LongLiteral;
import org.star_lang.star.compiler.ast.MacroDisplay;
import org.star_lang.star.compiler.ast.MacroError;
import org.star_lang.star.compiler.ast.Name;
import org.star_lang.star.compiler.ast.StringLiteral;
import org.star_lang.star.compiler.cafe.type.CafeTypeDescription;
import org.star_lang.star.data.type.ITypeDescription;
import org.star_lang.star.data.type.IValueSpecifier;
import org.star_lang.star.data.type.Location;
import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.ast.runtime.AstCategory;
import org.star_lang.star.operators.ast.runtime.AstFindFree;
import org.star_lang.star.operators.ast.runtime.AstFreeVars;
import org.star_lang.star.operators.ast.runtime.AstLocation;
import org.star_lang.star.operators.ast.runtime.AstMacroKey;
import org.star_lang.star.operators.ast.runtime.AstQuoter;
import org.star_lang.star.operators.ast.runtime.AstReplace;
import org.star_lang.star.operators.ast.runtime.MergeLocation;

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

public class AstOperators
{

  public static void declare(Intrinsics cxt)
  {
    List<IValueSpecifier> specs = new ArrayList<IValueSpecifier>();
    specs.add(Name.spec());
    specs.add(BooleanLiteral.spec());
    specs.add(CharLiteral.spec());
    specs.add(StringLiteral.spec());
    specs.add(IntegerLiteral.spec());
    specs.add(LongLiteral.spec());
    specs.add(FloatLiteral.spec());
    specs.add(BigDecimalLiteral.spec());
    specs.add(Apply.spec());

    ITypeDescription locDesc = new CafeTypeDescription(Location.nullLoc, ASyntax.type, ASyntax.class.getName(), specs);

    cxt.defineType(locDesc);

    cxt.declareBuiltin(new Builtin(AstLocation.name, AstLocation.funType(), AstLocation.class));
    cxt.declareBuiltin(new Builtin(AstMacroKey.name, AstMacroKey.funType(), AstMacroKey.class));
    cxt.declareBuiltin(new Builtin(AstCategory.name, AstCategory.type(), AstCategory.class));
    cxt.declareBuiltin(new Builtin(AstReplace.name, AstReplace.type(), AstReplace.class));
    cxt.declareBuiltin(new Builtin(MacroError.name, MacroError.type(), MacroError.class));
    cxt.declareBuiltin(new Builtin(AstFindFree.name, AstFindFree.type(), AstFindFree.class));
    cxt.declareBuiltin(new Builtin(AstFreeVars.name, AstFreeVars.type(), AstFreeVars.class));
    cxt.declareBuiltin(new Builtin(MacroDisplay.name, MacroDisplay.type(), MacroDisplay.class));

    cxt.declareBuiltin(new Builtin(MergeLocation.name, MergeLocation.type(), MergeLocation.class));
    cxt.declareBuiltin(new Builtin(AstQuoter.name, AstQuoter.type(), AstQuoter.class));
  }

}
