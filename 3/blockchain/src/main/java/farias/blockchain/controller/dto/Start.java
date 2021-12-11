package farias.blockchain.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Start {
  @ApiModelProperty("A dificuldade da mineração, de 0 a 255")
  public int difficulty;
}
