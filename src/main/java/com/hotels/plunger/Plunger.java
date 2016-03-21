/**
 * Copyright (C) 2014-2016 Expedia Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hotels.plunger;

import java.io.IOException;
import java.util.UUID;

import cascading.flow.Flow;
import cascading.operation.Aggregator;
import cascading.operation.Buffer;
import cascading.operation.Function;
import cascading.pipe.Pipe;
import cascading.stats.FlowStats;
import cascading.tap.Tap;
import cascading.tuple.Fields;
import cascading.tuple.Tuple;

/** Entry point to the plunger testing framework for cascading. */
public final class Plunger {

  private final PlungerFlow flow;

  /** Typically you should create a new {@link Plunger} instance for each test invocation. */
  public Plunger() {
    flow = new PlungerFlow();
  }

  /** Creates a named {@link Pipe} to deliver the provided {@link Data}. */
  public Pipe newNamedPipe(String name, Data data) {
    return new PipeFactory(data, name, flow).newInstance();
  }

  /** Creates a {@link Pipe} with a randomly generated name to deliver the provided {@link Data}. */
  public Pipe newPipe(Data data) {
    return newNamedPipe("plunger-test-pipe:" + UUID.randomUUID().toString(), data);
  }

  /**
   * Creates a {@link Bucket} to capture the data emitted by the provided tail {@link Pipe}. It is expected that the
   * {@link Tuple Tuples} captured will conform to the provided {@link Fields} declaration.
   */
  public Bucket newBucket(Fields fields, Pipe tail) {
    return new Bucket(fields, tail, flow);
  }

  /** Supplies a {@link TapDataWriter} for writing the provided {@link Data} to a {@link Tap} instance. */
  public static TapDataWriter writeData(Data data) {
    return new TapDataWriter(data);
  }

  /** Reads data from the supplied {@link Tap}. */
  public static Data readDataFromTap(Tap<?, ?, ?> source) throws IOException {
    return new TapDataReader(source).read();
  }

  /** Returns the internal {@link Flow} used by this instance. */
  public Flow<?> getFlow() {
    return flow.getFlow();
  }

  /** Returns the {@link FlowStats} generated by this instance. */
  public FlowStats getStats() {
    return flow.getStats();
  }

  /** Creates a stub builder to assist in testing {@link Aggregator} implementations. */
  public static <C> AggregatorCallStub.Builder<C> newAggregatorCallStubBuilder(Fields groupFields, Fields nonGroupFields) {
    return new AggregatorCallStub.Builder<C>(groupFields, nonGroupFields);
  }

  /** Creates a stub builder to assist in testing {@link Buffer} implementations. */
  public static <C> BufferCallStub.Builder<C> newBufferCallStubBuilder(Fields groupFields, Fields nonGroupFields) {
    return new BufferCallStub.Builder<C>(groupFields, nonGroupFields);
  }

  /** Creates a stub builder to assist in testing {@link Function} implementations. */
  public static <C> FunctionCallStub.Builder<C> newFunctionCallStubBuilder(Fields fields) {
    return new FunctionCallStub.Builder<C>(fields);
  }

}
