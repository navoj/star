/**
 * Implement query processing as a macro package
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
