package org.keycloak.webbuilder.builders;

import org.keycloak.webbuilder.ReleasesMetadata;
import org.keycloak.webbuilder.Versions;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReleaseNotesBuilder extends AbstractBuilder {

    private static final String DOCUMENT_ATTRIBUTES_TAG_URL = "https://raw.githubusercontent.com/%s/%s/docs/documentation/topics/templates/document-attributes.adoc";
    private static final String DOCUMENT_ATTRIBUTES_BRANCH_URL = "https://raw.githubusercontent.com/%s/release/%s/docs/documentation/topics/templates/document-attributes.adoc";
    private static final String RELEASE_NOTES_URL = "https://raw.githubusercontent.com/%s/release/%s/docs/documentation/release_notes/topics/%s.adoc";

    @Override
    protected String getTitle() {
        return "Release Notes";
    }

    @Override
    protected void build() throws Exception {
        ReleasesMetadata metadata = context.getReleasesMetadata();

        for (ReleasesMetadata.ReleaseSource source : metadata.getSources()) {
            buildRelease(source);
        }
    }

    public void buildRelease(ReleasesMetadata.ReleaseSource source) throws Exception {
        File releasesCache = new File(context.getCacheDir(), "releases/" + source.getId());
        List<Versions.Version> versions = context.versionsFor(source.getId());

        for (Versions.Version v : versions) {
            try {
                File releaseCacheDir = new File(releasesCache, v.getVersion());
                releaseCacheDir.mkdirs();

                File releaseNotesFile = new File(releaseCacheDir, "release-notes.html");
                File releaseNotesMissingFile = new File(releaseCacheDir, "release-notes.empty");

                if (releaseNotesFile.isFile()) {
                    printStep("exists", v.getVersion());
                } else if (releaseNotesMissingFile.isFile()) {
                    printStep("missing",  v.getVersion());
                } else {
                    Map<String, Object> attributes = new HashMap<>();

                    attributes.put("project_buildType", "latest");
                    attributes.put("leveloffset", "2");
                    attributes.put("fragment", "yes");

                    URL documentAttributesURL = new URL(String.format(DOCUMENT_ATTRIBUTES_BRANCH_URL, source.getRepo(), v.getVersionShorter()));
                    URL documentAttributesTagURL = new URL(String.format(DOCUMENT_ATTRIBUTES_TAG_URL, source.getRepo(), v.getVersion()));

                    Map<String, Object> branchAttributes = context.asciiDoctor().parseAttributes(documentAttributesURL, attributes);
                    Map<String, Object> tagAttributes = context.asciiDoctor().parseAttributes(documentAttributesTagURL, attributes);

                    attributes.putAll(branchAttributes);
                    attributes.putAll(tagAttributes);

                    URL releaseNotesURL = new URL(String.format(RELEASE_NOTES_URL, source.getId(), v.getVersionShorter(), v.getVersion().replace(".", "_")));

                    try {
                        context.asciiDoctor().writeFile(attributes, releaseNotesURL, releaseNotesFile.getParentFile(), releaseNotesFile.getName());
                        printStep("created", v.getVersion());
                    } catch (FileNotFoundException e) {
                        printStep("notfound",  v.getVersion());
                        releaseNotesMissingFile.createNewFile();
                    }
                }

                if (releaseNotesFile.isFile()) {
                    v.setReleaseNotes("cache/releases/"  + source.getId() + "/" + v.getVersion() + "/release-notes.html");
                }
            } catch (Exception e) {
                printStep("error", v.getVersion() + " (" + e.getClass().getSimpleName() + ")");
            }
        }
    }

}
