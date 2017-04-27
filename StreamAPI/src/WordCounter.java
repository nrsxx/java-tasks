import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;


public class WordCounter {

    private final File file;

    private WordCounter(String name) {
        file = new File(name);
    }

    private static Map<String, Integer> MyAccumulator(Map<String, Integer> previous, List<String> next) {

        return WordCounter.MyCombiner(previous, next.stream().filter((w) -> w.length() > 0)
                .collect(Collectors.groupingBy(String::toLowerCase, Collectors.summingInt((w) -> 1))));
    }

    private static Map<String, Integer> MyCombiner(Map<String, Integer> firstMap, Map<String, Integer> secondMap) {
        for (String key : secondMap.keySet()) {
            if (firstMap.containsKey(key)) {
                firstMap.put(key, firstMap.get(key) + secondMap.get(key));
            } else {
                firstMap.put(key, secondMap.get(key));
            }
        }
        return firstMap;
    }

    private Map<String, Integer> count() throws IOException {

        return Files.lines(file.toPath())
                .map((l) -> Arrays.asList(l.split("[\\p{Punct}\\s…«»—]")))
                .collect(HashMap::new,
                WordCounter::MyAccumulator,
                WordCounter::MyCombiner);
    }


    public static void main(String... args) {
        File file = new File("vs.txt");
        Map<String, Integer> map = new HashMap<>();
        try {
            Scanner scanner = new Scanner(file);
            ArrayList<String> strings = new ArrayList<>();
            while (scanner.hasNext()) {
                strings.add(scanner.nextLine());
            }

            for (String string : strings) {
                String[] words = string.toLowerCase().split("[\\p{Punct}\\s…«»—]");
                for (String word : words) {
                    if (map.containsKey(word)) {
                        map.put(word, map.get(word) + 1);
                    } else if (!word.equals("")) {
                        map.put(word, 1);
                    }
                }
            }

            WordCounter counter = new WordCounter("vs.txt");
            if (map.equals(counter.count())) {
                System.out.println("Works!");
            }

        } catch (IOException e) {
            System.out.println("Error");
        }
    }
}
