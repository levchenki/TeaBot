package com.uqii.teabot.botapi;

import com.uqii.teabot.botapi.callbackquery.CallbackQueryFacade;
import com.uqii.teabot.botapi.handlers.MessageHandler;
import com.uqii.teabot.config.TelegramBotConfig;
import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Log4j2
@Component
@Getter
public class TelegramBot extends TelegramLongPollingBot {

  private final String botUsername;
  private final String botToken;
  private final CallbackQueryFacade callbackQueryFacade;
  private final MessageHandler messageHandler;

  public TelegramBot(TelegramBotConfig telegramBotConfig, CallbackQueryFacade callbackQueryFacade,
      MessageHandler messageHandler) {
    this.botUsername = telegramBotConfig.getUsername();
    this.botToken = telegramBotConfig.getToken();
    this.callbackQueryFacade = callbackQueryFacade;
    this.messageHandler = messageHandler;
  }

  @Override
  public void onUpdateReceived(Update update) {
    if (update.hasCallbackQuery()) {
      CallbackQuery callbackQuery = update.getCallbackQuery();
      MethodWrapper methodWrapper = callbackQueryFacade.handleCallbackQuery(callbackQuery);
      executeMethods(methodWrapper);
    } else {
      Message message = update.getMessage();
      if (message != null && message.hasText()) {
        MethodWrapper methodWrapper = messageHandler.handleMessage(message);
        executeMethods(methodWrapper);
      }
    }
  }

  private void executeMethods(MethodWrapper methodWrapper) {
    List<BotApiMethod<?>> methods = methodWrapper.getMethods();
    try {
      for (BotApiMethod<?> method : methods) {
        execute(method);
      }
    } catch (TelegramApiException e) {
      log.error(e);
    }
  }
}
