echo "CREATING DIRECTORY maven_plugins"
mkdir maven_plugins
cd maven_plugins
echo ""
echo ""
echo "DOWNLOADING AND INSTALLING STARTS"
git clone https://github.com/TestingResearchIllinois/starts
cd starts
JAVA_TOOL_OPTIONS="-Djdk.attach.allowAttachSelf=true" mvn install
echo ""
echo "ADD FOLLOWING TO pom.xml IN EACH PROJECT WERE STARTS SHOULD BE USED:"
echo "<build>
  <plugins>
    <plugin>
      <groupId>edu.illinois</groupId>
      <artifactId>starts-maven-plugin</artifactId>
      <version>1.5-SNAPSHOT</version>
    </plugin>
  </plugins>
</build>"
echo ""
echo ""
echo "DOWNLOADING AND INSTALLING EKSTAZI"
cd ../
git clone https://github.com/gliga/ekstazi.git
cd ekstazi
JAVA_TOOL_OPTIONS="-Djdk.attach.allowAttachSelf=true" mvn install
echo ""
echo "ADD FOLLOWING TO pom.xml IN EACH PROJECT WERE EKSTAZI SHOULD BE USED:"
echo "
  <build>
    <plugins>
      <plugin>
        <groupId>org.ekstazi</groupId>
        <artifactId>ekstazi-maven-plugin</artifactId>
        <version>5.3.0</version> 
        <executions>
          <execution>
            <id>ekstazi</id>
            <goals>
              <goal>select</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>"