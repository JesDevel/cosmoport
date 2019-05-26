package com.space.model;

import java.util.Calendar;
import java.util.Date;

public class Model {
    public static Double getRating(Double speed, Boolean isUsed, Date prodDate) {
        Integer y0 = 3019;
        Calendar cal = Calendar.getInstance();
        cal.setTime(prodDate);
        Integer y1 = cal.get(Calendar.YEAR);
        Double rating  = (80 * speed * (isUsed ? 0.5 : 1.0)) / (y0 - y1 + 1);
        return Math.round(rating * 100) / 100.0d;
    }
}