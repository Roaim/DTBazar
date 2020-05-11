package app.roaim.dtbazar.storeservice.model;

import lombok.Value;

@Value
public class Image {
    String id;
    Source source;
    String url;

    public enum Source {
        SELF
    }
}
