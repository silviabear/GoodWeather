package edu.illinois.cs425_mp1.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CollectedDataWriter {
	
	public synchronized static void writeToLog(String line) throws IOException {
		Path filePath = Paths.get("all.log");
		if (!Files.exists(filePath)) {
		    Files.createFile(filePath);
		}
		Files.write(filePath, line.getBytes(), StandardOpenOption.APPEND);
	}
	
}