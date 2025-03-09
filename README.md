## СкладОк - Система учета текстильных изделий
Проект представляет собой API для автоматизации складского учета текстильных изделий (носочных изделий) с возможностью отслеживания их характеристик.
- - -
### Технологии
- Java 21
- Spring Boot
- Postgresql
- Hibernate
- Lombok
- Liquibase
- Docker
- Docker Compose
- - -
### Запуск проекта
Для запуска проекта необходимо установить Docker и Docker Compose.
1. Клонируйте репозиторий: 
```bash
git clone https://github.com/MrSlead/SkladOk.git
cd SkladOk
```
2. Соберите Java приложение:
```bash
maven clean package
```
3. Соберите и запустите контейнеры:
```bash
docker-compose up --build
```
### Описание API
#### 1. Регистрация поступления товара
- Метод: *POST*
- URL: http://localhost:8080/storage/items/incoming
- Тело запроса (JSON):
```json
{
  "itemColor": "строковое_значение",
  "materialPercentage": целое_число_0_100,
  "units": целое_положительное_число
}
```
Пример запроса:
```json
{
  "itemColor": "crimson",
  "materialPercentage": 90,
  "units": 100
}
```
Успешный ответ:
```
Операция выполнена успешна
```
#### 2. Регистрация выбытия товара
- Метод: *POST*
- URL: http://localhost:8080/storage/items/outgoing
- Тело запроса (JSON): Аналогично регистрации поступления товара.

Пример запроса:
```json
{
  "itemColor": "green",
  "materialPercentage": 10,
  "units": 50
}
```
Успешный ответ:
```
Операция выполнена успешна
```
#### 3. Получение информации о товаре
- Метод: *GET*
- URL: http://localhost:8080/storage/items
- Параметры запроса:
    - *itemColor* - строковое значение цвета
    - *compareType* - тип сравнения для процентного содержания материала (gt, lt, eq)
    - *materialPercentage* - числовое значение для сравнения 

Примеры запросов:
- /storage/items?itemColor=crimson&compareType=gt&materialPercentage=85
- /storage/items?itemColor=navy&compareType=lt&materialPercentage=15

Результат:
Общее количество единиц товара, соответствующего заданным критериям (числовое значение).

Пример ответа:
```
{
    "totalUnits": 242
}
```