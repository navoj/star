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
letdefs is package{

  fun multi() is let{
    thisActor has type ()=>actor of {
      drop has type action()
    };
    def thisActor is memo actor{
      prc drop() do
        logMsg(info,"i am $(thisOne())")
    }
    def thisOne is memo thisActor;
  }
  in thisActor();
    
  prc main() do {
    def M1 is multi();
    def M2 is multi();
    
    request M1 to drop();
    request M2 to drop();
  }
}