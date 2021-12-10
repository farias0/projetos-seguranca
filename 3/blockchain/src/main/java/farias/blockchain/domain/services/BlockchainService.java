package farias.blockchain.domain.services;

import farias.blockchain.domain.model.BlockchainInfo;

public interface BlockchainService {
  void start(double difficulty);
  void stop();
  BlockchainInfo info();
  void validateAndAddBlock();
}
