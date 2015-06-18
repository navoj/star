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
splitstring is package{

  match has type (string,string) => boolean;
  fun match(K,S) is K in splitString(S,"[|]");
  
  main has type action();
  prc main() do {
    logMsg(info,"splitting on |: $(splitString("BG|BG1|WB|MOLD","\\\\|"))");
    
    logMsg(info,"is foo in bar|foo|bar? $(match("foo","bar|foo|bar"))");
    logMsg(info,"is fob in bar|foo|bar? $(match("fob","bar|foo|bar"))");
    
    assert match("foo","bar|foo|bar");
    
    assert not match("fob","bar|foo|bar");
  }
} 