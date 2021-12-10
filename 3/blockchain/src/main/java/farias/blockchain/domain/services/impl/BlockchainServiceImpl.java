package farias.blockchain.domain.services.impl;

import farias.blockchain.domain.services.BlockchainService;
import farias.blockchain.domain.model.Block;
import farias.blockchain.domain.model.BlockchainInfo;
import farias.blockchain.domain.services.MinerService;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class BlockchainServiceImpl implements BlockchainService {

  private List<Block> chain;
  private int difficulty;
  private byte[] maxHash;

  private final MinerService minerService;

  public  BlockchainServiceImpl(MinerService minerService) {
    this.minerService = minerService;
  }

  @Override
  public BlockchainInfo info() {
    if (!isOn()) {
      return BlockchainInfo.builder().isOn(false).build();
    }

    return BlockchainInfo.builder()
        .isOn(isOn())
        .difficulty(difficulty)
        .chance(calculateChance(difficulty))
        .isValid(isValid(-1))
        .blocks(chain)
        .build();
  }

  private boolean isOn() {
    return chain != null;
  }

  @Override
  public void start(int difficulty) {
    chain = new ArrayList<>();
    this.difficulty = difficulty;
    this.maxHash = maxHashCalculator(this.difficulty);

    log.info("starting, difficulty={}, chance={}", difficulty, calculateChance(difficulty));
  }

  @Override
  public void stop() {
    for (var id : minerService.minersInfo().getMinersIds()){
      minerService.stopMiner(id);
    }

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

  @Override
  public byte[] getMaxHash() {
    return this.maxHash;
  }

  /**
   * Returns the biggest possible hash of a valid solution, given a difficulty.
   * The difficulty is the number of 0's at the start of the max hash.
   * @param difficulty from 0 to 255
   * @return the biggest possible hash
   */
    private static byte[] maxHashCalculator(int difficulty) {
      var byteArray = new byte[32];

      /*
        The difficulty says how many non-significant zeroes the biggest possible hash
        for a valid solution can have.
        Ex:
        difficulty=1: 0111111... (50% of hashes are smaller or equal to this)
        difficulty=3: 0001111... (12.5% of hashes)

        We need our hash as a byte[]. A SHA256 hash has 256 bits, therefore 32 bytes.
        We'll make these bytes bellow, one by one.
       */

      for (var i = 0; i<32; i++) {
        var byteDifficulty = difficulty - (i * 8); // difficulty, minus the bits to the left

        if (byteDifficulty >= 8) { // it means we still haven't got to the first significant byte
          byteArray[i] = (byte) 0b00000000;
        } else if (byteDifficulty >= 0) {
          int byteEasiness = 8 - byteDifficulty; // 0 to 7
          byteArray[i] = (byte) (Math.pow(2, byteEasiness) - 1); // 0 to 255, the bigger the difficulty, the smaller the byte
        } else { // it means from now on every byte is just 1's
          byteArray[i] = (byte) 0b11111111;
        }
      }

      return byteArray;
    }

    private static double calculateChance(int difficulty) {
      return  1/(Math.pow(2, difficulty));
    }
}
