package edu.illinois.cs425_mp1.adapter;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.*;

import edu.illinois.cs425_mp1.network.FileListener;
import edu.illinois.cs425_mp1.network.FileSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.illinois.cs425_mp1.monitor.HeartbeatAdapter;
import edu.illinois.cs425_mp1.network.Listener;
import edu.illinois.cs425_mp1.network.P2PSender;
import edu.illinois.cs425_mp1.types.MembershipList;
import edu.illinois.cs425_mp1.types.Request;
import edu.illinois.cs425_mp1.ui.Console;

/**
 * Now only support one-to-one UI and adapter
 *
 * @author silvia
 */
final public class Adapter {

    private static String localhost;

    private Listener requestListener = null;

    private Thread mainLoop = null;

    private Thread heartbeatThread = null;

    private HeartbeatAdapter heartbeatAdapter = null;

    private static Console console = null;

    private Logger log = LogManager.getLogger("adapterLogger");

    // IP addresses of all neighbors
    private final static String[] addresses = new String[]{
            "172.22.151.52",
            "172.22.151.53",
            "172.22.151.54",
            "172.22.151.55",
            "172.22.151.56",
            "172.22.151.57",
            "172.22.151.58",
    };

    private static final P2PSender[] channels = new P2PSender[addresses.length];

    /**
     * Following are file related variables
     * fileStoreLocation is only a local count
     **/
    private static HashMap<String, ArrayList<String>> fileStoreLocation = new HashMap<String, ArrayList<String>>();

    //TODO: test on vm the path
    private static String DFSFileLocation = "dfs/";

    private static String DFSOutputLocation = "output/";

    private static int numOfReplica = 3;

    private static ArrayList<String> dfsLocalFiles = new ArrayList<String>();


    static {
        //Get localhost value
        try {
            localhost = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.out.println("fail to inititate local node");
            System.exit(1);
        }
    }

    /**
     * Constructor method
     *
     * @param port the port local listener should bind to, better unified among nodes
     */
    public Adapter(int port) {
        requestListener = new Listener(port);
//        heartbeatAdapter = new HeartbeatAdapter();
//        heartbeatThread = new Thread(heartbeatAdapter);
        mainLoop = new Thread() {
            public synchronized void run() {
                log.trace("mainLoop runing");
                try {
                    requestListener.run();
                } catch (Exception e) {
                    log.trace("Main adapter stopped.");
                }
            }
        };
        mainLoop.start();
//        heartbeatThread.start();
    }

    /**
     * Broadcast a request to all alive neighbors
     *
     * @param request the request to send
     */
    public void sendBroadcastRequest(Request request) {
        for (int i = 0; i < addresses.length; i++) {
            sendP2PRequest(request, i + 1);
        }
    }

    /**
     * Send the request to a specific node, if send to itself,
     * take no effect
     *
     * @param request the request to send
     * @param node    the number of node, start from 1
     */
    public void sendP2PRequest(Request request, int node) {
        if (channels[node - 1] == null) {
            channels[node - 1] = new P2PSender(addresses[node - 1], Console.port);
            channels[node - 1].run();
        }
        channels[node - 1].send(request);
    }

    /**
     * Client should call this when stop adapter
     */
    public void close() {
        try {
            requestListener.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainLoop.interrupt();
    }

    /**
     * Register the user interface to be notified when new reply received
     *
     * @param c the Console instance to be registered
     */
    public void registerUI(Console c) {
        console = c;
    }

    public static String[] getNeighbors() {
        return addresses;
    }

    public static String getLocalAddress() {
        return localhost;
    }

    public static Console getConsole() {
        return console;
    }

    public static P2PSender[] getChannels() {
        return channels;
    }

    public static String getDFSLocation() {
        return DFSFileLocation;
    }

    public static String getDFSOutputLocation() {
        return DFSOutputLocation;
    }

    public static ArrayList<String> getLocalDFSFileList() {
        return dfsLocalFiles;
    }


    public static int getNumberOfReplica() {
        return numOfReplica;
    }

    public void leaveGroup() {
        heartbeatAdapter.leaveGroup();
    }

    public void joinGtoup() {
        heartbeatAdapter.joinGroup();
    }

    public static MembershipList getMembershipList() {
        return HeartbeatAdapter.getMembershipList();
    }

    public static int getNodeId(String host) {
        for (int i = 0; i < addresses.length; i++) {
            if (addresses[i].equals(host))
                return i + 1;
        }
        return -1;
    }

    /**
     * This will determine which node should this file goes to
     *
     * @param sdfsfilepath
     * @return
     */
    public int fileLocationHashing(String sdfsfilepath) {
        int sum = 0;
        for (int i = 0; i < sdfsfilepath.length(); i++) {
            sum += (int) sdfsfilepath.charAt(i) * 23; //WTF?
        }
        return sum % addresses.length;
    }


    public static void updateLocalFileList(String dfsfile) {
        dfsLocalFiles.add(dfsfile);
    }

    public static void deleteLocalFileList(String dfsfile) {
        dfsLocalFiles.remove(dfsfile);
    }

    public static boolean existLocalFileList(String dfsfile) {
        return dfsLocalFiles.contains(dfsfile);
    }

    public static synchronized void mergeFileStoreList(ArrayList<String> fileStore, String host) {
        for (String dfsfile : fileStore) {
            if (fileStoreLocation.containsKey(dfsfile)) {
                if (!fileStoreLocation.get(dfsfile).contains(host))
                    fileStoreLocation.get(dfsfile).add(host);
            } else {
                ArrayList<String> list = new ArrayList<String>();
                list.add(host);
                fileStoreLocation.put(dfsfile, list);
            }
        }
    }

    public static void updateFileStoreList() {
        fileStoreLocation.clear();
    }

    public static ArrayList<String> getFileStoreAddress(String dfsfile) {
        return fileStoreLocation.get(dfsfile);
    }

    public static Set<String> getAliveHosts() {
        return fileStoreLocation.keySet();
    }

    public static String getFileStoreString() {
        StringBuilder builder = new StringBuilder();
        Iterator it = fileStoreLocation.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String key = pair.getKey().toString();
            for (String item : (ArrayList<String>) pair.getValue()) {
                builder.append(key);
                builder.append(" : ");
                builder.append(item);
                builder.append("\n");
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return builder.toString();
    }

    public static ArrayList<String> checkFileStoreCorrect() {
        ArrayList<String> result = new ArrayList<String>();
        Iterator it = fileStoreLocation.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<String> list = (ArrayList<String>) pair.getValue();
            if (list.size() != getNumberOfReplica())
                result.add((String) pair.getKey());
            it.remove(); // avoids a ConcurrentModificationException
        }
        return result;
    }

}
