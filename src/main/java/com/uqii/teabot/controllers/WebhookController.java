package com.uqii.teabot.controllers;

import com.uqii.teabot.botapi.TelegramBot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebhookController {
	
	private final TelegramBot telegramBot;
	
	public WebhookController(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
	}
	
	@PostMapping("/")
	public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
		return telegramBot.onWebhookUpdateReceived(update);
	}
	
}
