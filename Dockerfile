FROM eclipse-temurin:17-jdk

# Instala o ffmpeg
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    apt-get clean

# Cria diretório do app
WORKDIR /app

# Copia o jar da aplicação
COPY target/videoconvert-0.0.1.jar videoconvert.jar

# Expõe porta se necessário
EXPOSE 8080

# Executa a aplicação
ENTRYPOINT ["java", "-jar", "videoconvert-0.0.1.jar"]