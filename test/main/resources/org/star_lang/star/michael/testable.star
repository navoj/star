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
testable is package {
    contract Testable of %t is {
        toTest has type (%t) => boolean;
    };

    implementation Testable over string is {
        toTest = __test_string;    
    } using {
        __test_string has type (string) => boolean;
        __test_string(s) is size(s) > 5;
    };

    implementation Testable over (list of %t where Testable over %t) is {
        toTest = __test_list;    
    } using {
        -- __test_list has type (list of %t) => boolean;
        __test_list(list of []) is true;
        __test_list(list of [H,..T]) is toTest(H) and __test_list(T);
    };    
}