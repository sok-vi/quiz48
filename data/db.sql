/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  vasya
 * Created: 27.01.2018
 */

-- таблица с учётными данными пользователей
CREATE TABLE users (
    id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    login VARCHAR(30) NOT NULL, -- логин
    name VARCHAR(255) NOT NULL,
    pwd VARCHAR(32) NOT NULL, -- пароль md5
    is_admin INTEGER NOT NULL DEFAULT 0 -- если установлено 1, то админ и надо ему показать всю статистику
);

CREATE UNIQUE INDEX users_id ON users (id);
CREATE UNIQUE INDEX users_login ON users (login);

-- таблица со списком тестов/викторин
CREATE TABLE quiz (
    id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    name VARCHAR(255) NOT NULL,
    is_fix INTEGER NOT NULL DEFAULT 0, -- 0 - фиксировнное количество вопросов - выбираются все связанные вопросы, 1 - выбираются случайные вопрос в количестве count
    count INTEGER NOT NULL DEFAULT 10, -- количество вопросов викторины
    time INTEGER NOT NULL DEFAULT 0, -- 0 если не требуется ограничить время прохождения викторины или в секундах ограничение
    sort INTEGER NOT NULL DEFAULT 0, -- сортировка - 1 сотировака
    level INTEGER NOT NULL DEFAULT 0, -- уровень сложности - 2 сортировака
    repeat INTEGER DEFAULT 1 -- 1 можно повторно проходить тест
);

CREATE UNIQUE INDEX quiz_id ON quiz (id);
CREATE INDEX quiz_sort ON quiz (sort);
CREATE INDEX quiz_level ON quiz (level);
CREATE INDEX quiz_repeat ON quiz (repeat);

-- таблица со списком воросов
CREATE TABLE query (
    id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    query LONG VARCHAR NOT NULL, -- сам вопрос
    answer VARCHAR(1024) NOT NULL, -- сам ответ
    quiz_id INTEGER NOT NULL, -- ссылка на quiz.id
    sort INTEGER NOT NULL DEFAULT 0, -- сортировка вопросов в тесте
    time INTEGER NOT NULL DEFAULT 0, -- время - если 0 время не ограничено, иначе количество секунд ограничивающих ответ
    is_fix INTEGER NOT NULL DEFAULT 0, -- если 0, то необходимо ввести ответ. иначе выбрать вариант из answers
    repeat INTEGER DEFAULT 1, -- 1 можно повторно отвечать на вопрос
    ext INTEGER DEFAULT 0, -- внешний ресурс в query записан путь от  ./content/
    weight INTEGER DEFAULT 0 -- вес при расчёте балов
);

CREATE UNIQUE INDEX query_id ON query (id);
CREATE UNIQUE INDEX query_v_id ON query (quiz_id);
CREATE UNIQUE INDEX query_sort ON query (sort);
CREATE INDEX query_repeat ON query (repeat);

-- таблица со вариантами ответов
CREATE TABLE answer (
    query_id INTEGER NOT NULL, -- ссылка query.id
    answer VARCHAR(1024) NOT NULL -- сам ответ
);

CREATE INDEX answer_query_id ON answer (query_id);

-- таблица с результатами викторины
CREATE TABLE quiz_result (
    id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
    quiz_id INTEGER NOT NULL, -- ссылка на quiz.id
    user_id INTEGER NOT NULL, -- ссылка на user.id
    status INTEGER NOT NULL DEFAULT 0, -- 0 викторина не пройдена; 1 - викторина пройдена
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- дата начала викторины
    time INTEGER NOT NULL DEFAULT 0, -- общее время виторины - обновляется после каждого ответа
    duplicate INTEGER NOT NULL DEFAULT 0 -- викторина проходится второй раз
);

CREATE INDEX quiz_result_id ON quiz_result (id);
CREATE INDEX quiz_result_quiz_id ON quiz_result (quiz_id);
CREATE INDEX quiz_result_user_id ON quiz_result (user_id);

-- таблица с конкретными ответами на воросы виторины
CREATE TABLE query_result (
    quiz_result_id INTEGER NOT NULL, -- ссылка на quiz_result.id
    query_id INTEGER NOT NULL, -- ссылка на query.id
    answer VARCHAR(1024) NOT NULL, -- ответ пользователя
    time INTEGER NOT NULL, -- время в секундах ответа на вопрос
    fail INTEGER NOT NULL DEFAULT 0 -- 0 правильно; 1 не правильно
);

CREATE INDEX query_result_quiz_result_id ON query_result (quiz_result_id);
CREATE INDEX query_result_query_id ON query_result (query_id);
CREATE INDEX query_result_fail ON query_result (fail);


