package com.uqii.teabot.botapi.callbackquery.impl;

import com.uqii.teabot.botapi.MethodWrapper;
import com.uqii.teabot.botapi.callbackquery.CallbackQueryHandler;
import com.uqii.teabot.botapi.handlers.HandlerFacade;
import com.uqii.teabot.botapi.utils.enums.CallbackQueryAction;
import com.uqii.teabot.botapi.utils.enums.CallbackQueryEditedValue;
import com.uqii.teabot.botapi.utils.enums.CallbackQueryType;
import com.uqii.teabot.botapi.utils.enums.SkippedValue;
import com.uqii.teabot.exceptions.UnknownCallbackQueryException;
import com.uqii.teabot.models.Category;
import com.uqii.teabot.services.RedisService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Log4j2
@Component
@AllArgsConstructor
public class TeaCallbackQueryHandler implements CallbackQueryHandler {

  HandlerFacade handlerFacade;
  RedisService redisService;

  @Override
  public MethodWrapper handleCallbackQuery(CallbackQuery callbackQuery) {
    String rawData = callbackQuery.getData();
    String[] splittedCallback = rawData.split(":");
    Integer messageId = callbackQuery.getMessage().getMessageId();
    Long userId = callbackQuery.getFrom().getId();

    CallbackQueryAction action = CallbackQueryAction.valueOf(splittedCallback[2]);

    EditMessageText editMessageText = new EditMessageText();
    editMessageText.setMessageId(messageId);
    editMessageText.setChatId(userId);

    switch (action) {
      case PAGE -> {
        // TEA:{category}:PAGE:{page}
        Category category = Category.valueOf(splittedCallback[1]);
        int page = Integer.parseInt(splittedCallback[3]);
        return handlerFacade.getCategory(editMessageText, userId, category, page);
      }
      case GET -> {
        // TEA:{id}:GET:{page}
        Long teaId = Long.valueOf(splittedCallback[1]);
        int pageOfCurrentTea = Integer.parseInt(splittedCallback[3]);
        return handlerFacade.getTea(editMessageText, userId, teaId, pageOfCurrentTea);
      }
      case BACK_TO_CATEGORY -> {
        // TEA::BACK_TO_CATEGORY
        return handlerFacade.getCategories(editMessageText, userId);
      }
      case RATE -> {
        // TEA:{id}:RATE:{page}
        Long teaId = Long.parseLong(splittedCallback[1]);
        int pageOfCurrentTea = Integer.parseInt(splittedCallback[3]);
        return handlerFacade.evaluateTea(editMessageText, userId, teaId, pageOfCurrentTea);
      }
      case CREATE -> {
        // TEA:{category}:CREATE
        Category category = Category.valueOf(splittedCallback[1]);
        return handlerFacade.createTea(editMessageText, userId, category);
      }
      case DELETE -> {
        // TEA:{id}:DELETE:{page}
        Long teaId = Long.valueOf(splittedCallback[1]);
        int pageOfCurrentTea = Integer.parseInt(splittedCallback[3]);
        return handlerFacade.deleteTea(editMessageText, userId, teaId, pageOfCurrentTea);
      }
      case EDIT -> {
        Long teaId = Long.valueOf(splittedCallback[1]);
        int pageOfCurrentTea = Integer.parseInt(splittedCallback[3]);

        if (splittedCallback.length == 5) {
          // TEA:{id}:EDIT:{page}:{editedValue}
          CallbackQueryEditedValue editedValue = CallbackQueryEditedValue.valueOf(
              splittedCallback[4]);
          return handlerFacade.editTeaValues(editMessageText, userId, teaId, pageOfCurrentTea,
              editedValue);
        } else {
          // TEA:{id}:EDIT:{page}
          return handlerFacade.editTea(editMessageText, userId, teaId, pageOfCurrentTea);
        }
      }
      case SKIP_VALUE -> {
        //TEA:{id}:SKIP_VALUE:{value}:{page}
        Long teaId = Long.valueOf(splittedCallback[1]);
        SkippedValue skippedValue = SkippedValue.valueOf(splittedCallback[3]);
        int pageOfCurrentTea = Integer.parseInt(splittedCallback[4]);

        if (skippedValue == SkippedValue.RATE_COMMENT) {
          return handlerFacade.skipRateComment(editMessageText, userId, teaId, pageOfCurrentTea);
        }
      }
    }
    throw new UnknownCallbackQueryException();
  }

  @Override
  public CallbackQueryType getHandlerQueryType() {
    return CallbackQueryType.TEA;
  }
}
