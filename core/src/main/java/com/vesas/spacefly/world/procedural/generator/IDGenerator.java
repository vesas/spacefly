package com.vesas.spacefly.world.procedural.generator;

public class IDGenerator {
    
    private static int currentId = 0;

    public static int getId()
    {
        return currentId++;
    }
}
