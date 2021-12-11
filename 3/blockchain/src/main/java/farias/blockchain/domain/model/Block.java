package farias.blockchain.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Block {
  private int id;
  private String previousBlockHash;
  // timestamp
  private String data;
}
