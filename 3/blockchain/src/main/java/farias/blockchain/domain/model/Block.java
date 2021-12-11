package farias.blockchain.domain.model;

import java.math.BigInteger;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Block {
  private int id;
  private byte[] previousBlockHash;
  private LocalDateTime dateTime; // more legible than timestamp
  private int minerId;

  public static Block getGenesis() {
    return Block.builder()
        .id(0)
        .previousBlockHash(BigInteger.ZERO.toByteArray())
        .dateTime(LocalDateTime.now())
        .minerId(-1)
        .build();
  }
}
