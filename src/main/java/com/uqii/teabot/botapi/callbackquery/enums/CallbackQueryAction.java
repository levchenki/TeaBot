package com.uqii.teabot.botapi.callbackquery.enums;

public enum CallbackQueryAction {
  PAGE(":PAGE:"),
  BACK_TO_TEA(":BACK_TO_TEA"),
  BACK_TO_CATEGORY(":BACK_TO_CATEGORY"),
  GET(":GET"),
  RATE(":RATE"),
  EDIT(":EDIT"),
  DELETE(":DELETE"),
  CREATE(":CREATE");

  private final String callback;

  CallbackQueryAction(String callback) {
    this.callback = callback;
  }

  @Override
  public String toString() {
    return this.callback;
  }
}
