package io.github.tavodin.oauth2study.projections;

public interface UserProjection {
    Long getId();
    String getEmail();
    String getPassword();
}
