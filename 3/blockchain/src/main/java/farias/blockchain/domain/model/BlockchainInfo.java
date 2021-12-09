package farias.blockchain.domain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BlockchainInfo {
  private boolean isOn;
  private double difficulty;
  private Boolean isValid;
  private List<Block> blocks;
}
