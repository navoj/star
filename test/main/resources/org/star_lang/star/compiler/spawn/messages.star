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
messages is package{
  -- implement message box in terms of tasks
  import concurrency;
  
  type mbox of %t is mbox{
    post has type (%t)=>task of %t;
    grab has type ()=> task of %t;
  }
  
  box() is mbox{
    private var Q := queue of {};
    
    private _ is background msgLoop();
   
    private grabMsgChnl is channel();
    private postMsgChnl is channel();
   
    post(M) is send(postMsgChnl,M);
    grab() is let{
      ReplyChnl is channel();
    } in wrapRv(sendRv(grabMsgChnl,ReplyChnl),(function(_) is recv(ReplyChnl)));
   
    private msgLoop() is task{
      while true do{
        select{
          on receive M on postMsgChnl do {
            Q := queue of {Q..;M}
          };
          when Q matches queue of {H;..QQ} on receive Reply on grabMsgChnl do {
            Q := QQ;
            send H to Reply
          }
        }
      }
    }
  }
}