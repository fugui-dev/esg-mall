package org.example.merchant.util;

import org.springframework.stereotype.Component;

import java.util.Random;



public class PasswordGeneratorUtil {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL = "!@#$%&*";
    private static final String PASSWORD_ALLOW = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL;



    public static String generateRandomPassword(int length) {

        if (length < 8) throw new IllegalArgumentException("密码长度至少8位");

        Random random = new Random();

        StringBuilder password = new StringBuilder(length);

        // 确保至少包含1个小写字母、1个大写字母、1个数字、1个特殊字符
        password.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
        password.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
        password.append(NUMBER.charAt(random.nextInt(NUMBER.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // 填充剩余字符
        for (int i = 4; i < length; i++) {
            password.append(PASSWORD_ALLOW.charAt(random.nextInt(PASSWORD_ALLOW.length())));
        }

        // 打乱顺序
        char[] arr = password.toString().toCharArray();
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }

        return new String(arr);
    }
}
