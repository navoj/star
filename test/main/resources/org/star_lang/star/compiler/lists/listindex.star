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
listindex is package{
  main() do {
    LL is list of {1; 2; 3; 4; 5; 6; 7};
    var KL := LL;
    
    assert LL[0]=1;
    
    KL[0] := -1;
    
    assert LL[0] = 1;
    assert KL[0] = -1;
    
    logMsg(info,"$(LL[2:4])");
    
    assert LL[4:]=list of {5; 6; 7};
    
    KL[4:6] := list of {10; 20};
    logMsg(info,"KL=$KL, KL[4:6]=$(KL[4:6])");
    assert KL=list of {-1;2;3;4;10;20;7};
    assert KL[4:6] = list of {10;20};
    
    CC is cons of {1; 2; 3; 4; 5; 6; 7};
    var KC := CC;
    
    assert CC[0]=1;
    KC[0] := -1;
    
    assert CC[0] = 1;
    assert KC[0] = -1;
    
    logMsg(info,"$(CC[2:4])");
    KC[4:5] := cons of {10; 20};
    logMsg(info,"KC=$KC");
    assert KC=cons of {-1;2;3;4;10;20;6;7};
    assert KC[4:6] = cons of {10; 20};
  }
}