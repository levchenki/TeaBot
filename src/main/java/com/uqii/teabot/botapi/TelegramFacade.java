package com.uqii.teabot.botapi;

import com.uqii.teabot.botapi.handlers.MessageHandler;
import com.uqii.teabot.botapi.handlers.callbackquery.CallbackQueryFacade;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@Component
public class TelegramFacade {
	
	private final MessageHandler messageHandler;
	private final CallbackQueryFacade callbackQueryFacade;
	
	public TelegramFacade(MessageHandler messageHandler, CallbackQueryFacade callbackQueryFacade) {
		this.messageHandler = messageHandler;
		this.callbackQueryFacade = callbackQueryFacade;
	}
	
	public BotApiMethod<?> handleUpdate(Update update) {
		
		if (update.hasCallbackQuery()) {
			CallbackQuery callbackQuery = update.getCallbackQuery();
			
			try {
				return callbackQueryFacade.processCallbackQuery(callbackQuery);
			} catch (Exception e) {
				log.error(e);
			}
			
		} else {
			Message message = update.getMessage();
			if (message != null && message.hasText()) {
				long userId = message.getFrom().getId();
				
				return handleInputMessage(message, userId);
			}
		}
		
		return null;
	}
	
	private BotApiMethod<?> handleInputMessage(Message message, long userId) {
		
		return messageHandler.handle(message, userId);
	}
}
