testOverride2 is package{

  contract PPrintable over %a is {
    sho has type (%a) => string;
  };

  implementation PPrintable over list of %a where PPrintable over %a is {
    fun sho(l) is "[]";
  };

  type HashTrie of (%k, %v) is Trie(integer, list of (HashTrie of (%k, %v)));

  showHashTrie has type (HashTrie of (%k, %v)) => string where PPrintable over %k and PPrintable over %v; -- '
  fun showHashTrie(Trie(bitmap, entries)) is sho(entries);

  implementation PPrintable over HashTrie of (%k, %v) where PPrintable over %k and PPrintable over %v is { -- '
    sho = showHashTrie;
  };
}