package com.uqii.teabot.botapi.utils.enums;

public enum CallbackQueryAction {
  PAGE(":PAGE:"),
  BACK_TO_TEA(":BACK_TO_TEA"),
  BACK_TO_CATEGORY(":BACK_TO_CATEGORY"),
  GET(":GET"),
  RATE(":RATE"),
  EDIT(":EDIT"),
  DELETE(":DELETE"),
  CREATE(":CREATE"),
  SKIP_VALUE(":SKIP_VALUE:");

  private final String callback;

  CallbackQueryAction(String callback) {
    this.callback = callback;
  }

  @Override
  public String toString() {
    return this.callback;
  }
}
