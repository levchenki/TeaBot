package com.uqii.teabot.botapi.utils.keyboards;

import com.uqii.teabot.botapi.handlers.callbackquery.CallbackQueryNavigation;
import com.uqii.teabot.botapi.handlers.callbackquery.CallbackQueryType;
import com.uqii.teabot.models.Evaluation;
import com.uqii.teabot.models.Tea;
import com.uqii.teabot.services.TeaService;
import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Component
@AllArgsConstructor
public class TeaKeyboards {
	
	ButtonsBuilder buttonsBuilder;
	
	TeaService teaService;
	
	public InlineKeyboardMarkup getAllTeasKeyboard(long userId, long categoryId, int page, boolean isAdmin) {
		
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		int offset = 7;
		int buttonsInRow = 1;
		CallbackQueryNavigation backCallback = CallbackQueryNavigation._BACK_TO_CATEGORY;
		CallbackQueryType prefix = CallbackQueryType.TEA_;
		
		List<Tea> evaluatedTeas = teaService.getEvaluatedTeas(categoryId, userId, page, offset);
		
		long count = teaService.getCount(categoryId);
		long pageCount = count / offset;
		
		if (count % offset != 0)
			pageCount++;
		
		var keyboard = getTeasButtonsList(evaluatedTeas, buttonsInRow, prefix);
		
		keyboard.add(buttonsBuilder.getPagination(page, pageCount, prefix, categoryId));
		
		if (isAdmin) {
			keyboard.add(Collections.singletonList(buttonsBuilder.getButton("Добавить", prefix.toString() + CallbackQueryType.CATEGORY_ + categoryId + CallbackQueryNavigation._CREATE)));
		}
		
		String backCallbackData = prefix + backCallback.toString();
		keyboard.add(buttonsBuilder.getBackButton(backCallbackData));
		
		inlineKeyboardMarkup.setKeyboard(keyboard);
		return inlineKeyboardMarkup;
	}
	
	
	public InlineKeyboardMarkup getOneTeaKeyboard(long teaId, long categoryId, boolean isEvaluated, boolean isAdmin) {
		
		CallbackQueryNavigation backCallback = CallbackQueryNavigation._BACK_TO_TEAS_LIST;
		CallbackQueryType prefix = CallbackQueryType.TEA_;
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<>(Collections.emptyList());
		
		if (isEvaluated)
			keyboard.add(Collections.singletonList(buttonsBuilder.getButton("Изменить", prefix.toString() + teaId + CallbackQueryNavigation._RATE)));
		else
			keyboard.add(Collections.singletonList(buttonsBuilder.getButton("Оценить", prefix.toString() + teaId + CallbackQueryNavigation._RATE)));
		
		if (isAdmin) {
			InlineKeyboardButton edit = buttonsBuilder.getButton("Редактировать", prefix.toString() + teaId + CallbackQueryNavigation._EDIT);
			InlineKeyboardButton delete = buttonsBuilder.getButton("Удалить", prefix.toString() + teaId + CallbackQueryNavigation._DELETE);
			keyboard.add(Arrays.asList(edit, delete));
		}
		
		keyboard.add(buttonsBuilder.getBackButton(prefix.toString() + CallbackQueryType.CATEGORY_ + categoryId + backCallback));
		
		inlineKeyboardMarkup.setKeyboard(keyboard);
		return inlineKeyboardMarkup;
	}
	
	public InlineKeyboardMarkup getBackToTeaKeyboard(long teaId) {
		
		if (teaService.getOneTea(teaId).isEmpty()) throw new IllegalArgumentException();
		
		CallbackQueryNavigation backCallback = CallbackQueryNavigation._BACK_TO_TEA;
		CallbackQueryType prefix = CallbackQueryType.TEA_;
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<>(Collections.emptyList());
		
		keyboard.add(buttonsBuilder.getBackButton(prefix.toString() + teaId + backCallback));
		inlineKeyboardMarkup.setKeyboard(keyboard);
		return inlineKeyboardMarkup;
	}
	
	public InlineKeyboardMarkup getBackToTeasListKeyboard(long categoryId) {
		
		CallbackQueryNavigation backCallback = CallbackQueryNavigation._BACK_TO_TEAS_LIST;
		CallbackQueryType prefix = CallbackQueryType.TEA_;
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<>(Collections.emptyList());
		
		keyboard.add(buttonsBuilder.getBackButton(prefix.toString() + CallbackQueryType.CATEGORY_ + categoryId + backCallback));
		inlineKeyboardMarkup.setKeyboard(keyboard);
		return inlineKeyboardMarkup;
	}
	
	public InlineKeyboardMarkup getBackToTeasListKeyboard(String text, long categoryId) {
		
		CallbackQueryNavigation backCallback = CallbackQueryNavigation._BACK_TO_TEAS_LIST;
		CallbackQueryType prefix = CallbackQueryType.TEA_;
		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		List<List<InlineKeyboardButton>> keyboard = new ArrayList<>(Collections.emptyList());
		
		keyboard.add(buttonsBuilder.getBackButton(text, prefix.toString() + CallbackQueryType.CATEGORY_ + categoryId + backCallback));
		inlineKeyboardMarkup.setKeyboard(keyboard);
		return inlineKeyboardMarkup;
	}
	
	private List<List<InlineKeyboardButton>> getTeasButtonsList(List<Tea> keyboardMarkupData, int buttonsInRow, CallbackQueryType prefix) {
		
		List<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();
		
		for (int i = 0; i < keyboardMarkupData.size(); i += buttonsInRow) {
			List<InlineKeyboardButton> sublist = new ArrayList<>(buttonsInRow);
			
			int buttonsCounter = i + buttonsInRow;
			for (int j = i; j < buttonsCounter && j < keyboardMarkupData.size(); j++) {
				Tea tea = keyboardMarkupData.get(j);
				String title;
				if (tea.getEvaluations().isEmpty()) {
					title = tea.getName();
				} else {
					Evaluation evaluation = tea.getEvaluations().stream().filter(e -> Objects.equals(e.getTea().getId(), tea.getId())).findFirst().orElseThrow();
					title = EmojiParser.parseToUnicode(tea.getName() + " :star:" + evaluation.getRating());
				}
				String callbackData = prefix + "ID_" + tea.getId();
				sublist.add(buttonsBuilder.getButton(title, callbackData));
			}
			buttonRows.add(sublist);
		}
		return buttonRows;
	}
}
