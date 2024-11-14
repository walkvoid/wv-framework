package com.github.walkvoid.wvframework.mavenplugins.install;

/**
 * @author jiangjunqing
 * @version 1.0.0
 * @date 2024/11/14
 * @desc
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;
import org.apache.maven.project.validation.ModelValidationResult;
import org.apache.maven.project.validation.ModelValidator;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.WriterFactory;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

@Mojo(name = "install-file", requiresProject = false, aggregator = true, threadSafe = true)
public class InstallMojo extends AbstractInstallMojo {

    @Parameter(property = "groupId")
    protected String groupId;

    @Parameter(property = "artifactId")
    protected String artifactId;

    @Parameter(property = "version")
    protected String version;

    @Parameter(property = "packaging")
    protected String packaging;

    @Parameter(property = "classifier")
    protected String classifier;

    @Parameter(property = "file", required = true)
    private File file;

    @Parameter(property = "javadoc")
    private File javadoc;

    @Parameter(property = "sources")
    private File sources;

    @Parameter(property = "pomFile")
    private File pomFile;

    @Parameter(property = "generatePom")
    private Boolean generatePom;

    @Parameter(property = "repositoryLayout", defaultValue = "default", required = true)
    private String repositoryLayout;

    @Component(role = ArtifactRepositoryLayout.class)
    private Map repositoryLayouts;

    @Parameter(property = "localRepositoryPath")
    private File localRepositoryPath;

    @Component
    private ModelValidator modelValidator;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!this.file.exists()) {
            String message = "The specified file '" + this.file.getPath() + "' not exists";
            this.getLog().error(message);
            throw new MojoFailureException(message);
        } else {
            if (this.localRepositoryPath != null) {
                try {
                    ArtifactRepositoryLayout layout = (ArtifactRepositoryLayout)this.repositoryLayouts.get(this.repositoryLayout);
                    this.getLog().debug("Layout: " + layout.getClass());
                    this.localRepository = new DefaultArtifactRepository(this.localRepository.getId(), this.localRepositoryPath.toURL().toString(), layout);
                } catch (MalformedURLException var14) {
                    throw new MojoExecutionException("MalformedURLException: " + var14.getMessage(), var14);
                }
            }

            if (this.pomFile != null) {
                this.processModel(this.readModel(this.pomFile));
            }

            this.validateArtifactInformation();
            Artifact artifact = this.artifactFactory.createArtifactWithClassifier(this.groupId, this.artifactId, this.version, this.packaging, this.classifier);
            if (this.file.equals(this.getLocalRepoFile(artifact))) {
                throw new MojoFailureException("Cannot install artifact. Artifact is already in the local repository.\n\nFile in question is: " + this.file + "\n");
            } else {
                File generatedPomFile = null;
                if (!"pom".equals(this.packaging)) {
                    ProjectArtifactMetadata pomMetadata;
                    if (this.pomFile != null) {
                        pomMetadata = new ProjectArtifactMetadata(artifact, this.pomFile);
                        artifact.addMetadata(pomMetadata);
                    } else {
                        generatedPomFile = this.generatePomFile();
                        pomMetadata = new ProjectArtifactMetadata(artifact, generatedPomFile);
                        if (!Boolean.TRUE.equals(this.generatePom) && (this.generatePom != null || this.getLocalRepoFile(pomMetadata).exists())) {
                            if (this.generatePom == null) {
                                this.getLog().debug("Skipping installation of generated POM, already present in local repository");
                            }
                        } else {
                            this.getLog().debug("Installing generated POM");
                            artifact.addMetadata(pomMetadata);
                        }
                    }
                }

                if (this.updateReleaseInfo) {
                    artifact.setRelease(true);
                }

                Collection metadataFiles = new LinkedHashSet();

                try {
                    this.installer.install(this.file, artifact, this.localRepository);
                    this.installChecksums(artifact, metadataFiles);
                } catch (ArtifactInstallationException var13) {
                    throw new MojoExecutionException("Error installing artifact '" + artifact.getDependencyConflictId() + "': " + var13.getMessage(), var13);
                } finally {
                    if (generatedPomFile != null) {
                        generatedPomFile.delete();
                    }

                }

                if (this.sources != null) {
                    artifact = this.artifactFactory.createArtifactWithClassifier(this.groupId, this.artifactId, this.version, "jar", "sources");

                    try {
                        this.installer.install(this.sources, artifact, this.localRepository);
                        this.installChecksums(artifact, metadataFiles);
                    } catch (ArtifactInstallationException var12) {
                        throw new MojoExecutionException("Error installing sources " + this.sources + ": " + var12.getMessage(), var12);
                    }
                }

                if (this.javadoc != null) {
                    artifact = this.artifactFactory.createArtifactWithClassifier(this.groupId, this.artifactId, this.version, "jar", "javadoc");

                    try {
                        this.installer.install(this.javadoc, artifact, this.localRepository);
                        this.installChecksums(artifact, metadataFiles);
                    } catch (ArtifactInstallationException var11) {
                        throw new MojoExecutionException("Error installing API docs " + this.javadoc + ": " + var11.getMessage(), var11);
                    }
                }

                this.installChecksums(metadataFiles);
            }
        }
    }

    private Model readModel(File pomFile) throws MojoExecutionException {
        Reader reader = null;

        Model var3;
        try {
            reader = ReaderFactory.newXmlReader(pomFile);
            var3 = (new MavenXpp3Reader()).read(reader);
        } catch (FileNotFoundException var9) {
            throw new MojoExecutionException("File not found " + pomFile, var9);
        } catch (IOException var10) {
            throw new MojoExecutionException("Error reading POM " + pomFile, var10);
        } catch (XmlPullParserException var11) {
            throw new MojoExecutionException("Error parsing POM " + pomFile, var11);
        } finally {
            IOUtil.close(reader);
        }

        return var3;
    }

    private void processModel(Model model) {
        Parent parent = model.getParent();
        if (this.groupId == null) {
            this.groupId = model.getGroupId();
            if (this.groupId == null && parent != null) {
                this.groupId = parent.getGroupId();
            }
        }

        if (this.artifactId == null) {
            this.artifactId = model.getArtifactId();
        }

        if (this.version == null) {
            this.version = model.getVersion();
            if (this.version == null && parent != null) {
                this.version = parent.getVersion();
            }
        }

        if (this.packaging == null) {
            this.packaging = model.getPackaging();
        }

    }

    private void validateArtifactInformation() throws MojoExecutionException {
        Model model = this.generateModel();
        ModelValidationResult result = this.modelValidator.validate(model);
        if (result.getMessageCount() > 0) {
            throw new MojoExecutionException("The artifact information is incomplete or not valid:\n" + result.render("  "));
        }
    }

    private Model generateModel() {
        Model model = new Model();
        model.setModelVersion("4.0.0");
        model.setGroupId(this.groupId);
        model.setArtifactId(this.artifactId);
        model.setVersion(this.version);
        model.setPackaging(this.packaging);
        model.setDescription("POM was created from install:install-file");
        return model;
    }

    private File generatePomFile() throws MojoExecutionException {
        Model model = this.generateModel();
        Writer writer = null;

        File var4;
        try {
            File pomFile = File.createTempFile("mvninstall", ".pom");
            writer = WriterFactory.newXmlWriter(pomFile);
            (new MavenXpp3Writer()).write(writer, model);
            var4 = pomFile;
        } catch (IOException var8) {
            throw new MojoExecutionException("Error writing temporary POM file: " + var8.getMessage(), var8);
        } finally {
            IOUtil.close(writer);
        }

        return var4;
    }

    public File getLocalRepositoryPath() {
        return this.localRepositoryPath;
    }

    public void setLocalRepositoryPath(File theLocalRepositoryPath) {
        this.localRepositoryPath = theLocalRepositoryPath;
    }
}
