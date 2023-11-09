package com.ak.bots;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.ak.bots.Commons.Constant.*;

public class TestBot extends TelegramLongPollingBot {

    private final String botUserName;
    private final String botToken;

    private InlineKeyboardMarkup keyboardM1;

    private InlineKeyboardMarkup keyboardM2;

    private InlineKeyboardButton nextButton;

    private InlineKeyboardButton backButton;

    private InlineKeyboardButton supportButton;

    private InlineKeyboardButton urlButton;

    private ReplyKeyboardMarkup replySupport;


    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public TestBot(String botUserName, String botToken) {
        this.botUserName = botUserName;
        this.botToken = botToken;
        this.initButtons().initKeyBoard();
    }
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasCallbackQuery()){
            this.processCallBack(update);
        } else{
            this.processNormalChat(update);
        }

    }

    private void processCallBack(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        try{
            if(callbackQuery.getData().equals("support")){
                sendReplyMenu(callbackQuery.getFrom().getId(), "Please support us", replySupport);
                return;
            }
            buttonTap(callbackQuery.getMessage().getChatId(), callbackQuery.getId(), callbackQuery.getData(), callbackQuery.getMessage().getMessageId());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void processNormalChat(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        System.out.println(user.getFirstName() + " wrote " + msg.getText());
        if(msg.isCommand()){
            if(START_COMMAND.equals(msg.getText())){
                this.sendText(user.getId(), START_MSG);
                this.sendInlineMenu(msg.getChatId(), MENU_SELECT_MSG, keyboardM1);
            }
        }
    }

    public void sendText(Long userId, String message){
        SendMessage sm = SendMessage.builder()
                .chatId(userId.toString())
                .text(message).build();
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public void sendInlineMenu(Long userId, String message, InlineKeyboardMarkup kb){
        SendMessage sm = SendMessage.builder().chatId(userId.toString())
                .parseMode("HTML").text(message)
                .replyMarkup(kb).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendReplyMenu(Long userId, String message, ReplyKeyboardMarkup kb){
        SendMessage sm = SendMessage.builder().chatId(userId.toString())
                .parseMode("HTML").text(message)
                .replyMarkup(kb).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void buttonTap(Long id, String queryId, String data, int msgId) throws TelegramApiException {

        EditMessageText newTxt = EditMessageText.builder()
                .chatId(id.toString())
                .messageId(msgId).text("").build();

        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .chatId(id.toString()).messageId(msgId).build();

        if(data.equals("next")) {
            newTxt.setText("MENU 2");
            newKb.setReplyMarkup(keyboardM2);
        } else if(data.equals("back")) {
            newTxt.setText("MENU 1");
            newKb.setReplyMarkup(keyboardM1);
        }

        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryId).build();

        execute(close);
        execute(newTxt);
        execute(newKb);
    }

    private void initKeyBoard () {
        keyboardM1 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(nextButton)).build();

//Buttons are wrapped in lists since each keyboard is a set of button rows
        keyboardM2 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(backButton))
                .keyboardRow(List.of(urlButton))
                .keyboardRow(List.of(supportButton))
                .build();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("rate");
        keyboardRow.add("donate");
        replySupport = ReplyKeyboardMarkup.builder().keyboardRow(keyboardRow).resizeKeyboard(true).build();
    }

    private TestBot initButtons() {
         nextButton = InlineKeyboardButton.builder()
                .text("Next").callbackData("next")
                .build();

         backButton = InlineKeyboardButton.builder()
                .text("Back").callbackData("back")
                .build();

         urlButton = InlineKeyboardButton.builder()
                .text("Tutorial")
                .url("https://core.telegram.org/bots/api")
                .build();
         supportButton = InlineKeyboardButton.builder()
                .text("Support").callbackData("support")
                .build();
        return this;
    }
}
