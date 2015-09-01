/**
 * Support functions for macro processing. 
 *
 * Copyright (c) 2015. Francis G. McCabe
 *
 * The TypeChecker implements the type inference module for Star
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

-- 
-- These functions are invoked by so-called compiled macro rules.

private import base;
private import arithmetic;
private import casting;
private import sequences;
private import option;
private import arrays;
private import folding;
private import iterable;
private import strings;

implementation equality over quoted is {
  (=) = astEqual;
} using {
  fun astEqual(boolAst(_,B),boolAst(_,B)) is true
   |  astEqual(charAst(_,C),charAst(_,C)) is true
   |  astEqual(stringAst(_,S),stringAst(_,S)) is true
   |  astEqual(integerAst(_,I),integerAst(_,I)) is true
   |  astEqual(longAst(_,L),longAst(_,L)) is true
   |  astEqual(floatAst(_,F),floatAst(_,F)) is true
   |  astEqual(nameAst(_,Id),nameAst(_,Id)) is true
   |  astEqual(applyAst(_,O,A),applyAst(_,P,B)) is astEqual(O,P) and equalArgs(A,B)
   |  astEqual(_,_) default is false
  
  fun equalArgs(A1,A2) where size(A1)=size(A2) is valof{
    var ix := 0;
    while ix<size(A1) do {
      if not astEqual(someValue(A1[ix]),someValue(A2[ix])) then
        valis false;
      ix := ix+1;
    }
    valis true;
  }
}

-- invoked as part of ./ search in macros
fun _dotSlashSearch(M) is let{
  ptn mtch(nil,V) from M(V)
   |  mtch(cons(0-1,Pth),V) from applyAst(_,mtch(Pth,V),Args)
   |  mtch(Pth,V) from applyAst(_,_,argSearch(Pth,V))
  
  ptn argSearch(cons(Ix,P),V) from A where Ix->E in A and E matches mtch(P,V)
} in mtch;

-- support the macro-time log feature
fun _macro_log(Msg,Rep) is valof{
  logMsg(info,"",Msg);
  valis Rep
}; 

fun __macro_error(Msg) is raise Msg;

-- support the macro-time name construction feature
_macro_catenate has type (quoted,quoted)=>string;
fun _macro_catenate(L,R) is getName(L)++getName(R) using{
  fun getName(nameAst(_,N)) is N
   |  getName(stringAst(_,S)) is S
   |  getName(integerAst(_,I)) is I as string
   |  getName(X) default is X as string
}

_macro_gensym has type (string)=>quoted;
fun _macro_gensym(N) is nameAst(noWhere,gensym(N))

fun __macro_detupleize(applyAst(Loc,Op,Args)) is detupleize(Args) using {
  fun detupleize(_empty()) is <| () |>
   |  detupleize(_pair(L,R)) is <| (?L , ?detupleize(R) ) |>
};

fun __macro_tupleize(Loc,Els) is __macro_tuple(Loc,liftEls(Els,_nil())) using {
  fun liftEls(<|()|>,Args) is Args
   |  liftEls(<|(?L,?R)|>,Args) is liftEls(L,liftEls(R,Args))
   |  liftEls(El,Args) is list of [El,..Args]
};

fun macroLog(M,V) is valof{
  logMsg(info,M);
  valis V;
}

fun __macro_tuple(Loc,Args) is applyAst(Loc,nameAst(Loc,"\$$(size(Args))"),Args);

__macro_id has type (quoted)=>quoted
fun __macro_id(X) is X

private implementation sizeable over cons of %e is {
  fun size(L) is consSize(L)
 
  fun isEmpty(nil) is true
   |  isEmpty(_) default is false

  private fun consSize(L) is valof{
    var LL := L;
    var S := 0;
    while LL matches cons(_,Lx) do {
      S := S+1;
      LL := Lx;
    };
    valis S;
  };
}

fun __macro_substitute(Trm,Tgt,Rep) is let{
  fun _subst(X) where X=Tgt is Rep
   |  _subst(applyAst(Loc,Op,Args)) is applyAst(Loc,_subst(Op),map(_subst,Args))
   |  _subst(X) default is X
} in _subst(Trm);

fun __macro_apply(Loc,Op,applyAst(_,_,Args)) is applyAst(Loc,Op,Args)

ptn __macro_deapply(Loc,Op,__macro_tuple(Loc,Args)) from applyAst(Loc,Op,Args)

fun __foldSemi(<| ?L ; ?R |>,F,S) is __foldSemi(R,F,__foldSemi(L,F,S))
 |  __foldSemi(Term,F,S) is F(Term,S)

fun __mapSemi(<| ?L ; ?R |>,F) is <| ?__mapSemi(L,F) ; ?__mapSemi(R,F) |>
 |  __mapSemi(Term,F) is F(Term)

fun __wrapSemi(list of [],X) is X
 |  __wrapSemi(list of [X],<|nothing|>) is X
 |  __wrapSemi(list of [X,..Y],R) is <| ?X ; ?__wrapSemi(Y,R) |>

fun macroString(nameAst(Lc,N)) is stringAst(Lc,N)

fun __macro_isNumber(integerAst(_,_)) is true
 |  __macro_isNumber(longAst(_,_)) is true
 |  __macro_isNumber(floatAst(_,_)) is true
 |  __macro_isNumber(_) default is false

fun __macro_isTuple(applyAst(_,nameAst(_,Op),_)) is Op matches `\$[0-9]+`
 |  __macro_isTuple(_) default is false

fun __macro_location(boolAst(L,_)) is L
 |  __macro_location(charAst(L,_)) is L
 |  __macro_location(stringAst(L,_)) is L
 |  __macro_location(integerAst(L,_)) is L
 |  __macro_location(longAst(L,_)) is L
 |  __macro_location(floatAst(L,_)) is L
 |  __macro_location(nameAst(L,_)) is L
 |  __macro_location(applyAst(L,_,_)) is L
 |  __macro_location(_) default is noWhere

fun __display_macro(Q) is stringAst(__macro_location(Q),__macro_display(Q));

implementation concatenate over quoted is {
  fun X++Y is qConcat(X,Y)
} using {
  fun qConcat(stringAst(Lc,L),stringAst(_,R)) is stringAst(Lc,L++R)
   |  qConcat(stringAst(Lc,L),R) is applyAst(Lc,nameAst(Lc,"++"),list of [stringAst(Lc,L),R])
   |  qConcat(nameAst(Lc,L),stringAst(_,R)) is nameAst(Lc,L++R)
   |  qConcat(nameAst(Lc,L),nameAst(_,R)) is nameAst(Lc,L++R)
}