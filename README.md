# Генератор коротких ссылок short-links

Проект написан с помощью Spring-Boot, данные хранятся во встроенной БД H2 (in memory mode)

Собирать проект с помощью /other/apache-maven-3.6.0
Из командной строки в корне проекта:
	mvn clean package
	
В папке /other/ собранный проект со всеми зависимостями short-links-0.0.1.jar
Необходима установленная JRE 1.8+
Запуск из командной строки:
	java -jar short-links-0.0.1.jar
	
К запущенному сервису можно обращаться при помощи утилиты CURL по IP машины на которой запущен сервис (порт 8080)

Ручное тестирование проводил c помощью - curl 7.29.0 (x86_64-redhat-linux-gnu)
Сервис считает валидными только ссылки c полным URI (http и https)

### Примеры:

#### Создать или получить короткую ссылку
```
curl -X POST host/generate -H 'Content-type:application/json' -d '{"original":"http:://kontur.ru"}'
[ {
  "link" : "0zyM1J"
} ]
```

#### Просмотреть статистику по всем ссылкам
```
curl -X GET -G 192.168.1.192:8080/stats
[ {
  "link" : "0zyM1J",
  "original" : "http://kontur.ru",
  "rank" : 1,
  "count" : 0
} ]
```

#### Переход по короткой ссылке приводит к редиректу на оригинальную ссылку
```
curl -v 192.168.1.192:8080/l/0zyM1J
* About to connect() to 192.168.1.192 port 8080 (#0)
*   Trying 192.168.1.192...
* Connected to 192.168.1.192 (192.168.1.192) port 8080 (#0)
> GET /l/0zyM1J HTTP/1.1
> User-Agent: curl/7.29.0
> Host: 192.168.1.192:8080
> Accept: */*
>
< HTTP/1.1 302
< Location: http://kontur.ru
< Content-Length: 0
< Date: Fri, 04 Jan 2019 20:28:43 GMT
<
* Connection #0 to host 192.168.1.192 left intact
```

#### Просмотреть статистику по конкетной ссылке (счётчик переходов увеличился)
```
curl -X GET -G 192.168.1.192:8080/stats/0zyM1J
[ {
  "link" : "0zyM1J",
  "original" : "http://kontur.ru",
  "rank" : 1,
  "count" : 1
} ]
```

#### Постраничный вывод (page начиная с 0)
```
curl -X GET -G 192.168.1.192:8080/stats -d 'page=0' -d 'count=1'
[ {
  "link" : "0zyM1J",
  "original" : "http://kontur.ru",
  "rank" : 1,
  "count" : 1
} ]
```