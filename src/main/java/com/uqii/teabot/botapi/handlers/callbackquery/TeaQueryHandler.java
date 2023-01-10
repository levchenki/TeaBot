package com.uqii.teabot.botapi.handlers.callbackquery;

import com.uqii.teabot.botapi.handlers.HandlerService;
import com.uqii.teabot.services.RedisService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Log4j
@Component
@AllArgsConstructor
public class TeaQueryHandler implements CallbackQueryHandler {
	
	HandlerService handlerService;
	RedisService redisService;
	
	@Override
	public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
		
		String data = callbackQuery.getData().split("^" + CallbackQueryType.TEA_)[1];
		
		int messageId = callbackQuery.getMessage().getMessageId();
		Long userId = callbackQuery.getFrom().getId();
		
		// todo очищение кеша
		redisService.removeByPattern("*" + userId + "*");
		
		EditMessageText editMessageText = new EditMessageText();
		editMessageText.setMessageId(messageId);
		editMessageText.setChatId(userId);
		
		if (data.contains("PAGE")) {
			String[] page_s = data.split("_PAGE_");
			long categoryId = Long.parseLong(page_s[0]);
			int page = Integer.parseInt(page_s[1]);
			
			return handlerService.getTeasList(categoryId, editMessageText, page);
		} else if (data.contains("ID")) {
			String[] id_s = data.split("ID_");
			long id = Long.parseLong(id_s[1]);
			
			return handlerService.getTea(editMessageText, id);
		} else if (data.contains(CallbackQueryNavigation._BACK_TO_TEAS_LIST.toString())) {
			long categoryId = Long.parseLong(data.split("_")[1]);
			
			// todo сохранять страницу в кеш
			return handlerService.getTeasList(categoryId, editMessageText, 0);
		} else if (data.contains(CallbackQueryNavigation._BACK_TO_CATEGORY.toString())) {
			
			return handlerService.getCategories(editMessageText);
		} else if (data.contains(CallbackQueryNavigation._BACK_TO_TEA.toString())) {
			long teaId = Long.parseLong(data.split("_")[0]);
			
			return handlerService.getTea(editMessageText, teaId);
		} else if (data.contains(CallbackQueryNavigation._RATE.toString())) {
			
			return handlerService.getRatingKeyboard(editMessageText);
		} else if (data.contains(CallbackQueryNavigation._EDIT.toString())) {
			long teaId = Long.parseLong(data.split("_")[0]);
			
			return handlerService.getEditTea(editMessageText, teaId);
		} else if (data.contains(CallbackQueryNavigation._DELETE.toString())) {
			long teaId = Long.parseLong(data.split("_")[0]);
			
			return handlerService.getDeleteTea(editMessageText, teaId);
		} else if (data.contains(CallbackQueryNavigation._CREATE.toString())) {
			long categoryId = Long.parseLong(data.split("_")[1]);
			
			return handlerService.getCreateTea(editMessageText, categoryId);
		}
		return null;
	}
	
	@Override
	public CallbackQueryType getHandlerQueryType() {
		return CallbackQueryType.TEA_;
	}
}