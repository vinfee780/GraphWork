# Приложение для генерации направленных графов, визуализации их топологии и поиска всех элементарных замкнутых петель (циклов). 
Программа используется для анализа вычислительной сложности алгоритмов на графах, применяемых при расчетах по формуле Мейсона.

Тестировалось на:

Ubuntu 24.04.3-LTS с jdk 21.0.9

Windows 11 24H2 c jdk 21.0.9

Использованные библиотеки:

JUnit 5

Apache Log4j 2

## Для сборки и запуска проекта
Клонировать проект:
```bash
git clone https://github.com/vinfee780/GraphWork.git
```
Перейти в папку
```bash
cd GraphWork
```

Для сборки
```bash
./gradlew build
```

Запустить:
```bash
java -jar build/libs/GraphWork-1.0-SNAPSHOT.jar
```

Запуск тестов:
```bash
./gradlew test
```

Javadoc:
```bash
./gradlew javadoc
```
