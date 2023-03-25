package com.uqii.teabot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class TelegramBotConfig {

  @Value("${bot.username}")
  private String username;

  @Value("${bot.token}")
  private String token;
}

