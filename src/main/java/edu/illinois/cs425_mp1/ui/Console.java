package edu.illinois.cs425_mp1.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

import edu.illinois.cs425_mp1.types.FileRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.types.Command;
import edu.illinois.cs425_mp1.types.Request;

/**
 * This is the console
 * Created by Wesley on 8/30/15.
 */
public class Console {

    static Logger log = LogManager.getLogger("consoleLogger");
    private static Adapter adapter;
    //port for listener is consistent universally
    public static final int port = 6753;

    /**
     * Main entry
     *
     * @param args
     */
    public static void main(String[] args) {
        log.info("Start Console init...");
        Console c = new Console();
        adapter = new Adapter(port);
        adapter.registerUI(c);
        log.info("Console init finished");
        c.start();
    }

    /**
     * Start method
     */
    private void start() {
        String line = null;
        while (true) {
            System.out.println("Choose the num of operation:");
            System.out.println("1. grep. 2. list membership list 3. list self id 4. leave group 5. join group " +
                    "6. put sdfsfile 7. get sdfsfile 8. delete sdfsfile 9.store file 10. list sdfsfile 0. Exit");
            line = read();
            int num = parseNum(line);
            switch (num) {
                case -1:
                    System.out.println("Please enter a valid number.");
                    continue;
                case 0:
                    System.exit(0);
                case 1:
                    grep();
                    break;
                case 2:
                    listMembership();
                    break;
                case 3:
                    listSelfId();
                    break;
                case 4:
                    leaveGroup();
                    break;
                case 5:
                    joinGroup();
                    break;
                case 6:
                    putFile();
                    break;
                case 7:
                    getFile();
                    break;
                case 8:
                    deleteFile();
                    break;
                case 9:
                    storeFile();
                    break;
                case 10:
                    listFile();
                    break;
                default:
                    System.out.println("Invalid option");
                    continue;
            }
        }
    }

