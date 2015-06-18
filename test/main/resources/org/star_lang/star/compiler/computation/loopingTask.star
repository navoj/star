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
loopingTask is package{
  import task;
  
  type found of %t is found(%t) or notFound; 
  
  fun ww(X) is task{
    var C := 0;
    for E in X do
      C := C+E;
    valis C;
  };
  
  fun ff(K,L) is task{
    for (KK,V) in L do{
      if K=KK then
        valis found(V);
    };
    valis notFound;
  };
  
  prc main() do {
    def ZZ is valof ww(list of {1;2;3;4;5});
    assert ZZ=15;
    
    def MM is list of {(1,"alpha"); (2,"beta"); (3,"gamma"); (4,"delta")};
    def T1 is ff(3,MM);
    logMsg(info,"not yet started: $T1");
    def V1 is valof T1;
    logMsg(info,"value of search is $V1");
    assert V1=found("gamma");
    assert valof ff(5,MM)=notFound;
  }
}
