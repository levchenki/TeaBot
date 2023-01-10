package com.uqii.teabot.config;

import com.uqii.teabot.botapi.TelegramBot;
import com.uqii.teabot.botapi.TelegramFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	
	private final TelegramBotConfig telegramBotConfig;
	
	public AppConfig(TelegramBotConfig telegramBotConfig) {
		this.telegramBotConfig = telegramBotConfig;
	}
	
	@Bean
	public TelegramBot telegramBot(TelegramFacade telegramFacade) {
		TelegramBot telegramBot = new TelegramBot(telegramFacade);
		telegramBot.setBotPath(telegramBotConfig.getWebhookPath());
		telegramBot.setBotUsername(telegramBotConfig.getUsername());
		telegramBot.setBotToken(telegramBotConfig.getToken());
		
		return telegramBot;
	}
}
