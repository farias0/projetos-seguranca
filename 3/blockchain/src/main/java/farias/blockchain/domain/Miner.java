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
  private long startTime;

  private BigInteger hashesCount = BigInteger.ZERO;
  private BigInteger passCount = BigInteger.ZERO;
  private BigInteger failCount = BigInteger.ZERO;

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
    startTime = System.currentTimeMillis();

    while (true) {
      var hash = digest.digest(currentTestValue.toByteArray());
      var intHash = new BigInteger(1, hash);

      int comparison = intHash.compareTo(maxHash);

      if (comparison <= 0) {
        passCount = passCount.add(BigInteger.ONE);
        SpringContext.getBean(BlockchainService.class).validateAndAddBlock(id, currentTestValue.toByteArray());
      } else {
        failCount = failCount.add(BigInteger.ONE);
      }

      // hashesCount++;

      if (msPassed() >= 300000) {
        var total = passCount.add(failCount);
        //var passRatio = new BigDecimal(passCount).divide(new BigDecimal(total));
        log.info("miner {}: passed={}, total={}", id, passCount, total);
        Thread.sleep(2000); // TODO deal gracefully with this being interrupted
        // hashesCount = 0;
        return;
      }

      // Thread.sleep(1000);

      currentTestValue = currentTestValue.add(BigInteger.ONE);

      if (false) { // just so the linter shut up about end conditions
        return;
      }
    }
  }

  private long msPassed() {
    var now = System.currentTimeMillis();
    return now - startTime;
  }
}
