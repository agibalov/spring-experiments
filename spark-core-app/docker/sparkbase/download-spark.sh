#/bin/sh

mirror=$(curl https://www.apache.org/dyn/closer.cgi\?as_json\=1 | sed -rn 's/.*"preferred":.*"(.*)"/\1/p')
echo ${mirror}
url="${mirror}spark/spark-${SPARK_VERSION}/spark-${SPARK_VERSION}-bin-hadoop2.6.tgz"
wget "${url}" -O "/tmp/spark-${SPARK_VERSION}.tgz"
