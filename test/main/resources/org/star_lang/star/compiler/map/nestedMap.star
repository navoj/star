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
nestedMap is package{
  MM has type map of (string,map of (string,integer));
  MM is map of {"alpha" -> map of {"beta"->2; "gamma"->3};
                "delta" -> map of {}};
  
  main() do {
    assert size(MM)=2;
    assert size(MM["alpha"])=2;
    
    assert present MM["delta"];
    assert not present MM["beta"];
    assert present MM["alpha"]["beta"];
  }
}