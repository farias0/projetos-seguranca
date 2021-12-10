package farias.blockchain.domain.services.impl;

import farias.blockchain.domain.Miner;
import farias.blockchain.domain.services.BlockchainService;
import farias.blockchain.domain.services.MinerService;
import farias.blockchain.domain.model.MinersInfo;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class MinerServiceImpl implements MinerService {

  private final HashMap<Integer, Thread> minerMap = new HashMap<>();
  private int lastId = -1;

  private final BlockchainService blockchainService;

  public MinerServiceImpl(@Lazy BlockchainService blockchainService) {
    this.blockchainService = blockchainService;
  }

  @Override
  public void startMiner() throws InterruptedException {
    var id = ++lastId;
    var thread = Miner.createThread(blockchainService.getMaxHash());
    thread.start();

    minerMap.put(id, thread);

    log.info("started miner id={}", id);
  }

  @Override
  public void stopMiner(int id) {
    for (var thread : minerMap.entrySet()) {
      if (thread.getKey() == id) {
        thread.getValue().interrupt();
        minerMap.remove(thread.getKey());
        log.info("stopped miner id={}", id);
        return;
      }
    }
    throw new IllegalArgumentException("Miner not found");
  }

  @Override
  public MinersInfo minersInfo() {
    return MinersInfo.builder()
        .numberOfMinersRunning(minerMap.size())
        .minersIds(new ArrayList<>(minerMap.keySet()))
        .build();
  }
}
