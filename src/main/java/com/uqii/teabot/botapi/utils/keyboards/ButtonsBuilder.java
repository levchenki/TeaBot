package com.uqii.teabot.botapi.utils.keyboards;

import com.uqii.teabot.botapi.handlers.callbackquery.CallbackQueryType;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Component
public class ButtonsBuilder {
	
	public List<InlineKeyboardButton> getPagination(int page, long pageCount, CallbackQueryType prefix, long parentId) {
		List<InlineKeyboardButton> navigateRows = new ArrayList<>();
		
		if (pageCount <= 1) {
			return navigateRows;
		}
		
		String paginationCallbackData = prefix.toString() + parentId + "_PAGE_";
		
		if (page == 0)
			navigateRows.add(getButton("->", paginationCallbackData + (page + 1)));
		else if (page == pageCount - 1)
			navigateRows.add(getButton("<-", paginationCallbackData + (page - 1)));
		else {
			navigateRows.add(getButton("<-", paginationCallbackData + (page - 1)));
			navigateRows.add(getButton("->", paginationCallbackData + (page + 1)));
		}
		return navigateRows;
	}
	
	public List<InlineKeyboardButton> getBackButton(String backCallbackData) {
		return Collections.singletonList(getButton("Назад", backCallbackData));
	}
	
	public List<InlineKeyboardButton> getBackButton(String text, String backCallbackData) {
		return Collections.singletonList(getButton(text, backCallbackData));
	}
	
	public InlineKeyboardButton getButton(String buttonText, String buttonCallbackData) {
		InlineKeyboardButton button = new InlineKeyboardButton();
		button.setText(buttonText);
		button.setCallbackData(buttonCallbackData);
		return button;
	}
}
