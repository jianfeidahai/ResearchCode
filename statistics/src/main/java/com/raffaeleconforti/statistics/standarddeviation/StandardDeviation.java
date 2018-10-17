package com.raffaeleconforti.statistics.standarddeviation;

import com.raffaeleconforti.statistics.StatisticsMeasureAbstract;
import com.raffaeleconforti.statistics.mean.Mean;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 14/11/16.
 */
public class StandardDeviation extends StatisticsMeasureAbstract {

    private Mean mean = new Mean();

    @Override
    public double evaluate(Double val, double... values) {
        try {
            double avg = mean.evaluate(null, values);
            double sd = 0;
            for(int i = 0; i < values.length; i++) {
                sd += Math.pow((values[i] - avg), 2);
            }
            return Math.sqrt(sd / (values.length - 1));
        }catch (ArrayIndexOutOfBoundsException e) {

        }
        return 0;
    }

}
