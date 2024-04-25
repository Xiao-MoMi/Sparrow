/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.momirealms.sparrow.common.dependency;

import net.momirealms.sparrow.common.dependency.relocation.Relocation;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The dependencies used by Sparrow.
 */
public enum Dependency {

    ASM(
            "org.ow2.asm",
            "asm",
            "9.1",
            "maven",
            "asm"
    ),
    ASM_COMMONS(
            "org.ow2.asm",
            "asm-commons",
            "9.1",
            "maven",
            "asm-commons"
    ),
    JAR_RELOCATOR(
            "me.lucko",
            "jar-relocator",
            "1.7",
            "maven",
            "jar-relocator"
    ),
    H2_DRIVER(
            "com.h2database",
            "h2",
            "2.2.224",
            "maven",
            "h2database"
    ),
    SQLITE_DRIVER(
            "org.xerial",
            "sqlite-jdbc",
            "3.45.3.0",
            "maven",
            "sqlite-jdbc"
    ),
    ADVENTURE_API(
            "com.github.Xiao-MoMi",
            "Adventure-Bundle",
            "4.16.0",
            "jitpack",
            "adventure-bundle",
            Relocation.of("adventure", "net{}kyori{}adventure"),
            Relocation.of("option", "net{}kyori{}option"),
            Relocation.of("examination", "net{}kyori{}examination")
    ),
    NBT_API(
            "de{}tr7zw",
            "item-nbt-api",
            "2.12.3",
            "codemc",
            "item-nbt-api",
            Relocation.of("nbtapi", "de{}tr7zw{}changeme{}nbtapi")
    ),
    CLOUD_CORE(
            "org{}incendo",
            "cloud-core",
            "2.0.0-beta.4",
            "maven",
            "cloud-core",
            Relocation.of("cloud", "org{}incendo{}cloud")
    ),
    CLOUD_BRIGADIER(
            "org{}incendo",
            "cloud-brigadier",
            "2.0.0-beta.5",
            "maven",
            "cloud-brigadier",
            Relocation.of("cloud", "org{}incendo{}cloud")
    ),
    CLOUD_SERVICES(
            "org{}incendo",
            "cloud-services",
            "2.0.0-beta.4",
            "maven",
            "cloud-services",
            Relocation.of("cloud", "org{}incendo{}cloud")
    ),
    CLOUD_BUKKIT(
            "org{}incendo",
            "cloud-bukkit",
            "2.0.0-beta.5",
            "maven",
            "cloud-bukkit",
            Relocation.of("cloud", "org{}incendo{}cloud")
    ),
    CLOUD_PAPER(
            "org{}incendo",
            "cloud-paper",
            "2.0.0-beta.5",
            "maven",
            "cloud-paper",
            Relocation.of("cloud", "org{}incendo{}cloud")
    ),
    BOOSTED_YAML(
            "dev{}dejvokep",
            "boosted-yaml",
            "1.3.4",
            "maven",
            "boosted-yaml",
            Relocation.of("boostedyaml", "dev{}dejvokep{}boostedyaml")
    ),
    INVENTORY_ACCESS(
            "xyz{}xenondevs{}invui",
            "inventory-access",
            "1.29",
            "xenondevs",
            "inventory-access",
            Relocation.of("inventoryaccess", "xyz{}xenondevs{}inventoryaccess")
    ),
    INVENTORY_ACCESS_NMS(
            "xyz{}xenondevs{}invui",
            "?",
            "1.29",
            "xenondevs",
            "?",
            Relocation.of("inventoryaccess", "xyz{}xenondevs{}inventoryaccess")
    );

    private final List<Relocation> relocations;
    private final String repo;
    private final String groupId;
    private final String version;
    private String artifactId;
    private String artifactSuffix;
    private String customArtifactName;

    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

    Dependency(String groupId, String artifactId, String version, String repo, String artifact) {
        this(groupId, artifactId, version, repo, artifact, new Relocation[0]);
    }

    Dependency(String groupId, String artifactId, String version, String repo, String artifact, Relocation... relocations) {
        this.artifactId = artifactId;
        this.artifactSuffix = "";
        this.groupId = groupId;
        this.version = version;
        this.relocations = new ArrayList<>(Arrays.stream(relocations).toList());
        this.repo = repo;
        this.customArtifactName = artifact;
    }

    public Dependency setCustomArtifactName(String customArtifactName) {
        this.customArtifactName = customArtifactName;
        return this;
    }

    public Dependency setArtifactID(String artifactId) {
        this.artifactId = artifactId;
        return this;
    }

    public Dependency setArtifactSuffix(String artifactSuffix) {
        this.artifactSuffix = artifactSuffix;
        return this;
    }

    private static String rewriteEscaping(String s) {
        return s.replace("{}", ".");
    }

    public String getFileName(String classifier) {
        String name = customArtifactName.toLowerCase(Locale.ROOT).replace('_', '-');
        String extra = classifier == null || classifier.isEmpty()
                ? ""
                : "-" + classifier;
        return name + "-" + this.version + extra + ".jar";
    }

    String getMavenRepoPath() {
        return String.format(MAVEN_FORMAT,
                rewriteEscaping(groupId).replace(".", "/"),
                rewriteEscaping(artifactId),
                version,
                rewriteEscaping(artifactId),
                version + artifactSuffix
        );
    }

    public List<Relocation> getRelocations() {
        return this.relocations;
    }

    /**
     * Creates a {@link MessageDigest} suitable for computing the checksums
     * of dependencies.
     *
     * @return the digest
     */
    public static MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public String getRepo() {
        return repo;
    }
}
