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
anonRefRecord is package{
  type socialNetworkType is alias of {
    nodeTable has type list of socialNetNode;
    linkTable has type list of socialNetLink;
  };

  -- The actual (not-yet-active) model, an (initially empty) social network
  socialNetwork has type ref(socialNetworkType);
  var socialNetwork := {
      nodeTable = list of []; 
      linkTable = list of [];
  };
  
  main() do {
    assert socialNetwork.nodeTable = list of []
  }
}