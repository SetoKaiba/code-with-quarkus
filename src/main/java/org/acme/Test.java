package org.acme;

import org.infinispan.protostream.annotations.ProtoFactory;
import org.infinispan.protostream.annotations.ProtoField;

public class Test {
    private String id;
    private String name;

    @ProtoFactory
    public Test(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @ProtoField(1)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @ProtoField(2)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
