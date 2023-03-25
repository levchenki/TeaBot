package com.uqii.teabot.botapi.callbackquery.enums;

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
