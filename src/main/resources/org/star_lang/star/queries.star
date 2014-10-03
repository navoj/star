/**
 * Implement query processing as a macro package 
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

# all ?X where ?C ==> transCondition(C, X, addElement, allAux, NoneFound);
# any of ?X where ?C ==> transCondition(C, X, chooseElement, anyAux, NoneFound);
# reduction ?F of { all ?X where ?C } ==> transCondition(C, X, F, redAux, NoneFound); 
 
-- Ptn in Src --> iterate(Src, AuxFun, InitState)
-- where AuxFun looks like:
-- auxF(Ptn,St) is (X,St);
-- auxF(_,S) default is S;
# transCondition( ?Ptn in ?Src, ?Aux, ?Acc, ?Add, ?Init) ==> iterate(Src, auxGen(Ptn, Add, Acc, Aux), Init);

-- C1 and C2
#transCondition(?C1 and ?C2, ?Aux, ?Acc, ?Add, ?Init) ==> 
   transCondition(C1, 

( andGen( transCondition(C2, Aux, #$"init"), #$"init"), Init);

-- Aux function generators

# auxGen(?Ptn, ?Adder, ?Acc, ?Aux) ==> Aux(Ptn,Acc,Adder);

# allAux(?Ptn,?Acc,?Adder) ==> let { #$"auxF"(Ptn,St) is Adder(Acc,St); #$"auxF"(_,St) is St } in #$"auxF";

# anyAux(?Ptn, ?Acc, ?Adder) ==> let { #$"auxF"(Ptn,NoneFound) is NoMore(Acc); #$"auxF"(_,St) is St } in #$"auxF";

# redAux(?Ptn, ?Acc, ?Fun) ==> let{ #$"red"(Ptn,NoneFound) is Acc; #$"red"(Ptn,ContinueWith(XX)) is Fun(Acc,XX) } in #$"red"; 
