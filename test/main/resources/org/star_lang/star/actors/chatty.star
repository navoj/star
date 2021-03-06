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

-- Actor test cases
-- This is a simple tick-tock scenario

chatty is package{  
  type talker is alias of actor of {ear has type occurrence of string};
  
  chatty has type (()=>talker)=>talker;
  fun chatty(Who) is actor{
    private var lifeTime := 100; 
    on Msg on ear do{
      logMsg(info,"I hear #Msg");
      if lifeTime>0 then {
        lifeTime := lifeTime-1;
        notify Who() with "Did you hear[#Msg]?" on ear;
      }
    };
  };
  
  prc main() do
    let{
      def ping is memo chatty(pong);
      def pong is memo chatty(ping);
    } in {notify ping() with "hello" on ear};
}
  
  