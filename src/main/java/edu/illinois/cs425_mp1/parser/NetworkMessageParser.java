package edu.illinois.cs425_mp1.parser;

import java.io.IOException;

import edu.illinois.cs425_mp1.adapter.Adapter;
import edu.illinois.cs425_mp1.io.CollectedDataWriter;
import edu.illinois.cs425_mp1.io.ShellExecutor;
import edu.illinois.cs425_mp1.types.Command;
import edu.illinois.cs425_mp1.types.FileRequest;
import edu.illinois.cs425_mp1.types.Reply;
import edu.illinois.cs425_mp1.types.Request;

import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Receive the request from network and parse the execution results.
 *
 * @author silvia
 */
public class NetworkMessageParser {
    /**
     * @param request
     * @return the wrapped reply with timestamp and reply message
     */
    private static Logger log = LogManager.getLogger("parserLogger");

    public static Reply acceptNetworkRequest(Request request) {
        Command c = request.getCommand();
        if (c == Command.GREP) {
            log.trace("receive grep request");
            return new Reply(ShellExecutor.execute(request.getBody()),
                    request.getRequestId(),
                    Adapter.getLocalAddress(),
                    Command.GREP
            );
        }
        return null;
    }

    /**
     * This method handles all the file request, and reply back (if possible)
     *
     * @param ctx
     * @param req
     */
    public static void acceptNetworkFileRequest(ChannelHandlerContext ctx, FileRequest req) {
        FileRequest frequest = (FileRequest) req;
        Command c = frequest.getCommand();
        if (c == Command.PUT) {
            log.trace("receive PUT command");
            //StringBuilder contains file
            //Body is the path
            String tgrpath = Adapter.getDFSLocation() + frequest.getBody();
            try {
                CollectedDataWriter.writeToFile(tgrpath, frequest.getBuffer());
            } catch (IOException e) {
                log.error("error on writing to " + tgrpath);
                log.trace("cannot write to local file, send error msg back");
                Command puterror = Command.PUTBACK;
                puterror.setCmd("Put failed: " + Adapter.getLocalAddress());
                ctx.writeAndFlush(new FileRequest(puterror, ""));
                return;
            }
            //TODO: update local tracker
//            Adapter.updateFileMeta(frequest.getBody(), Adapter.getLocalAddress());
            Adapter.updateLocalFileList(frequest.getBody());
            ctx.writeAndFlush(new FileRequest(Command.PUTBACK, ""));
        } else if (c == Command.PUTBACK) {
            log.trace("receive PUTBACK command");
//            Adapter.getConsole().print(c.toString());
        } else if (c == Command.GET) {
            log.trace("receive GET command for " + frequest.getBody());
            //Body is the path
            //StringBuilder is empty
            String tgrpath = Adapter.getDFSLocation() + getCommandDFSPath(frequest.getBody());
            FileRequest reply = new FileRequest(Command.GETBACK, frequest.getBody());
            if (!Adapter.haveFile(getCommandDFSPath(frequest.getBody()))) {
                // not in ArrayList
                log.error("try get " + tgrpath + " but not such file");
                Command geterror = Command.GETBACK;
                geterror.setCmd("Get failed: " + Adapter.getLocalAddress());
                ctx.writeAndFlush(new FileRequest(geterror, ""));
                return;
            }
            try {
                reply.fillBufferOnLocal(tgrpath);
            } catch (IOException e) {
                // in list but not in disk
                log.error("try get " + tgrpath + " but not such file");
                Command geterror = Command.GETBACK;
                geterror.setCmd("Get failed: " + Adapter.getLocalAddress());
                ctx.writeAndFlush(new FileRequest(geterror, ""));
                return;
            }
            ctx.writeAndFlush(reply);
        } else if (c == Command.GETBACK) {
            log.trace("receive GETBACK command");
//            Adapter.getConsole().print(c.toString());
            if (c.toString().equals("done")) {
                try {
                    String tgrpath = getCommandLocalPath(frequest.getBody());
                    CollectedDataWriter.writeToFile(tgrpath, frequest.getBuffer());
                } catch (IOException e) {
                    Adapter.getConsole().print("Cannot write to local disk");
                    return;
                }
            }

        } else if (c == Command.DELETE) {
            // delete
            // body is the dfsfile path
            if (Adapter.haveFile(frequest.getBody())) {
                Adapter.deleteLocalFileList(frequest.getBody());
            }
        } else if (c == Command.QUERY) {
            //send local store list back
            FileRequest toSend = new FileRequest(Command.QUERYBACK, Adapter.getLocalAddress());
            toSend.fillStoreOnList();
            ctx.writeAndFlush(toSend);
        } else if (c == Command.QUERYBACK) {
            Adapter.mergeFileStoreList(frequest.getList(), frequest.getBody());
        } else {
            log.trace("Unknow file request " + frequest.getCommand());
        }

        return;

    }

    public synchronized static void acceptNetworkReply(Reply reply) {
        Command c = reply.getCommand();
        if (c == Command.GREP) {
            Adapter.getConsole().print("QUERY RESULT FROM: " + reply.getReplierAddress());
            Adapter.getConsole().print("FETCH TIME: " + reply.getTimeStamp());
            try {
                CollectedDataWriter.writeToLog(reply.getBody());
            } catch (IOException e) {
                log.trace("Write to disk fail");
            }
            int count = StringUtils.countMatches(reply.getBody(), "\n");
            Adapter.getConsole().print("QUERY RESULT COUNT: " + count);
        }
    }

    /**
     * This is the parser for get
     * of format "abc:def"
     * abc is location on DFS
     * def is location on local (to which should be write)
     *
     * @param body
     * @return
     */
    private static String getCommandDFSPath(String body) {
        return body.split(":")[0];
    }

    private static String getCommandLocalPath(String body) {
        return body.split(":")[1];
    }

}
