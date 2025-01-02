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
        private String repo;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRepo() {
            return repo;
        }

        public void setRepo(String repo) {
            this.repo = repo;
        }
    }
}
