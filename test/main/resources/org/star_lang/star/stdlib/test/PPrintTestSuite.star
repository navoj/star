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
PPrintTestSuite is package {
import StdLib;

#left((tstThen), 40);
#left((tstBind), 40);

#left((docConcatBreak), 40);

binop(left, op, right) is
	docGroup(docNest(2,
				docGroup(docText(left) docConcatBreak docText(op))
				docConcatBreak
				docText(right)));

cond is binop("a", "==", "b");
expr1 is binop("a", "<<", 2);
expr2 is binop("a", "+", "b");

ifthen(c, e1, e2) is
	docGroup(docGroup(docNest(2, docText("if") docConcatBreak c))
			 docConcatBreak
			 docGroup(docNest(2, docText("then") docConcatBreak e1))
			 docConcatBreak
			 docGroup(docNest(2, docText("else") docConcatBreak e2)));

doc is ifthen(cond, expr1, expr2);

ppCondTest is TestLabel("cond",
	TestOne(
		AssertEqual("32",
					"if a == b then a << 2 else a + b",
					docPretty(32, doc))
			tstThen
		AssertEqual("10",
		 			"if a == b\nthen\n  a << 2\nelse a + b",
		 			docPretty(10, doc))
		 	tstThen
		AssertEqual("8",
					"if\n  a == b\nthen\n  a << 2\nelse\n  a + b",
					docPretty(8, doc))
			tstThen
		AssertEqual("7",
					"if\n  a ==\n    b\nthen\n  a <<\n    2\nelse\n  a + b",
					docPretty(7, doc))
			tstThen
		AssertEqual("6",
					"if\n  a ==\n    b\nthen\n  a <<\n    2\nelse\n  a +\n    b",
					docPretty(6, doc))
	));

ppTest is TestLabel("PPrint",
	TestList(list_to_List(list{ppCondTest})));
			
}