package com.uqii.teabot.botapi;

import java.util.List;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

@Getter
public class MethodWrapper {

  private final List<BotApiMethod<?>> methods;

  public MethodWrapper(BotApiMethod<?>... methods) {
    this.methods = List.of(methods);
  }
}
