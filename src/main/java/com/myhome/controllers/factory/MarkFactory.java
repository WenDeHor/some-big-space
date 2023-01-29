package com.myhome.controllers.factory;

import com.myhome.models.Mark;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MarkFactory {
    private static Map<String, Map<String, String>> mapFactory = new HashMap<>();
    private static List<Mark> marks = Arrays.asList(Mark.values());

    public static Mark getMarkFromFactory(String name, String mark) {
        setFactory();
        Map<String, String> stringStringMap = mapFactory.get(name);
        return Mark.valueOf(stringStringMap.get(mark));
    }

    private static void setFactory() {
        List<Mark> environment = marks.stream()
                .filter(list -> list.name().contains("ENVIRONMENT"))
                .collect(Collectors.toList());
        Map<String, String> collectENVIRONMENT = environment.stream()
                .collect(Collectors.toMap(Mark::getName, Mark::name));
        mapFactory.put("ENVIRONMENT", collectENVIRONMENT);

        List<Mark> characters = marks.stream()
                .filter(list -> list.name().contains("CHARACTERS"))
                .collect(Collectors.toList());
        Map<String, String> collectCHARACTERS = characters.stream()
                .collect(Collectors.toMap(Mark::getName, Mark::name));
        mapFactory.put("CHARACTERS", collectCHARACTERS);

        List<Mark> atmosphere = marks.stream()
                .filter(list -> list.name().contains("ATMOSPHERE"))
                .collect(Collectors.toList());
        Map<String, String> collectATMOSPHERE = atmosphere.stream()
                .collect(Collectors.toMap(Mark::getName, Mark::name));
        mapFactory.put("ATMOSPHERE", collectATMOSPHERE);

        List<Mark> plot = marks.stream()
                .filter(list -> list.name().contains("PLOT"))
                .collect(Collectors.toList());
        Map<String, String> collectPLOT = plot.stream()
                .collect(Collectors.toMap(Mark::getName, Mark::name));
        mapFactory.put("PLOT", collectPLOT);

        List<Mark> impression = marks.stream()
                .filter(list -> list.name().contains("IMPRESSION"))
                .collect(Collectors.toList());
        Map<String, String> collectIMPRESSION = impression.stream()
                .collect(Collectors.toMap(Mark::getName, Mark::name));
        mapFactory.put("IMPRESSION", collectIMPRESSION);
    }
}
