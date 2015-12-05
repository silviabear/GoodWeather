git pull origin master
mvn clean install
mvn -e exec:java -Dexec.mainClass="edu.illinois.cs425.g36.app1.Main" -Dexec.args="$1"
