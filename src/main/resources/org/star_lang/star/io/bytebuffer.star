import star;
bytebuffer is package {
  private import buffer;

  type bytebuffer is alias of binary

  _raw_bytebuffer has type (bytebuffer) => binary
  _raw_bytebuffer(bb) is bb

  bytebuffer has type (integer) => bytebuffer
  bytebuffer(capacity) is _bb_allocate(capacity)

  bytebuffer_toString(bb) is valof {
    ro_bb0 is _bb_asReadOnlyBuffer(bb);
    ro_bb is bytebuffer_flip(ro_bb0);
    var res := nil;
    while (bytebuffer_hasRemaining(ro_bb)) do {
      (_word8(b), _) is bytebuffer_getWord8(ro_bb);
      c is (b as char);
      res := cons(c, res);
    }
    valis revImplode(res);
  }

  bytebuffer_toInts(bb) is valof {
    ro_bb0 is _bb_asReadOnlyBuffer(bb);
    ro_bb is bytebuffer_flip(ro_bb0);
    var res := nil;
    while (bytebuffer_hasRemaining(ro_bb)) do {
      (i, _) is bytebuffer_getWord8(ro_bb);
      res := cons(i, res);
    }
    valis reverse(res);
  }

  bytebuffer_hasRemaining has type (bytebuffer) => boolean;
  bytebuffer_hasRemaining(bb) is _bb_hasRemaining(bb);

  bytebuffer_remaining has type (bytebuffer) => integer;
  bytebuffer_remaining(bb) is _bb_remaining(bb);

  bytebuffer_putWord8 has type (bytebuffer, word8) => bytebuffer;
  bytebuffer_putWord8(bb, _word8(i)) is valof {
    _bb_putWord8(bb, i);
    valis bb;
  }

  bytebuffer_getWord8 has type (bytebuffer) => (word8, bytebuffer);
  bytebuffer_getWord8(bb) is (_word8(_bb_getWord8(bb)), bb);

  bytebuffer_putWord16 has type (bytebuffer, word16) => bytebuffer;
  bytebuffer_putWord16(bb, _word16(s)) is valof {
    _bb_putWord16(bb, s);
    valis bb;
  };

  bytebuffer_getWord16 has type (bytebuffer) => (word16, bytebuffer);
  bytebuffer_getWord16(bb) is (_word16(_bb_getWord16(bb)), bb);

  bytebuffer_putWord32 has type (bytebuffer, word32) => bytebuffer;
  bytebuffer_putWord32(bb, _word32(s)) is valof {
    _bb_putWord32(bb, s);
    valis bb;
  };

  bytebuffer_getWord32 has type (bytebuffer) => (word32, bytebuffer);
  bytebuffer_getWord32(bb) is (_word32(_bb_getWord32(bb)), bb);

  bytebuffer_putWord64 has type (bytebuffer, word64) => bytebuffer;
  bytebuffer_putWord64(bb, _word64(s)) is valof {
    _bb_putWord64(bb, s);
    valis bb;
  };

  bytebuffer_getWord64 has type (bytebuffer) => (word64, bytebuffer);
  bytebuffer_getWord64(bb) is (_word64(_bb_getWord64(bb)), bb);

  bytebuffer_getBytebuffer has type (bytebuffer, integer) => (bytebuffer, bytebuffer);
  bytebuffer_getByteBuffer(bb, sz) is (_bb_getByteBuffer(bb, sz), bb);

  bytebuffer_putByteBuffer has type (bytebuffer, bytebuffer) => (bytebuffer, bytebuffer);
  bytebuffer_putByteBuffer(dst, src) is valof {
    _bb_putByteBuffer(dst, src);
    valis (dst, src);
  };

  /* after series of put, set limit to current position and reset position to zero,
  *  thus trimming to the actually put part, ready for reading */
  bytebuffer_flip has type (bytebuffer) => bytebuffer;
  bytebuffer_flip(bb) is valof {
    _bb_flip(bb);
    valis bb;
  };

  /* reset position to zero and limit to capacity, ready for new puts */
  bytebuffer_clear has type (bytebuffer) => bytebuffer;
  bytebuffer_clear(bb) is valof {
    _bb_clear(bb);
    valis bb;
  };

  /* copy remaining bytes to the front of the buffer, set the position to next byte (ready for new puts), and limit to capacity */
  bytebuffer_compact has type (bytebuffer) => bytebuffer;
  bytebuffer_compact(bb) is valof {
    _bb_compact(bb);
    valis bb;
  };

  bytebuffer_position has type (bytebuffer) => integer;
  bytebuffer_position(bb) is _bb_position(bb);

  bytebuffer_set_position has type (bytebuffer, integer) => bytebuffer;
  bytebuffer_set_position(bb, p) is valof {
    _bb_set_position(bb, p);
    valis bb;
  };

  bytebuffer_limit has type (bytebuffer) => integer;
  bytebuffer_limit(bb) is _bb_limit(bb);

  bytebuffer_set_limit has type (bytebuffer, integer) => bytebuffer;
  bytebuffer_set_limit(bb, i) is valof {
    _bb_set_limit(bb, i);
    valis bb;
  };

  bytebuffer_capacity has type (bytebuffer) => integer;
  bytebuffer_capacity(bb) is _bb_capacity(bb);
  
  bytebuffer_to_array has type (bytebuffer) => array of word8;
  bytebuffer_to_array(bb0) is valof {
    bb is _bb_asReadOnlyBuffer(bb0);
    var res := array of {};
    while(bytebuffer_hasRemaining(bb)) do {
      (b, _) is bytebuffer_getWord8(bb);
      res := _apnd(res, b);
    }
    valis res;
  }

  bytebuffer_slice has type (bytebuffer) => bytebuffer;
  bytebuffer_slice(bb0) is _bb_slice(bb0);

  bytebuffer_asReadOnlyBuffer has type (bytebuffer) => bytebuffer;
  bytebuffer_asReadOnlyBuffer(bb) is _bb_asReadOnlyBuffer(bb);

  bytebuffer_readonly_slice has type (bytebuffer, integer, integer) => bytebuffer;
  bytebuffer_readonly_slice(bb0, from_, length) is valof {
    bb1 is _bb_asReadOnlyBuffer(bb0);
    bb2 is bytebuffer_set_position(bb1, from_);
    bb3 is bytebuffer_set_limit(bb2, from_ + length);
    valis bytebuffer_slice(bb3);
  }
}
