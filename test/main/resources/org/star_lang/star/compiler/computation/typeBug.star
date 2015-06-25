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
typeBug is package {
  import task;
  import cml;

  fun serve(ch) is task {
    valis valof
      (switch await(recv(ch)) in {
        case true is task { valis valof serve(ch); }
        case false is task { valis (); }
      })
  }

  prc main() do {
    def ch is channel();
    def _ is valof backgroundF(serve(ch));
    assert (valof send(ch, true)) = ();
    assert (valof send(ch, false)) = ();
  }

};