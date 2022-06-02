package org.acme;

import org.infinispan.protostream.annotations.ProtoField;

public class Test {
    private String id;
    private String name;

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

    @Override
    public String toString() {
        return "Test{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
