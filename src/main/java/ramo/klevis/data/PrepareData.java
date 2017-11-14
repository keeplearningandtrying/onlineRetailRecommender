package ramo.klevis.data;


import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by klevis.ramo on 11/14/2017.
 */
public class PrepareData {

    private static final String ONLINE_RETAIL_XLSX = "Online Retail.xlsx";

    public List<Row> readRowsFromFile() throws IOException, URISyntaxException {
        return Files.readAllLines(getPath(ONLINE_RETAIL_XLSX), StandardCharsets.ISO_8859_1)
                .stream().parallel().skip(1).map(line -> {
                    String[] data = line.split(",");
                    return new Row(data[6], data[7], data[1], data[2], Integer.parseInt(data[3]), Double.parseDouble(data[5]));

                }).collect(Collectors.toList());
    }

    public List<User> transformRowsToUserAndItems(List<Row> rows) {
        HashMap<String, User> map = new HashMap<>();
        for (Row row : rows) {
            if (map.containsKey(row.getUserId())) {
                User user = map.get(row.getUserId());
                addItem(row, user);
            } else {
                User user = new User(row.getUserId(), row.getUserCountry());
                addItem(row, user);
            }
        }
        return new ArrayList<>(map.values());
    }

    private void addItem(Row row, User user) {
        user.addItem(new Item(row.getItemID(), row.getItemDescription(), row.getItemPrice()));
    }


    private Path getPath(String path) throws IOException, URISyntaxException {
        return getPath(this.getClass().getResource("/" + path).toURI());
    }

    private Path getPath(URI uri) throws IOException {
        Path start;
        try {
            start = Paths.get(uri);
        } catch (FileSystemNotFoundException e) {
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            FileSystems.newFileSystem(uri, env);
            start = Paths.get(uri);
        }
        return start;
    }
}