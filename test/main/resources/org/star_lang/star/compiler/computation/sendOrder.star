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
sendOrder is package {
  import task;
  
  var RR := list of [];
  
  senderC(list of []) is task { valis () };
  senderC(list of [msg,..rest]) is task {
    logMsg(info, "sending $(__display(msg)); remaining: #(__display(rest))");
    RR := list of {RR..;msg};
    valis valof senderC(rest);
  };

  main() do {
    _ is valof senderC(list of {1;2;3});
    
    logMsg(info,"RR=$RR");
    assert RR = list of {1;2;3};
  }
}