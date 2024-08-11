echo "INSTALLING JAVA 11, GRADLE, JUNIT AND MAVEN DEPENDENCIES"
./scripts/install-deps.sh
echo ""
echo ""
echo "INSTALLING TestingResearchIllinois/starts and gliga/ekstazi maven plugins"
./scripts/install-maven-plugins.sh
echo ""
echo ""
echo "INSTALLING THE FORK Doldas/matrixer-rts-performance-analyzer of ikristola/matrixer"
./scripts/install-matrixer.sh
