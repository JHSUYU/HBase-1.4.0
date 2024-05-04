/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hadoop.hbase.util;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.classification.InterfaceAudience;

/**
 * A generator of random keys and values for load testing. Keys are generated
 * by converting numeric indexes to strings and prefixing them with an MD5
 * hash. Values are generated by selecting value size in the configured range
 * and generating a pseudo-random sequence of bytes seeded by key, column
 * qualifier, and value size.
 */
@InterfaceAudience.Private
public class LoadTestKVGenerator {

  private static final Log LOG = LogFactory.getLog(LoadTestKVGenerator.class);
  private static int logLimit = 10;

  /** A random number generator for determining value size */
  private Random randomForValueSize = new Random();

  private final int minValueSize;
  private final int maxValueSize;

  public LoadTestKVGenerator(int minValueSize, int maxValueSize) {
    if (minValueSize <= 0 || maxValueSize <= 0) {
      throw new IllegalArgumentException("Invalid min/max value sizes: " +
          minValueSize + ", " + maxValueSize);
    }
    this.minValueSize = minValueSize;
    this.maxValueSize = maxValueSize;
  }

  /**
   * Verifies that the given byte array is the same as what would be generated
   * for the given seed strings (row/cf/column/...). We are assuming that the
   * value size is correct, and only verify the actual bytes. However, if the
   * min/max value sizes are set sufficiently high, an accidental match should be
   * extremely improbable.
   */
  public static boolean verify(byte[] value, byte[]... seedStrings) {
    byte[] expectedData = getValueForRowColumn(value.length, seedStrings);
    boolean equals = Bytes.equals(expectedData, value);
    if (!equals && LOG.isDebugEnabled() && logLimit > 0) {
      LOG.debug("verify failed, expected value: " + Bytes.toStringBinary(expectedData)
        + " actual value: "+ Bytes.toStringBinary(value));
      logLimit--; // this is not thread safe, but at worst we will have more logging
    }
    return equals;
  }

  /**
   * Converts the given key to string, and prefixes it with the MD5 hash of
   * the index's string representation.
   */
  public static String md5PrefixedKey(long key) {
    String stringKey = Long.toString(key);
    String md5hash = MD5Hash.getMD5AsHex(Bytes.toBytes(stringKey));

    // flip the key to randomize
    return md5hash + "-" + stringKey;
  }

  /**
   * Generates a value for the given key index and column qualifier. Size is
   * selected randomly in the configured range. The generated value depends
   * only on the combination of the strings passed (key/cf/column/...) and the selected
   * value size. This allows to verify the actual value bytes when reading, as done
   * in {#verify(byte[], byte[]...)}
   * This method is as thread-safe as Random class. It appears that the worst bug ever
   * found with the latter is that multiple threads will get some duplicate values, which
   * we don't care about.
   */
  public byte[] generateRandomSizeValue(byte[]... seedStrings) {
    int dataSize = minValueSize;
    if(minValueSize != maxValueSize) {
      dataSize = minValueSize + randomForValueSize.nextInt(Math.abs(maxValueSize - minValueSize));
    }
    return getValueForRowColumn(dataSize, seedStrings);
  }

  /**
   * Generates random bytes of the given size for the given row and column
   * qualifier. The random seed is fully determined by these parameters.
   */
  private static byte[] getValueForRowColumn(int dataSize, byte[]... seedStrings) {
    long seed = dataSize;
    for (byte[] str : seedStrings) {
      final String bytesString = Bytes.toString(str);
      if (bytesString != null) {
        seed += bytesString.hashCode();
      }
    }
    Random seededRandom = new Random(seed);
    byte[] randomBytes = new byte[dataSize];
    seededRandom.nextBytes(randomBytes);
    return randomBytes;
  }

}
