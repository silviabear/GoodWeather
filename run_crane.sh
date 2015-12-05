git pull origin master
mvn clean install
mvn -X -e exec:java -Dexec.mainClass="edu.illinois.cs425.g36.app1.Main"
