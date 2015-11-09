git config --global credential.helper cache
rm -rf dfs/*
rm -rf output/*
git pull origin master
export MAVEN_OPTS=-Xms1024m
mvn install
mvn -e exec:java -Dexec.mainClass="edu.illinois.cs425_mp1.ui.Console"
