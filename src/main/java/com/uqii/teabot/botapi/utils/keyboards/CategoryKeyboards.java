package com.uqii.teabot.botapi.utils.keyboards;

import com.uqii.teabot.botapi.handlers.callbackquery.CallbackQueryNavigation;
import com.uqii.teabot.botapi.handlers.callbackquery.CallbackQueryType;
import com.uqii.teabot.models.Category;
import com.uqii.teabot.services.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CategoryKeyboards {
	
	ButtonsBuilder buttonsBuilder;
	
	CategoryService categoryService;
	
	public InlineKeyboardMarkup getAllCategoriesKeyboard() {
		int buttonsInRow = 2;
		CallbackQueryType prefix = CallbackQueryType.CATEGORY_;
		List<Category> categories = categoryService.getCategories();
		
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		
		List<List<InlineKeyboardButton>> keyboard = getCategoriesButtonsList(categories, buttonsInRow, prefix);
		
		inlineKeyboardMarkup.setKeyboard(keyboard);
		return inlineKeyboardMarkup;
	}
	
	public InlineKeyboardMarkup getAllSubcategoriesKeyboard() {
		int buttonsInRow = 1;
		CallbackQueryType prefix = CallbackQueryType.CATEGORY_;
		List<Category> subcategories = categoryService.getSubcategories();
		
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> keyboard = getCategoriesButtonsList(subcategories, buttonsInRow, prefix);
		
		String backCallbackData = prefix + CallbackQueryNavigation._BACK_TO_CATEGORY.toString();
		keyboard.add(buttonsBuilder.getBackButton(backCallbackData));
		
		inlineKeyboardMarkup.setKeyboard(keyboard);
		return inlineKeyboardMarkup;
	}
	
	private List<List<InlineKeyboardButton>> getCategoriesButtonsList(List<Category> keyboardMarkupData, int buttonsInRow, CallbackQueryType prefix) {
		List<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();
		
		for (int i = 0; i < keyboardMarkupData.size(); i += buttonsInRow) {
			List<InlineKeyboardButton> sublist = new ArrayList<>(buttonsInRow);
			
			int buttonsCounter = i + buttonsInRow;
			for (int j = i; j < buttonsCounter && j < keyboardMarkupData.size(); j++) {
				String text = keyboardMarkupData.get(j).getName();
				String callbackData = prefix + "ID_" + keyboardMarkupData.get(j).getId();
				sublist.add(buttonsBuilder.getButton(text, callbackData));
			}
			buttonRows.add(sublist);
		}
		return buttonRows;
	}
}
