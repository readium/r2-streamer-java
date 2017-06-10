package org.readium.r2_streamer.model.publication.SMIL;

import java.io.Serializable;

/**
 * Created by gautam chibde on 23/5/17.
 */

public class Clip implements Serializable {
    private static final long serialVersionUID = -3313414920068632537L;

    public String relativeUrl;

    public double start;

    public double end;

    public double duration;

    public Clip() {
    }

    @Override
    public String toString() {
        return "Clip{" +
                "relativeUrl='" + relativeUrl + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", duration=" + duration +
                '}';
    }
}
