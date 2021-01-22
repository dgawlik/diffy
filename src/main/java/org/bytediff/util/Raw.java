package org.bytediff.util;

import org.bytediff.engine.IdCharset;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

public class Raw {

  public static char[] bytesToChars(byte[] arr) {
    IdCharset id = new IdCharset();
    try {
      return id.newDecoder().decode(ByteBuffer.wrap(arr)).array();
    } catch (CharacterCodingException e) {
      throw new RuntimeException("Unable to id encode byte[] to char[]", e);
    }
  }
}
