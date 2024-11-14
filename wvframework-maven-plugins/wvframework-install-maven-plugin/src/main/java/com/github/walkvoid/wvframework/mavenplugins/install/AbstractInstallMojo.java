package com.github.walkvoid.wvframework.mavenplugins.install;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.digest.Digester;
import org.codehaus.plexus.digest.DigesterException;
import org.codehaus.plexus.util.FileUtils;

public abstract class AbstractInstallMojo extends AbstractMojo {
    @Component
    protected ArtifactFactory artifactFactory;

    @Component
    protected ArtifactInstaller installer;

    @Parameter(property = "localRepository", required = true, readonly = true)
    protected ArtifactRepository localRepository;

    @Parameter(property = "createChecksum", defaultValue = "false")
    protected boolean createChecksum;

    @Parameter(property = "updateReleaseInfo", defaultValue = "false")
    protected boolean updateReleaseInfo;

    @Component(hint = "md5")
    protected Digester md5Digester;

    @Component(hint = "sha1")
    protected Digester sha1Digester;

    public AbstractInstallMojo() {
    }

    protected File getLocalRepoFile(Artifact artifact) {
        String path = this.localRepository.pathOf(artifact);
        return new File(this.localRepository.getBasedir(), path);
    }

    protected File getLocalRepoFile(ArtifactMetadata metadata) {
        String path = this.localRepository.pathOfLocalRepositoryMetadata(metadata, this.localRepository);
        return new File(this.localRepository.getBasedir(), path);
    }

    protected void installChecksums(Artifact artifact, Collection metadataFiles) throws MojoExecutionException {
        if (this.createChecksum) {
            File artifactFile = this.getLocalRepoFile(artifact);
            this.installChecksums(artifactFile);
            Collection metadatas = artifact.getMetadataList();
            if (metadatas != null) {
                Iterator it = metadatas.iterator();

                while(it.hasNext()) {
                    ArtifactMetadata metadata = (ArtifactMetadata)it.next();
                    File metadataFile = this.getLocalRepoFile(metadata);
                    metadataFiles.add(metadataFile);
                }
            }

        }
    }

    protected void installChecksums(Collection metadataFiles) throws MojoExecutionException {
        Iterator it = metadataFiles.iterator();

        while(it.hasNext()) {
            File metadataFile = (File)it.next();
            this.installChecksums(metadataFile);
        }

    }

    private void installChecksums(File installedFile) throws MojoExecutionException {
        boolean signatureFile = installedFile.getName().endsWith(".asc");
        if (installedFile.isFile() && !signatureFile) {
            this.installChecksum(installedFile, installedFile, this.md5Digester, ".md5");
            this.installChecksum(installedFile, installedFile, this.sha1Digester, ".sha1");
        }

    }

    private void installChecksum(File originalFile, File installedFile, Digester digester, String ext) throws MojoExecutionException {
        this.getLog().debug("Calculating " + digester.getAlgorithm() + " checksum for " + originalFile);

        String checksum;
        try {
            checksum = digester.calc(originalFile);
        } catch (DigesterException var9) {
            throw new MojoExecutionException("Failed to calculate " + digester.getAlgorithm() + " checksum for " + originalFile, var9);
        }

        File checksumFile = new File(installedFile.getAbsolutePath() + ext);
        this.getLog().debug("Installing checksum to " + checksumFile);

        try {
            checksumFile.getParentFile().mkdirs();
            FileUtils.fileWrite(checksumFile.getAbsolutePath(), "UTF-8", checksum);
        } catch (IOException var8) {
            throw new MojoExecutionException("Failed to install checksum to " + checksumFile, var8);
        }
    }
}
