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
eqgenerate is package {
    type Node is Node {
        name has type string;
        children has type list of Node;
    };
    main() do {
        node is Node{name=""; children=list of []};
        assert node=node;
        node2 is Node{name="2"; children=list of [node]};
        assert node!=node2;
        assert node2=node2;
        node3 is Node{name="3";children=list of [node]};
        assert node2!=node3 and node3=node3;
    }
 }