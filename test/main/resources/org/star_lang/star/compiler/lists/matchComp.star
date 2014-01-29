/**
 * 
 * Copyright (C) 2013 Starview Inc
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

  peel(list{},list{}) is list{};
  peel(list{X;..XX},list{X;..YY}) is list{X;..peel(XX,YY)};
  peel(list{X;..XX},list{Y;..YY}) where X!=Y is peel(XX,YY);
  
  main() do {
    logMsg(info,"peel of [1,2,3],[1,3,3] is $(peel(list{1;2;3},list{1;3;3}))");
    assert peel(list{1;2;3},list{1;3;3})=list{1;3}
  }
}