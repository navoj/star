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
