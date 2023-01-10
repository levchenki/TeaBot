package com.uqii.teabot.botapi.handlers.callbackquery;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;
import java.util.Optional;

@Log4j
@Component
@AllArgsConstructor
public class CallbackQueryFacade {
	
	private final List<CallbackQueryHandler> handlers;
	
	public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {
		
		String data = callbackQuery.getData();
		log.debug("callback data: " + data);
		
		CallbackQueryType callbackQueryType = CallbackQueryType.valueOf(data.split("_")[0] + "_");
		
		Optional<CallbackQueryHandler> handler = handlers.stream().filter(cbQuery -> cbQuery.getHandlerQueryType().equals(callbackQueryType)).findFirst();
		
		return handler.map(queryHandler -> queryHandler.handleCallbackQuery(callbackQuery)).orElseThrow();
	}
}
