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
matchComp is package{

  peel(list of [],list of []) is list of [];
  peel(list of [X,..XX],list of [X,..YY]) is list of [X,..peel(XX,YY)];
  peel(list of [X,..XX],list of [Y,..YY]) where X!=Y is peel(XX,YY);
  
  main() do {
    logMsg(info,"peel of [1,2,3],[1,3,3] is $(peel(list of [1,2,3],list of [1,3,3]))");
    assert peel(list of [1,2,3],list of [1,3,3])=list of [1,3]
  }
}