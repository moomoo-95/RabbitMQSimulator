package moomoo.rmq.simulator.service;

import lombok.extern.slf4j.Slf4j;
import moomoo.rmq.rmqif.RmqManager;
import moomoo.rmq.simulator.command.CommandServer;
import moomoo.rmq.simulator.util.CommonUtil;

import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.*;

@Slf4j
public class ServiceManager {

    private static final class Singleton { private static final ServiceManager INSTANCE = new ServiceManager(); }

    private static final String SERVICE_NAME = "Rabbit MQ Simulator";

    // 프로세스 중복 실행 방지
    private File f;
    private FileChannel fileChannel;
    private FileLock lock;

    private CommandServer commandServer;

    private boolean isQuit = false;

    private ServiceManager() {
        // nothing
    }

    public static ServiceManager getInstance() {
        return Singleton.INSTANCE;
    }

    public void loop() {
        startService();

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            try {
                stopService();
                systemUnlock();
                log.error("\n============================================\n" +
                                "=== {} Process shutdown ===\n" +
                                "============================================\n", SERVICE_NAME);
            } catch (Exception e) {
                log.error("addShutdownHook ", e);
            }
        }));


        while (!isQuit) {
            CommonUtil.trySleep(1000);
        }
        log.debug("{} Process End", SERVICE_NAME);
    }

    private void startService() {
        systemLock();

//        RmqManager.getInstance().startRmq();

        commandServer = new CommandServer();
        commandServer.run();
    }

    public void stopService() {
        commandServer.stop();

//        RmqManager.getInstance().stopRmq();
        isQuit = true;
    }

    /**
     * @fn private void systemLock
     * @brief 시스템 동시 실행 방지를 위해 Lock 을 거는 메서드
     */
    private void systemLock(){
        String tmpDir=System.getProperty("java.io.tmpdir"); // /tmp/rmqSimulator.lock
        try {
            f = new File(tmpDir, System.getProperty("lock_file", "rmqSimulator.lock"));
            fileChannel = FileChannel.open(f.toPath(), CREATE, READ, WRITE);
            lock = fileChannel.tryLock();
            if (lock == null) {
                log.error("{} process already running", SERVICE_NAME);
                System.exit(1);
            } else {
                log.debug("{} systemLock ok.", SERVICE_NAME);
            }
        } catch (Exception e) {
            log.error("ServiceManager.systemLock",e);
        }
    }

    /**
     * @fn private void systemUnlock
     * @brief 시스템 동시 실행 방지를 위해 걸었던 Lock 을 해제하는 메서드
     */
    private void systemUnlock(){
        try {
            lock.release();
            fileChannel.close();
            Files.delete(f.toPath());
            log.warn("{} systemUnlock ok.", SERVICE_NAME);
        } catch (Exception e) {
            log.error("ServiceManager.systemUnlock ",e);
        }
    }
}
