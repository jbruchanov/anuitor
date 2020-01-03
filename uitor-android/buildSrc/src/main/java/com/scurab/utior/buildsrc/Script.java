package com.scurab.utior.buildsrc;

import com.google.gson.Gson;
import com.scurab.extractorbuilder.ExtractorsBuilder;
import com.scurab.extractorbuilder.ReflectionHelperBuilder;
import com.scurab.extractorbuilder.RegisterBuilder;
import com.scurab.extractorbuilder.Structure;
import com.squareup.kotlinpoet.FileSpec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        final File outputDirFile = new File(targetFolderPath);
        final Path outputDirPath = Paths.get(outputDirFile.getAbsolutePath());
        structure.allItems().forEach((key, structureItem) -> {
            FileSpec fileSpec = builder.build(key, structureItem, targetPackage);
            try {
                //simple check to avoid overwriting already existing extractors files
                //for example FragmentExtractor, different packages for default and androidx
                Path outFile = outputDirPath.resolve(String.format("%s/%s.kt", fileSpec.getPackageName().replace('.', '/'), fileSpec.getName()));
                String preGenFileContent = null;
                if (Files.exists(outFile) && Files.size(outFile) > 0) {
                    preGenFileContent = new String(Files.readAllBytes(outFile));
                }
                fileSpec.writeTo(new File(targetFolderPath));
                if (preGenFileContent != null &&
                        !preGenFileContent.equals(new String(Files.readAllBytes(outFile)))) {
                    throw new IllegalStateException(String.format("%s already generated with different content!", outFile));
                }
                extractors.put(key, String.format("%s.%s", fileSpec.getPackageName(), fileSpec.getName()));
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
        });

        File registerFile = new File(targetFolderPath + "/" + targetPackage.replace('.', '/') + "/ExtractorsRegister.kt");
        writeTo(new RegisterBuilder().build(extractors, targetPackage), registerFile);

        String reflectionBuilderCode = new ReflectionHelperBuilder().build(structure, targetPackage);
        writeTo(reflectionBuilderCode, new File(targetFolderPath + "/" + targetPackage.replace('.', '/') + "/ReflectionHelper.java"));
    }

    private static void writeTo(String content, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
