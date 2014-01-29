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
testOverride2 is package{

  contract PPrintable over %a is {
    show has type (%a) => string;
  };

  implementation PPrintable over list of %a where PPrintable over %a is {
    show(l) is "[]";
  };

  type HashTrie of (%k, %v) is Trie(integer, list of (HashTrie of (%k, %v)));

  showHashTrie has type (HashTrie of (%k, %v)) => string where PPrintable over %k 'n PPrintable over %v; -- '
  showHashTrie(Trie(bitmap, entries)) is show(entries);

  implementation PPrintable over HashTrie of (%k, %v) where PPrintable over %k 'n PPrintable over %v is { -- '
    show = showHashTrie;
  };
}