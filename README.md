# Source
[Building Persistable One-to-One Chat Application using Spring Boot and WebSockets](https://medium.com/@amrkhaled_47016/building-persistable-one-to-one-chat-application-using-spring-boot-and-websockets-303ba5d30bb0)
#Requirements
##Java
openjdk version "15.0.2" 2021-01-19 (обязательно)
##Maven
Apache Maven 3.8.6 (мб соберется с более свежими)
##npm
6.14.4 (мб соберется с более свежими)
##nodejs
v18.7.0 (мб соберется с более свежими)
# Build
##Сперва собрать service-auth-client 
1. gradle build
2. gradle publishToMavenLocal для сборки в локальный maven репозиторий
##Далее собрать и запустить auth-service
mvn clean install
##Сборка chat-service
mvn clean install
##В конце собираем фронт (jwt-social-login-client)
1. npm install
2. npm start

Поднимется на localhost:3000

