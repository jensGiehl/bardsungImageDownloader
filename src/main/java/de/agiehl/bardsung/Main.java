package de.agiehl.bardsung;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final String BASE_URL = "https://cardcreator.steamforged.com";

    private static final String HERO_ICON_BASE_URL = BASE_URL + "/icons/bardsungHero/";
    private static final String HERO_FRAME_BASE_URL = BASE_URL + "/frames/bardsungHero/";
    private static final String CHARACTERISTICS_BASE_URL = BASE_URL + "/characteristics/bardsungHero/";
    private static final String BACKGROUND_BASE_URL = BASE_URL + "/backgrounds/bardsungHero/";

    public static void main(String[] args) {
        List<String> allLinks = new ArrayList<>();

        for (Heros hero : Heros.values()) {
            String heroName = hero.name().toLowerCase();
            allLinks.add(HERO_ICON_BASE_URL + heroName + "_unselected.svg");
            allLinks.add(HERO_ICON_BASE_URL + heroName + "_selected.svg");

            for (Dice die : Dice.values()) {
                allLinks.add(HERO_FRAME_BASE_URL + heroName + "/BS-Element-Hero-Profile-" + StringUtils.capitalize(heroName) + "-" + die.name() + ".png");
            }

            for (Stats stat : Stats.values()) {
                allLinks.add(HERO_FRAME_BASE_URL + heroName + "/BS-Element-Hero-Profile-" + StringUtils.capitalize(heroName) + "-" + StringUtils.capitalize(stat.name().toLowerCase()) + ".png");
            }

            allLinks.add(CHARACTERISTICS_BASE_URL + heroName + "/right/BS-Element-Hero-Profile-" + StringUtils.capitalize(heroName) + "-Modifier-WIS-Right.png");
            allLinks.add(CHARACTERISTICS_BASE_URL + heroName + "/left/BS-Element-Hero-Profile-" + StringUtils.capitalize(heroName) + "-Modifier-CHA-Left.png");

            allLinks.add(BACKGROUND_BASE_URL + "BS-Hero-Profile-Card-BG-" + StringUtils.capitalize(heroName) + ".jpeg");
        }

        allLinks.addAll(loadLinksFromFile("/common.txt"));
        allLinks.addAll(loadLinksFromFile("/enemyBackgrounds.txt"));
        allLinks.addAll(loadLinksFromFile("/enemyCommon.txt"));
        allLinks.addAll(loadLinksFromFile("/enemyIcons.txt"));
        allLinks.addAll(loadLinksFromFile("/icons.txt"));

        allLinks.forEach(System.out::println);

        String folder = args.length >= 1 ? args[0] : "";
        allLinks.forEach(file -> Main.downloadFile(file, folder));
    }

    private static void downloadFile(String url, String folder) {
        try {
            String targetFilename = folder + Arrays.stream(url.split("/"))
                    .skip(3).map(path -> File.separator + path)
                    .collect(Collectors.joining());

            FileUtils.copyURLToFile(
                    new URL(url),
                    new File(targetFilename),
                    2000,
                    2000);
        } catch (IOException e) {
            System.err.println(url + " not found!");
        }
    }

    private static List<String> loadLinksFromFile(String filename) {
        URL url = Main.class.getResource(filename);
        try (Stream<String> stream = Files.lines(Path.of(url.toURI()))) {
            return stream.collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

}
