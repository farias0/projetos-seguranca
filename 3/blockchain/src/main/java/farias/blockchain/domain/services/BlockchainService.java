package farias.blockchain.domain.services;

import farias.blockchain.domain.model.Block;
import farias.blockchain.domain.model.BlockchainInfo;

public interface BlockchainService {
  void start(int difficulty);
  void stop();
  BlockchainInfo info();
  void validateAndAddBlock(int minerId, byte[] solution);
  byte[] getMaxHash();
}
