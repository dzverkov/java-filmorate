# Filmorate project.

Модель базы данных.
![Схема БД](Model%20database.png)

### Примеры запросов:

База данных должна поддерживать бизнес-логику, 
предусмотренную в приложении. Подумайте о том, 
как будет происходить получение всех фильмов, 
пользователей. 
А как — топ N наиболее популярных фильмов. 
Или список общих друзей с другим пользователем.

1. Список фильмов.

        SELECT 
          FILM_ID
        , NAME
        , DESCRIPTION
        , RELEASE_DATE
        , DURATION
        , R.NAME AS MPA
       FROM FILMS F
       LEFT JOIN MPA R ON F.MPA_ID = R.MPA_ID;

2. Список пользователей.  

       SELECT
         USER_ID
       , EMAIL
       , LOGIN
       , NAME
       , BIRTHDAY
       FROM USERS;
3. 10 самых популярных фильмов.  

       SELECT
         FILM_ID
       , NAME
       , DESCRIPTION
       , RELEASE_DATE
       , DURATION
       FROM FILMS F
       JOIN FILM_LIKES FL ON F.FILM_ID = FL.FILM_ID
       GROUP BY    
         FILM_ID
       , NAME
       , DESCRIPTION
       , RELEASE_DATE
       , DURATION
       ORDER BY COUNT(*) DESC
       LIMIT 10;

4. Список общих друзей двух пользователей.  

        WITH
          USER_FRIENDS AS (
            SELECT 
              USER_ID
            , FRIEND_ID
            FROM FRIENDS F
            WHERE IS_CONFIRMED <> 0
          )
        SELECT
          USER_ID
        , EMAIL
        , LOGIN
        , NAME
        , BIRTHDAY
       FROM USERS U
       JOIN USER_FRIENDS UF1 ON U.USER_ID = UF1.FRIEND_ID
       JOIN USER_FRIENDS UF2 ON UF1.FRIEND_ID = UF2.FRIEND_ID 
         AND UF1.FRIEND_ID <> UF2.USER_ID AND UF2.FRIEND_ID <> UF1.USER_ID
       WHERE UF1.USER_ID = :USER_ID1
         AND  UF2.USER_ID = :USER_ID2