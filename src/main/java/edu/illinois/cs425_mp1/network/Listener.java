package edu.illinois.cs425_mp1.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * This class is the main listener class that sets up the server required config
 * @Author: Wesley
 */
public class Listener {

    static Logger log = LogManager.getLogger("networkLogger");

    private final int port;
    private ChannelFuture cf;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public Listener(int port) {
        this.port = port;
    }

    /**
     * Tell the listener to run
     */
    public void run(){
        log.trace("listener tries self-configuring on localhost @" + port);
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();

            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      public void initChannel(SocketChannel ch) throws Exception {
                                          ChannelPipeline p = ch.pipeline();
                                          p.addLast(
                                                  new ObjectEncoder(),
                                                  new ObjectDecoder(200000000,ClassResolvers.cacheDisabled(null)),
//                                                  new LoggingHandler(LogLevel.INFO),
                                                  new ListenerHandler());
                                      }
                                  }
                    );
        try {
        // Bind and start to accept incoming connections.
        log.trace("listener finished configuration, start listening @" + port);
        cf = b.bind(port).sync().channel().closeFuture().sync();
        
        } catch (InterruptedException e){
            log.error("binding failed due to interruption");
        } catch (Exception e){
            log.error("binding failed (address maybe in use)");
        } finally {
            log.trace("listener starts to shutdown");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();

        }
        log.trace("shutdown complete");
    }

    /**
     * Forcefully shutdown
     */
    public void close(){
        try {
            log.trace("listener tries forcefully shutdown");
            cf.channel().close().sync();
        } catch (InterruptedException e){
            log.error("exception caught during shutdown listener");
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        log.trace("shutdown complete");

    }
}
