FROM kdz-registry.cn-hangzhou.cr.aliyuncs.com/public/centos7_jdk8:141_devtool
MAINTAINER Fisher "linqiang.tiang@coolcollege.cn"

RUN mkdir -p /opt/apps/coolcollege
ADD coolcollege-intelligent-web/target/coolcollege-intelligent-web-1.0.0.jar /coolcollege-intelligent-web-1.0.0.jar
ENV TZ 'Asia/Shanghai'
EXPOSE 30000
ENTRYPOINT ["java","-jar","/coolcollege-intelligent-web-1.0.0.jar"]

