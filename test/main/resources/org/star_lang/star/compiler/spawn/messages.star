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
messages is package{
  -- implement message box in terms of tasks
  
  type mbox of %t is mbox{
    post has type (%t)=>task of %t;
    grab has type ()=> task of %t;
  }
  
  fun box() is mbox{
    private var Q := queue of [];
    
    { ignore background msgLoop(); }
   
    private def grabMsgChnl is channel();
    private def postMsgChnl is channel();
   
    fun post(M) is send(postMsgChnl,M);
    fun grab() is let{
      def ReplyChnl is channel();
    } in wrapRv(sendRv(grabMsgChnl,ReplyChnl),(_)=> is recv(ReplyChnl));
   
    private
    fun msgLoop() is task{
      while true do{
        wait for chooseRv([
          wrapRv(recv(postMsgChnl),(M) => task{ Q:=queue of [Q..,M] })
          guardRv(task {
                    if Q matches queue of [H,..QQ] then
                      valis wrapRv(recv(grabMsgChnl),(Reply)=>task {
                        Q := QQ;
                        sendRv(Reply,H)
                      })
                    })
        ])
        /*
        select{
          on receive M on postMsgChnl do {
            Q := queue of [Q..,M]
          };
          on receive Reply on grabMsgChnl where Q matches queue of [H,..QQ] do {
            Q := QQ;
            send H to Reply
          }
        }
        */
      }
    }
  }
}