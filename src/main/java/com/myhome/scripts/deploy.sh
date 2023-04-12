#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -P 22 \ C:\MyFiles\FATHER\JAVA\homepage\target\homepage-0.0.1-SNAPSHOT.jar \ root@185.230.88.219:/home/
echo 'Restart server...'

ssh -i ~/.ssh/id_rsa root@185.230.88.219 << EOF
pgrep java | xargs kill -9
nohup java -jar homepage-0.0.1-SNAPSHOT.jar > log.txt &
EOF

echo 'Bye'