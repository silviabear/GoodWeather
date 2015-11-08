rm dfs/*
git pull origin master
mvn install
mvn -e exec:java -Dexec.mainClass="edu.illinois.cs425_mp1.ui.Console"
