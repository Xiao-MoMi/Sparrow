package net.momirealms.sparrow.common.feature.skull;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public enum DefaultSkulls implements Skull {
    STEVE("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmYxYjA0MWRhMDkxYjlmYzY2ZDczZWQ2NDJmNmYxYTljN2FiNmFhMzBkY2RjZDNlODllZGI1YjAzMGI3YzY1MiJ9fX0="),
    ALEX("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZhY2QwNmU4NDgzYjE3NmU4ZWEzOWZjMTJmZTEwNWViM2EyYTQ5NzBmNTEwMDA1N2U5ZDg0ZDRiNjBiZGZhNyJ9fX0="),
    ARI("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGMwNWFiOWUwN2IzNTA1ZGMzZWMxMTM3MGMzYmRjZTU1NzBhZDJmYjJiNTYyZTliOWRkOWNmMjcxZjgxYWE0NCJ9fX0="),
    KAI("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTVjZGMzMjQzYjIxNTNhYjI4YTE1OTg2MWJlNjQzYTRmYzFlM2MxN2QyOTFjZGQzZTU3YTdmMzcwYWQ2NzZmMyJ9fX0="),
    NOOR("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTBlNzVjZDQyOWJhNjMzMWNkMjEwYjliZDE5Mzk5NTI3ZWUzYmFiNDY3YjVhOWY2MWNiOGEyN2IxNzdmNjc4OSJ9fX0="),
    SUNNY("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTNiZDE2MDc5Zjc2NGNkNTQxZTA3MmU4ODhmZTQzODg1ZTcxMWY5ODY1ODMyM2RiMGY5YTYwNDVkYTkxZWU3YSJ9fX0="),
    ZURI("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVkZGRiNDFkY2FmZWY2MTZlOTU5YzI4MTc4MDhlMGJlNzQxYzg5ZmZiZmVkMzkxMzRhMTNlNzViODExODYzZCJ9fX0="),
    EFE("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmVjZTcwMTdiMWJiMTM5MjZkMTE1ODg2NGIyODNiOGI5MzAyNzFmODBhOTA0ODJmMTc0Y2NhNmExN2U4ODIzNiJ9fX0="),
    MAKENA("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2NiM2JhNTJkZGQ1Y2M4MmMwYjA1MGMzZjkyMGY4N2RhMzZhZGQ4MDE2NTg0NmY0NzkwNzk2NjM4MDU0MzNkYiJ9fX0=");

    private final String base64;

    DefaultSkulls(String base64) {
        this.base64 = base64;
    }

    @NotNull
    @Override
    public CompletableFuture<String> getBase64() {
        return CompletableFuture.completedFuture(base64);
    }
}
