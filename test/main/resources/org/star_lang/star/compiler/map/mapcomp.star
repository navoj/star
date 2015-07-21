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
mapcomp is package {
    main has type action();
    prc main() do {
        def M1 is dictionary of ["a"->0,"b"->1];
        def M2 is dictionary of ["b"->0,"c"->1];
        for K1->V1 in M1 do {
            if(not (K1->V2 in M2)) then logMsg(info, "$K1 is not in $M2");
        }
        for K2->V2 in M2 do {
            if(not (K2->V1 in M1)) then logMsg(info, "$K2 is not in $M1");
        }
    }
}