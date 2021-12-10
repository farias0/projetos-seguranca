package farias.blockchain.domain.impl;

import farias.blockchain.domain.Miner;
import farias.blockchain.domain.MinerService;
import farias.blockchain.domain.model.MinersInfo;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class MinerServiceImpl implements MinerService {

  private final HashMap<Integer, Thread> minerMap = new HashMap<>();
  private int lastId = -1;

  @Override
  public void startMiner() throws InterruptedException {
    var id = ++lastId;
    var thread = Miner.createThread();
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
