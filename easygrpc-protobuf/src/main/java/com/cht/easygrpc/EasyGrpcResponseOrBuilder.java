// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: EasyGrpc.proto

package com.cht.easygrpc;

public interface EasyGrpcResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:EasyGrpcResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int32 code = 1;</code>
   * @return The code.
   */
  int getCode();

  /**
   * <code>string msg = 2;</code>
   * @return The msg.
   */
  String getMsg();
  /**
   * <code>string msg = 2;</code>
   * @return The bytes for msg.
   */
  com.google.protobuf.ByteString
      getMsgBytes();

  /**
   * <code>string resultJson = 3;</code>
   * @return The resultJson.
   */
  String getResultJson();
  /**
   * <code>string resultJson = 3;</code>
   * @return The bytes for resultJson.
   */
  com.google.protobuf.ByteString
      getResultJsonBytes();
}