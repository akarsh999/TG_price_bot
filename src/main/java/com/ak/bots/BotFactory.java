package com.ak.bots;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BotFactory {
    @Value("${test.bot.token}")
    private String testBotToken;

    @Value("${test.bot.name}")
    private String testBotUserName;

    public TestBot createTestBot(){
        return new TestBot(testBotUserName,testBotToken);
    }
}
