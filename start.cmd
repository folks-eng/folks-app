@rem set NET_DEBUG="-Djavax.net.debug=all"

set DEBUG_OPTS="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=9791,server=y,suspend=n"

set JAVA_OPTS="--add-opens=java.base/java.nio=ALL-UNNAMED --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Djdk.httpclient.HttpClient.log=errors,requests,headers"

java %DEBUG_OPT% %NET_DEBUG% %JAVA_OPTS% -Dhttp.port=9443 -Dhttp.ssl=true -Duser.timezone=Asia/Kolkata -jar target/folks-app-0.0.1-SNAPSHOT.jar
