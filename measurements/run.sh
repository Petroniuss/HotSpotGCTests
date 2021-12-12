#!/bin/bash
# Provide path to jvm
java=/home/maciek/git/jdk/build/linux-x86_64-server-release/jdk/bin/java

program=binary-trees

echo "ThetaGC"
$java -XX:+UnlockExperimentalVMOptions -XX:+UseThetaGC -Xlog:gc -Xint ../$program/Application.java $1 > theta_logs
echo "SerialGC"
$java -XX:+UseSerialGC -Xlog:gc -Xint ../$program/Application.java $1 > serial_logs
echo "EpsilonGC (+Compaction)"
$java -XX:+UnlockExperimentalVMOptions -XX:+EpsilonMarkCompactGC -XX:+UseEpsilonGC -Xlog:gc -Xint ../$program/Application.java $1 > epsilon2_logs
# echo "EpsilonGC"
# $java -XX:+UnlockExperimentalVMOptions -XX:+EpsilonMarkCompactGC -XX:+UseEpsilonGC -Xlog:gc -Xint ../$program/Application.java $1 > epsilon_logs
