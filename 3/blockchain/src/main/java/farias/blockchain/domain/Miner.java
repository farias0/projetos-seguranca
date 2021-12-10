package farias.blockchain.domain;

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

  private int hashesCount = 0;
  private int passCount = 0;
  private int failCount = 0;

  private Miner(byte[] maxHashAccepted) throws NoSuchAlgorithmException {
    this.maxHash = new BigInteger(1, maxHashAccepted);
  }

  // created to avoid the passing the constructor's "throws" up
  public static Thread createThread(byte[] maxHashAccepted) {
    try {
      return new Thread(new Miner(maxHashAccepted));
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
        // TODO
      } else {
        failCount++;
      }

      hashesCount++;

      if (hashesCount % 1000 == 0) {
        var passRatio = (double) passCount/(failCount + passCount);
        log.info("REPORT: passed={}, failed={}, ratio={}", passCount, failCount, passRatio);
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
