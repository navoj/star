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
weights is package {
 type vertex is alias of integer;
 type weight is alias of integer;
 type intmap of t is alias of dictionary of (integer, t);
 type graph is alias of intmap of intmap of weight;

 weight has type (graph, vertex, vertex) => option of weight;
 weight(g, i, j) is
   (present g[i] ? (present g[i][j] ? some(g[i][j]) | none) | none);
}