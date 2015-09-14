Foo is package {
type FooType is FooType{};
};

Bar is package {
private import Foo;
type BarType is alias of FooType;
}

Baz is package {
private import Bar;
private import Foo; /* doesn't help */
type BazType is BazType{
 bar has type BarType;
};
}