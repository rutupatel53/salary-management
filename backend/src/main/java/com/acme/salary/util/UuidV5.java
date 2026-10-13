package com.acme.salary.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public final class UuidV5 {
 private UuidV5() { }
 public static UUID fromName(UUID namespace,String name){
  try {
   MessageDigest sha1=MessageDigest.getInstance("SHA-1");
   ByteBuffer bytes=ByteBuffer.allocate(16+name.getBytes(StandardCharsets.UTF_8).length);
   bytes.putLong(namespace.getMostSignificantBits()).putLong(namespace.getLeastSignificantBits()).put(name.getBytes(StandardCharsets.UTF_8));
   byte[] hash=sha1.digest(bytes.array());hash[6]=(byte)((hash[6]&0x0f)|0x50);hash[8]=(byte)((hash[8]&0x3f)|0x80);
   ByteBuffer uuid=ByteBuffer.wrap(hash);return new UUID(uuid.getLong(),uuid.getLong());
  }catch(NoSuchAlgorithmException e){throw new IllegalStateException("SHA-1 is required by the UUID v5 standard.",e);}
 }
}
