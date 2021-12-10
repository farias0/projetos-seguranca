package farias.blockchain.controller;

import farias.blockchain.controller.dto.Start;
import farias.blockchain.domain.BlockchainService;
import farias.blockchain.domain.MinerService;
import farias.blockchain.domain.model.BlockchainInfo;
import farias.blockchain.domain.model.MinersInfo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@AllArgsConstructor
@RequestMapping(
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class Controller {

  private final BlockchainService blockchainService;
  private final MinerService minerService;

  @PostMapping("/start")
  public ResponseEntity<Void> start(@RequestBody Start data) {
    log.info("start");

    blockchainService.start(data.getDifficulty());

    return ResponseEntity.ok(null);
  }

  @PostMapping("/stop")
  public ResponseEntity<Void> stop() {
    log.info("stop");

    blockchainService.stop();

    return ResponseEntity.ok(null);
  }

  @GetMapping("/info")
  public ResponseEntity<BlockchainInfo> info() {
    log.info("info");

    return ResponseEntity.ok(blockchainService.info());
  }

  @PutMapping("/blocks/{id}")
  public ResponseEntity<Void> putBlock(@PathVariable int id) {
    log.info("putBlock id={}", id);

    return ResponseEntity.ok(null);
  }

  @PostMapping("/miner")
  public ResponseEntity<Void> runMiner() throws InterruptedException {
    log.info("runMiner");

    minerService.startMiner();

    return ResponseEntity.ok(null);
  }

  @GetMapping("/miner")
  public ResponseEntity<MinersInfo> minersInfo() {
    log.info("minersInfo");

    return ResponseEntity.ok(minerService.minersInfo());
  }

  @DeleteMapping("/miner/{id}")
  public ResponseEntity<Void> deleteMiner(@PathVariable int id) {
    log.info("deleteMiner id={}", id);

    minerService.stopMiner(id);

    return ResponseEntity.ok(null);
  }
}
