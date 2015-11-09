git config --global credential.helper cache
rm -rf dfs/*
rm -rf output/*
git pull origin master
mvn install
mvn -e exec:java -Xmx1024m -Dexec.mainClass="edu.illinois.cs425_mp1.ui.Console"
