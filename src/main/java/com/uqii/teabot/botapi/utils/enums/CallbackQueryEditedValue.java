package com.uqii.teabot.botapi.utils.enums;

public enum CallbackQueryEditedValue {
  EDIT_NAME(":EDIT_NAME"),
  EDIT_PRICE(":EDIT_PRICE"),
  EDIT_DESCRIPTION(":EDIT_DESCRIPTION");

  private final String callback;

  CallbackQueryEditedValue(String callback) {
    this.callback = callback;
  }

  @Override
  public String toString() {
    return this.callback;
  }
}
