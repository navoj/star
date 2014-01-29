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
iterations is package{  
  #prefix((NoMore),300);
  #prefix((ContinueWith),300);
    
  implementation sizeable over (IterState of %t where sizeable over %t) is {
    size = maybeSize;
    isEmpty = maybeEmpty;
  } using {
    maybeSize(NoneFound) is 0;
    maybeSize(NoMore L) is size(L);
    maybeSize(ContinueWith L) is size(L);
    
    maybeEmpty(NoneFound) is true;
    maybeEmpty(NoMore L) is isEmpty(L);
    maybeEmpty(ContinueWith L) is isEmpty(L);
  }
  
  iterates(_empty(),_,St) is St;
  iterates(_,_,NoMore X) is NoMore X;
  iterates(_pair(H,T),F,St) is iterates(T,F,F(H,St));
  
  addElement(X,NoneFound) is ContinueWith cons(X,nil);
  addElement(X,ContinueWith L) is ContinueWith cons(X,L);
  
  negate(NoMore true,A,B) is A();
  negate(ContinueWith true,A,B) is A();
  negate(_,A,B) default is B();
  
  otherWise(NoneFound,F) is F();
  otherWise(St,_) default is St;
  
  countElement(X,N,St) where size(St)<N is addElement(X,St);
  countElement(_,_,St) default is St
  
  type gen is fem or mel;
  
  parent is cons(("j","k"), cons(("s","k"), cons(("j","p"), cons(("s","p"), cons(("k","ss"),cons(("d","ss"),nil))))));
  gender is cons(("j",fem), cons(("s",mel), cons(("k",fem), cons(("p",mel), cons(("d", mel), cons(("ss",mel), nil)))))); 
  
  PP is cons of {("f","s"); ("m","s")};
  P is cons of {("j","k");("s","k");("j","p");("s","p");("k","ss");("d","ss");..PP};
  
  alpha is cons of { (1,"alpha"); (2,"beta"); (3,"gamma") };
  beta is cons of { ("omega",-1); ("iota",0); ("gamma",3) };
  
  show(E,St) is valof{
    logMsg(info,"$E in $St");
    valis St;
  }
  
  main() do
  {
    logMsg(info,"Parents are $parent");
    logMsg(info,"Parents are $P");
    logMsg(info,"Genders are $gender");
    
    XX is iterates(parent,show,NoneFound);
    
    -- Emulate: all X where (X,Peter) in parent
    X0 is iterates(parent,
      let{
        sF((X,"p"),St) is addElement(X,St);
        sF(_,S) default is S;
      } in sF,NoneFound);
    logMsg(info,"X0=$X0");
    assert X0=ContinueWith (cons of { "s"; "j" });
    
    -- Emulate: anyof X where (X,Peter) in parent and (X,male) in gender
    X1 is iterates(parent,
      let{
        sF((X,J),NoneFound) where J="p" is
          iterates(gender,
            let{
              sG((Xx,mel),NoneFound) is NoMore Xx;
              sG(_,S) default is S
            } in sG,NoneFound)
            sF(_,A) default is A
      } in sF,NoneFound);
    logMsg(info,"X1=$X1");
    assert X1=NoMore "s";
    
    -- Emulate: all X where (X,Peter) in parent and (X,male) in gender
    X2 is iterates(parent,
      let{
        sF((X,J),Rl) where J="p" is
          iterates(gender,
            let{
              sG((Xx,mel),St) where X=Xx is addElement(X,St);
              sG(_,S) default is S
            } in sG,Rl)
        sF(_,A) is A
      } in sF,NoneFound);
    logMsg(info,"X2=$X2");
    assert X2=ContinueWith (cons of {"s"});
    assert X2!=NoMore(cons of {"s"});
    
    -- Emulate: all (X,Y) where (X,Y) in alpha or (Y,X) in beta
    X3 is iterates(beta,
      let{
        sB((Y,X),St) is addElement((X,Y),St);
        sB(_,St) default is St;
      } in sB,iterates(alpha,
        let{
          sA((X,Y),St) is addElement((X,Y),St);
          sA(_,St) default is St;
        } in sA,NoneFound));
    logMsg(info,"X3=$X3");
    
    -- Emulate: all X where (X,Y) in alpha and not (Y,X) in beta
    X4 is iterates(alpha,
      let{
        fA((X,Y),Sa) is negate(iterates(beta,
          let{
            fB(Tpl,NoneFound) where Tpl=(Y,X) is NoMore true
            fB(_,Sb) is Sb
          } in fB,NoneFound),
          (function() is Sa),
          (function() is addElement(X,Sa)));
        fA(_,Sa) default is Sa
      } in fA, NoneFound);
    logMsg(info,"X4=$X4");
    assert X4=ContinueWith(cons of {2;1});
    
    -- Emulate: all X where (X,Y) in alpha otherwise ((_,Y) in beta and "Def" matches X)
    X5 is otherWise(
      iterates(alpha,
        let{
          fA((X,Y),St) is addElement((X,Y),St);
          fA(_,St) default is St
        } in fA,NoneFound),
        (function() is iterates(beta,
          let{
            fB((_,Y),St) where "Def" matches X is addElement((Y,X),St);
            fB(_,St) default is St
          } in fB,NoneFound)));
    logMsg(info,"X5=$X5");
    
    -- Emulate: all X where (X,Y) in alpha and X=4 otherwise ((_,Y) in beta and "Def" matches X)
    X6 is otherWise(
      iterates(alpha,
        let{
          fA((X,Y),St) where X=4 is addElement((X,Y),St);
          fA(_,Sa) default is Sa
        } in fA,NoneFound),
        (function() is iterates(beta,
          let{
            fB((_,Y),St) where  "Def" matches X is addElement((Y,X),St);
            fB(_,St) default is St
          } in fB,NoneFound)));
    logMsg(info,"X6=$X6");
    
    -- Emulate: 2 of X where ("John",X) in parent and (X,male) in gender
    X7 is iterates(parent,
      let{
        sF(("j",X),Rl) is
          iterates(gender,
            let{
              sG((X7a,mel),St) where X=X7a is countElement(X,2,St);
              sG(_,S) default is S
            } in sG,Rl)
        sF(_,A) is A
      } in sF,NoneFound);
    logMsg(info,"X7=$X7");
    assert size(X7)<=2;
    
    -- Emulate: all (X,Y) where (X,Y) in alpha and (Y,X) in beta
    X8 is iterates(alpha,
      let{
        fA((X,Y),Sa) is iterates(beta,
          let{
            fB(Tpl,St) where Tpl=(Y,X) is addElement((X,Y),St);
            fB(_,St) is St
          } in fB,NoneFound);
        fA(_,Sa) default is Sa
      } in fA, NoneFound);
    logMsg(info,"X8=$X8");
    assert X8=ContinueWith(cons of {(3,"gamma")}); 
       
    -- Emulate: all (X,Y) where (X,Y) in alpha and Y>1
    X9 is iterates(alpha,
      let{
        fA((X,Y),St) where X>1 is addElement((X,Y),St);
        fA(_,Sa) default is Sa
      } in fA, NoneFound);
    logMsg(info,"X9=$X9");
    assert X9=ContinueWith(cons of {(3,"gamma"); (2,"beta")});
    
    -- Emulate: (X,Y) in alpha implies (Y,X) in beta
    X10 is negate(iterates(alpha,
      let{
        fA((X,Y),ContinueWith true) is 
          negate(iterates(beta,
          let{
            fB(Xa,NoneFound) where (Y,X)=Xa is NoMore true;
            fB(_,St) is St;
          } in fB,
          NoneFound),
          (function() is ContinueWith true),
          (function() is NoMore false));
        fA(_,St) is St;
      } in fA,
      ContinueWith true),
      (function() is true),
      (function() is false))
    logMsg(info,"X10=$X10");
    assert not X10;
    
    -- Emulate: (X,Y) in alpha implies (X,Y) in alpha
    X11 is negate(iterates(alpha,
     let{
        fA((X,Y),ContinueWith true) is 
          negate(iterates(alpha,
          let{
            fB(Xa,NoneFound) where (X,Y)=Xa is NoMore true;
            fB(_,St) is St;
          } in fB,
          NoneFound),
          (function() is ContinueWith true),
          (function() is NoMore false));
        fA(_,St) is St;
      } in fA,
      ContinueWith true),
      (function() is true),
      (function() is false));
    logMsg(info,"X11=$X11");
    assert X11;
    
    
     -- Emulate: ("k",Y) in gender ? Y=fem | ("k",_) in parent
     
     X12 is negate(
       iterates(gender,
         let{
           fA(("k",Y), _) is NoMore (Y=fem)
           fA(_,St) is St
         } in fA,NoneFound),
       (function() is NoMore true),
       (function() is 
          iterates(P, 
            let{
              fB(("k",_),_) is NoMore true;
              fB(_,St) is St
            } in fB,
            NoneFound))
     );
   
    logMsg(info,"X12=$X12");
    assert X12=NoMore true;
  };
}