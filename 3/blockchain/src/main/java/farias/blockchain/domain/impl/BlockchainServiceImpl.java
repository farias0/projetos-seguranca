package farias.blockchain.domain.impl;

import farias.blockchain.domain.BlockchainService;
import farias.blockchain.domain.model.Block;
import farias.blockchain.domain.model.BlockchainInfo;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class BlockchainServiceImpl implements BlockchainService {

  private List<Block> chain;
  private double difficulty;

  @Override
  public BlockchainInfo info() {
    if (!isOn()) {
      return BlockchainInfo.builder().isOn(false).build();
    }

    return BlockchainInfo.builder()
        .isOn(isOn())
        .difficulty(difficulty)
        .isValid(isValid(-1))
        .blocks(chain)
        .build();
  }

  private boolean isOn() {
    return chain != null;
  }

  @Override
  public void start(double difficulty) {
    chain = new ArrayList<>();
    this.difficulty = difficulty;

    log.info("starting, difficulty={}", difficulty);
  }

  @Override
  public void stop() {
    // TODO stop all miners

    chain = null;

    log.info("blockchain cleared");
  }

  private boolean isValid(int blocksBackwards) {
    if (!isOn()) {
      throw new IllegalStateException(); // TODO create a BlockchainOffException
    }

    int upUntil;
    if (blocksBackwards > 0) {
      upUntil = chain.size() - 1 - blocksBackwards;
    } else if (blocksBackwards == -1) {
      upUntil = 0;
    } else {
      throw new IllegalArgumentException();
    }

    for (var currentIndex = chain.size() - 1; currentIndex >= upUntil; currentIndex-- ) {

    }

    return true;
  }

  @Override
  public void validateAndAddBlock() {

  }
}
