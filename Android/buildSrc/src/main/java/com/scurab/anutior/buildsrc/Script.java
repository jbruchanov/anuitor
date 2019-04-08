package com.scurab.anutior.buildsrc;

import com.google.gson.Gson;
import com.scurab.extractorbuilder.ExtractorsBuilder;
import com.scurab.extractorbuilder.RegisterBuilder;
import com.scurab.extractorbuilder.Structure;
import com.squareup.kotlinpoet.FileSpec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Script {

    public static void generateClasses(String targetPackage,
                                       String filePath,
                                       String targetFolderPath) throws FileNotFoundException {
        Gson gson = new Gson();
        File file = new File(filePath);
        ExtractorsBuilder builder = new ExtractorsBuilder();
        Structure structure = gson.fromJson(new FileReader(file), Structure.class);
        Map<String, String> extractors = new HashMap<>();
        structure.allItems().forEach((key, structureItem) -> {
            FileSpec fileSpec = builder.build(key, structureItem, targetPackage);
            try {
                fileSpec.writeTo(new File(targetFolderPath));
                extractors.put(key, String.format("%s.%s", fileSpec.getPackageName(), fileSpec.getName()));
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
        });

        try {
            String registerCode = new RegisterBuilder().build(extractors, targetPackage);
            File registerFile = new File(targetFolderPath + "/" + targetPackage.replace('.', '/') + "/ExtractorsRegister.kt");
            FileWriter fileWriter = new FileWriter(registerFile);
            fileWriter.write(registerCode);
            fileWriter.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
