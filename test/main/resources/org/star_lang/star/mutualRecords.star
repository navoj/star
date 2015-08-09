
/*
 * Copyright (c) 2015. Francis G. McCabe
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

mutualRecords is package{
	type Person is noone
	            or some{
		            name has type string;
		            inform has type action(integer);
              };

  genA(V) is some{
	  name is "A";
	  inform(M){
		  -- logMsg(info,"$name told $M");
		  if M>0 then
		    V.inform(M-1);
		}
  }

  genB(V) is some{
	  name is "B";
	  inform(M){
		  -- logMsg(info,"$name told $M");
		  if M>0 then
		    V.inform(M-1);
		}
  }

  genC(V) is some{
	  name is "C";
	  inform(M){
		  -- logMsg(info,"$name told $M");
		  if M>0 then
		    V().inform(M-1);
		}
  }

  setup() is let{
	  A is genA(B);
	  Af() is A;
	  B is genB(C);
	  C is genC(Af);
	} in A;
	
	main() do {
		A is setup();
		A.inform(1000);
	}
}