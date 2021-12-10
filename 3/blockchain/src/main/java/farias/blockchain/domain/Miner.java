package farias.blockchain.domain;

import farias.blockchain.domain.services.BlockchainService;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Miner implements Runnable {

  private final MessageDigest digest = MessageDigest.getInstance("SHA-256"); // NOT THREAD SAFE! use one per miner
  private BigInteger currentTestValue = BigInteger.ZERO;
  private BigInteger maxHashAccepted;

  private int count = 0;

  private Miner(BigInteger maxHashAccepted) throws NoSuchAlgorithmException {
    this.maxHashAccepted = maxHashAccepted;
  }

  public static Thread createThread() {
    var difficulty = getBlockchainService().info().getDifficulty();
    log.info("extracted difficulty=" + difficulty); // TODO
    try {
      return new Thread(new Miner(BigInteger.ONE));
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException("This will never happen ¯\\_(ツ)_/¯");
    }
  }

  @SneakyThrows
  @Override
  public void run() {
    while (true) {
      var hash = digest.digest(currentTestValue.toByteArray());

      log.info(count + " " + new BigInteger(hash));

      currentTestValue = currentTestValue.add(BigInteger.ONE);
      count++;

      Thread.sleep(100000);
      log.info(count + " " + new BigInteger(hash));
      return;
    }
  }

  private static BlockchainService getBlockchainService() {
    return SpringContext.getBean(BlockchainService.class);
  }
}
