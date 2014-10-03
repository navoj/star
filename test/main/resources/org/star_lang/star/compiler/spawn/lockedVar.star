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
lockedVar is package{
  type cell of %t is cell(%t);
  
  getLocked(C) is valof{
    sync(C){
      valis deRef(C);
    }
  }
  
  getLockedV2(C) is valof{
    XX is valof{
      sync(C){
        valis C
      }
    };
    valis deRef(XX);
  }
  
  deRef(cell(X)) is X;
  
  main() do {
    C is cell(34);
    
    assert getLocked(C)=34;
    
    assert getLockedV2(C)=34;
  }
}