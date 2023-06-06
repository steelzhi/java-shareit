/*
Никита, здравствуйте.
Задание выполнил, проверьте, пожалуйста.
У меня возник вопрос по unit-тестам: тесты для слоя репозиториев я написал, но возможно ли написать также тесты для
слоев сервиса и контроллера с учетом того, что этим слоям требуется конкретный класс-репозиторий, а у нас теперь
вместо него интерфейс? Не смог найти информации, как это можно сделать (если это вообще реализуемо).

 */

package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItApp {

    public static void main(String[] args) {
        SpringApplication.run(ShareItApp.class, args);
    }

}