    /**
     * Helper for read
     *
     * @return
     */
    private String read() {
        try {
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(System.in));
            String input;
            if ((input = br.readLine()) != null) {
                return input;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Error";
    }

    /**
     * Integer parser
     *
     * @param line
     * @return
     */
    private static int parseNum(String line) {
        try {
            int num = Integer.valueOf(line);
            return num;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Define the behavior of put
     *
     * @return
     */
    private int putFile() {
        System.out.println("Enter 'localfilename' 'sdfsfilename'");
        String input = read();
        String[] filenames = input.replace("\n", "").split(" ");
        if (filenames.length != 2) {
            putFile();
        } else {
            String localfilename = filenames[0];
            String sdfsfilename = filenames[1];
            //Construct fileReuqst
            FileRequest tosend = new FileRequest(Command.PUT, sdfsfilename);
            try {
                tosend.fillBufferOnLocal(localfilename);
            } catch (IOException e) {
                System.out.println("Cannot find " + localfilename);
                return 0;
            }
            int nodeId = adapter.fileLocationHashing(sdfsfilename);
            int numOfReplica = adapter.getNumberOfReplica();
            for (int i = 0; i < numOfReplica; i++) {
                adapter.sendP2PRequest(tosend, (nodeId + i) % 7 + 1);
            }
        }
        return 0;
    }

    /**
     * Define the behavior of get
     *
     * @return
     */
    private int getFile() {
        System.out.println("Enter 'sdfsfilename' 'localfilename'");
        String input = read();
        String[] filenames = input.replace("\n", "").split(" ");
        if (filenames.length != 2) {
            getFile();
        } else {
            String localfilename = filenames[1];
            String sdfsfilename = filenames[0];
            String reqBody = sdfsfilename + ":" + localfilename;
            FileRequest tosend = new FileRequest(Command.GET, reqBody);

            //If the file requested stores in local
//            if (Adapter.existLocalFileList(sdfsfilename)) {
//                int selfId = Adapter.getNodeId(Adapter.getLocalAddress());
//                adapter.sendP2PRequest(tosend, selfId);
//                return 0;
//            }
//            ArrayList<String> hostsThatHaveFiles = Adapter.getFileStoreAddress(sdfsfilename);
//            if (hostsThatHaveFiles == null) {
//                Adapter.updateFileStoreList();
//                //check its right
//                tosend = new FileRequest(Command.QUERY, "");
//                adapter.sendBroadcastRequest(tosend);
//                try {
//                    Thread.sleep(2000);
//                } catch (Exception e) {
//                    Thread.currentThread().interrupt();
//                }
//                hostsThatHaveFiles = Adapter.getFileStoreAddress(sdfsfilename);
//            }
//            adapter.sendP2PRequest(tosend, Adapter.getNodeId(hostsThatHaveFiles.get(0)));
//        }
            int nodeId = adapter.fileLocationHashing(sdfsfilename);
            int numOfReplica = adapter.getNumberOfReplica();
            for (int i = 0; i < numOfReplica; i++) {
                adapter.sendP2PRequest(tosend, (nodeId + i) % 7 + 1);
            }

        }
        return 0;
    }

    /**
     * Delete a file
     *
     * @return
     */
    private int deleteFile() {
        System.out.println("Enter 'sdfsfilename'");
        String sdfsfilename = read().replace("\n", "");
        FileRequest tosend = new FileRequest(Command.DELETE, sdfsfilename);
        adapter.sendBroadcastRequest(tosend);
        return 0;
    }

    /**
     * Return what is stored on local machine
     *
     * @return
     */
    private int storeFile() {
        ArrayList<String> stores = adapter.getLocalDFSFileList();
        print("File stored at " + Adapter.getLocalAddress());
        for (String item : stores)
            print(item);
        return 0;
    }

    /**
     * List the entire dfs system
     *
     * @return
     */
    private int listFile() {
        System.out.println("Enter 'sdfsfilename'");
        String sdfsfilename = read().replace("\n", "");
        Adapter.updateFileStoreList();
        //check its right
        FileRequest tosend = new FileRequest(Command.QUERY, "");
        adapter.sendBroadcastRequest(tosend);
        wait(2000);
        //check all files have exactly 3 replica
        ArrayList<String> needsReplica = Adapter.checkFileStoreCorrect();
        log.trace("Getting needs replica " + needsReplica.size());

        if (needsReplica.size() == 0) {
            log.trace("print default");
//            print(Adapter.getFileStoreString());
            for (String host : Adapter.getFileStoreAddress(sdfsfilename))
                print(host);
            return 0;
        } else {
            for (String fileToBeReplicated : needsReplica) {
                log.trace(fileToBeReplicated + " needs replicate");
                ArrayList<String> currentCopyHosts = Adapter.getFileStoreAddress(fileToBeReplicated);
                log.trace(fileToBeReplicated + " has " + currentCopyHosts.size());
                String firstHost = currentCopyHosts.get(0);
                FileRequest getit = new FileRequest(Command.GET, fileToBeReplicated + ":tmp/" + fileToBeReplicated);
                adapter.sendP2PRequest(getit, Adapter.getNodeId(firstHost));
                wait(1000);
                log.trace("get done");
                FileRequest sendit = new FileRequest(Command.PUT, fileToBeReplicated);
                try {
                    sendit.fillBufferOnLocal(Adapter.getDFSOutputLocation() + "tmp/" + fileToBeReplicated);
                } catch (Exception e) {
                    print("cannot replicate");
                }
                int numOfCopies = Adapter.getNumberOfReplica() - currentCopyHosts.size();
                log.trace("needs to send " + numOfCopies);
                int originNodeId = adapter.fileLocationHashing(fileToBeReplicated);
                for (int i = 0; i < numOfCopies; i++) {
                    int newId = originNodeId + Adapter.getNumberOfReplica();
                    log.trace("send to " + newId);
                    adapter.sendP2PRequest(sendit, (newId + i) % 7 + 1);

                }
            }

        }
        //update the new store
        Adapter.updateFileStoreList();
        tosend = new FileRequest(Command.QUERY, "");
        adapter.sendBroadcastRequest(tosend);
        wait(2000);

        print(Adapter.getFileStoreString());
//        for (String host : Adapter.getFileStoreAddress(sdfsfilename))
//            print(host);
        return 0;
    }

    /**
     * Grep command
     *
     * @return
     */
    private int grep() {
        System.out.println("Please enter the argument after grep:");
        System.out.println("For example: \"grep -c blah trace.log\"");
        String line = read();
        String arg = line;
        System.out.println("Please enter the number of node, 0 if broadcast");
        line = read();
        Request request = new Request(Command.GREP, arg);
        int i = parseNum(line);
        if (i > 0) {
            adapter.sendP2PRequest(request, i);
        } else {
            adapter.sendBroadcastRequest(request);
        }
        return 0;
    }

    private int leaveGroup() {
        adapter.leaveGroup();
        return 0;
    }

    private int joinGroup() {
        adapter.joinGtoup();
        return 0;
    }

    private int listMembership() {
        System.out.println(Adapter.getMembershipList());
        return 0;
    }

    private int listSelfId() {
        System.out.println(Adapter.getMembershipList().getSelfId());
        return 0;
    }


    /**
     * Wait
     *
     * @param milliseconds
     */
    private void wait(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Callback for adapter when new message received
     *
     * @param s
     */
    public void print(String s) {
        System.out.println(s);
    }
}
