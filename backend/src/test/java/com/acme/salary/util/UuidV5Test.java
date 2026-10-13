package com.acme.salary.util;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
class UuidV5Test {
 @Test void matchesRfc4122DnsNamespaceVector(){
  UUID dns=UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
  assertEquals(UUID.fromString("2ed6657d-e927-568b-95e1-2665a8aea6a2"),UuidV5.fromName(dns,"www.example.com"));
 }
}
