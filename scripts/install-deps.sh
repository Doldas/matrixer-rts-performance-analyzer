echo "INSTALLING JAVA 11"
sudo apt-get -y install openjdk-11-jdk
echo "export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64" >> ~/.bashrc
echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> ~/.bashrc
source ~/.bashrc
echo "JAVA_HOME set to $JAVA_HOME"
java -version
echo "! IF JAVA IS NOT JAVA-11 AS STANDARD PLEASE EXECUTE 'sudo update-alternatives --config java' AND CHOOSE JAVA 11 AS STANDARD !"
JAVA_TOOL_OPTIONS="-Djdk.attach.allowAttachSelf=true"
echo ""
echo "INSTALLING JUNIT AND JUNIT5"
sudo apt-get -y install junit
sudo apt-get -y install junit5
junit -v
echo ""
echo "INSTALLING GRADLE"
sudo apt-get -y install gradle
echo ""
echo "INSTALLING MAVEN"
sudo apt-get -y install maven