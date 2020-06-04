package hust.sqa.btl.ga;

import hust.sqa.btl.utils.ValueConfig;

import java.util.Random;


public class StringGenerator {
    public static Random randomGenerator = new Random();

    public String newString() {
        // tập các kí tự tạo chuỗi
        String AlphaNumericString = ValueConfig.CHARACTER_SET;

        // tạo StringBuffer
        StringBuilder sb = new StringBuilder(AlphaNumericString.length());
        int length = ValueConfig.MIN_STRING_LENGTH + randomGenerator.nextInt(ValueConfig.MAX_STRING_LENGTH - ValueConfig.MIN_STRING_LENGTH);
        for (int i = 0; i < length; i++) {
            // tạo một số ngẫu nhiên trong khoảng từ 0 đến AlphaNumericString.length()
            int index = (int) (AlphaNumericString.length() * Math.random());
            // thêm ký tự từng kí tự một vào cuối của sb
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    public String newString(int low, int up) {

        // tập các kí tự tạo chuỗi
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz" ;

        // tạo StringBuffer
        StringBuilder sb = new StringBuilder(AlphaNumericString.length());

        int numberChar = low + randomGenerator.nextInt(up - low + 1);
        for (int i = 0; i < numberChar; i++) {
            // tạo một số ngẫu nhiên trong khoảng từ 0 đến AlphaNumericString.length()
            int index = (int) (AlphaNumericString.length() * Math.random());
            // thêm ký tự từng kí tự một vào cuối của sb
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }
}