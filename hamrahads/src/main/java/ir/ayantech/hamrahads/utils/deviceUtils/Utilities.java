package ir.ayantech.hamrahads.utils.deviceUtils;

import android.util.Log;

/**
 * Created by pardis on 5/6/2017.
 */

public class Utilities {

    public String milli_to_time(long millisecond) {
        Log.i("iosjdgsdlnk", "" + millisecond);
        String hours = String.valueOf((int) (millisecond / (60 * 60 * 1000)));
        String minutes = String.valueOf(((int) (millisecond % (60 * 60 * 1000) / (60 * 1000))));
        String seconds = String.valueOf(((int) ((millisecond % (60 * 60 * 1000)) % (60 * 1000) / 1000)));
        if (Integer.parseInt(hours) < 10)
            hours = "0" + hours;
        if (Integer.parseInt(minutes) < 10)
            minutes = "0" + minutes;
        if (Integer.parseInt(seconds) < 10)
            seconds = "0" + seconds;
        if (Integer.parseInt(hours) > 0)
            return hours + ":" + minutes + ":" + seconds;
        else
            return minutes + ":" + seconds;
    }

    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        percentage = (((double) currentSeconds) / totalSeconds) * 100;
        return percentage.intValue();
    }

    public int progressToTime(int progress, long totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);
        return currentDuration * 1000;
    }
}
