package util;

import java.util.concurrent.TimeUnit;

import com.vesas.spacefly.DebugHelper;

public class FrameTime {
    public static long frametime;
    public static long glclear;
    public static long glenable;
    public static long cleartime;
    public static long visib;
    public static long features;
    public static long roomfeatures;
    public static long corridorfeatures;
    public static long featvisib;
    

    public static void initFrame() {
        visib = 0;
        features = 0;
        roomfeatures = 0;
        corridorfeatures = 0;
        featvisib = 0;
    }

    public static String asString() {
        
        DebugHelper.calcGCStats();

        String str = String.format("(micros) frametime: %-8s" + 
            " clear: %-7s" + 
            " clearglclear: %-7s" + 
            " clearglenable: %-7s" + 
            " visib: %-6s" + 
            " features: %-7s" + 
            " featvisib: %-6s" +
            " roomfeatures: %-6s" +
            " corridorfeatures: %-6s" + 
            " | GC runs: %-4s" + 
            " GC time: %-4s", 
            TimeUnit.NANOSECONDS.toMicros(frametime),
            TimeUnit.NANOSECONDS.toMicros(cleartime),
            TimeUnit.NANOSECONDS.toMicros(glclear),
            TimeUnit.NANOSECONDS.toMicros(glenable),
            TimeUnit.NANOSECONDS.toMicros(visib),
            TimeUnit.NANOSECONDS.toMicros(features),
            TimeUnit.NANOSECONDS.toMicros(featvisib),
            TimeUnit.NANOSECONDS.toMicros(roomfeatures),
            TimeUnit.NANOSECONDS.toMicros(corridorfeatures),
            DebugHelper.totalGarbageCollections,
            DebugHelper.garbageCollectionTime
            );
        return str;
    }
}
