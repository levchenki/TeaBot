package com.uqii.teabot.botapi.callbackquery.impl;

import com.uqii.teabot.botapi.MethodWrapper;
import com.uqii.teabot.botapi.callbackquery.CallbackQueryHandler;
import com.uqii.teabot.botapi.callbackquery.enums.CallbackQueryAction;
import com.uqii.teabot.botapi.callbackquery.enums.CallbackQueryType;
import com.uqii.teabot.botapi.handlers.HandlerFacade;
import com.uqii.teabot.exceptions.UnknownCallbackQueryException;
import com.uqii.teabot.models.Category;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Log4j2
@Component
@AllArgsConstructor
public class CategoryCallbackQueryHandler implements CallbackQueryHandler {

  HandlerFacade handlerFacade;

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
      case GET -> {
        Category category = Category.valueOf(splittedCallback[1]);

        if (category == Category.OOLONG) {
          return handlerFacade.getSubcategories(editMessageText, userId);
        } else {
          int page = splittedCallback.length == 4 ? Integer.parseInt(splittedCallback[3]) : 0;
          return handlerFacade.getCategory(editMessageText, userId, category, page);
        }
      }
      case BACK_TO_CATEGORY -> {
        return handlerFacade.getCategories(editMessageText, userId);
      }
    }
    throw new UnknownCallbackQueryException();
  }

  @Override
  public CallbackQueryType getHandlerQueryType() {
    return CallbackQueryType.CATEGORY;
  }
}
