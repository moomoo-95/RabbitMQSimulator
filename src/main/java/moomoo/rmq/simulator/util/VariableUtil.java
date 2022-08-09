package moomoo.rmq.simulator.util;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Slf4j
public class VariableUtil {

    private static final Random random = new Random();

    private static final int ASCII_0 = 48;
    private static final int ASCII_Z = 90;

    private VariableUtil() {
        // nothing
    }

    /**
     * UUID를 생성하는 메서드
     */
    public static String createUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 현재 시간을 생성하는 메서드
     */
    public static String createCurrentDate(String format) {
        return new SimpleDateFormat(format).format(new Date(System.currentTimeMillis()));
    }

    /**
     * length 길이인 임의의 String 값을 생성하는 메서드
     */
    public static String createRandomString(int length) {
        if (length > 32) length = 32;
        else if (length < 1) length = 1;

        StringBuilder builder = new StringBuilder(length);
        for(int idx = 0; idx < length; idx++) {
            int randomChar = random.nextInt(ASCII_Z - ASCII_0) + ASCII_0;
            builder.append((char) randomChar);
        }
        return builder.toString();
    }

    /**
     * length 길이인 임의의 Int 값을 생성하는 메서드
     */
    public static String createRandomInt(int length) {
        if (length > 10) length = 10;
        else if (length < 1) length = 1;

        int overInt = (int) Math.pow(10, length);
        int underInto = (int) Math.pow(10, length-1.0);

        int randomChar = random.nextInt(overInt - underInto - 1) + underInto;

        return String.valueOf(randomChar);
    }
}
