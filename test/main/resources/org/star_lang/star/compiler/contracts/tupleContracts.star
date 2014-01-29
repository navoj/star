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
tupleContracts is package {
  type redBlackTree of %a is RBT(redBlackTree of %a, %a, redBlackTree of %a);

  rbInsert has type (redBlackTree of %a, %a) => redBlackTree of %a where comparable over %a;
  rbInsert(s1, x) is s1

  type set of %s is Set(redBlackTree of ((%s, ())))

  setInsert has type (set of %i, %i) => set of %i where comparable over %i 'n equality over %i;
  setInsert(Set(t),a) is Set(rbInsert(t,(a,())));
}
