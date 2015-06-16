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
MyMap is package {

  contract Measured over %a determines %v where Monoid over %v is {
    measure has type (%a) => %v;
  };

/* measure in definitions of other measures */
  measure_a has type (%a) => %v where Measured over %a determines %v and Monoid over %v; -- '
  fun measure_a(a) is measure(a);

  contract Monoid over %v is {
    mempty has type %v;              -- identity of mappend
    mappend has type (%v, %v) => %v; -- an associative operation
  };

  type FingerTree of (%v, %a) is
      FingerTreeEmpty
      or Single(%a)
      or Deep(%v, Digit of %a, FingerTree of (%v, Node of (%v, %a)), Digit of %a);

  fun fingerTreeMeasure(FingerTreeEmpty) is mempty
   |  fingerTreeMeasure(Single(x)) is measure_a(x)
   |  fingerTreeMeasure(Deep(v, _, _, _)) is v

  implementation Measured over FingerTree of (%v, %a) determines %v where Measured over %a determines %v and Monoid over %v is { -- '
    measure = fingerTreeMeasure;
  };

  type Node of (%v, %a) is
      Node02(%v, %a, %a)
      or Node03(%v, %a,%a,%a)
      ;

  fun nodeMeasure(Node02(v, _,_)) is v
   |  nodeMeasure(Node03(v, _,_,_)) is v;

  implementation Measured over Node of (%v, %a) determines %v where Monoid over %v is {
    measure = nodeMeasure;
  };

/** smart constructors */
  node02 has type (%a,%a) => Node of (%v, %a) where Measured over %a determines %v and Monoid over (%v);
  fun node02(a01,a02) is Node02(mappend(measure_a(a01), measure_a(a02)), a01,a02);
  node03 has type (%a,%a,%a) => Node of (%v, %a) where Measured over %a determines %v and Monoid over (%v);
  fun node03(a01,a02,a03) is Node03(mappend(measure_a(a01), mappend(measure_a(a02), measure_a(a03))), a01,a02,a03);

  type Digit of %a is
      Digit00
      or Digit01(%a)
      or Digit02(%a,%a)
      or Digit03(%a,%a,%a)
      or Digit04(%a,%a,%a,%a)
      ;

  fun digitMeasure(Digit00) is mempty
   |  digitMeasure(Digit01(a)) is measure_a(a)
   |  digitMeasure(Digit02(a01,a02)) is mappend(measure_a(a01), measure_a(a02))
   |  digitMeasure(Digit03(a01,a02,a03)) is mappend(measure_a(a01), mappend(measure_a(a02), measure_a(a03)))
   |  digitMeasure(Digit04(a01,a02,a03,a04)) is mappend(measure_a(a01), mappend(measure_a(a02), mappend(measure_a(a03), measure_a(a04))))
   
  implementation Measured over Digit of %a determines %v where Measured over %a determines %v and Monoid over %v is {
    measure = digitMeasure;
  };

/* map */
  digitMap has type (((%a) => %b), Digit of %a) => Digit of %b
  fun digitMap(f, Digit00) is Digit00
   |  digitMap(f, Digit01(a01)) is Digit01(f(a01))
   |  digitMap(f, Digit02(a01, a02)) is Digit02(f(a01), f(a02))
   |  digitMap(f, Digit03(a01, a02, a03)) is Digit03(f(a01), f(a02), f(a03))
   |  digitMap(f, Digit04(a01, a02, a03, a04)) is Digit04(f(a01), f(a02), f(a03), f(a04))

/* smart constructor */
  deep has type (Digit of %a, FingerTree of (%v, Node of (%v, %a)), Digit of %a) => FingerTree of (%v, %a) where Measured over %a determines %v and Monoid over %v; -- '
  fun deep(pr, m, sf) is Deep(mappend(measure(pr), mappend(measure(m), measure(sf))), pr, m, sf)

  fingerTreeMap has type (((%a) => %b), (FingerTree of (%v, %a))) => FingerTree of (%u, %b) where Measured over %a determines %v and Measured over %b determines %u and Monoid over %v and Monoid over %u; -- '
  fun fingerTreeMap(f, FingerTreeEmpty) is FingerTreeEmpty
   |  fingerTreeMap(f, Single(a)) is Single(f(a))
   |  fingerTreeMap(f, Deep(_, l, m, r)) is let {
        -- node_f has type (Node of (%v, %a)) => Node of (%u, %b) where Measured over %a determines %v and Measured over %b determines %u and Monoid over %v and Monoid over %u; -- '
        fun node_f(Node02(_, a01, a02)) is node02( f(a01), f(a02))
         |  node_f(Node03(_, a01, a02, a03)) is node03(f(a01), f(a02), f(a03))
      } in valof {
        def fm is fingerTreeMap(node_f, m);
        valis deep(digitMap(f, l), fingerTreeMap(node_f, m), digitMap(f, r));
      };
}
