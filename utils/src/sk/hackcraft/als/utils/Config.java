package sk.hackcraft.als.utils;

import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Interface representing configuration, divided by sections containing key
 * value pairs.
 */
public interface Config {

    /**
     * Check section availability.
     *
     * @param section name of section
     * @return true if section exists, false otherwise
     */
    boolean hasSection(String section);

    /**
     * Returns section.
     *
     * @param section name of section
     * @return requested section
     * @throws NoSuchElementException if section doesn't exists
     */
    Section getSection(String section);

    /**
     * Returns all sections.
     *
     * @return all sections
     */
    Set<? extends Section> getAllSections();

    /**
     * Interface representing configuration section.
     */
    interface Section {

        /**
         * Gets section name.
         *
         * @return section name
         */
        String getName();

        /**
         * Check if section contains pair with specified key.
         *
         * @param key specified key
         * @return true if section contains pair with specified key, false
         * otherwise
         */
        boolean hasPair(String key);

        /**
         * Gets specified pair.
         *
         * @param key pair key
         * @return specified pair
         */
        Pair getPair(String key);

        /**
         * Returns all pairs.
         *
         * @return all pairs
         */
        Set<? extends Pair> getAllPairs();
    }

    /**
     * Interface representing configuration key value pair.
     */
    interface Pair {

        /**
         * Gets pair key.
         *
         * @return pair key
         */
        String getKey();

        /**
         * Gets pair value as string.
         *
         * @return value as string
         */
        String getStringValue();

        /**
         * Gets pair value as array of strings. Delimiter for parsing value is
         * ','. Whitespaces before and after each value are trimmed.
         *
         * @return value as array of strings
         */
        String[] getStringValueAsArray();

        /**
         * Gets pair value as int.
         *
         * @return value as int
         * @throws NumberFormatException if it's not possible to convert value to int
         */
        int getIntValue();

        /**
         * Gets pair value as boolean
         *
         * @return value as boolean
         */
        boolean getBooleanValue();

        /**
         * Gets pair value as double.
         *
         * @return value as double
         * @throws NumberFormatException if it's not possible to convert value to double
         */
        double getDoubleValue();
    }
}
