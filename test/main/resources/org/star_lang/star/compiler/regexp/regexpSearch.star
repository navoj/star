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
regexpSearch is package{

  def fooPattern is ( () from (S matching `.*foo.*`));

  subjectPatterns has type list of ((string, () <= string));
  def subjectPatterns is list of [
    ("foo", fooPattern)
  ];

  def testResult is all D where (D, _) in subjectPatterns;
  
  prc main() do {
    logMsg(info,"$testResult");
    assert testResult=list of ["foo"];
  }
}