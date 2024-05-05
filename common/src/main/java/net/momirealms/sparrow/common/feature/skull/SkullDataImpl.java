package net.momirealms.sparrow.common.feature.skull;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record SkullDataImpl(
        String owner,
        UUID uuid,
        String textureBase64
) implements SkullData {

    @NotNull
    @Override
    public String getTextureBase64() {
        return textureBase64;
    }

    @NotNull
    @Override
    public String getOwner() {
        return owner;
    }

    @NotNull
    @Override
    public UUID getUUID() {
        return uuid;
    }

    public static class Builder implements SkullData.Builder {

        private String owner = SkullData.defaultOwner;
        private String textureBase64 = SkullData.defaultTextureBase64;
        private UUID uuid = SkullData.defaultUUID;

        @Override
        public SkullData.Builder textureBase64(String base64) {
            this.textureBase64 = base64;
            return this;
        }

        @Override
        public SkullData.Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        @Override
        public SkullData.Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        @Override
        public SkullData build() {
            return new SkullDataImpl(owner, uuid, textureBase64);
        }
    }
}
