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
mbox is package{

  type mbox of %t is mbox{
    post has type action(%t);
    grab has type ()=>%t;
  }

  box() is mbox{
    private var Q := queue of {};
    private def lock is 1; -- value not important
     
    grab() is valof{
      sync(lock){
        when Q matches _back(F,E) do{
          Q := F;
          valis E;
        }
      }
    }; 
    post(M) do {
      sync(lock) {
        Q := _cons(M,Q);
      }
    }
  }
}