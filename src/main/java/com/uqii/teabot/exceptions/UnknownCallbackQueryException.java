package com.uqii.teabot.exceptions;

public class UnknownCallbackQueryException extends RuntimeException {

  public UnknownCallbackQueryException() {
    super("Unknown callback query type");
  }
}
