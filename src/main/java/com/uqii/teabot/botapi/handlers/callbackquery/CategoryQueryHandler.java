package com.uqii.teabot.botapi.handlers.callbackquery;

import com.uqii.teabot.botapi.handlers.HandlerService;
import com.uqii.teabot.models.Category;
import com.uqii.teabot.services.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@AllArgsConstructor
public class CategoryQueryHandler implements CallbackQueryHandler {
	
	CategoryService categoryService;
	HandlerService handlerService;
	
	@Override
	public BotApiMethod<?> handleCallbackQuery(CallbackQuery callbackQuery) {
		
		String data = callbackQuery.getData().split("^" + CallbackQueryType.CATEGORY_)[1];
		
		int messageId = callbackQuery.getMessage().getMessageId();
		Long userId = callbackQuery.getFrom().getId();
		EditMessageText editMessageText = new EditMessageText();
		
		editMessageText.setMessageId(messageId);
		editMessageText.setChatId(userId);
		
		if (data.contains("ID")) {
			long id = Long.parseLong(data.split("ID_")[1]);
			
			Category category = categoryService.getById(id).orElseThrow();
			
			if (category.getName().equals("Улуны")) {
				return handlerService.getSubcategories(editMessageText);
			} else {
				int page = 0;
				return handlerService.getTeasList(id, editMessageText, page);
			}
		} else if (data.contains(CallbackQueryNavigation._BACK_TO_CATEGORY.toString())) {
			return handlerService.getCategories(editMessageText);
		}
		// todo возвращать не null
		return null;
	}
	
	@Override
	public CallbackQueryType getHandlerQueryType() {
		return CallbackQueryType.CATEGORY_;
	}
}

