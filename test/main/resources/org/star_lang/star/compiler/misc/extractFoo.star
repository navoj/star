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
extractFoo is package{
  extractFoo has type for all f,t such that (t) => f where t implements{ foo has type f };
  extractFoo(x) is x.foo;
  
  type foo is foo{
    foo has type string;
  } or bar{
    foo has type string;
    bb has type integer;
  }

  main() do{
    FF is { foo = 23; bar = "alpha"};
    
    assert extractFoo(FF)=23;
    
    GG is foo{foo="beta"}
    
    assert extractFoo(GG)="beta"
  }
} 