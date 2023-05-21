/*
Никита, приветствую!
Задание выполнил - проверьте, пожалуйста.
Хотел у Вас спросить: т.к. в этом модуле мы используем структуру не по типам классов, а по фичам, то нужно ли в папке
каждой фичи создавать папки "controller", "model" и т.п. (по аналогии с папками "dto", которые уже были созданы в
первичной ветке)? Так-то оно, вроде, правильно, но почти во всех этих папках всего по одному классу, и визуально эти
папки больше затрудняют восприятие.
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