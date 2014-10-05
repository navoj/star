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
import metamodeldefn;

modeltypes is package {
  type In is In{STR_STREAM has type stream of any};
  type MODEL_IN is MODEL_IN{
    ATTRIBUTE_UPDATES has type stream of dictionary of (string, any);
    DEFAULT has type stream of mModel;
    GET_CONFIGURATION has type (string) => mModel
  };
  type IManage is IManage{
    PAUSE has type action();
    PREPARE has type action();
    RELEASE has type action();
    RESUME has type action();
    START has type action();
    START_DATAFLOW has type action();
    STOP has type action();
    STOP_DATAFLOW has type action()
  };
  
  type MODEL_OUT is MODEL_OUT{
    DEFAULT has type stream of mModel;
    DEFAULT_RELATION has type list of mModel
  };
  
  createIn(notifyFunc, requestFunc) is { 
    STR_STREAM(x) do notifyPort(notifyFunc, "STR_STREAM", x);
  };

  createMODEL_IN(notifyFunc, requestFunc) is {
    ATTRIBUTE_UPDATES(x) do notifyPort(notifyFunc, "ATTRIBUTE_UPDATES", x);
    DEFAULT(x) do notifyPort(notifyFunc, "DEFAULT", x);
    GET_CONFIGURATION(x) do requestPort(requestFunc, "GET_CONFIGURATION", x);
  };

  createIManage(notifyFunc, requestFunc) is {
    PAUSE(x) do requestPort(requestFunc, "PAUSE", x);
    PREPARE(x) do requestPort(requestFunc, "PREPARE", x);
    RELEASE(x) do requestPort(requestFunc, "RELEASE", x);
    RESUME(x) do requestPort(requestFunc, "RESUME", x);
    START(x) do requestPort(requestFunc, "START", x);
    START_DATAFLOW(x) do requestPort(requestFunc, "START_DATAFLOW", x);
    STOP(x) do requestPort(requestFunc, "STOP", x);
    STOP_DATAFLOW(x) do requestPort(requestFunc, "STOP_DATAFLOW", x);
  };
  
  notifyPort(Fn,Name,Arg) do Fn(Arg);
  requestPort(Fn,Name,Arg) do Fn(Arg);
  queryPort(Fn,Name,Arg) is Fn(Arg);
}