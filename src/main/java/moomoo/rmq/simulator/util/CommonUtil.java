package moomoo.rmq.simulator.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtil {

    private CommonUtil() {
        // nothing
    }

    public static void trySleep(int msec) {
        try {
            if (msec < 0) msec = 0;
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            // Thread Pool에 interrupt 발생을 알림.
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("AppUtil.trySleep ", e);
        }
    }

    /**
     * n 이 Integer type 으로 변환될 수 있는지 여부를 반환하는 메서드
     */
    public static boolean isInteger(String n) {
        try {
            Integer.parseInt(n);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * n을 Integer type 으로 변환시키는 메서드
     * 변환 할 수 없을 경우 d 반환
     */
    public static int parseInteger(String n, int d) {
        return isInteger(n) ? Integer.parseInt(n) : d;
    }

    /**
     * n 이 long type 으로 변환될 수 있는지 여부를 반환하는 메서드
     */
    public static boolean isLong(String n) {
        try {
            Long.parseLong(n);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
