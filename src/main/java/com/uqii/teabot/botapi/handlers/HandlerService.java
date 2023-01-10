package com.uqii.teabot.botapi.handlers;

import com.uqii.teabot.botapi.utils.keyboards.CategoryKeyboards;
import com.uqii.teabot.botapi.utils.keyboards.TeaKeyboards;
import com.uqii.teabot.models.*;
import com.uqii.teabot.services.*;
import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class HandlerService {
	
	TeaKeyboards teaKeyboards;
	CategoryKeyboards categoryKeyboards;
	CategoryService categoryService;
	EvaluationService evaluationService;
	UserService userService;
	TeaService teaService;
	RedisService redisService;
	
	public BotApiMethod<?> getTeasList(long categoryId, EditMessageText editMessageText, int page) {
		long userId = getUserIdByMessage(editMessageText);
		boolean isAdmin = userService.isAdminUser(userId);
		userService.setStateUser(userId, BotState.GET_TEAS_LIST);
		
		Category category = categoryService.getById(categoryId).orElseThrow();
		editMessageText.setText(category.getName() + ". Страница " + (page + 1));
		
		editMessageText.setReplyMarkup(teaKeyboards.getAllTeasKeyboard(userId, categoryId, page, isAdmin));
		return editMessageText;
	}
	
	public BotApiMethod<?> getTea(EditMessageText editMessageText, long teaId) {
		long userId = getUserIdByMessage(editMessageText);
		boolean isAdmin = userService.isAdminUser(userId);
		
		userService.setTeaUser(userId, teaId);
		
		long categoryId = teaService.getOneTea(teaId).orElseThrow().getCategory().getId();
		
		Tea tea = teaService.getOneTea(teaId).orElseThrow();
		double averageRating = evaluationService.getAverageRating(teaId);
		
		List<String> stringList = new ArrayList<>();
		stringList.add(tea.getName());
		
		if (tea.getPrice() != null) stringList.add("Стоимость: " + tea.getPrice());
		
		if (tea.getDescription() != null && !tea.getDescription().isEmpty())
			stringList.add("Описание: " + tea.getDescription());
		
		if (averageRating > 0) {
			String averageRatingEmoji = EmojiParser.parseToUnicode("Средний рейтинг: ");
			stringList.add(averageRatingEmoji + averageRating + " / 10.0");
		} else stringList.add("Нет оценок");
		
		StringBuilder answer = new StringBuilder();
		
		evaluationService.getOneEvaluation(userId, teaId).ifPresentOrElse((evaluation) -> {
			
			String yourRatingEmoji = EmojiParser.parseToUnicode("Ваша оценка: " + evaluation.getRating() + ":star:");
			stringList.add(yourRatingEmoji);
			
			if (evaluation.getComment() != null && !evaluation.getComment().isEmpty())
				stringList.add("Комментарий: " + evaluation.getComment());
			
			editMessageText.setReplyMarkup(teaKeyboards.getOneTeaKeyboard(teaId, categoryId, true, isAdmin));
		}, () -> {
			stringList.add("Вы ещё не оценили этот чай");
			
			editMessageText.setReplyMarkup(teaKeyboards.getOneTeaKeyboard(teaId, categoryId, false, isAdmin));
		});
		
		for (String line: stringList) {
			answer.append(line).append("\n");
		}
		
		editMessageText.setText(answer.toString());
		return editMessageText;
	}
	
	public BotApiMethod<?> getCategories(EditMessageText editMessageText) {
		long userId = getUserIdByMessage(editMessageText);
		userService.setStateUser(userId, BotState.GET_CATEGORIES);
		
		editMessageText.setText("Категории");
		editMessageText.setReplyMarkup(categoryKeyboards.getAllCategoriesKeyboard());
		return editMessageText;
	}
	
	public BotApiMethod<?> getSubcategories(EditMessageText editMessageText) {
		long userId = getUserIdByMessage(editMessageText);
		userService.setStateUser(userId, BotState.GET_CATEGORIES);
		
		editMessageText.setText("Улуны");
		editMessageText.setReplyMarkup(categoryKeyboards.getAllSubcategoriesKeyboard());
		return editMessageText;
	}
	
	public BotApiMethod<?> getRatingKeyboard(EditMessageText editMessageText) {
		long userId = getUserIdByMessage(editMessageText);
		userService.setStateUser(userId, BotState.EVALUATING_TEA);
		
		User user = userService.getOneUser(userId).orElseThrow();
		
		if (user.getChosenTeaId() == null) {
			userService.setStateUser(userId, BotState.GET_TEA);
			return deleteMessage(editMessageText);
		}
		
		Long teaId = user.getChosenTeaId();
		
		String evaluatingTeaKey = "user:" + userId + ":evaluating_tea";
		redisService.setToHash(evaluatingTeaKey, "tea", teaId.toString());
		
		editMessageText.setText("Введите вашу оценку от 1 до 10");
		editMessageText.setReplyMarkup(teaKeyboards.getBackToTeaKeyboard(teaId));
		
		return editMessageText;
	}
	
	private BotApiMethod<?> deleteMessage(EditMessageText editMessageText) {
		return new DeleteMessage(editMessageText.getChatId(), editMessageText.getMessageId());
	}
	
	private long getUserIdByMessage(EditMessageText editMessageText) {
		return Long.parseLong(editMessageText.getChatId());
	}
	
	public BotApiMethod<?> getCreateTea(EditMessageText editMessageText, Long categoryId) {
		long userId = getUserIdByMessage(editMessageText);
		String newTeaKey = "user:" + userId + ":new_tea";
		
		redisService.setToHash(newTeaKey, "category", categoryId.toString());
		
		userService.setStateUser(userId, BotState.CREATING_TEA);
		
		editMessageText.setText("Добавление чая. Введите название");
		
		editMessageText.setReplyMarkup(teaKeyboards.getBackToTeasListKeyboard(categoryId));
		
		return editMessageText;
	}
	
	
	public BotApiMethod<?> getEditTea(EditMessageText editMessageText, Long teaId) {
		long userId = getUserIdByMessage(editMessageText);
		String editTeaKey = "user:" + userId + ":edit_tea";
		
		Tea tea = teaService.getOneTea(teaId).orElseThrow();
		
		redisService.setToHash(editTeaKey, "tea", teaId.toString());
		
		userService.setStateUser(userId, BotState.EDITING_TEA);
		
		editMessageText.setText("Редактирование чая. Название: " + tea.getName() + ".\nВведите новое название");
		editMessageText.setReplyMarkup(teaKeyboards.getBackToTeaKeyboard(teaId));
		return editMessageText;
	}
	
	public BotApiMethod<?> getDeleteTea(EditMessageText editMessageText, Long teaId) {
		long userId = getUserIdByMessage(editMessageText);
		String deleteTeaKey = "user:" + userId + ":delete_tea";
		
		Tea tea = teaService.getOneTea(teaId).orElseThrow();
		
		redisService.setToHash(deleteTeaKey, "tea", teaId.toString());
		userService.setStateUser(userId, BotState.DELETING_TEA);
		
		editMessageText.setText("Удаление чая \"" + tea.getName() + "\". Напишите название чая для подтверждения.");
		editMessageText.setReplyMarkup(teaKeyboards.getBackToTeaKeyboard(teaId));
		return editMessageText;
	}
	
	public BotApiMethod<?> creatingTeaDialog(Message message, Long userId) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(userId);
		
		String newTeaKey = "user:" + userId + ":new_tea";
		
		long categoryId = Long.parseLong(redisService.getFromHash(newTeaKey, "category"));
		
		sendMessage.setReplyMarkup(teaKeyboards.getBackToTeasListKeyboard("Отмена", categoryId));
		
		if (redisService.isEmpty(newTeaKey, "name")) {
			redisService.setToHash(newTeaKey, "name", message.getText());
			
			sendMessage.setText("Введите стоимость");
		} else if (redisService.isEmpty(newTeaKey, "price")) {
			if (message.getText().matches("\\d+") && Integer.parseInt(message.getText()) > 0) {
				
				redisService.setToHash(newTeaKey, "price", message.getText());
				
				sendMessage.setText("Введите описание");
			} else {
				sendMessage.setText("Ошибка! Введите число");
			}
			
		} else if (redisService.isEmpty(newTeaKey, "description")) {
			
			redisService.setToHash(newTeaKey, "description", message.getText());
			
			String name = redisService.getFromHash(newTeaKey, "name");
			long price = Long.parseLong(redisService.getFromHash(newTeaKey, "price"));
			String description = redisService.getFromHash(newTeaKey, "description");
			
			Category category = categoryService.getById(categoryId).orElseThrow();
			
			Tea tea = new Tea(name, description, price, category);
			
			teaService.saveTea(tea);
			
			redisService.removeKey(newTeaKey);
			sendMessage.setText("Чай успешно добавлен!");
			sendMessage.setReplyMarkup(teaKeyboards.getBackToTeasListKeyboard(categoryId));
			userService.setStateUser(userId, BotState.GET_TEAS_LIST);
		}
		return sendMessage;
	}
	
	public BotApiMethod<?> editingTeaDialog(Message message, Long userId) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(userId);
		String editTeaKey = "user:" + userId + ":edit_tea";
		
		long teaId = Long.parseLong(redisService.getFromHash(editTeaKey, "tea"));
		
		Tea tea = teaService.getOneTea(teaId).orElseThrow();
		
		sendMessage.setReplyMarkup(teaKeyboards.getBackToTeaKeyboard(teaId));
		
		if (redisService.isEmpty(editTeaKey, "name")) {
			
			redisService.setToHash(editTeaKey, "name", message.getText());
			
			sendMessage.setText("Стоимость: " + tea.getPrice() + "\nВведите новую стоимость");
		} else if (redisService.isEmpty(editTeaKey, "price")) {
			if (message.getText().matches("\\d+") && Integer.parseInt(message.getText()) > 0) {
				
				redisService.setToHash(editTeaKey, "price", message.getText());
				
				sendMessage.setText("Описание: " + tea.getPrice() + "\nВведите новое описание");
			} else {
				sendMessage.setText("Ошибка! Введите число");
			}
			
		} else if (redisService.isEmpty(editTeaKey, "description")) {
			redisService.setToHash(editTeaKey, "description", message.getText());
			
			String newTeaName = redisService.getFromHash(editTeaKey, "name");
			long newTeaPrice = Long.parseLong(redisService.getFromHash(editTeaKey, "price"));
			String newTeaDescription = redisService.getFromHash(editTeaKey, "description");
			
			tea.setName(newTeaName);
			tea.setPrice(newTeaPrice);
			tea.setDescription(newTeaDescription);
			
			teaService.saveTea(tea);
			
			redisService.removeKey(editTeaKey);
			sendMessage.setText("Чай успешно изменён!");
			sendMessage.setReplyMarkup(teaKeyboards.getBackToTeaKeyboard(teaId));
			userService.setStateUser(userId, BotState.GET_TEA);
		}
		
		return sendMessage;
	}
	
	public BotApiMethod<?> evaluatingTeaDialog(Message message, Long userId) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(userId);
		String evaluatingTeaKey = "user:" + userId + ":evaluating_tea";
		
		// todo можно получать айди чая из кеша, а не из бд (как очищать кеш правильно?)
		// long teaId = Long.parseLong(redisService.getFromHash(evaluatingTeaKey, "tea"));
		
		User user = userService.getOneUser(userId).orElseThrow();
		if (user.getChosenTeaId() == null) {
			sendMessage.setText("Вы не выбрали чай...");
			return sendMessage;
		}
		
		long teaId = user.getChosenTeaId();
		
		if (redisService.isEmpty(evaluatingTeaKey, "rating")) {
			if (message.getText().matches("\\d+") && (Integer.parseInt(message.getText()) > 0 && Integer.parseInt(message.getText()) <= 10)) {
				
				redisService.setToHash(evaluatingTeaKey, "rating", message.getText());
				
				sendMessage.setText("Введите комментарий!");
				sendMessage.setReplyMarkup(teaKeyboards.getBackToTeaKeyboard(teaId));
			} else {
				
				sendMessage.setText("Введите целое число от 1 до 10");
				sendMessage.setReplyMarkup(teaKeyboards.getBackToTeaKeyboard(teaId));
			}
		} else if (redisService.isEmpty(evaluatingTeaKey, "comment")) {
			
			redisService.setToHash(evaluatingTeaKey, "comment", message.getText());
			
			Tea tea = teaService.getOneTea(teaId).orElseThrow();
			
			long rating = Long.parseLong(redisService.getFromHash(evaluatingTeaKey, "rating"));
			String comment = redisService.getFromHash(evaluatingTeaKey, "comment");
			
			evaluationService.getOneEvaluation(userId, teaId).ifPresentOrElse((e) -> {
				e.setRating(rating);
				e.setComment(comment);
				evaluationService.saveEvaluation(e);
			}, () -> evaluationService.saveEvaluation(new Evaluation(rating, comment, user, tea)));
			
			user.setState(BotState.GET_TEA);
			redisService.removeKey(evaluatingTeaKey);
			sendMessage.setText("Оценка поставлена");
			sendMessage.setReplyMarkup(teaKeyboards.getBackToTeaKeyboard(teaId));
		}
		return sendMessage;
	}
	
	public BotApiMethod<?> deletingTeaDialog(Message message, Long userId) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(userId);
		
		String deleteTeaKey = "user:" + userId + ":delete_tea";
		
		long teaId = Long.parseLong(redisService.getFromHash(deleteTeaKey, "tea"));
		
		Tea tea = teaService.getOneTea(teaId).orElseThrow();
		
		if (tea.getName().equalsIgnoreCase(message.getText())) {
			long categoryId = tea.getCategory().getId();
			teaService.deleteTea(teaId);
			userService.setStateUser(userId, BotState.GET_TEAS_LIST);
			
			sendMessage.setText("Чай удалён");
			redisService.removeKey(deleteTeaKey);
			sendMessage.setReplyMarkup(teaKeyboards.getBackToTeasListKeyboard(categoryId));
		} else {
			sendMessage.setText("Названия не совпадают.");
			sendMessage.setReplyMarkup(teaKeyboards.getBackToTeaKeyboard(teaId));
		}
		return sendMessage;
	}
}