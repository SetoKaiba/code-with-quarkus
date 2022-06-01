package org.acme;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = {Test.class})
public interface TestSchema extends GeneratedSchema {
}
