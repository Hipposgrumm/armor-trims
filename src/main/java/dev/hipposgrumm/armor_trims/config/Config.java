package dev.hipposgrumm.armor_trims.config;

import dev.hipposgrumm.armor_trims.Armortrims;
import net.minecraft.locale.Language;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

//? if forge {
import net.minecraftforge.fml.loading.FMLPaths;
//?} else {
/*import net.fabricmc.loader.api.FabricLoader;
*///?}

public class Config {
    private static final String[] OPTION_NAMES = {
            "New Smithing Table GUI",
            "Don't Consume Smithing Templates",
            "Disable Vanilla Netherite Upgrading",
            "Disable New Netherite Upgrading",
            "Allow Untrimming",
            "JEI/REI Enabled"
    };
    private static final boolean[] OPTION_DEFAULTS = {
            true,   // New Smithing Table GUI
            false,  // Don't Consume Smithing Templates
            true,   // Disable Vanilla Netherite Upgrading
            false,  // Disable New Netherite Upgrading
            true,   // Allow Untrimming
            true    // JEI Enabled
    };

    static final File file;
    static final Watcher watcher;
    private static final HashMap<String, Boolean> config = new HashMap<>();

    static {
        //? if fabric {
        /*Path path = FabricLoader.getInstance().getConfigDir();
         *///?} elif forge {
        Path path = FMLPaths.CONFIGDIR.get();
        //?}
        file = path.resolve(Armortrims.MODID + ".toml").toFile();

        watcher = new Watcher(file);
    }

    public static boolean enableNewSmithingGUI;
    public static boolean dontConsumeSmithingTemplates;
    public static boolean disableVanillaNetheriteUpgrade;
    public static boolean disableNetheriteUpgrade;
    public static boolean enableUntrimming;
    public static boolean enableJei;

    public static void registerConfig() {
        try {
            file.getParentFile().mkdirs();
            if (file.createNewFile()) {
                String contents = writeConfig(OPTION_DEFAULTS);
                changeConfig(contents);
            }
        } catch (SecurityException e) {
            Armortrims.LOGGER.warn("config/{} could not be created: No permission", file.getName());
        } catch (IOException e) {
            Armortrims.LOGGER.warn("config/{} could not be created!", file.getName());
        }

        watcher.start();

        refreshOptions();
    }

    static String writeConfig(boolean[] vals) {
        Language lang = Language.getInstance();
        Map<String, String> mine = new HashMap<>();

        try (InputStream file = Config.class.getResourceAsStream(String.format("/assets/%s/lang/en_us.json", Armortrims.MODID))) {
            if (file != null) Language.loadFromJson(file, mine::put);
        } catch (Exception ignored) {}

        Function<String, String> get = trans -> {
            if (lang.has(trans)) {
                return lang.getOrDefault(trans);
            } else {
                return mine.get(trans);
            }
        };

        String contents = "";
        contents += writeOption(OPTION_NAMES[0], vals[0], get.apply("gui.armor_trims.config.option.newgui.desc"));
        contents += writeOption(OPTION_NAMES[1], vals[1], get.apply("gui.armor_trims.config.option.consume_templates.desc"));
        contents += writeOption(OPTION_NAMES[2], vals[2], get.apply("gui.armor_trims.config.option.disable_vanilla.desc"));
        contents += writeOption(OPTION_NAMES[3], vals[3], get.apply("gui.armor_trims.config.option.disable_modded.desc"));
        contents += writeOption(OPTION_NAMES[4], vals[4], get.apply("gui.armor_trims.config.option.untrimming.desc"));
        contents += writeOption(OPTION_NAMES[5], vals[5], get.apply("gui.armor_trims.config.option.jei.desc"));
        return contents;
    }

    private static String writeOption(String name, Object defaultValue, String description) {
        String contents = "";
        if (!description.isEmpty()) contents += String.format("# %s\n", description);
        contents += String.format("\"%s\" = %s\n\n", name, defaultValue);
        return contents;
    }

    static void changeConfig(String contents) throws IOException {
        PrintWriter writer = new PrintWriter(file);
        writer.write(contents);
        writer.close();
    }

    private static void loadConfig() {
        config.clear();
        try {
            Scanner reader = new Scanner(file);
            for(int l=1;reader.hasNextLine();l++) {
                String entry = reader.nextLine().trim();
                if(!entry.isEmpty() && !entry.startsWith("#")) {
                    if (entry.startsWith("\"")) {
                        StringBuilder key = new StringBuilder();
                        boolean building = true;
                        for (char c:entry.toCharArray()) {
                            if (!building && c == '"') {
                                building = true;
                                break;
                            } else if (building) {
                                building = false;
                            } else {
                                key.append(c);
                            }
                        }
                        if (building) {
                            entry = entry.substring(key.length()+2);
                            int index = entry.indexOf('=');
                            if (entry.indexOf('=') >= 0) {
                                entry = entry.substring(index+1).trim();
                                config.put(key.toString(), Boolean.valueOf(entry));
                            } else {
                                Armortrims.LOGGER.warn("Syntax error in config/{}: no value found on line {} (missing (\"=\"))", file.getName(), l);
                            }
                        } else {
                            Armortrims.LOGGER.warn("Syntax error in config/{}: Missing closing quote mark on line {}", file.getName(), l);
                        }
                    } else {
                        Armortrims.LOGGER.warn("Syntax error in config/{}: Missing opening quote mark on line {}", file.getName(), l);
                    }
                }
            }
            refreshOptions();
        } catch (IOException ignored) {}
    }

    private static void refreshOptions() {
        enableNewSmithingGUI =           config.getOrDefault(OPTION_NAMES[0], OPTION_DEFAULTS[0]);
        dontConsumeSmithingTemplates =   config.getOrDefault(OPTION_NAMES[1], OPTION_DEFAULTS[1]);
        disableVanillaNetheriteUpgrade = config.getOrDefault(OPTION_NAMES[2], OPTION_DEFAULTS[2]);
        disableNetheriteUpgrade =        config.getOrDefault(OPTION_NAMES[3], OPTION_DEFAULTS[3]);
        enableUntrimming =               config.getOrDefault(OPTION_NAMES[4], OPTION_DEFAULTS[4]);
        enableJei =                      config.getOrDefault(OPTION_NAMES[5], OPTION_DEFAULTS[5]);
    }

    public static class Watcher extends TimerTask {
        private final File file;
        private long timeStamp;

        public Watcher(File file) {
            this.file = file;
        }

        public void start() {
            skipUpdate();
            new Timer().schedule(this,new Date(),1000);
        }

        public void skipUpdate() {
            this.timeStamp = file.lastModified();
        }

        public void run() {
            long timeStamp = file.lastModified();

            if (this.timeStamp != timeStamp) {
                loadConfig();
                Armortrims.LOGGER.info("Config {} was updated!", file.getName());
                this.timeStamp = timeStamp;
            }
        }
    }
}
