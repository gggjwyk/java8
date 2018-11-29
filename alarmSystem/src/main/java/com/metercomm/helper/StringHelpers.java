package com.metercomm.helper;

import com.metercomm.exceptions.AlarmSyntaxException;

/**
 * @author wanghan
 * @create 2018-09-18 23:35
 * @desc
 **/
public class StringHelpers {

    public static String transformSemiColonSeparatedStringToJson(String str) {
        StringBuilder sb = new StringBuilder();

        sb.append("{");

        String[] splitBySemicolon = str.split(";");

        for (int i = 0; i < splitBySemicolon.length; i++) {
            String[] splitByComma = splitBySemicolon[i].split(",", 2);

            if (splitByComma.length < 2) {
                throw new AlarmSyntaxException("Cannot transform to json string");
            }

            sb.append( (i==0? "" : ",") + "'" + splitByComma[0] + "':'" + splitByComma[1] + "'");
        }

        sb.append("}");

        return sb.toString();

    }
}
