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
zebra is package{

/*
zebra(H,W,Z) :-
	H = [house(norwegian,_,_,_,_), house(_,_,_,_,blue), house(_,_,_,milk,_), _, _],
	member(house(englishman,_,_,_,red), H),
	member(house(spaniard,dog,_,_,_), H),
	member(house(_,_,_,coffee,green), H),
	member(house(ukranian,_,_,tea,_), H),
	followedBy(house(_,_,_,_,ivory), house(_,_,_,_,green), H),
	member(house(_,snails,winston,_,_), H),
	member(house(_,_,kools,_,yellow), H),
	nextTo(house(_,_,chesterfield,_,_), house(_,fox,_,_,_), H),
	nextTo(house(_,_,kools,_,_), house(_,horse,_,_,_), H),
	member(house(_,_,lucky-strike,orange-juice,_), H),
	member(house(japanese,_,parliaments,_,_), H),
	member(house(W,_,_,water,_), H),
	member(house(Z,zebra,_,_,_), H).
*/

  def nats is cons of ["englishman","norwegian","ukranian","japanese","spaniard"]
  
  def cigs is cons of ["winston","lucky-strike","parliaments","chesterfield","kools"]
  
  def cols is cons of ["red", "green", "blue", "ivory", "yellow"]
  
  def pets is cons of ["dog", "zebra", "snails", "fox", "horse"]
  
  def drinks is cons of ["coffee", "water", "milk", "tea", "orange"]
  
  def nums is cons of [1,2,3,4,5]
  
  type house is 
  	h{nat has type string; nat default is "none";
  	  drink has type string; drink default is "none";
  	  col has type string; col default is "none";
      pet has type string; pet default is "none";
      cig has type string; cig default is "none";
      num has type integer};
  
  
  removeHouse has type (house,cons of house) => cons of house;
  fun removeHouse(_,cons of []) is cons of []
   |  removeHouse(E,cons of [E1,..L]) where E=E1 is L
   |  removeHouse(E,cons of [E1,..L]) default is cons of [E1,..removeHouse(E,L)]
  
  spanConstraint has type (house,cons of house) => (cons of house,boolean);
  -- spaniard has dog
  fun spanConstraint(H,L) where H matches h{nat="none";pet="none"} is 
  		(cons of [h{nat ="spaniard";pet="dog";num=H.num;col=H.col;drink=H.drink;cig=H.cig},..removeHouse(H,L)], true)
   |  spanConstraint(_,L) default is (L,false)
    
  engConstraint has type (house,cons of house) => (cons of house,boolean);
  -- english in  a red house 
  fun engConstraint(H,L)   is 
 	 ( "none" = H.nat and "none" = H.col ? 
 		(cons of [h{nat ="englishman";col="red";num=H.num;drink=H.drink;pet=H.pet;cig=H.cig},..removeHouse(H,L)], true)
     : (L,false)
     );
     
  type pair is p(integer,cons of house);
  
  coffeeConstraint has type (house,cons of house) => pair;
  -- get type error if use just a pair!
  -- coffee in green house which has its number returned
  fun coffeeConstraint(H3,L3) is 
       	  ( "none" = H3.drink  and "none" = H3.col ?    	 	   
     	   p(H3.num, cons of [h{drink ="coffee";col="green";num=H3.num;nat=H3.nat;pet=H3.pet;cig=H3.cig},..removeHouse(H3,L3)])
     	 : p(0,L3)
     	 );
     	 	  	
  teaConstraint has type (house,cons of house) => (cons of house,boolean);
  -- tea drunk by ukranian
  fun teaConstraint(H4,L4) is 
      	       ( "none" = H4.drink  and "none" = H4.nat ? 
     	 	     (cons of [h{drink ="tea";nat="ukranian";num=H4.num;col=H4.col;pet=H4.pet;cig=H4.cig},..removeHouse(H4,L4)],true)
     	 	   : (L4,false));
   
  findHouse has type (integer,cons of house) => house;
  fun findHouse(N,cons of [H,..L]) where (H matches h{num=M} and N=M) is H
   |  findHouse(N,cons of [_,..L]) is findHouse(N,L)
   |  findHouse(_,_) default is h{num=0}
  
  
  ivoryConstraint has type (integer, cons of house) => (cons of house,boolean);
  -- ivory house is house num N
  -- ivoryConstraint(N,L) where H matching h{col="none";num=N} in L is 
  fun ivoryConstraint(N,L) is 
  	(findHouse(N,L) matches h{col="none";num=N1} matching H and N1=N ?
   		(cons of [h{nat=H.nat;pet=H.pet;num=N;col="ivory";drink=H.drink;cig=H.cig},..removeHouse(H,L)],true)
    : (L,false));
  
 
  snailsConstraint has type (house,cons of house) => (cons of house,boolean);
  -- pet snails and cig winston in same house
  fun snailsConstraint(H,L)   is 
  	( "none" = H.cig  and "none" = H.pet ? 
  		(cons of [h{nat =H.nat;pet="snails";num=H.num;col=H.col;drink=H.drink;cig="winston"},..removeHouse(H,L)], true)
    : (L,false)
    );
   
  koolsConstraint has type (house,cons of house) => pair;
  -- kools smoked in yellow house which has number returned
  fun koolsConstraint(H,L)   is 
 	 ( "none" = H.cig and "none" = H.col ? 
 		p(H.num, cons of [h{nat =H.nat;col="yellow";num=H.num;drink=H.drink;pet=H.pet;cig="kools"},..removeHouse(H,L)])
     : p(0,L)
     );
     
  japConstraint has type (house,cons of house) => (cons of house,boolean);
  fun japConstraint(H,L) where H matches h{nat="none";cig="none"} is 
  		(cons of [h{nat ="japanese";pet=H.pet;num=H.num;col=H.col;drink=H.drink;cig="parliaments"},..removeHouse(H,L)], true)
   |  japConstraint(_,L) default is (L,false)
  
  luckConstraint has type (house,cons of house) => (cons of house,boolean);
  fun luckConstraint(H,L) where H matches h{cig="none";drink="none"} is 
  		(cons of [h{nat =H.nat;pet=H.pet;num=H.num;col=H.col;drink="orange";cig="lucky-strike"},..removeHouse(H,L)], true)
   |  luckConstraint(_,L) default is (L,false);
  
  foxConstraint has type (house,cons of house) => pair;
  fun foxConstraint(H,L) where H matches h{pet="none"} is 
  		p(H.num,cons of [h{nat =H.nat;pet="fox";num=H.num;col=H.col;drink=H.drink;cig=H.cig},..removeHouse(H,L)])
   |  foxConstraint(_,L) default is p(0,L)
  
  chestConstraint has type (integer, cons of house) => (cons of house,boolean);
  -- chesterfields smoked in house num N
  -- chestConstraint(N,L) where H matching h{cig="none";num=N} in L  is 
  fun chestConstraint(N,L) where (findHouse(N,L) matches h{cig="none";num=M} matching H and M=N)  is 
  		(cons of [h{nat=H.nat;pet=H.pet;num=N;col=H.col;drink=H.drink;cig="chesterfield"},..removeHouse(H,L)],true)
   | chestConstraint(_,L) default is (L,false)
  
  horseConstraint has type (integer, cons of house) => (cons of house,boolean);
  -- horse in house number N
  -- horseConstraint(N,L) where (H matching h{pet="none";num=N}) in L is 
  fun horseConstraint(N,L) where (findHouse(N,L) matches  h{pet="none";num=M} matching H and M=N) is 
  		(cons of [ h{nat=H.nat;pet="horse";num=H.num;col=H.col;drink=H.drink;cig=H.cig},..removeHouse(H,L)],true)
   |  horseConstraint(_,L) default is (L,false)
  
  waterConstraint has type (house,cons of house) => (cons of house,boolean)
  -- water in some house
  fun waterConstraint(H,L) where H matches h{drink="none"} is 
  		(cons of [ h{nat=H.nat;pet=H.pet;num=H.num;col=H.col;drink="water";cig=H.cig},..removeHouse(H,L)],true)
   |  waterConstraint(_,L) default is (L,false)
  
  zebraConstraint has type (house,cons of house) => (cons of house,boolean);
  -- zebra in some house
  fun zebraConstraint(H,L) where H matches h{pet="none"} is 
  		(cons of [h{nat=H.nat;pet="zebra";num=H.num;col=H.col;drink=H.drink;cig=H.cig},..removeHouse(H,L)],true)
   |  zebraConstraint(_,L) default is (L,false)

  def L1 is cons of [h{nat="norwegian";num=1},h{num=2;col="blue"},h{drink="milk";num=3},h{num=4},h{num=5}];
  -- above captures constraints that norwegian in house 1, he is next to the blue house, & milk drunk in house 3
  
  def answer is any of L15 where
      (H1 where engConstraint(H1,L1) matches (L2,true)) in L1 and 
        (H2 where spanConstraint(H2,L2) matches (L3,true)) in L2 and 
          (H3 where coffeeConstraint(H3,L3) matches p(N4,L4) and N4>1) in L3 and 
            (H4 where teaConstraint(H4,L4) matches (L5,true)) in L4 and 
              ivoryConstraint(N4-1,L5) matches (L6,true) and 
                (H6 where snailsConstraint(H6,L6) matches (L7,true)) in L6 and 
                  (H7 where koolsConstraint(H7,L7) matches p(N8,L8) and N8>0) in L7 and 
                    (H8 where japConstraint(H8,L8) matches (L9,true) ) in L8 and
                      (H9 where luckConstraint(H9,L9) matches (L10,true)) in L9 and
                        (H10 where foxConstraint(H10,L10) matches p(N11,L11) and N11 >0) in L10 and
                          (N1 where (N1>0 and N1<6))  in cons of [N11-1,N11+1] and
                            chestConstraint(N1,L10) matches (L12,true) and 
                              (N2 where N2>0 and N2<6 and horseConstraint(N2,L12) matches (L13,true)) in cons of {N8-1;N8+1} and
                                (H13 where waterConstraint(H13,L13) matches (L14,true)) in L13 and
                                  (H14 where zebraConstraint(H14,L14) matches (L15,true)) in L14
     
  prc main() do {     
    logMsg(info,"Starting zebra puzzle $L1");

    for (H1 where engConstraint(H1,L1) matches (L2,true)) in L1 do { 
      logMsg(info,"1: English in red house\n $L2"); -- english in  a red house 
      for (H2 where spanConstraint(H2,L2) matches (L3,true)) in L2 do { 
        logMsg(info,"2: Spaniard in with dog\n $L3"); -- spaniard has dog
        for (H3 where coffeeConstraint(H3,L3) matches p(N4,L4) and N4>1) in L3 do {
          -- coffee in green house which has number N4, not house 1 because to right of ivory house
          logMsg(info,"3: Coffee in green house number $N4\n $L4");
          
          for (H4 where teaConstraint(H4,L4) matches (L5,true)) in L4 do {
            if ivoryConstraint(N4-1,L5) matches (L6,true) then { 
      	      -- ivory house to left of green house which has num N4
 		      logMsg(info,"5: ivory house  to left of green house at number $(N4-1)\n $L6");
 			  for (H6 where snailsConstraint(H6,L6) matches (L7,true)) in L6 do{ 
      	        -- pet snails and cig winston in same house 
        		logMsg(info,"6: snails with winstons\n $L7");
        	    for (H7 where koolsConstraint(H7,L7) matches p(N8,L8) and N8>0) in L7 do { 
      	          -- kools smoked in yellow house number N8 
        		  logMsg(info,"7: kools in yellow house number $N8\n $L8");
        	      for (H8 where japConstraint(H8,L8) matches (L9,true) ) in L8 do { 
        	        logMsg(info,"8: Japanese smokes parliaments $L9");
       	            -- japanese smokes parliaments 
        	        for (H9 where luckConstraint(H9,L9) matches (L10,true)) in L9 do { 
       	           	  logMsg(info,"9: lucky-strink with orange juice\n $L10");
                      --  lucky_strike with orange juice
       	              for (H10 where foxConstraint(H10,L10) matches p(N11,L11) and N11 >0) in L10 do { 
      	                -- fox in some house number N11
  						logMsg(info,"10: fox is in house number $N11\n$L11");
       	                for (N1 where (N1>0 and N1<6))  in cons of {N11-1;N11+1} do { 
  						  logMsg(info,"11.1 checking if chest in house $N1");
  						  if chestConstraint(N1,L10) matches (L12,true) then {
  						    logMsg(info,"11: chesterfieds next to fox house number $N1\n$L12");
      	                    -- chesterfields smoked in house next to fox in house num N11
       	                    for (N2 where N2>0 and N2<6 and horseConstraint(N2,L12) matches (L13,true)) in cons of {N8-1;N8+1} do { 
  						      logMsg(info,"12: kools next to horse house number\n $N2");
  					     	  -- horse in house next to one with kools cig, which has no N8
        	                  for (H13 where waterConstraint(H13,L13) matches (L14,true)) in L13 do {
        	                    -- water in some house
 								logMsg(info,"13: water drunk somewhere\n $L14");
        	                                 
       	                        for (H14 where zebraConstraint(H14,L14) matches (L15,true)) in L14 do {
       	                          -- zebra in some house
      							  -- if ZH where ZH.pet="zebra" in L14 then logMsg(info,"\n$ZH.nat owns the zebra");
      							  -- if h{drink="water"} matching WH in L14 then logMsg(info,"\n$WH.nat drinks water"); 
      						      -- ZO is (select all ZH from ZH where ZH.pet="zebra" in L14)[0].nat;
      							  -- logMsg(info,"\n$ZO owns the zebra"); 
      							  logMsg(info,"\nzebra solution is\n $L15");
      							}
      						  }
      			    		}
      			          }
  						}
					  }
					}
				  }
				}
			  }
		    }
		  }
	    }	
	  }
	}
	
    logMsg(info,"query solution is $answer")
  }
}
     
		     