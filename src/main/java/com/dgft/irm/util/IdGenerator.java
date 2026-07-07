package com.dgft.irm.util;

import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {
    }

    public static String next() {
        return UUID.randomUUID().toString();
    }
}