package com.ak.tg;

import com.ak.bots.BotFactory;
import com.ak.bots.TestBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@ComponentScan(basePackages = {"com.ak"})
public class TgApplication implements CommandLineRunner {

	@Autowired
	BotFactory botFactory;

	public static void main(String[] args) {

		SpringApplication.run(TgApplication.class, args);

	}


	@Override
	public void run(String... args) throws Exception {
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		botsApi.registerBot(botFactory.createTestBot());
	}
}
