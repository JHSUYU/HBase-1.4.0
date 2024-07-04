if [ -d "/Users/lizhenyu/Desktop/proj_failure_recovery/source_code/hbase-1.4.0/hbase-assembly/target/hbase-1.4.0-bin.tar.gz" ]; then
rm -rf /Users/lizhenyu/Desktop/proj_failure_recovery/source_code/hbase-1.4.0/hbase-assembly/target/hbase-1.4.0-bin.tar.gz
fi
if [ -d "/Users/lizhenyu/Desktop/proj_failure_recovery/source_code/hbase-1.4.0/hbase-assembly/target/hbase-1.4.0" ]; then
rm -rf /Users/lizhenyu/Desktop/proj_failure_recovery/source_code/hbase-1.4.0/hbase-assembly/target/hbase-1.4.0-bin
fi
#mvn clean package -DskipTests assembly:single validate -Denforcer.skip=true
mvn package assembly:single -Dmaven.main.skip=true -DskipTests
cd /Users/lizhenyu/Desktop/proj_failure_recovery/source_code/hbase-1.4.0/hbase-assembly/target
if [ -d "/Users/lizhenyu/Desktop/proj_failure_recovery/hbase-1.4.0" ]; then
rm -rf /Users/lizhenyu/Desktop/proj_failure_recovery/hbase-1.4.0
fi
tar -zxvf hbase-1.4.0-bin.tar.gz
mv hbase-1.4.0 /Users/lizhenyu/Desktop/proj_failure_recovery/
echo "---------------Done---------------"