package org.matrixer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.*;

class ProjectPreparerTest {

    Path targetDirectory = TestUtils.targetDirectory();
    URI testRepoURL = TestUtils.testRepoURL();

    @BeforeEach
    void setUp() throws GitAPIException, IOException {
        if (Files.exists(targetDirectory)) {
            var repo = GitRepository.open(targetDirectory);
            repo.restore();
            return;
        }
        GitRepository.clone(testRepoURL.toString(), targetDirectory.toFile());
    }

    @Test
    void testClonesRemoteRepositoryToTargetDirectory() throws GitAPIException, IOException {

        Path target = TestUtils.targetDirectory();
        FileUtils.removeDirectory(target);

        URI testRepoURL = TestUtils.testRepoURL();
        Properties properties = new Properties();
        properties.setTargetDir(target);
        properties.setRemoteURL(testRepoURL);

        ProjectPreparer preparer = new ProjectPreparer();
        Project project = preparer.prepare(properties);

        assertTrue(Files.exists(target.resolve(".git")));
        assertEquals(target, project.directory());
    }

    @Test
    void testCanUseLocalProject() throws IOException, GitAPIException {
        Path target = TestUtils.targetDirectory();
        Properties properties = new Properties();
        properties.setTargetDir(target);

        ProjectPreparer preparer = new ProjectPreparer();
        Project project = preparer.prepare(properties);

        assertEquals(target, project.directory());
    }

    @Test
    void prepareCreatesOutputDirectory() throws GitAPIException, IOException {
        Path target = TestUtils.targetDirectory();
        Properties properties = new Properties();
        properties.setTargetDir(target);

        ProjectPreparer preparer = new ProjectPreparer();
        Project project = preparer.prepare(properties);

        assertTrue(Files.exists(project.outputDirectory()));
    }

    @Test
    void throwsExceptionForUnsupportedProject() {
        Path emptyTarget = FileUtils.createTempDirectory();
        Properties properties = new Properties();
        properties.setTargetDir(emptyTarget);

        assertThrows(Exception.class, () -> new ProjectPreparer().prepare(properties));
    }

    @Nested
    class Gradle {
        @Test
        void prepareInjectsGradleBuildScript() throws GitAPIException, IOException {
            Path target = TestUtils.targetDirectory();
            Properties properties = new Properties();
            properties.setTargetDir(target);
            ProjectPreparer preparer = new ProjectPreparer();

            Project project = preparer.prepare(properties);
            String agentString = TestUtils.agentString(project.outputDirectory());
            String expected = "jvmArgs \"" + agentString + "\"";
            assertFileContainsString(project.buildScript(), expected);
        }
    }

    @Nested
    class Maven {
        @BeforeEach
        void setupMaven() {
            TestUtils.removeGradleFiles(targetDirectory);
        }

        @Test
        void prepareInjectsMavenBuildScript() throws GitAPIException, IOException {
            Path target = TestUtils.targetDirectory();
            Properties properties = new Properties();
            properties.setTargetDir(target);
            ProjectPreparer preparer = new ProjectPreparer();

            Project project = preparer.prepare(properties);
            String agentString = TestUtils.agentString(project.outputDirectory());
            String agentArg = "<argLine>" + agentString + "</argLine>";
            String manifest = "<useManifestOnlyJar>false</useManifestOnlyJar>";
            assertFileContainsString(project.buildScript(), agentArg);
            assertFileContainsString(project.buildScript(), manifest);
        }
    }

    void assertFileContainsString(Path file, String string) {
        try {
            var result = FileUtils.searchInFile(string, file);
            if (!result.isPresent()) {
                throw new AssertionError(file + " did not contain:\n" + string);
            }
            Integer index = result.get();
            assertTrue(index != -1, "Did not find\n'" + string + "'\nin\n" + file + "\n");
        } catch (IOException e) {
            throw new AssertionError("assertFileContainsString: " + e.getMessage());
        }
    }
}
