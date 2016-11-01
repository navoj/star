package org.star_lang.star.operators.ast;

import java.util.ArrayList;
import java.util.List;

import org.star_lang.star.compiler.ast.ASyntax;
import org.star_lang.star.compiler.ast.AApply;
import org.star_lang.star.compiler.ast.BooleanLiteral;
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
import org.star_lang.star.operators.ast.runtime.AstWithCategory;
import org.star_lang.star.operators.ast.runtime.MergeLocation;


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

public class AstOperators {

  public static void declare() {
    List<IValueSpecifier> specs = new ArrayList<>();
    specs.add(Name.spec());
    specs.add(BooleanLiteral.spec());
    specs.add(StringLiteral.spec());
    specs.add(IntegerLiteral.spec());
    specs.add(LongLiteral.spec());
    specs.add(FloatLiteral.spec());
    specs.add(AApply.spec());

    ITypeDescription locDesc = new CafeTypeDescription(Location.nullLoc, ASyntax.type, ASyntax.class.getName(), specs);

    Intrinsics.declare(locDesc);

    Intrinsics.declare(new Builtin(AstLocation.name, AstLocation.funType(), AstLocation.class));
    Intrinsics.declare(new Builtin(AstMacroKey.name, AstMacroKey.funType(), AstMacroKey.class));
    Intrinsics.declare(new Builtin(AstCategory.name, AstCategory.type(), AstCategory.class));
    Intrinsics.declare(new Builtin(AstWithCategory.name, AstWithCategory.type(), AstWithCategory.class));
    Intrinsics.declare(new Builtin(AstReplace.name, AstReplace.type(), AstReplace.class));
    Intrinsics.declare(new Builtin(MacroError.name, MacroError.type(), MacroError.class));
    Intrinsics.declare(new Builtin(AstFindFree.name, AstFindFree.type(), AstFindFree.class));
    Intrinsics.declare(new Builtin(AstFreeVars.name, AstFreeVars.type(), AstFreeVars.class));
    Intrinsics.declare(new Builtin(MacroDisplay.name, MacroDisplay.type(), MacroDisplay.class));

    Intrinsics.declare(new Builtin(MergeLocation.name, MergeLocation.type(), MergeLocation.class));
    Intrinsics.declare(new Builtin(AstQuoter.name, AstQuoter.type(), AstQuoter.class));
  }

}
