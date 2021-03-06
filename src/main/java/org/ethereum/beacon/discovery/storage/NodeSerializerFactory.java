/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.ethereum.beacon.discovery.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.tuweni.bytes.Bytes;
import org.ethereum.beacon.discovery.format.SerializerFactory;
import org.ethereum.beacon.discovery.schema.NodeRecordFactory;
import org.ethereum.beacon.discovery.schema.NodeRecordInfo;

/** Serializer for {@link NodeRecordInfo}, {@link NodeIndex} and {@link NodeBucket} */
public class NodeSerializerFactory implements SerializerFactory {
  @SuppressWarnings({"rawtypes"})
  private final Map<Class, Function<Bytes, Object>> deserializerMap = new HashMap<>();

  @SuppressWarnings({"rawtypes"})
  private final Map<Class, Function<Object, Bytes>> serializerMap = new HashMap<>();

  public NodeSerializerFactory(NodeRecordFactory nodeRecordFactory) {
    deserializerMap.put(
        NodeRecordInfo.class, bytes1 -> NodeRecordInfo.fromRlpBytes(bytes1, nodeRecordFactory));
    serializerMap.put(NodeRecordInfo.class, o -> ((NodeRecordInfo) o).toRlpBytes());
    deserializerMap.put(NodeIndex.class, NodeIndex::fromRlpBytes);
    serializerMap.put(NodeIndex.class, o -> ((NodeIndex) o).toRlpBytes());
    deserializerMap.put(
        NodeBucket.class, bytes -> NodeBucket.fromRlpBytes(bytes, nodeRecordFactory));
    serializerMap.put(NodeBucket.class, o -> ((NodeBucket) o).toRlpBytes());
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T> Function<Bytes, T> getDeserializer(Class<? extends T> objectClass) {
    if (!deserializerMap.containsKey(objectClass)) {
      throw new RuntimeException(String.format("Type %s is not supported", objectClass));
    }
    return bytes -> (T) deserializerMap.get(objectClass).apply(bytes);
  }

  @Override
  public <T> Function<T, Bytes> getSerializer(Class<? extends T> objectClass) {
    if (!serializerMap.containsKey(objectClass)) {
      throw new RuntimeException(String.format("Type %s is not supported", objectClass));
    }
    return value -> serializerMap.get(objectClass).apply(value);
  }
}
