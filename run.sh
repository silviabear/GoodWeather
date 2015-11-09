git config --global credential.helper cache
rm -rf dfs/*
rm -rf output/*
git pull origin master
export MAVEN_OPTS=-Xmx1024m
mvn install
mvn -e exec:java -Xms500m -Xmx1024m -Dexec.mainClass="edu.illinois.cs425_mp1.ui.Console"
