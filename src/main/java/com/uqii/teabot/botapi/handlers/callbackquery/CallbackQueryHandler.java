package com.uqii.teabot.botapi.handlers.callbackquery;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackQueryHandler {
	
	BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery);
	
	CallbackQueryType getHandlerQueryType();
}