package org.bytediff.engine;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import javax.annotation.Nonnull;


/**
 * Utility charset that id converts byte array to char arrray
 */
public class IdCharset extends Charset {

  public static class IdEncoder extends CharsetEncoder {

    public IdEncoder(Charset charset) {
      super(charset, 1, 1, new byte[]{(byte) '?'});
    }

    @Override
    protected CoderResult encodeLoop(@Nonnull final CharBuffer inputBuffer,
        @Nonnull final ByteBuffer outputBuffer) {
      while (inputBuffer.hasRemaining()) {
        outputBuffer.put((byte) inputBuffer.get());
      }
      return CoderResult.UNDERFLOW;
    }
  }

  public static class IdDecoder extends CharsetDecoder {

    protected IdDecoder(Charset charset) {
      super(charset, 1, 1);
    }

    @Override
    protected CoderResult decodeLoop(@Nonnull final ByteBuffer inputBuffer,
        @Nonnull final CharBuffer outputBuffer) {
      while (inputBuffer.hasRemaining()) {
        outputBuffer.put((char) inputBuffer.get());
      }
      return CoderResult.UNDERFLOW;
    }
  }

  public IdCharset() {
    super("id", null);
  }

  @Override
  public boolean contains(Charset charset) {
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
