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
deepRecord is package{
  -- Test deeper access to records
  
  type outer is outer{
    name has type string;
    inner has type inner;
  };
  
  type inner is inner{
    val has type integer;
  }
  
  main() do {
    O is outer{ name="fred"; inner=inner{val=10}};
    
    assert O.name="fred";
    assert O.inner.val=10;
    
    R is list of [ O];
    
    XX is anyof X.name where X in R and X.inner.val=10;
    assert XX = "fred"
  }
}