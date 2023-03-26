package com.uqii.teabot.botapi.utils.enums;

public enum CallbackQueryType {
  CATEGORY("CATEGORY:"),
  TEA("TEA:");

  private final String callback;

  CallbackQueryType(String callback) {
    this.callback = callback;
  }

  @Override
  public String toString() {
    return this.callback;
  }
}
