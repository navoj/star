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
tcbug is package {
-- copied verbatim from Prelude:
type Either of (%a, %b) is Left(%a) or Right(%b)
type CollEnumerator of (%val, %seed) is alias of
    ((Iteratee of (%val, %seed), %seed) => %seed);
type Iteratee of (%val, %seed) is alias of
    ((%seed, %val) => Either of (%seed, %seed));

-- bug triggering code
enumerateEnumerator has type (CollEnumerator of (%val, %seed)) => CollEnumerator of ((integer, %val), %seed);
enumerateEnumerator(enum) is
  (function (it, seed) is
    enum((function ((count, seed1), val1) is
      case it(seed1, (count, val1)) in {
        Right(v) is Right((count+1, v));
        Left(v) is Left(v)}),
        (0, seed)));
}