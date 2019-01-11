package com.imdb.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ResponseUtil {

    public static HashMap listDecorator(ArrayList lists , HashMap attributes) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
        Date now = new Date();
        String nowString = sdf.format(now);

        HashMap response = new HashMap();
        if (lists != null) {
            String key = "lists";
            if (attributes != null && attributes.get("listsKey") != null) {
                key = (String)attributes.get("listsKey");
                attributes.remove("listsKey");
            }
            response.put(key, lists);
            response.put("size", lists.size());
        }
        response.put("timestamp", nowString);
        if (attributes != null) {
            response.putAll(attributes);
        }

        return response;
    }
}
