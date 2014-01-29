/**
 * 
 * Copyright (C) 2013 Starview Inc
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
pingpong is package{
  -- test the use of mail boxes to have a non-stack based ping/pong
  
  import mbox;
  
  type talker is alias of actor of {ear has type stream of string};
  
  chatty has type (()=>talker)=>talker;
  chatty(Who) is actor{
    private var lifeTime := 1000;
    Bx has type mbox of string;
    private Bx is box();
    private H is spawn{
      while true do{
        Msg is Bx.grab();
        notify Who() with Msg on ear;
      }
    };
    
    on Msg on ear do{
      logMsg(info,"I hear #Msg");
      if lifeTime>0 then {
        lifeTime := lifeTime-1;
        Bx.post(Msg);
      }
    };
  };
 
  main() do {
    let{
      ping is memo chatty(pong);
      pong is memo chatty(ping);
    } in {notify ping() with "hello" on ear};
    sleep(1000l);
  }
}
  
  