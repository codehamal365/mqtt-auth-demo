# Dockerfile
# 基于的镜像
FROM openjdk:11-jdk-oracle

VOLUME /opt/tmp

ADD mqtt-aut-hdemo.jar app.jar

# -Djava.security.egd=file:/dev/./urandom 可解决tomcat可能启动慢的问题
# 具体可查看：https://www.cnblogs.com/mightyvincent/p/7685310.html
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

# 对外端口
EXPOSE 8081