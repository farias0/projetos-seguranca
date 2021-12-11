package farias.blockchain.controller;

import farias.blockchain.controller.dto.General;
import farias.blockchain.controller.dto.Start;
import farias.blockchain.domain.services.BlockchainService;
import farias.blockchain.domain.services.MinerService;
import farias.blockchain.domain.model.BlockchainInfo;
import farias.blockchain.domain.model.MinersInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
  @ApiOperation("Iniciar a blockchain")
  public ResponseEntity<General> start(@RequestBody Start data) {
    log.info("start");

    blockchainService.start(data.getDifficulty());

    return ResponseEntity.ok(General.builder().message("Blockchain started").build());
  }

  @PostMapping("/stop")
  @ApiOperation("Parar os mineradores e limpar a blockchain")
  public ResponseEntity<General> stop() {
    log.info("stop");

    blockchainService.stop();

    return ResponseEntity.ok(General.builder().message("Blockchain stopped").build());
  }

  @GetMapping("/info")
  @ApiOperation("Informação sobre o estado atual da blockchain")
  public ResponseEntity<BlockchainInfo> info() {
    log.info("info");

    return ResponseEntity.ok(blockchainService.info());
  }

  @PutMapping("/blocks/{id}")
  @ApiOperation("Alterar os dados de um bloco através do seu ID")
  public ResponseEntity<Void> putBlock(@PathVariable int id) {
    log.info("putBlock id={}", id);

    return ResponseEntity.ok(null);
  }

  @PostMapping("/miner")
  @ApiOperation("Iniciar um minerador")
  public ResponseEntity<General> runMiner() throws InterruptedException {
    log.info("runMiner");

    minerService.startMiner();

    return ResponseEntity.ok(General.builder().message("Miner started").build());
  }

  @GetMapping("/miner")
  @ApiOperation("Informações sobre os mineradores sendo executados")
  public ResponseEntity<MinersInfo> minersInfo() {
    log.info("minersInfo");

    return ResponseEntity.ok(minerService.minersInfo());
  }

  @DeleteMapping("/miner/{id}")
  @ApiOperation("Interromper um minerador")
  public ResponseEntity<General> deleteMiner(@PathVariable int id) {
    log.info("deleteMiner id={}", id);

    minerService.stopMiner(id);

    return ResponseEntity.ok(General.builder().message("Miner deleted").build());
  }
}
