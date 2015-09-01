/*
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

primitiveIO is package {
  private import task;  
  
  -- Socket interface
  type socket is alias of __asynchChannel;
  type serverSocket is alias of __listenSocket;
  type port is alias of integer

  connectTcp has type (string, port) => task of socket

  socketClose has type action(socket)
  socketRead has type (socket) => task of integer
  socketWrite has type (socket) => task of integer

  -- A port number of zero will let the system pick up an ephemeral port.
  listenTcp has type (port) => serverSocket
  acceptConnection has type (serverSocket) => task of socket
  serverSocketClose has type action(serverSocket)

  private
  _primitive_as_task_callback(primitive) is
    taskWait((procedure (wakeup) do {
	  callback is (procedure (res) do {
	      switch res in {
		    case failed(err) do wakeup(task { raise err });
		    case success(S) do wakeup(taskReturn(S))
	      }
	    });
	  primitive(callback)
	}));

  -- Client sockets

  connectTcp(string(addr), integer(port)) is _primitive_as_task_callback(
    (procedure(callback) do __tcp_connect(addr,port,callback)))
  
  socketClose(s) do _socket_close(s)
  
  socketRead(s, bb) is
    _primitive_as_task_callback(nonInteger,
      (procedure (callback) do {
        _socket_read(s, _raw_bytebuffer(bb), callback);
      }))

  socketWrite(s, bb) is
    _primitive_as_task_callback(nonInteger,
      (procedure (callback) do {
        _socket_write(s, _raw_bytebuffer(bb), callback);
      }))

  -- Server sockets

  listenTcp(addr, port) is _listen_tcp(addr, port) -- assert port 0-65535?

  acceptConnection(s) is
    _primitive_as_task_callback(_get_nonValue(), -- does not work at runtime?!: nonValue,
      (procedure (callback) do {
        _accept_connection(s, callback);
      }))

  serverSocketClose(s) do _server_socket_close(s)

  -- Addresses
  implementation pPrint over inetAddress is {
    ppDisp(addr) is ppStr(_inet_address_to_string(addr));
  }
  
  -- file I/O
  
}
