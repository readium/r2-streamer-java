package com.readium.r2_streamer.model.publication.SMIL;

import org.w3c.dom.Element;

/**
 * Created by gautam chibde on 23/5/17.
 */

public final class SMILParser {

    //Describes the different time string format of the SMIL tags
    private enum SMILTimeFormat {
        splitMonadic,
        splitDyadic,
        splitTriadic,
        millisecond,
        second,
        hour,
    }

    /**
     * Converts a smile time string into seconds String.
     *
     * @param time The smile time String.
     * @return The converted value in Seconds as String.
     */
    public static String smilTimeToSeconds(String time) {
        if (time.contains("h")) {
            return convertToSeconds(time, SMILTimeFormat.hour);
        } else if (time.contains("s")) {
            return convertToSeconds(time, SMILTimeFormat.second);

        } else if (time.contains("ms")) {
            return convertToSeconds(time, SMILTimeFormat.millisecond);
        } else {
            int count = time.split(":").length;
            switch (count) {
                case 1:
                    return convertToSeconds(time, SMILTimeFormat.splitMonadic);
                case 2:
                    return convertToSeconds(time, SMILTimeFormat.splitDyadic);
                case 3:
                    return convertToSeconds(time, SMILTimeFormat.splitTriadic);
                default:
                    return ""; // Should return null?
            }
        }
    }

    /**
     * Convert the smileTime to the equivalent in seconds given it's type.
     *
     * @param time The SMILTime String.
     * @param type type format of smileTime
     * @return The converted value in Seconds as String.
     */
    private static String convertToSeconds(String time, SMILTimeFormat type) {
        double seconds = 0.0;
        switch (type) {
            case hour:
                double ms = Double.parseDouble(time.replaceAll("ms", ""));
                return String.valueOf(ms / 1000.0);
            case second:
                return time.replaceAll("s", "");
            case millisecond:
                String[] hourMin = time.split(time.replaceAll("h", ""));
                double hrToSec = Double.parseDouble(hourMin[0]) * 3600.0;
                double minToSec = Double.parseDouble(hourMin[1]) * 0.6 * 60.0;
                return String.valueOf(hrToSec + minToSec);
            case splitMonadic:
                return time;
            case splitDyadic:
                String[] minSec = time.split(":");
                seconds += Double.parseDouble(minSec[0]) * 60.0;
                seconds += parseSeconds(time);
                return String.valueOf(seconds);
            case splitTriadic:
                String[] hourMinSec = time.split(":");

                seconds += (Double.parseDouble(hourMinSec[0])) * 3600.0;

                seconds += (Double.parseDouble(hourMinSec[1])) * 60.0;

                seconds += parseSeconds(hourMinSec[2]);
                return String.valueOf(seconds);
            default:
                return "";
        }
    }

    /**
     * Parse the <audio> XML element, children of <par> elements.
     *
     * @param element The audio XML element.
     * @return The formatted string representing the data.
     */
    public static String parseAudio(Element element) {
        String audio, clipBegin, clipEnd;
        if (element.hasAttribute("src")) {
            audio = element.getAttribute("src");
        } else {
            return null;
        }
        if (element.hasAttribute("clipBegin")) {
            clipBegin = element.getAttribute("clipBegin");
        } else {
            return null;
        }
        if (element.hasAttribute("clipEnd")) {
            clipEnd = element.getAttribute("clipEnd");
        } else {
            return null;
        }
        /// Clean relative path elements "../"
        if (audio.startsWith("../")) {
            audio = audio.substring(3, audio.length());
        }
        return audio +
                "#t=" +
                smilTimeToSeconds(clipBegin) +
                "," +
                smilTimeToSeconds(clipEnd);
    }

    /**
     * Return the seconds double value from a possible SS.MS format.
     *
     * @param time The seconds String.
     * @return The translated Double value.
     */
    private static double parseSeconds(String time) {
        String[] sec = time.split(":");
        double seconds;
        if (sec.length == 2) {
            seconds = Double.parseDouble(sec[0]);
            seconds += (Double.parseDouble(sec[1])) / 1000.0;
        } else {
            seconds = Double.parseDouble(time);
        }
        return seconds;
    }
}
