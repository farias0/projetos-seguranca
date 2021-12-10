package farias.blockchain.domain.services;

import farias.blockchain.domain.model.MinersInfo;

public interface MinerService {
  void startMiner() throws InterruptedException;
  void stopMiner(int id);
  MinersInfo minersInfo();
}
