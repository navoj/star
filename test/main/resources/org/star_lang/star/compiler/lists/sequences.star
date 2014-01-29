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
sequences is package{
  -- exercise the sequence notation
  
  funnyReverse has type (%s)=>%s where sequence over %s determines %e;
  funnyReverse(sequence of {}) is sequence of {};
  funnyReverse(sequence of {H;..M..;B}) is sequence of {B;..funnyReverse(M)..;H};
  
  litCons is cons of {2;3};
  
  backEnd is cons of {litCons..;4;5};
  
  frontEnd is cons of {0;1;..litCons};
  
  litQueue is queue of {2;3};
  
  enQ is queue of {litQueue..; 4; 5};
  
  fQ is queue of {0;1;..enQ};
  
  main() do {
    assert litCons = cons of {2;3};
    
    assert backEnd = cons of {2;3;4;5};
    
    assert frontEnd = cons of {0;1;2;3};
    
    assert funnyReverse(frontEnd) = cons of {3;2;1;0};
    
    logMsg(info,"funny reverse of $fQ is $(funnyReverse(fQ))");
    
    assert funnyReverse(fQ) = queue of {5; 4; 3; 2; 1; 0};
    
    logMsg(info,"concat of queue of {0;1} and $enQ is $(queue of {0;1} ++ enQ)");
    
    assert queue of {0;1} ++ enQ = fQ;
    
    var IQ := queue of {};
    for ix in iota(1,100,1) do{
      IQ := queue of {IQ..;ix}
      -- logMsg(info,"IQ after $ix is $(__display(IQ))");
    }
    
    for ix in IQ do
      logMsg(info,"el=$ix");
    logMsg(info,"IQ=$IQ");
  }
}