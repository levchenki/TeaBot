package com.uqii.teabot.botapi.callbackquery;

import com.uqii.teabot.botapi.MethodWrapper;
import com.uqii.teabot.botapi.callbackquery.enums.CallbackQueryType;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackQueryHandler {

  MethodWrapper handleCallbackQuery(CallbackQuery callbackQuery);

  CallbackQueryType getHandlerQueryType();
}
