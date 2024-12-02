package org.keycloak.webbuilder;

import java.util.List;

public class ReleasesMetadata {
    public List<ReleaseSource> sources;

    public List<ReleaseSource> getSources() {
        return sources;
    }

    public void setSources(List<ReleaseSource> sources) {
        this.sources = sources;
    }

    public static class ReleaseSource {

        private String id;
        private String dir;
        private String github;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public String getGithub() {
            return github;
        }

        public void setGithub(String github) {
            this.github = github;
        }
    }
}
