# zk_talk

Start Zookeeper
================

Start the zookeeper service with 
```
docker-compose up -d
```


Start zkCLI 
====================

Start a bash session in the zookeper docker 

```
docker run --link zktalk_zoo1_1:zk1  --link zktalk_zoo1_2:zk2  --link zktalk_zoo1_3:zk3 --net zktalk_default -it zookeeper /bin/bash
```

and then start the zkCli client
```
zkCli.sh --server zk1
```
