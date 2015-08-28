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

actionRules is package{

  type weekday is sunday or monday or tuesday or wednesday or thursday or friday or saturday;
  
  showDay has type action(weekday);
  prc showDay(sunday) do logMsg(info,"sunday")
   |  showDay(monday) do logMsg(info,"monday")
   |  showDay(tuesday) do logMsg(info,"tuesday")
   |  showDay(wednesday) do logMsg(info,"wednesday")
   |  showDay(thursday) do logMsg(info,"thursday")
   |  showDay(friday) do logMsg(info,"friday")
   |  showDay(saturday) do logMsg(info,"saturday")
   |  showDay(Other) default do logMsg(info,"some day $Other")
  
  prc main() do {
    showDay(wednesday);
    showDay(saturday);
    showDay(sunday);
  }
}
                