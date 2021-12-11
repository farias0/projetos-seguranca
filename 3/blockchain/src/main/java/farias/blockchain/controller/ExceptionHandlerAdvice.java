package farias.blockchain.controller;

import farias.blockchain.controller.dto.Error;
import java.util.NoSuchElementException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ExceptionHandlerAdvice {

  // TODO should use my own exceptions

  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoSuchElementException.class)
  public Error handleNoSuchElementException(NoSuchElementException ex) {
    log.info("NOT FOUND: {}", ex.getMessage());
    return new Error(ex.getMessage());
  }
}
