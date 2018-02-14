package com.stratio.zktalk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;

import java.util.List;

class MyWatcher implements CuratorWatcher {

    private final CuratorFramework curatorFramework;
    private final String path;

    MyWatcher(CuratorFramework curatorFramework, String path) {
        this.curatorFramework = curatorFramework;
        this.path = path;
    }

    public void process(WatchedEvent watchedEvent) throws Exception {
        System.out.println("Receiveds EVENT " + watchedEvent.getType());
        try {
            switch (watchedEvent.getType()) {

                case NodeChildrenChanged: {
                    printClusterMembers(curatorFramework);
                    break;
                }
            }
        } finally {
            arm();
        }

    }

    public void arm() throws Exception {
        System.out.println("Arming watch for " + path);
        curatorFramework
                .getChildren()
                .usingWatcher(this)
                .forPath(path);
    }

    private void printClusterMembers(CuratorFramework curatorFramework) throws Exception {
        System.out.println("Current members of cluster:");
        List<String> children = curatorFramework
                .getChildren()
                .usingWatcher(this)
                .forPath(path);
        for (String c : children){
            System.out.println("\t" +c );
        }
    }
}

/**
 * Created by oier on 14/02/2018.
 */
public class App {

    static String PATH = "/clustermembers";

    public static void main(String[] args) {


        String CLIENT_ID =  args[0];
        String ZK_SERVER =  args[1];
        System.out.println("Start ");

        CuratorFramework curatorFramework;
        curatorFramework = CuratorFrameworkFactory.newClient(ZK_SERVER, new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();

        try {
            initMembersPath(curatorFramework);

            MyWatcher watch = new MyWatcher(curatorFramework, PATH);
            watch.arm();

            createEphemeralNode(PATH + "/" + CLIENT_ID, curatorFramework);

            waitSeconds(60);

            curatorFramework.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return;


    }

    private static void createEphemeralNode(String path, CuratorFramework curatorFramework) throws Exception {
        curatorFramework.
                create().
                withMode(CreateMode.EPHEMERAL).
                forPath(path);
    }

    private static void waitSeconds(long seconds ) {

        while (true) {
            try {
                Thread.currentThread().sleep(1000 * seconds);
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private static void initMembersPath(CuratorFramework curatorFramework) throws Exception {

        Stat stats = curatorFramework.checkExists().forPath(PATH);
        if (stats == null) {
            System.out.println("Creating path :" + PATH);
            curatorFramework.create().forPath(PATH);
        }


    }
}
