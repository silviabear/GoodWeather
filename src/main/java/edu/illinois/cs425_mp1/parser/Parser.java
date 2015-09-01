package edu.illinois.cs425_mp1.parser;
import edu.illinois.cs425_mp1.types.Request;

public interface Parser {
    String parse(Request body);
}
