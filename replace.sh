#!/bin/bash

# 设置变量
HBASE_DIR="/Users/lizhenyu/Desktop/proj_failure_recovery/source_code/hbase-1.4.0"  # 请替换为您的 HBase 源代码目录
HBASE_VERSION="1.4.0"  # 请根据您的 HBase 版本进行调整
SOURCE_CLASS_FILE="/Users/lizhenyu/Desktop/proj_failure_recovery/source_code/RecoveryChecker/sootOutput/org/apache/hadoop/hbase/ipc/Handler.class"  # 请替换为您修改后的 .class 文件路径
TARGET_CLASS_PATH="/Users/lizhenyu/Desktop/proj_failure_recovery/source_code/hbase-1.4.0/hbase-server/target/classes/org/apache/hadoop/hbase/ipc/Handler.class"
CLASS_PATH_IN_JAR="/Users/lizhenyu/Desktop/proj_failure_recovery/hbase-1.4.0/lib/hbase-server-1.4.0.jar"  # 请替换为 .class 文件在 JAR 中的路径

if [ ! -d "$HBASE_DIR" ]; then
    echo "HBase dir does not exist: $HBASE_DIR"
    exit 1
fi

HBASE_SERVER_DIR="$HBASE_DIR/hbase-server"
HBASE_SERVER_TARGET_DIR="$HBASE_DIR/hbase-server/target/classes"
HBASE_SERVER_JAR="$HBASE_SERVER_DIR/target/hbase-server-$HBASE_VERSION.jar"

cp SOURCE_CLASS_FILE TARGET_CLASS_PATH


#
if [ ! -d "$HBASE_DIR" ]; then
    echo "hbase dir does not exist: $HBASE_DIR"
fi

cd HBASE_SERVER_TARGET_DIR

jar cvf ../hbase-server-1.4.0.jar *

cp HBASE_SERVER_JAR CLASS_PATH_IN_JAR


#
#mvn package -Pdist -DskipTests -Dmaven.javadoc.skip=true -Dmaven.main.skip=true assembly:single







#if [ ! -f "$HBASE_SERVER_JAR" ]; then
#    echo "hbase-server JAR 文件不存在: $HBASE_SERVER_JAR"
#    echo "尝试构建 HBase..."
#fi
#
#echo "replace Handler.class 文件..."
#jar uvf "$HBASE_SERVER_JAR" -C "$(dirname "$MODIFIED_CLASS_FILE")" "$(basename "$MODIFIED_CLASS_FILE")"
#
#echo "Handler.class 文件已更新在 JAR 中。"
#
## 使用 assembly:single 生成二进制分发包
#echo "生成 HBase 二进制分发包..."
#cd "$HBASE_DIR"
#mvn clean package assembly:single -DskipTests
#
#BINARY_TARBALL="$HBASE_DIR/hbase-assembly/target/hbase-$HBASE_VERSION-bin.tar.gz"
#if [ -f "$BINARY_TARBALL" ]; then
#    echo "HBase 二进制分发包已成功创建: $BINARY_TARBALL"
#else
#    echo "无法找到生成的二进制分发包。请检查构建过程。"
#    exit 1
#fi

#
#if [ -d "/Users/lizhenyu/Desktop/proj_failure_recovery/source_code/hbase-1.4.0/hbase-assembly/target/hbase-1.4.0-bin.tar.gz" ]; then
#rm -rf /Users/lizhenyu/Desktop/proj_failure_recovery/source_code/hbase-1.4.0/hbase-assembly/target/hbase-1.4.0-bin.tar.gz
#fi
#if [ -d "/Users/lizhenyu/Desktop/proj_failure_recovery/source_code/hbase-1.4.0/hbase-assembly/target/hbase-1.4.0" ]; then
#rm -rf /Users/lizhenyu/Desktop/proj_failure_recovery/source_code/hbase-1.4.0/hbase-assembly/target/hbase-1.4.0-bin
#fi
#mvn package -Pdist -DskipTests -Dmaven.javadoc.skip=true -Dmaven.main.skip=true assembly:single
#cd /Users/lizhenyu/Desktop/proj_failure_recovery/source_code/hbase-1.4.0/hbase-assembly/target
#if [ -d "/Users/lizhenyu/Desktop/proj_failure_recovery/hbase-1.4.0" ]; then
#rm -rf /Users/lizhenyu/Desktop/proj_failure_recovery/hbase-1.4.0
#fi
#tar -zxvf hbase-1.4.0-bin.tar.gz
#mv hbase-1.4.0 /Users/lizhenyu/Desktop/proj_failure_recovery/
echo "---------------Done---------------"