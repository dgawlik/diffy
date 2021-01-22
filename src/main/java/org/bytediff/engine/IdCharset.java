package org.bytediff.engine;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class IdCharset extends Charset {

  public static class IdEncoder extends CharsetEncoder {

    public IdEncoder(Charset cs) {
      super(cs, 1, 1, new byte[]{(byte) '?'});
    }

    @Override
    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
      while (in.hasRemaining()) {
        out.put((byte) in.get());
      }
      return CoderResult.UNDERFLOW;
    }
  }

  public static class IdDecoder extends CharsetDecoder {

    protected IdDecoder(Charset cs) {
      super(cs, 1, 1);
    }

    @Override
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
      while (in.hasRemaining()) {
        out.put((char) in.get());
      }
      return CoderResult.UNDERFLOW;
    }
  }

  public IdCharset() {
    super("id", null);
  }

  @Override
  public boolean contains(Charset cs) {
    return false;
  }

  @Override
  public CharsetDecoder newDecoder() {
    return new IdDecoder(this);
  }

  @Override
  public CharsetEncoder newEncoder() {
    return new IdEncoder(this);
  }
}
