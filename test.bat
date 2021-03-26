ECHO OFF
set arg1=%1
set arg2=%2

call java -jar target/testiis-jar-with-dependencies.jar %arg1% %arg2%
