сборка проекта - mvn clean compile assembly:single  
Запуск скрипта Sql - включен по умолчанию, в настроках properties
1) Чтобы выгрузить содержимое таблицы БД в XML файл, нужно прописать в командной строке, test.bat и имя файла. Пример: test.bat test.xml
2) Для синхронизации БД и xml файла, test.bat sync имя файла.  Пример: test.bat sync test.xml. 