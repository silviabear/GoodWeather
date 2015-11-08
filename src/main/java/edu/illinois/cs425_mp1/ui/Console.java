package edu.illinois.cs425_mp1.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

    static Logger log = LogManager.getLogger("mainLogger");
    private static Adapter adapter;
    //port for listener is consistent universally
    public static final int port = 6753;

    public static void main(String[] args) {
        log.info("Start Console init...");
        Console c = new Console();
        adapter = new Adapter(port);
        adapter.registerUI(c);
        log.info("Console init finished");
        c.start();
    }

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

    private static int parseNum(String line) {
        try {
            int num = Integer.valueOf(line);
            return num;
        } catch (Exception e) {
            return -1;
        }
    }

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
            if (Adapter.existLocalFileList(sdfsfilename)) {
                int selfId = Adapter.getNodeId(Adapter.getLocalAddress());
                adapter.sendP2PRequest(tosend, selfId);
                return 0;
            }
            Adapter.updateFileStoreList();
            //check its right
            tosend = new FileRequest(Command.QUERY, "");
            adapter.sendBroadcastRequest(tosend);
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
            ArrayList<String> hostsThatHaveFiles = Adapter.getFileStoreAddress(sdfsfilename);
            if (hostsThatHaveFiles == null) {
                print("File not exist in system");
                return 0;
            }
            adapter.sendP2PRequest(tosend, Adapter.getNodeId(hostsThatHaveFiles.get(0)));
        }
        return 0;
    }

    private int deleteFile() {
        System.out.println("Enter 'sdfsfilename'");
        String sdfsfilename = read().replace("\n", "");
        FileRequest tosend = new FileRequest(Command.DELETE, sdfsfilename);
        adapter.sendBroadcastRequest(tosend);
        return 0;
    }

    private int storeFile() {
        ArrayList<String> stores = adapter.getLocalDFSFileList();
        print("File stored at " + Adapter.getLocalAddress());
        for (String item : stores)
            print(item);
        return 0;
    }

    private int listFile() {
        System.out.println("Enter 'sdfsfilename'");
        String sdfsfilename = read().replace("\n", "");
        Adapter.updateFileStoreList();
        //check its right
        FileRequest tosend = new FileRequest(Command.QUERY, "");
        adapter.sendBroadcastRequest(tosend);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        //check all files have exactly 3 replica

        print(Adapter.getFileStoreString());
        return 0;
    }

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
     * High level wrapper (TBD)
     *
     * @param f
     * @param sdfsfilename
     */
    private void sendFile(FileRequest f, String sdfsfilename) {

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
