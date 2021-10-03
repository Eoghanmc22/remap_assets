package com.mcecraft.assets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        String version = args[1];
        String rootDir = args[0];

        Gson gson = new GsonBuilder().create();

        try {
            FileReader fileReader = new FileReader(rootDir + "assets/indexes/" + version + ".json");

            JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);

            fileReader.close();


            JsonObject objects = jsonObject.get("objects").getAsJsonObject();
            Path out = Path.of("remapped_"+version);
            if (Files.exists(out)) {
                System.out.println("Output already exists");
                System.exit(1);
            }

            objects.entrySet().forEach(entry -> {
                String path = entry.getKey();
                String hash = entry.getValue().getAsJsonObject().get("hash").getAsString();

                Path sourceDir = Path.of(rootDir + "assets/objects/" + hash.substring(0, 2));
                Path destination = out.resolve(path);

                try {
                    Path source = Files.list(sourceDir).filter(path1 -> path1.getFileName().startsWith(hash)).findAny().get();
                    Files.createDirectories(destination.getParent());
                    Files.copy(source, destination);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
