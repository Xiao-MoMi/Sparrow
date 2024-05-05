package net.momirealms.sparrow.common.util;

import java.math.BigInteger;
import java.util.UUID;

public class UUIDUtils {

    public static UUID fromUnDashedUUID(String id) {
        return id == null ? null : new UUID(
                new BigInteger(id.substring(0, 16), 16).longValue(),
                new BigInteger(id.substring(16, 32), 16).longValue()
        );
    }

    public static String toUnDashedUUID(UUID uuid) {
        return uuid.toString().replace("-", "");
    }
}
