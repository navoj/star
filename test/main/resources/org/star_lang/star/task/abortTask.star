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
abortTak is package{
  import task;
  
  fun aa(X) is task {
    if X > 10 then
      raise "too large"
    else
      valis X*2;
  }
  
  fun aa2(X) is taskWait(((wakeup) do {
    if X > 10 then
      wakeup(taskFail(exception(nonString,"too large" cast any,__location__)))
    else
      wakeup(taskReturn(X*2));
  })); 

  bb has type (integer, (integer) => task of integer) => task of integer
  fun bb(X, f) is task {
    try {
      def v is valof f(X);
      valis v;
    } on abort {
      _ do valis X;
    }
  }

  prc main() do {
    assert valof bb(5, aa) = 10;
    assert valof bb(15, aa) = 15;
    
    assert valof bb(5, aa2) = 10;
    assert valof bb(15, aa2) = 15;
  }
}