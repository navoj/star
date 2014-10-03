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
mapmerge is package {  
  import treemap;
  main() do {
    var t := trEmpty;
    for x in iota(1, 10, 1) do {
      t[x] := x;
    }
    
    logMsg(info,"depth of t is $(tree_depth(t))");
    
    var u := trEmpty;
    w is _merge(u, t);
    
    logMsg(info,"depth of w is $(tree_depth(w))");
  }
}