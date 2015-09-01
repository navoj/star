/**
 * define the signature of a priority ordered queue 
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

 
type priorityQ of elem is priorityQ{
  queue has kind type of type;

  emptyQ has type queue of elem;
  isEmptyQ has type (queue of elem)=>boolean;
  
  insertQ has type (elem,queue of elem)=>queue of elem;
  findQMin has type (queue of elem)=>elem;
  deleteQMin has type (queue of elem)=>queue of elem;
  mergeQ has type (queue of elem,queue of elem)=>queue of elem;
}

type comparison of elem is compare{
  lt has type (elem,elem)=>boolean;
  le has type (elem,elem)=>boolean;
  eq has type (elem,elem)=>boolean;
  ge has type (elem,elem)=>boolean;
  gt has type (elem,elem)=>boolean;
}