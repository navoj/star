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
complexMacroLets is package{

 -- test some macro lets
 
  #iden(?I) ==> I;
  
  -- Macro that processes lists
  
  # processList(?List,?App,?Deflt,?Init) ==> procList(List,Init) ## { 
    # procList((),?SoFar) ==> #*Deflt(SoFar);
    # procList((?L,?R),?SoFar) ==> procList(R,#*procList(L,SoFar));
    # procList(?X,?SoFar) ==> #*App(X,SoFar);
  };
  
  -- Some example processors
  #glue(?List) ==> list of { processList(List,procl,iden,()) } ## {
    #procl(?Elt,()) ==> Elt ;
    #procl(?Elt, ?SoFar) ==> #(Elt;SoFar)#;
  }
  
  assert glue(("A",("B",("C",())))) = glue(((((),"A"),"B"),"C"));
}
  