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
dependency is package{

  type stack of %t is alias of list of %t;

  analyseGraph(G) is let{
    var nodes := keys(G);
    var groups := list of {};
    
    analyseNode(N, S) is valof{
      var L0 is size(S);
      
      var Stk := stack of {N;..S};
      var Low := L0;
      
      for Cn in G[N] do{
        (Stk,Low) := analyseConnection(Cn,Stk,Low);
      }
      
      if Low>=L0 then{
        popStack(Stk, L0);
        valis (S,Low)
      }
      else
        valis (Stk,Low)
    };
    
    analyseConnection(N,Stack,Low) is valof{
      Ix is indexOf(Stack,N);

      if Ix>=0 then valis (Stack,min(Ix,Low)) 
      else if N in nodes then {
        delete (NN where NN=N) in nodes;
        
        (nStack,nLow) is analyseNode(N,Stack);
        valis (nStack,min(Low,nLow));
      }
      else
        valis (Stack,Low);
    };
    
    popStack has type (stack of %t,integer)=>() where pPrint over %t;
    popStack(S,L0) do {
      rel is S[0:(size(S)-L0)];
      groups := list of [groups..,rel];
    }
   
    sortGraph() is valof{
      while not isEmpty(nodes) do{
        N is someValue(nodes[0]);
        remove nodes[0];

        ignore analyseNode(N,stack of {});
      }
      valis groups;
    }
      
  } in sortGraph();
  
  keys(M) is list of {all K where K->V in M};
 
  indexOf(S,N) is valof{
	var SS := S;
	var Ix := size(S)-1;
	while SS matches list of [E,..R] do {
      if E=N then
        valis Ix
      else{
		SS := R;
		Ix := Ix-1;
      }
    }
	valis -1;
  };
  
  type node is Nd(string);
  
  main() do {
	G is dictionary of{
	  Nd("A") -> list of [Nd("D")];
	  Nd("B") -> list of [Nd("C"), Nd("F")];
	  Nd("C") -> list of [Nd("D")];
	  Nd("D") -> list of [Nd("B")];
	  Nd("E") -> list of [Nd("F"), Nd("G")];
	  Nd("F") -> list of [Nd("E"), Nd("G")];
	  Nd("G") -> list of []
	};
	
	Sorted is analyseGraph(G);
   
	logMsg(info,"sorted graph is $Sorted");
	
	assert Sorted=list of [ list of [Nd("G")],
	                        list of [Nd("E"), Nd("F")],
	                        list of [Nd("C"), Nd("B"), Nd("D")],
	                        list of [Nd("A")]
	                       ];
  };
}
