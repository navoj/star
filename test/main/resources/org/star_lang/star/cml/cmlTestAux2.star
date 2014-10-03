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
cmlTestAux2 is package {

  import cml;
  import task;
  
  throwIn has type (channel of %a, %a, integer) => task of %a
  throwIn(ch, v, 0) is taskReturn(v)
  throwIn(ch, v, count) is
    taskBind(send(ch, v),
      (function (_) is
        taskBind(await(recvRv(ch)),
          (function (v2) is
            throwIn(ch, v2, count-1)))))
  
}
