delta is package {
  -- import quick;
  import metamodeldefn;
  
  type delta is noChange or delta(location, dOperation, ((%t)=>%t), %t);

  type location is alias of string;
 
  type dOperation is dOprCreate or dOprUpdate or dOprDelete or dOprTruncatelistAt or dOprUpsert or dNoOp;
 
  contract diffable of %t is {
    diff has type (%t,%t,location) => list of delta;
  }

  -- Utility function to visualize the result of 'diff'
  showChanges has type (list of delta, %t) => boolean;
  showChanges(list of [], X) is true;
  showChanges(list of [noChange,..D], X) is showChanges(D, X);
  showChanges(list of [delta(Loc, Opr, F, V),..D], X) is valof {
  	-- logMsg(info, "DELTA:=> Apply Operation: $Opr at Location: $(Loc) :: Change:-> $(F(X))");
  	logMsg(info, "DELTA:=> Apply Operation: $Opr at Location: $(Loc) :: Change:-> $(V)");
  	valis showChanges(D, X);
  }
  
  -- Utility function to apply all delta's to Old Model
  apply has type (list of delta, %t) => %t;
  apply(list of [],X) is X;
  apply(list of [delta(Loc, Opr, F, V),..D],X) is 
    valof{
	  D1 is F(X);
	  -- logMsg(info,"applying delta at $Loc is $D1");
	  valis apply(D,D1);
  };
  
  -- More utility functions...
  listToRel(L) is __list2relation(L);
  relToList(R) is __relation2list(R);

  relToSortedList(R) is all XX where XX in R order by XX;
  sortRel(RR) is valof{
     L is all XX where XX in RR order by XX;
     valis (relation of {for Elm[n] in L do elemis Elm})};
 
   	-- Constant function...
  KFunc has type (%t)=>((%t)=>%t);   	
  KFunc(X) is (function(_) is X);
 
 
  -- For lists...  

  implementation diffable over (list of %t where diffable over %t) is {
    diff = listDiff(0);
  } using {
	listDiff(Ix) is (function(X1,X2,Loc) is listDiffer(X1,X2,Ix,Loc));
		
	listDiffer(X,X,_,_) is list of [noChange];
	listDiffer(list of [H1,..T1],list of [H2,..T2],Ix,Loc) is let {
	  HD is diff(H1, H2, listElLoc(Loc, Ix))
	  HT is listDiffer(T1,T2,Ix+1,Loc);
    } in HD++HT;
    listDiffer(list of [],L2,Ix,Loc) is list of [delta(listElLoc(Loc, Ix), dOprUpsert, (function(L) is L2), L2)];
    listDiffer(L1,list of [],Ix,Loc) is list of [delta(listElLoc(Loc, Ix), dOprTruncatelistAt, (function(L) is front(L,Ix)), front(L1, Ix))];

    front(A, Ix) is A[0:Ix];
    listElLoc(Loc,Ix) is "$Loc[$Ix]";
  }


  -- For Maps...
  hashRemoveFn(M, K1) is dictionary of [];
  
  hashUpsertFn(M, K2, V) is valof{
   var tm := dictionary of [];
   set K2 in tm to V;
   valis (tm);
  }

  implementation diffable over (dictionary of (%k,%v) where diffable over %k and diffable over %v) is {
   diff = hashDiff();
  } using {
    hashDiff() is (function(X1, X2, Loc) is hashDiffer(X1, X2, Loc));

    hashDiffer has type (dictionary of (%k, %v), dictionary of (%k, %v), string) => list of delta;
    hashDiffer(M1, M2, Loc) is valof {
	  
	  D1 is list of {
		  for(K1->V1 in M1) do {
		    if not (K1->V1 in M2) then
		      elemis(hashRemove(M1, K1, Loc));
		  }
	  }
	  
	  D2 is list of {
	  	for(K2->V2 in M2) do {
	  	  if not (K2->V2 in M1) then
	  	    elemis(hashUpsert(M1, K2, V2, Loc));
	  	}
	  }
	  valis(D1++D2);
	}   

   hashRemove(M, KK, Loc) is delta(hashElLoc(Loc, KK), dOprDelete, (function(MM) is hashRemoveFn(MM, KK)), hashRemoveFn(M, KK));
   hashUpsert(M, KK, V, Loc) is delta(hashElLoc(Loc, KK), dOprUpsert, (function(MM) is hashUpsertFn(MM, KK, V)), hashUpsertFn(M, KK, V));
   
   hashElLoc(Loc,Key) is "$Loc.$Key";
  }


  -- For Relations...
  relRemoveFn(R, El) is list of [];
  relUpsertFn(R, El) is relation{El};
    
  implementation diffable over relation of %t is {
  	diff = relDiff();  
  } using {
    relDiff() is (function(R1, R2, Loc) is relDiffer(R1, R2, Loc));
    
    relDiffer has type (relation of %t, relation of %t, string) => list of delta;
    relDiffer(R1, R2, Loc) is valof {
      D1 is list of {
        for X in R1 do {
          if not (X in R2) then
            elemis(relRemove(R1, X, Loc));
        }
      }
      D2 is list of {
        for X in R2 do {
          if not (X in R1) then
            elemis(relUpsert(R1, X, Loc));
        }
      }
      valis(D1++D2);
    }
    relRemove(R, E, Loc) is delta(relElLoc(Loc, E), dOprDelete, (function(RR) is relRemoveFn(RR, E)), relRemoveFn(R, E));
    relUpsert(R, E, Loc) is delta(relElLoc(Loc, E), dOprUpsert, (function(RR) is relUpsertFn(RR, E)), relUpsertFn(R, E));
    
    relElLoc(Loc, Ele) is "$Loc[$Ele]";
  }
    

  -- For all basic standard types...
  
  implementation diffable over any is {
    diff = scalarDiff();
  } using {
    scalarDiff() is (function(X1,X2,Loc) is scalarDiffer(X1,X2,Loc));
    scalarDiffer(I,I,_) is list of [];
    scalarDiffer(I1,I2,Loc) is list of [delta(Loc, dOprUpdate, KFunc(I2), I2)]
  }
  
  implementation diffable over void is {
    diff = scalarDiff();
  } using {
    scalarDiff() is (function(X1,X2,Loc) is scalarDiffer(X1,X2,Loc));
    scalarDiffer(I,I,_) is list of [];
    scalarDiffer(I1,I2,Loc) is list of [delta(Loc, dOprUpdate, KFunc(I2), I2)]
  } 
    
  implementation diffable over fixed is {
    diff = scalarDiff();
  } using {
    scalarDiff() is (function(X1,X2,Loc) is scalarDiffer(X1,X2,Loc));
    scalarDiffer(I,I,_) is list of [];
    scalarDiffer(I1,I2,Loc) is list of [delta(Loc, dOprUpdate, KFunc(I2), I2)]
  }
  
  implementation diffable over integer is {
    diff = scalarDiff();
  } using {
    scalarDiff() is (function(X1,X2,Loc) is scalarDiffer(X1,X2,Loc));
    scalarDiffer(I,I,_) is list of [];
    scalarDiffer(I1,I2,Loc) is list of [delta(Loc, dOprUpdate, KFunc(I2), I2)]
  }
    
  implementation diffable over float is {
    diff = scalarDiff();
  } using {
    scalarDiff() is (function(X1,X2,Loc) is scalarDiffer(X1,X2,Loc));
    scalarDiffer(I,I,_) is list of [];
    scalarDiffer(I1,I2,Loc) is list of [delta(Loc, dOprUpdate, KFunc(I2), I2)]
  }
  
  implementation diffable over long is {
    diff = scalarDiff();
  } using {
    scalarDiff() is (function(X1,X2,Loc) is scalarDiffer(X1,X2,Loc));
    scalarDiffer(I,I,_) is list of [];
    scalarDiffer(I1,I2,Loc) is list of [delta(Loc, dOprUpdate, KFunc(I2), I2)]
  }
  
  implementation diffable over decimal is {
    diff = scalarDiff();
  } using {
    scalarDiff() is (function(X1,X2,Loc) is scalarDiffer(X1,X2,Loc));
    scalarDiffer(I,I,_) is list of [];
    scalarDiffer(I1,I2,Loc) is list of [delta(Loc, dOprUpdate, KFunc(I2), I2)]
  }

  implementation diffable over string is {
    diff = scalarDiff();
  } using {
    scalarDiff() is (function(X1,X2,Loc) is scalarDiffer(X1,X2,Loc));
    scalarDiffer(I,I,_) is list of [];
    scalarDiffer(I1,I2,Loc) is list of [delta(Loc, dOprUpdate, KFunc(I2), I2)]
  }
  
  implementation diffable over boolean is {
    diff = scalarDiff();
  } using {
    scalarDiff() is (function(X1,X2,Loc) is scalarDiffer(X1,X2,Loc));
    scalarDiffer(I,I,_) is list of [];
    scalarDiffer(I1,I2,Loc) is list of [delta(Loc, dOprUpdate, KFunc(I2), I2)]
  }

  implementation diffable over timestamp is {
    diff = scalarDiff();
  } using {
    scalarDiff() is (function(X1,X2,Loc) is scalarDiffer(X1,X2,Loc));
    scalarDiffer(I,I,_) is list of [];
    scalarDiffer(I1,I2,Loc) is list of [delta(Loc, dOprUpdate, KFunc(I2), I2)]
  }
  
  main() do {  
	-- diff for lists
	def s1 is list of ["a", "b", "c", "d"];
	def s2 is list of ["x", "Y"];
	def dltaL is diff(s2, s1, "MyList");
	
	logMsg(info, "\n\nDelta of given Lists is ==> $dltaL");
	logMsg(info, "diff of [str] => $(showChanges(dltaL, s2))");
	
	-- diff for relations
  	def r1 is relation {40; 20; 10; 30;0; 50};
  	def r2 is relation {3;1; 2};
	def sr1 is sortRel(r1);
	def sr2 is sortRel(r2);
	-- logMsg(info, "diff of Sorted-rels: => $(showChanges(diff(sr1, sr2, "MyRel"), sr1))");
	def dltaR is diff(r1, r2, "MyRel");
	logMsg(info, "\n\nDelta of given Relations ===> $dltaR");
	logMsg(info, "diff of rels: => $(showChanges(dltaR, r1))");
 	
	-- diff for Maps...
	var m1 := dictionary of [1->1, 2->2, 3->3];
    var m2 := dictionary of [1->1, 2->2, 4->4, 5->5];
    -- def d is diff(m1, m2, "MyMap");
  	-- logMsg(info, "diff(m1, m2) ==> $d");
  	def dltaM is diff(m1, m2, "MyMap");
  	logMsg(info, "\n\nDelta of given Maps is ==> $dltaM");
  	logMsg(info, "diffing m1 & m2 =>$(showChanges(dltaM, m1))");

  } 
}
