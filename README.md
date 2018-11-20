# zk_talk

Start Zookeeper
================

Start the zookeeper service with 
```
docker-compose up -d
```


Build the example
===================

```
mvn clean compile package
```

Open three terminals and run one line in each terminal: 

```
mvn exec:java  -Dexec.args="node01 localhost:2182"
mvn exec:java  -Dexec.args="node02 localhost:2183"
mvn exec:java  -Dexec.args="node03 localhost:2184"
```

(Optional) Start zkCLI and explore Zookeeper 
==============================================

Start a bash session in the zookeper docker

```
docker run --link zk_talk_zoo1_1:zk1  --link zk_talk_zoo2_1:zk2  --link zk_talk_zoo3_1:zk3 --net zk_talk_default -it zookeeper /bin/bash
```

and then start the zkCli client
```
zkCli.sh --server zk1
