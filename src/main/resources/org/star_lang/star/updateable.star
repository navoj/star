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
private import base;
contract updateable over %r determines %t is {
  _extend has type (%r,%t)=>%r;
  _merge has type (%r, %r) => %r;
  _delete has type (%r, ()<=%t) => %r;
  _update has type (%r, ()<=%t, (%t)=>%t) => %r;
}

-- Update sub-language
# update ?Ptn in ?Tgt with ?Exp :: action :- Ptn::pattern :& Tgt::lvalue :& Exp::expression;
# delete ?Ptn in ?Tgt :: action :- Ptn::pattern :& Tgt::expression;
# extend ?Tgt with ?Exp :: action :- Tgt::lvalue :& Exp::expression;
# merge ?Tgt with ?Exp :: action :- Tgt::lvalue :& Exp::expression;

# extend ?Tgt with ?Exp ==> Tgt := _extend(Tgt,Exp);
# merge ?Tgt with ?Exp ==> Tgt := _merge(Tgt, Exp);
# delete ?Ptn in ?Tgt ==> Tgt := _delete(Tgt, (pattern() from Ptn));
# update ?Ptn in ?Tgt with ?Exp ==> Tgt := _update(Tgt, (pattern() from Ptn), (function(Ptn) is Exp));
