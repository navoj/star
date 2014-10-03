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
/*
 * Unique identifiers with (in the future) fast lookup.
 */

Id is package {
import Prelude;
import PPrint;

type Id is Id{
	prettyName has type string;
	uniqueName has type string;
	numbers has type list of integer;
};

IdNamed(p) from Id{ prettyName = p; };

/* We'd like to do this, but Star compiler bug:

implementation display over Id is {
	display = displayId;
} using {
	displayId(i) is "Id(" ++ i.prettyName ++ "/" ++ i.uniqueName ++ ")";
};
*/

displayId has type (Id) => string;
displayId(i) is "Id(" ++ i.prettyName ++ "/" ++ i.uniqueName ++ ")"

showId has type (Id) => Doc;
showId(i) is docText(displayId(i));

idUniqueChars is list of ["$", "%", "&", "/",
		"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
		"L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
		"W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g",
		"h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
		"s", "t", "u", "v", "w", "x", "y", "z"]; 
idUniqueCharsCount is size(idUniqueChars);

makeUniqueString has type (integer) => string;
makeUniqueString(n) is valof {
	var i := 0;
	var s := "";
	while i < n do {
		s := s ++ idUniqueChars[random(idUniqueCharsCount)];
		i := i + 1;
	}
	valis s;
}

idUniqueNameSize is 32;
idRandomRange is 2147483648;

makeId has type (string) => Id;
makeId(pretty) is
	Id{
		prettyName = pretty;
		uniqueName = makeUniqueString(idUniqueNameSize);
		-- yes, I do know it's a hack
		numbers = list of[ random(idRandomRange),
						 random(idRandomRange),
						 random(idRandomRange),
						 random(idRandomRange),
						 random(idRandomRange),
						 random(idRandomRange),
						 random(idRandomRange),
						 random(idRandomRange) ];
	};

idEqual(id1, id2) is id1.uniqueName = id2.uniqueName;

/* #### Star drops the ball:
implementation equality over Id is {
	(=) = idEqual;
};
*/

idCompare has type (Id, Id) => Ordering;
idCompare(Id{numbers = ns1}, Id{numbers = ns2}) is valof {
	for n1[i1] in ns1 do {
		n2 is ns2[i1];
		if n1 < n2
		then valis LT
		else if n2 < n1
		then valis GT
	}
	valis EQ;
};

idLess(id1, id2) is idCompare(id1, id2) = LT;
idLessEqual(id1, id2) is
	case idCompare(id1, id2) in {
		LT is true;
		EQ is true;
	} default false;
idGreater(id1, id2) is idCompare(id1, id2) = GT;
idGreaterEqual(id1, id2) is
	case idCompare(id1, id2) in {
		LT is false;
		EQ is true;
	} default true;

implementation comparable over Id is {
	(<) = idLess;
	(<=) = idLessEqual;
	(>) = idGreater;
	(>=) = idGreaterEqual;
};

}