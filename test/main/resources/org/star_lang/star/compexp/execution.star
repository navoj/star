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
execution is package{
  type unit is unit;
  
  contract execution over %%M is {
    _zero has type ()=> %%M of unit;
    _bind has type for all %s, %t such that (%%M of %s, (%s)=>%%M of %t) => %%M of %t;
    _combine has type for all %s, %t such that (%%M of %s,(%%M of %s)=>%%M of %t)=>%%M of %t;
    _return has type for all %s such that (%s) => %%M of %s;
    _delay has type for all %s such that (()=>%%M of %s) => %%M of %s;
    _run has type for all %s such that (%%M of %s)=>%s;
  }
  
  #prefix((bind),1150);
  #prefix((return),1000);
  #infix((execute),999);
  #infix((build),999);
  
  # ?Tp execute {?A} :: expression :- A;*execAction;
  # ?Tp build {?A} :: expression :- A;*execAction;
  
  #bind ?V to ?Exp :: execAction :- V::pattern :& Exp::expression;
  #?V is ?Exp :: execAction :- V::pattern :& Exp::expression;
  #var ?V is ?Exp :: execAction :- V::pattern :& Exp::expression;
  #var ?V := ?Exp :: execAction :- V::pattern :& Exp::expression;
  #?V := ?Exp :: execAction :- V::pattern :& Exp::expression;
  #return ?Exp :: execAction :- Exp::expression;
  #{?A} :: execAction :- A;* execAction;
  #if ?Tst then ?Th else ?El :: execAction :- Tst::condition :& Th::execAction :& El::execAction;
  #if ?Tst then ?Th :: execAction :- Tst::condition :& Th::execAction;
  #nothing :: execAction;

  # ?Tp execute {?A} ==> _run(Tp build {?A});
  # ?Tp build {?A} ==> delayT(Tp,(act2exec(A,Tp) has type Tp of %tt)) ## {
    #act2exec(#(bind ?V to ?Exp;?Next)#,?Tp) ==> _bind(Exp, (function(V) is act2exec(Next,Tp)));   
    #act2exec(#(return ?Exp)#,?Tp) ==> _return(Exp);
    #act2exec(#(valis ?Exp)#,?Tp) ==> _return(Exp);
    #act2exec(#(var ?V := ?Exp;?Next)#,?Tp) ==> _bind(_return(Exp), (function(V) is act2exec(Next,Tp)));
    #act2exec(#(var ?V is ?Exp;?Next)#,?Tp) ==> _bind(_return(Exp), (function(V) is act2exec(Next,Tp)));
    #act2exec(#(?V is ?Exp;?Next)#,?Tp) ==> _bind(_return(Exp), (function(V) is act2exec(Next,Tp)));
    #act2exec(#(if ?Tst then ?Th else ?El)#,?Tp) ==> Tst?act2exec(Th,Tp)|act2exec(El,Tp);
    #act2exec(#(if ?Tst then ?Th)#,?Tp) ==> Tst?act2exec(Th,Tp)|_zero();
    #act2exec(#(?V := ?Exp)#,?Tp) ==> valof{ V := Exp; valis _zero() };
    #act2exec(#(?A1;?A2)#,?Tp)==> _combine(act2exec(A1,Tp),(function(unit) is act2exec(A2,Tp)));
    #act2exec(#(?A1;)#,?Tp) ==> act2exec(A1,Tp);
    #act2exec(nothing,?Tp) ==> _zero(); 
    
    #delayT(?Tp,?Exp) ==> _delay((function() is Exp));
  };
}