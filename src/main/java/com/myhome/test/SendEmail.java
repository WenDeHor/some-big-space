package com.myhome.test;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class SendEmail {

    public static void main(String [] args) {
        // Необходимо указать адрес электронной почты получателя
        String to = "xalivan@ukr.net";

        // Необходимо указать адрес электронной почты отправителя
        String from = "mather@gmail.com";

        // Предполагая, что вы отправляете электронное письмо с localhost
        String host = "localhost";

        // Получить свойства системы
        Properties properties = System.getProperties();

        // Настроить почтовый сервер
        properties.setProperty("8080", host);

        // Получение объекта Session по умолчанию
        Session session = Session.getDefaultInstance(properties);

        try {
            // Создание объекта MimeMessage по умолчанию
            MimeMessage message = new MimeMessage(session);

            // Установить От: поле заголовка
            message.setFrom(new InternetAddress(from));

            // Установить Кому: поле заголовка
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Установить тему: поле заголовка
            message.setSubject("Это тема письма!");

            // Теперь установите фактическое сообщение
            message.setText("Это актуальное сообщение");

            // Отправить сообщение
            Transport.send(message);
            System.out.println("Сообщение успешно отправлено....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
