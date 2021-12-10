package farias.blockchain.domain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MinersInfo {
  private int numberOfMinersRunning;
  private List<Integer> minersIds;
}
