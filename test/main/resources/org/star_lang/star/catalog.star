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
catalog is package{
	type catalog of %t is manifest{
		entries has type map of (string,catalogReference of %t);
	}
	
	type catalogReference of %t is local(catalogEntry of %t)
	                            or remote{uri has type uri of %t};
	
	type catalogEntry of %t is entry(%t)
	                        or catalog(catalog of %t);
	
	type uri of %t is uri{
		scheme has type string;
		ref has type string;
		resolve has type ()=>%t;
	}
}