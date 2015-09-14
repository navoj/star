package org.star_lang.star.operators.asynchio;

import org.star_lang.star.operators.Builtin;
import org.star_lang.star.operators.Intrinsics;
import org.star_lang.star.operators.asynchio.runtime.AsynchFileOps.AsynchFileClose;
import org.star_lang.star.operators.asynchio.runtime.AsynchFileOps.AsynchFileOpen;
import org.star_lang.star.operators.asynchio.runtime.AsynchFileOps.AsynchFileRead;
import org.star_lang.star.operators.asynchio.runtime.AsynchFileOps.AsynchFileWrite;
import org.star_lang.star.operators.asynchio.runtime.AsynchSocketOps.AsynchTcpAccept;
import org.star_lang.star.operators.asynchio.runtime.AsynchSocketOps.AsynchTcpRead;
import org.star_lang.star.operators.asynchio.runtime.AsynchSocketOps.AsynchTcpWrite;
import org.star_lang.star.operators.asynchio.runtime.AsynchSocketOps.AsynchTcpClose;
import org.star_lang.star.operators.asynchio.runtime.AsynchSocketOps.AsynchTcpConnect;
import org.star_lang.star.operators.asynchio.runtime.AsynchSocketOps.AsynchTcpListen;
import org.star_lang.star.operators.asynchio.runtime.AsynchSocketOps.AsynchServerClose;
import org.star_lang.star.operators.asynchio.runtime.FileChnl;
import org.star_lang.star.operators.asynchio.runtime.ServerChnl;
import org.star_lang.star.operators.asynchio.runtime.SocketChnl;

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
public class AsynchIo
{
  public static void declare()
  {
    SocketChnl.declare();
    FileChnl.declare();
    ServerChnl.declare();

    Intrinsics.declare(new Builtin(AsynchTcpConnect.name, AsynchTcpConnect.type(), AsynchTcpConnect.class));
    Intrinsics.declare(new Builtin(AsynchTcpListen.name, AsynchTcpListen.type(), AsynchTcpListen.class));
    Intrinsics.declare(new Builtin(AsynchTcpAccept.name, AsynchTcpAccept.type(), AsynchTcpAccept.class));
    Intrinsics.declare(new Builtin(AsynchTcpRead.name, AsynchTcpRead.type(), AsynchTcpRead.class));
    Intrinsics.declare(new Builtin(AsynchTcpWrite.name, AsynchTcpWrite.type(), AsynchTcpWrite.class));
    Intrinsics.declare(new Builtin(AsynchTcpClose.name, AsynchTcpClose.type(), AsynchTcpClose.class));
    Intrinsics.declare(new Builtin(AsynchServerClose.name, AsynchServerClose.type(), AsynchServerClose.class));
    Intrinsics.declare(new Builtin(AsynchFileOpen.name, AsynchFileOpen.type(), AsynchFileOpen.class));
    Intrinsics.declare(new Builtin(AsynchFileRead.name, AsynchFileRead.type(), AsynchFileRead.class));
    Intrinsics.declare(new Builtin(AsynchFileWrite.name, AsynchFileWrite.type(), AsynchFileWrite.class));
    Intrinsics.declare(new Builtin(AsynchFileClose.name, AsynchFileClose.type(), AsynchFileClose.class));
  }
}
