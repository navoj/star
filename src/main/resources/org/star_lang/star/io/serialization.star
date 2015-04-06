import star;
serialization is package {

  private import compute;
  private import task;
  private import sequencePlus;
  private import bitstring;
  private import string;
  private import bytebuffer;
  import buffer;

  java org.star_lang.star.operators.pio.runtime.SerializeFloat;
  java org.star_lang.star.operators.pio.runtime.SerializeString;

/* type constructor for mutually recursive types */
  type SerializationStreamWriterArgs is SSWArgs(bytebuffer, SerializationStreamWriter);
  
/* SerializationStreamWriter removes at least one byte from the
 * bytebuffer, that is, it returns a (possibly new, but usually the
 * same) bytebuffer with at least one byte fewer 'remaining' (in the
 * meaning of bytebuffer_remaining()). In the best case, there is no
 * byte 'remaining'.  The bytebuffer argument is already flipped, ready
 * for reading.  The intention is that the SerializationStreamWriter
 * reads the bytes and writes them to a stream or sth. similar.  The
 * returned bytebuffer's capacity must be equal to the capacity of the
 * bytebuffer argument.  The SerializationStreamWriter argument is
 * mainly for convenience: it allows anonymous functions to return
 * themselves. */
 
/* Note: "writer" means that the serialization stream is written out;
 * of course, the "writer" has to read the shove buffer (the
 * serialization done so far) to write it out. */
 
  type SerializationStreamWriter is alias of ((SerializationStreamWriterArgs) => task of (SerializationStreamWriterArgs));

/* The state of the shover state monad: current shover buffer and
 stream writer; * the stream writer is called when the buffer "is
 full", to write out some bytes * and make room in the shover buffer
 (see comment at SerializationStreamWriter). */
  type ShoverState is
      ShoverState{
	buffer has type bytebuffer;
	sStreamWriter has type SerializationStreamWriter;
      };

/* type of the shover state monad */
  type shoverM of %t is shoverM(
   (ShoverState) => task of ((%t, ShoverState))
  );

  implementation (computation) over shoverM is {
    _encapsulate is shoverReturn;
    _combine is shoverBind;
    _abort(_) is raise "no fail operation for shoverM";
    _handle(m, h) is raise "no handle operation for shoverM";
  } using {
    shoverReturn(x) is shoverM((function (s) is task { valis (x, s) }));
    shoverBind(shoverM(m), f) is shoverM((function (s) is
	  task {
	    (t1, s1) is valof m(s);
	    shoverM(f1) is f(t1);
	    /* work around starbug: valis valof f1(s1) */
	    tmp is valof f1(s1);
	    valis tmp;
	  }));
  };

-- must be implemented, but is never called actually; will probably replaced by injection
  implementation execution over shoverM is {
    _perform(_, _) is raise "Not implemented";
  };

/* type of all shovers */
  type shover is alias of shoverM of ();

/* helper function: put needed_bytes, but empty buffer first, if they don't fit */
  ensure_put has type (integer, (bytebuffer, %i) => bytebuffer) => ((ShoverState, %i) => task of ShoverState);
  ensure_put(needed_bytes, put) is
      (function (s0 matching ShoverState{buffer=bb0;sStreamWriter=sStreamWriter0}, i) is valof {
	 s1 is (bytebuffer_remaining(bb0) >= needed_bytes
	  ? s0
	    | valof task {
	      bb1 is bytebuffer_flip(bb0);
	      SSWArgs(bb2, sStreamWriter1) is valof writeBufferToStream(SSWArgs(bb1, sStreamWriter0));
	      valis (s0 substitute {buffer=bb2;sStreamWriter=sStreamWriter1})});
	 valis task {
	   bb is put(s1.buffer, i);
	   valis s1 substitute {buffer = bb}
	 }});
/* -- compiler got the following wrong (alternative branch entered on true condition)
 -- that construct works in fillBufferAtLeast, though
 task {
 s1 is (bytebuffer_remaining(bb0) >= needed_bytes
 ? s0
 | valof task {
 bb1 is bytebuffer_flip(bb0);
 (bb2, sStreamWriter1) is valof writeBufferToStream((bb1, sStreamWriter0));
 valis (s0 substitute {buffer=bb2;sStreamWriter=sStreamWriter1})});
 bb is put(s1.buffer, i);
 valis s1 substitute {buffer = bb}
 });
 */

/* shove word of N bits (%i) into bytebuffer, emptying buffer, if necessary */
  shoveWordN has type (integer, (bytebuffer, %i) => bytebuffer) => ((%i) => shover);
  shoveWordN(needed_bytes, bb_putWordN) is let {
    ensured_putWordN is ensure_put(needed_bytes, bb_putWordN);
  } in
  (function (i) is shoverM(
    (function (s1) is task {
       s2 is valof ensured_putWordN(s1, i);
       valis ((), s2);
     })));

/* shove 8-bit word into bytebuffer */
  shoveWord8 has type (word8) => shover;
  shoveWord8 is let { shover is shoveWordN(1, bytebuffer_putWord8); } in
  (function(i) is shover(i));

/* shove 16-bit word into bytebuffer */
  shoveWord16 has type (word16) => shover;
  shoveWord16 is let { shover is shoveWordN(2, bytebuffer_putWord16); } in
  (function(i) is shover(i));
  
/* shove 32-bit word into bytebuffer */
  shoveWord32 has type (word32) => shover;
  shoveWord32 is let { shover is shoveWordN(4, bytebuffer_putWord32); } in
  (function(i) is shover(i));

/* shove 64-bit word into bytebuffer */
  shoveWord64 has type (word64) => shover;
  shoveWord64 is let { shover is shoveWordN(8, bytebuffer_putWord64); } in
  (function(l) is shover(l));

/* put contents of source bytebuffer into destination bytebuffer as is */
/*
/* implementation considerations:
 * strategy 1: always fill shove buffer first
 *             (+) easier implementation,
 *             (+) never initiate a small write
 *             (-) lots of copying, in particular for large writes
 * strategy 2: send given buffer directly
 *             (+) allows large writes for large send
 *             (-) might initiate a small write: write (only) the
 *                 few bytes of the shove byte buffer and/or the last
 *                 bytes of the large send buffer (possible fix for the
 *                 latter: don't write if rest of send buffer fits in
 *                 shove buffer)
 * scenarios:
 | shove buffer | sent buffer | scenario 1 | scenario 2          |
 |--------------+-------------+------------+---------------------|
 | small        | small       | ok         | ok                  |
 | small        | large       | ok         | great               |
 | large        | small       | great      | great               |
 | large        | large       | ok         | possibly small send |
 *
 * The following implements strategy 2 without the fix for small
 * writes.
 */
  shoveByteBuffer has type (bytebuffer) => shover;
  shoveByteBuffer(src0) is shoverM(
   (function(s0 matching ShoverState{buffer=bb0;sStreamWriter=sStreamWriter0}) is
	((bytebuffer_remaining(bb0) >= bytebuffer_remaining(src0))
	 ? task { /* src0 fits in shove byte buffer */
	     (bb1, _) is bytebuffer_putByteBuffer(bb0, src0); /* fill w/o stream writing */
	     valis ((), s0 substitute {buffer=bb1}) }
	   | task {
	     bb1 is bytebuffer_flip(bb0);
	     SSWArgs(bb2, sStreamWriter1) is valof writeBufferToStream(SSWArgs(bb1, sStreamWriter0));
	     bb3 is bytebuffer_clear(bb2);
	     SSWArgs(_, sStreamWriter2) is valof writeBufferToStream(SSWArgs(src0, sStreamWriter1));
	     valis ((), s0 substitute{buffer=bb3;sStreamWriter=sStreamWriter2});
	   })));

/* write buffer to streeam using serialization stream writer; returns cleared buffer */
  writeBufferToStream has type (SerializationStreamWriterArgs) => task of SerializationStreamWriterArgs;
  writeBufferToStream(args) is task {
    SSWArgs(bb0, sStreamWriter) is valof helper(args);
    bb1 is bytebuffer_clear(bb0);
    valis SSWArgs(bb1, sStreamWriter);
  } using {
    helper has type (SerializationStreamWriterArgs) => task of SerializationStreamWriterArgs;
    helper(args0 matching SSWArgs(bb0, sStreamWriter)) where bytebuffer_hasRemaining(bb0) is task {
      args1 is valof sStreamWriter(args0);
      tmp is valof helper(args1);
      valis tmp;
    }
    helper(args0) default is task { valis args0 };
  }

/* run shover on bytebuffer */
  runShover has type (bytebuffer, SerializationStreamWriter, shover) => task of SerializationStreamWriterArgs;
  runShover(os, sStreamWriter, shoverM(f)) is task {
    (_, s) is valof f(ShoverState{buffer=os;sStreamWriter=sStreamWriter});
    valis SSWArgs(s.buffer, s.sStreamWriter);
  };

/* type constructor for mutually recursive types */
  type SerializationStreamReaderArgs is SSRArgs(bytebuffer, SerializationStreamReader); -- for compiler / task monad
/* SerializationStreamReader tries to fill a (probably already
 * partially filled) bytebuffer with at least one byte (eg. by reading
 * from some stream); it returns a filled bytebuffer and a
 * serialization stream reader, which each may be new objects or the
 * same as passed in. If the bytebuffer is newly allocated, it must
 * behave as if it was not newly allocated (ie. must contain original
 * contents and have same capacity; limit might have changed, though).
 * The bytebuffer's position is supposed to have advanced by at least
 * one byte. The serialization stream reader argument is mainly for
 * convenience: it allows anonymous functions to return themselves. */
/* Note: "reader" means the serialization stream is read-in. Of course,
 * the "reader" has to write the read-in bytes in to the yank buffer. */
  type SerializationStreamReader is alias of ((SerializationStreamReaderArgs) => task of SerializationStreamReaderArgs);

/* The state of the yanker state monad: the current yank buffer and a reader,
 * which is called if the yank buffer "gets empty" to fill the yank buffer. */
  type YankerState is
      YankerState {
	buffer has type bytebuffer;
	sStreamReader has type SerializationStreamReader;
      };
  
/* read %t from bytebuffer */
  type yanker of %t is yankerM(
   (YankerState) => task of ((%t, YankerState))
  );

  implementation (computation) over yanker is {
    _encapsulate is yankerReturn;
    _combine is yankerBind;
    
    _abort(_) is raise "no fail operation for yankerM";
    _handle(m, h) is raise "no handle operation for yankerM";
  } using {
    yankerReturn(x) is yankerM((function (s) is task { valis (x, s) }));
    yankerBind(yankerM(m), f) is yankerM((function (s0) is 
	  task {
	    (v1, s1) is valof m(s0);
	    yankerM(f1) is f(v1);
	    valis valof f1(s1);
	  }));
  };

-- must be implemented, but is never called actually; will probably replaced by injection
  implementation execution over yanker is {
    _perform(_, _) is raise "Not implemented";
  };

/* read word of N bits (%i) from bytebuffer */
  yankWordN has type ((YankerState) => task of ((%i, YankerState))) => yanker of %i;
  yankWordN(bb_getN) is
      yankerM((function (s1) is task {
	  (first, s2) is valof bb_getN(s1);
	  valis (first, s2);
	}));
  
  yankerEval has type (task of %t) => yanker of %t
  yankerEval(t) is yankerM((function (s1) is task {
      v is valof t;
      valis (v, s1);
    }))

/* ensure a get operation for required_bytes can complete without buffer underflow by
 * calling the stream reader of the yanker. */
   ensure_get has type (integer, (bytebuffer) => (%i, bytebuffer)) => ((YankerState) => task of ((%i, YankerState)));
  ensure_get(required_bytes, getter) is
      (function (s0 matching YankerState{buffer=bb0;sStreamReader=sStreamReader0}) is task {
	 s1 is (bytebuffer_remaining(s0.buffer) >= required_bytes
	  ? s0
	    | valof task {
	      bb1 is bytebuffer_compact(bb0);
	      SSRArgs(bb2, sStreamReader1) is valof fillBufferAtLeast(required_bytes, SSRArgs(bb1, sStreamReader0));
	      valis s0 substitute{buffer=bb2;sStreamReader=sStreamReader1}
	    });
	 (v, bb) is getter(s1.buffer);
	 valis (v, s1 substitute {buffer = bb});
       });

/* read word of 8 bits from bytebuffer */
  yankWord8 has type yanker of word8;
  yankWord8 is yankWordN(ensure_get(1, bytebuffer_getWord8));

/* read word of 16 bits from bytebuffer */
  yankWord16 has type yanker of word16;
  yankWord16 is yankWordN(ensure_get(2, bytebuffer_getWord16));

/* read word of 32 bits from bytebuffer */
  yankWord32 has type yanker of word32;
  yankWord32 is yankWordN(ensure_get(4, bytebuffer_getWord32));

/* read word of 64 bits from bytebuffer */
  yankWord64 has type yanker of word64;
  yankWord64 is yankWordN(ensure_get(8, bytebuffer_getWord64));

/* read sz bytes (word8) into a bytebuffer */
/* the returned bytebuffer will be read-only and its contents will
 * only be valid until the next yank; if you need the bytebuffer for
 * longer, copy it (which this function does not do for performance
 * reasons) */
/* implementation considerations:
 * strategy one: always fill yank buffer
 *               (+) easier implementation, (+) never initiate a small read
 *               (-) lots of copying for large receives
 * strategy two: use extra receive buffer if larger than yank buffer
 *               (+) allows large reads for large receive buffers
 *               (-) might initiate a small read: fill (only) the last
 *                   bytes of the large receive buffer (possible fix:
 *                   "append" yank buffer to receive buffer)
 * scenarios:
 | yank buffer | rcvd. buffer | strategy 1 | strategy 2 |
 |-------------+--------------+------------+------------|
 | small       | small        | ok         | ok         |
 | small       | large        | ok         | great      |
 | large       | small        | great      | great      |
 | large       | large        | ok         | ok         |
 *
 * The following implements strategy two without the fix (as the fix
 * leads to having the yank byte buffer in memory twice, which might be
 * a cost larger than expected).
 */
  yankByteBuffer has type (integer) => yanker of bytebuffer;
  yankByteBuffer(sz) is yankerM((function(s0 matching YankerState{buffer=bb0;sStreamReader=sStreamReader0}) is
	(sz <= bytebuffer_capacity(bb0)
	 ?  /* can use our own buffer */
	   task {
	     bb1 is bytebuffer_compact(bb0);
	     SSRArgs(bb2, sStreamReader1) is valof fillBufferAtLeast(sz, SSRArgs(bb1, sStreamReader0));
	     result_bb0 is bytebuffer_readonly_slice(bb1, 0, sz);
	     bb3 is bytebuffer_set_position(bb2, sz); /* already read into result_bb0 */
	     valis (result_bb0, s0 substitute {buffer=bb3; sStreamReader=sStreamReader1});
	   }
	   | task { /* need larger result buffer */
	     result_bb0 is bytebuffer(sz);
	     (result_bb1, _) is bytebuffer_putByteBuffer(result_bb0, bb0); /* always succeeds, as capacity(result_bb0) = sz > capacity(bb0) */
	     bb1 is bytebuffer_set_limit(bb0, 0);
	     SSRArgs(result_bb2, sStreamReader1) is valof fillBufferAtLeast(sz, SSRArgs(result_bb1, sStreamReader0));
	     result_bb3 is bytebuffer_asReadOnlyBuffer(result_bb2); /* for consistency with other branch */
	     valis (result_bb3, s0 substitute {buffer=bb1; sStreamReader=sStreamReader1});
	   })));

/* fill buffer with at least sz bytes, returning flipped bytebuffer */
/* This is different from setting a byte buffer limit, as it allows reading more bytes. */
  fillBufferAtLeast has type (integer, SerializationStreamReaderArgs) => task of SerializationStreamReaderArgs;
  fillBufferAtLeast(sz, SSRArgs(bb0, sStreamReader)) where bytebuffer_position(bb0) >= sz is
      task {
	bb1 is bytebuffer_flip(bb0);
	valis SSRArgs(bb1, sStreamReader);
      };
  fillBufferAtLeast(sz, args0 matching SSRArgs(_, sStreamReader)) default is
      task {
	args1 is valof sStreamReader(args0);
	tmp is valof fillBufferAtLeast(sz, args1);
	valis tmp;
      }

/* run yanker on bytebuffer */
      runYanker has type (yanker of %t, bytebuffer, SerializationStreamReader) => task of ((%t, SerializationStreamReaderArgs));
  runYanker(yankerM(f), bb, sStreamReader) is task {
    (v, s) is valof f(YankerState{buffer=bb; sStreamReader=sStreamReader});
    valis (v, SSRArgs(s.buffer, s.sStreamReader));
  };

/* %t that can be written and read from bytebuffer */
/* invariant: decode o bytebuffer_flip o encode == id */
  contract serializable over %t is {
    shove has type (%t) => shover;
    yank has type yanker of %t;
  };

/* always failing stream reader and writer for fixed-size buffers */
  failing_sStreamReader(s) is task { raise "buffer underflow during decoding" };
  failing_sStreamWriter(s) is task { raise "buffer overflow during encoding" };

/* shove a %t into bytebuffer; if the buffer is full, call SerializationStreamWriter w to empty it. */
/* the returned bytebuffer is possibly the same as the argument */
  encodeWithWriter has type (bytebuffer, SerializationStreamWriter, %t) => task of ((bytebuffer, SerializationStreamWriter)) where serializable over %t;
  encodeWithWriter(os1, w, x) is task {
    SSWArgs(os2,w2) is valof runShover(os1, w, shove(x));
    valis (os2,w2);
  };

  encodeWithWriterAndShover has type (bytebuffer, SerializationStreamWriter, shover) => task of ((bytebuffer, SerializationStreamWriter));
  encodeWithWriterAndShover(os1, w, s) is task {
    SSWArgs(os2, w2) is valof runShover(os1, w, s);
    valis (os2, w2);
  };

/* shove a %t into bytebuffer; if the buffer is full, fail */
/* the returned bytebuffer is possibly the same as the argument */
  encode has type (bytebuffer, %t) => bytebuffer where serializable over %t;
  encode(os0, x) is valof {
    (os1, _) is valof (encodeWithWriter(os0, failing_sStreamWriter, x));
    valis os1;
  };

/* yank %t from a bytebuffer; if bytebuffer gets empty, call SerializationStreamReader r to refill bytebuffer */
  decodeWithReader has type (bytebuffer, SerializationStreamReader) => task of ((%t, (bytebuffer, SerializationStreamReader))) where serializable over %t;
  decodeWithReader(b, r) is task {
    (v, SSRArgs(b2, r2)) is valof runYanker(yank, b, r);
    valis (v, (b2, r2));
  };

  decodeWithReaderAndYanker has type (bytebuffer, SerializationStreamReader, yanker of %t) => task of ((%t, (bytebuffer, SerializationStreamReader)));
  decodeWithReaderAndYanker(b, r, y) is task {
    (v, SSRArgs(b2, r2)) is valof runYanker(y, b, r);
    valis (v, (b2, r2));
  };

/* yank %t from a bytebuffer; fail if bytebuffer gets empty */
  decode has type (bytebuffer) => (%t, bytebuffer) where serializable over %t;
  decode(b) is valof {
    (v, (bb, _)) is valof (decodeWithReader(b, failing_sStreamReader));
    valis (v, bb);
  }



/***************************************************/
/* implementation serializable over built-in types */
/***************************************************/

  implementation serializable over integer is {
    shove(i) is shoveInteger(i);
    yank is yankInteger;
  } using {
    /* In star, 'nonInteger' is also an integer; to avoid writing a
     * prefix tag byte for each integer (needing 5 bytes for every
     * integer), we write a suffix tag byte only for 'nonInteger' and one
     * MAGIC_INT (needing 5 bytes only for these two cases), which
     * represents both 'nonInteger' and the real integer MAGIC_INT. */
    MAGIC_INT is 0x80000002; /* sth. that is "rare"; here: largest negative integer plus two */
    shoveInteger(nonInteger) is shoverM computation {
      perform shoveWord32(word32(MAGIC_INT));
      perform shoveWord8(word8(0));
      valis ();
    };
    shoveInteger(i) is shoverM computation {
      perform shoveWord32(word32(i));
      if i = MAGIC_INT then
	perform shoveWord8(word8(1));
      valis ();
    };

    yankInteger is yanker computation {
      _word32(i) is valof yankWord32;
      if i = MAGIC_INT then {
	_word8(t) is valof yankWord8;
	if t = 0 then {
	  valis nonInteger;
	} else {			-- t = 1
	  valis i;
	}
      }
      else {
	valis i;
      }
    };
  }

/* serialize chars as three bytes; suffices to write all possible
 Unicode characters, which have 21 bits (note: (strict) UTF-8 is not a
 sufficient encoding to transmit and receive characters, so we stay
 with sending three bytes). */
  implementation serializable over char is {
    shove is shoveChar;

    yank is yankChar;
  } using {
    MAGIC_CHAR is 0xE08080;
    shoveChar(nonChar) is shoveChar0(MAGIC_CHAR);
    shoveChar(c) default is shoveChar0(c as integer);

    shoveChar0(sv) is shoverM computation {
      perform shoveWord8(word8(sv .&. 0xff));
      perform shoveWord16(word16((sv .>>>. 8) .&. 0xffff));
      valis ();
    };

    yankChar is yanker computation {
      _word8(o1) is valof yankWord8;
      _word16(s2) is valof yankWord16;
      i is (o1 .|. (s2 .<<. 8));
      valis (i = MAGIC_CHAR
       ? nonChar
	 | (i as char));
    };
  };

/* serialize string, using UTF-8 internally */
  implementation serializable over string is {
    shove(s) is shoveString(s);
    yank is yankString;
  } using {
    shoveString(nonString) is shoveWord32(word32(-1));
    shoveString(s) is shoverM computation {
      utf8 is __string_to_utf8(s);
      sz is bytebuffer_remaining(utf8);
      perform shoveWord32(word32(sz));
      perform shoveByteBuffer(utf8);
      valis ();
    };

    yankString is yanker computation {
      sz is valof yankWord32;
      valis valof yankString0(sz);
    }

    yankString0(_word32(-1)) is yanker computation { valis nonString };
    yankString0(_word32(sz)) default is yanker computation {
      bb is valof yankByteBuffer(sz);
      valis __string_of_utf8(bb);
    };
  };

/* serialize boolean */
  implementation serializable over boolean is {
    shove(false) is shoveWord8(word8(0));
    shove(true) is shoveWord8(word8(1));

    yank is yanker computation {
      valis convBoolean(valof yankWord8);
    };
  } using {
    convBoolean(_word8(0)) is false;
    convBoolean(_word8(1)) is true;
  };

/* serialize cons, using a tag byte for each cell */
/* An alternative, at the cost of computing the cons list's length,
 is to write the cons length plus all items without tag byte. */
/* TODO do not build up stack upon receive */
  implementation serializable over cons of %a where serializable over %a is {
    shove(l) is shoveCons(l);
    yank is yankCons;
  } using {
    shoveCons(nil) is shoveWord8(word8(0));
    shoveCons(cons(a,rest)) is shoverM computation {
      perform shoveWord8(word8(1));
      perform shove(a);
      valis valof shoveCons(rest);
    };

    yankCons has type yanker of cons of %a;
    yankCons is yanker computation {
      w is valof yankWord8;
      valis valof yankCons2(w);
    };

    yankCons2(_word8(0)) is yanker computation { valis nil; };
    yankCons2(_word8(1)) is yanker computation {
      a is valof yank;
      rest is valof yankCons;
      valis cons(a, rest);
    };
  };


/* serialize array by writing out its size plus its items */
  implementation serializable over list of %a where serializable over %a is {
    shove(l) is shoveArray(l);
    yank is yankArray;
  } using {
    shoveArray(l) is shoverM computation {
      sz is size(l);
      perform shove(sz);
      valis valof shoveArray2(0, sz, l);
    };

    shoveArray2(sz, sz, _) is shoverM computation { valis (); };
    shoveArray2(i, sz, l) default is shoverM computation {
      perform shove(l[i]);
      valis valof shoveArray2(i+1, sz, l);
    };

    yankArray is yanker computation {
      sz is valof yank;
      valis valof yankArray2(0, sz, _nil());
    }

    yankArray2(sz, sz, arr) is yanker computation { valis arr };
    yankArray2(i,  sz, arr) default is yanker computation {
      a is valof yank;
      valis valof yankArray2(i+1, sz, _apnd(arr, a));
    }
  };

/* serialize long using tag byte for nonLong */
/* An alternative is to use the same 'trick' as for integer */
  implementation serializable over long is {
    shove(l) is shoveLong(l);
    yank is yankLong;

  } using {
    MAGIC_LONG is 0x8000000000000002L;
    shoveLong(nonLong) is shoveWord64(word64(MAGIC_LONG));
    shoveLong(l) default is shoveWord64(word64(l));

    yankLong is yanker computation {
      l is valof yankWord64;
      valis valof yankLong0(l);
    }

    yankLong0(_word64(l)) where l = MAGIC_LONG is yanker computation { valis nonLong };
    yankLong0(_word64(l)) default is yanker computation { valis l };
  };

/* serialize pairs */
  implementation serializable over ((%a, %b)) where serializable over %a and serializable over %b is { -- '
    shove(p) is shovePair(p);
    yank is yankPair;
  } using {
    shovePair((a,b)) is shoverM computation {
      perform shove(a);
      valis valof shove(b);
    };
    yankPair is yanker computation {
      a is valof yank;
      b is valof yank;
      valis (a,b);
    }
  };
  
/* serialize triples */
  implementation serializable over ((%a, %b, %c)) where serializable over %a and serializable over %b and serializable over %c is {
    shove(p) is shove0(p);
    yank is yank0;
  } using {
    shove0((a,b,c)) is shoverM computation {
      perform shove(a);
      perform shove(b);
      valis valof shove(c);
    };
    yank0 is yanker computation {
      a is valof yank;
      b is valof yank;
      c is valof yank;
      valis (a,b,c);
    }
  };
/* serialize decimal by serializing its sting representation */
/* A more compact alternative is to serialize in blocks of 4 digits
 * (which fit into a word32) plus the decimal point position, or
 * in blocks of 18 or 19 digits (which fit into a word64; using 19
 * digits requires special handling of intermediate word64 values which
 * are negative longs). */
  implementation serializable over decimal is {
    shove(d) is shoveDecimal(d);
    yank is yankDecimal;
  } using {
    shoveDecimal(nonDecimal) is shoveWord8(word8(0));
    shoveDecimal(d) is shoverM computation {
      perform shoveWord8(word8(1));
      valis valof shoveDecimal0(d);
    }

    shoveDecimal0 has type (decimal) => shover;
    shoveDecimal0(d) is let {
      s is (d as string);
    } in shove(s);

    yankDecimal is yanker computation {
      w is valof yankWord8;
      valis valof yankDecimal0(w);
    }
    yankDecimal0(_word8(0)) is yanker computation { valis nonDecimal };
    yankDecimal0(_word8(1)) is yanker computation {
      s is valof yank;
      valis ((s has type string) as decimal);
    }
  }

/* serialize floats as word64 plus a suffix tag byte for nonFloat and NaN */
  implementation serializable over float is {
    shove(f) is shoveFloat(f);
    yank is yankFloat;
  } using {
    shoveFloat(nonFloat) is shoverM computation {
      perform shoveFloatBits(__float_nan_bits());
      valis valof shoveWord8(word8(0));
    };
    shoveFloat(f) is shoverM computation {
      perform shoveFloatBits(__float_to_bits(f));
      if __float_is_nan(f) then {
	perform shoveWord8(word8(1))
      }
      valis ();
    }

    shoveFloatBits(l) is shoveWord64(word64(l));

    yankFloat is yanker computation {
      _word64(l) is valof yankWord64;
      f is __float_of_bits(l);
      if __float_is_nan(f) then {
	_word8(o) is valof yankWord8;
	valis o = 0 ? nonFloat | f;
      } else {
	valis f;
      }
    };
  }

/* serialize dictionaries */
  implementation serializable over dictionary of (%k, %v) where serializable over %k and serializable over %v is { -- '
    shove(m) is shoveHash(m);
    yank is yankHash;
  } using {
    shoveHash(h) is shoverM computation {
      /* currently, there is no way to iterate over elements of a dictionary,
       * so we copy them to an array */
      var elements := array of {};
      for K->V in h do {
	elements := array of {(K,V);..elements};
      }
      valis valof shove(elements);
    }

    yankHash is yanker computation {
      elements is valof yank;
      valis hashFromElements(elements);
    }
    /* need type declaration for elements for for-loop */
    hashFromElements has type (array of ((%k, %v))) => dictionary of (%k, %v);
    hashFromElements(elements) is valof {
      var res := dictionary of {};
      for (K,V) in elements do {
        res[K] := V;
      }
      valis res;
    };
  }

/* serialize queues */
  implementation serializable over queue of %a where serializable over %a is {
    shove(q) is shoveQueue(q);
    yank is yankQueue;
  } using {
    shoveQueue(q) is shoverM computation {
      perform shove(q.front);
      valis valof shove(q.back);
    };
    yankQueue is yanker computation {
      f is valof yank;
      b is valof yank;
      valis queue{front=f;back=b};
    }
  };
}
