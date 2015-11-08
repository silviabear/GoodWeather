git fetch new master
git reset --hard FETCH_HEAD
git clean -df
mvn install
mvn -e exec:java -Dexec.mainClass="edu.illinois.cs425_mp1.ui.Console"
