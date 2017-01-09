package sk.hackcraft.als.utils;

import java.io.*;

/**
 * Class for loading and saving ini files.
 */
public class IniFileConfig {

    /**
     * Returns config class representing specified ini file.
     *
     * @param iniFile file to load
     * @return config representing ini file
     * @throws IOException if it's not possible to load or parse ini file
     */
    public MemoryConfig load(File iniFile) throws IOException {
        MemoryConfig config = new MemoryConfig();

        String actualSection = null;

        try (FileReader fileReader = new FileReader(iniFile); BufferedReader reader = new BufferedReader(fileReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // skip empty lines
                if (line.trim().length() == 0) {
                    continue;
                }

                // skip comment lines
                if (line.charAt(0) == ';') {
                    continue;
                }

                // found section
                if (line.charAt(0) == '[') {
                    actualSection = line.substring(1, line.length() - 1);

                    MemoryConfig.MemorySection section = new MemoryConfig.MemorySection(actualSection);
                    config.addSection(section);
                    continue;
                }

                // parsing key value pairs
                String[] tokens = line.split("=");

                String key = tokens[0].trim();

                String value;
                if (tokens.length > 1) {
                    value = tokens[1].trim();
                } else {
                    value = "";
                }

                MemoryConfig.MemoryPair pair = new MemoryConfig.MemoryPair(key, value);
                config.getSection(actualSection).addPair(pair);
            }
        }

        return config;
    }

    /**
     * Save config to specified ini file.
     *
     * @param config          config to save
     * @param destinationPath destination ini file
     * @throws IOException if it's not possible to save config
     */
    public void save(Config config, String destinationPath) throws IOException {
        File outputFile = new File(destinationPath);

        try (FileWriter fileWriter = new FileWriter(outputFile); BufferedWriter writer = new BufferedWriter(fileWriter)) {
            boolean firstSection = true;

            for (Config.Section section : config.getAllSections()) {
                if (firstSection) {
                    firstSection = false;
                } else {
                    writer.newLine();
                }

                writer.write("[" + section.getName() + "]");
                writer.newLine();

                for (Config.Pair pair : section.getAllPairs()) {
                    writer.write(pair.getKey() + "=" + pair.getStringValue());
                    writer.newLine();
                }
            }
        }
    }
}
