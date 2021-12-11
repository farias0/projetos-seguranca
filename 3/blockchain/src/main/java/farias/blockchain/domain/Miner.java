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
  private BigInteger currentTestValue = BigInteger.ZERO; // initial test value
  private final BigInteger maxHash;
  private final int id; // we're keeping this id in two different places. should fix this

  private int hashesCount = 0;
  private int passCount = 0;
  private int failCount = 0;

  private Miner(int id, byte[] maxHashAccepted) throws NoSuchAlgorithmException {
    this.id = id;
    this.maxHash = new BigInteger(1, maxHashAccepted);
  }

  public int getId() {
    return this.id;
  }

  // created to avoid the passing the constructor's "throws" up
  public static Thread createThread(int id, byte[] maxHashAccepted) {
    try {
      return new Thread(new Miner(id, maxHashAccepted));
    } catch (NoSuchAlgorithmException ex) {
      throw new IllegalStateException("This will never happen ¯\\_(ツ)_/¯");
    }
  }

  @SneakyThrows
  @Override
  public void run() {
    while (true) {
      var hash = digest.digest(currentTestValue.toByteArray());
      var intHash = new BigInteger(1, hash);

      int comparison = intHash.compareTo(maxHash);

      if (comparison <= 0) {
        passCount++;
        SpringContext.getBean(BlockchainService.class).validateAndAddBlock(id, currentTestValue.toByteArray());
      } else {
        failCount++;
      }

      hashesCount++;

      if (hashesCount % 1000 == 0) {
        var passRatio = (double) passCount/(failCount + passCount);
        log.info("miner {}: passed={}, failed={}, ratio={}", id, passCount, failCount, passRatio);
        Thread.sleep(2000); // TODO deal with this exception
        hashesCount = 0;
      }

      // Thread.sleep(1000);

      currentTestValue = currentTestValue.add(BigInteger.ONE);

      if (false) { // just so the linter shut up about end conditions
        return;
      }
    }
  }
}
