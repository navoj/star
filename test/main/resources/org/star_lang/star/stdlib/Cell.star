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
/*
 * Mutable reference cells.
 */

Cell is package {

import Prelude;

type CellInstruction of %a is CellGet or CellSet(%a);

type Cell of %a is alias of ((CellInstruction of %a) => Maybe of %a);

makeCell has type (%a) => Cell of %a;
makeCell(a) is
	let {
		var x := a;  
	} in (function (i) is
			case i in {
				CellGet is Just(x);
				CellSet(n) is valof {
					x := n;
					valis Nothing
				}
			});

cellRef has type (Cell of %a) => %a;
cellRef(c) is case c(CellGet) in { Just(x) is x };

cellSet has type action(Cell of %a, %a);
cellSet(c, x) do {
	_ is c(CellSet(x));		
}

}