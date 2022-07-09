package com.kk.util;


import java.util.Random;

public class ValidateCodeUtils {

    public static String generateValidateCode(int Num) {
        String code = "";
        Random r = new Random();
        for (int i = 0; i < Num; i++) {
            code += r.nextInt(10);
        }
        return code;
    }
}
