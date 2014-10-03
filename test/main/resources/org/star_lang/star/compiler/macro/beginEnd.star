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
beginEnd is package{
  #prefix("procedure",1200);
  #prefix("return",1200);
  #pair("begin","end",2000);
  #begin ?B end ==> {B};
  #begin end ==> {};
  
  #procedure #(?Tmpl #@ ?body)# :: statement :- body::action;
  #begin ?B end :: action :- B;*action;
  #begin end :: action;
  
  
  #procedure #( #(?Tmpl)# begin ?body./#(return ?E)# end )#==> Tmpl is valof {body./#(valis E)#}; 
  #procedure #( #(?Tmpl)# begin ?body end)# ==> Tmpl do body ; 
}