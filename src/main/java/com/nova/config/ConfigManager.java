package com.nova.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nova.NovaClient;
import com.nova.module.Module;
import com.nova.module.ModuleManager;
import com.nova.setting.Setting;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private ConfigManager() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("novaclient.json");
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            for (Module m : ModuleManager.all()) {
                JsonObject o = new JsonObject();
                if (m.toggleable) {
                    o.addProperty("enabled", m.isEnabled());
                    o.addProperty("key", m.key);
                }
                JsonObject s = new JsonObject();
                for (Setting st : m.settings) s.add(st.name, st.save());
                o.add("settings", s);
                root.add(m.name, o);
            }
            Files.writeString(file(), GSON.toJson(root));
        } catch (Exception e) {
            NovaClient.LOGGER.error("Could not save config", e);
        }
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        try {
            JsonObject root = JsonParser.parseString(Files.readString(f)).getAsJsonObject();
            for (Module m : ModuleManager.all()) {
                if (!root.has(m.name)) continue;
                JsonObject o = root.getAsJsonObject(m.name);
                if (m.toggleable) {
                    if (o.has("enabled")) m.loadEnabled(o.get("enabled").getAsBoolean());
                    if (o.has("key")) m.key = o.get("key").getAsInt();
                }
                if (o.has("settings")) {
                    JsonObject s = o.getAsJsonObject("settings");
                    for (Setting st : m.settings) if (s.has(st.name)) st.load(s.get(st.name));
                }
            }
        } catch (Exception e) {
            NovaClient.LOGGER.error("Could not read config, using defaults", e);
        }
    }
}
