package com.cht.easygrpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.*;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.*;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.32.1)",
    comments = "Source: EasyGrpc.proto")
public final class EasyGrpcServiceGrpc {

  private EasyGrpcServiceGrpc() {}

  public static final String SERVICE_NAME = "EasyGrpcService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<EasyGrpcRequest,
      EasyGrpcResponse> getCallMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "call",
      requestType = EasyGrpcRequest.class,
      responseType = EasyGrpcResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<EasyGrpcRequest,
      EasyGrpcResponse> getCallMethod() {
    io.grpc.MethodDescriptor<EasyGrpcRequest, EasyGrpcResponse> getCallMethod;
    if ((getCallMethod = EasyGrpcServiceGrpc.getCallMethod) == null) {
      synchronized (EasyGrpcServiceGrpc.class) {
        if ((getCallMethod = EasyGrpcServiceGrpc.getCallMethod) == null) {
          EasyGrpcServiceGrpc.getCallMethod = getCallMethod =
              io.grpc.MethodDescriptor.<EasyGrpcRequest, EasyGrpcResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "call"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EasyGrpcRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EasyGrpcResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EasyGrpcServiceMethodDescriptorSupplier("call"))
              .build();
        }
      }
    }
    return getCallMethod;
  }

  private static volatile io.grpc.MethodDescriptor<EasyGrpcRequest,
      EasyGrpcResponse> getCallStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "callStream",
      requestType = EasyGrpcRequest.class,
      responseType = EasyGrpcResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<EasyGrpcRequest,
      EasyGrpcResponse> getCallStreamMethod() {
    io.grpc.MethodDescriptor<EasyGrpcRequest, EasyGrpcResponse> getCallStreamMethod;
    if ((getCallStreamMethod = EasyGrpcServiceGrpc.getCallStreamMethod) == null) {
      synchronized (EasyGrpcServiceGrpc.class) {
        if ((getCallStreamMethod = EasyGrpcServiceGrpc.getCallStreamMethod) == null) {
          EasyGrpcServiceGrpc.getCallStreamMethod = getCallStreamMethod =
              io.grpc.MethodDescriptor.<EasyGrpcRequest, EasyGrpcResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "callStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EasyGrpcRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  EasyGrpcResponse.getDefaultInstance()))
              .setSchemaDescriptor(new EasyGrpcServiceMethodDescriptorSupplier("callStream"))
              .build();
        }
      }
    }
    return getCallStreamMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static EasyGrpcServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EasyGrpcServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EasyGrpcServiceStub>() {
        @Override
        public EasyGrpcServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EasyGrpcServiceStub(channel, callOptions);
        }
      };
    return EasyGrpcServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static EasyGrpcServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EasyGrpcServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EasyGrpcServiceBlockingStub>() {
        @Override
        public EasyGrpcServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EasyGrpcServiceBlockingStub(channel, callOptions);
        }
      };
    return EasyGrpcServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static EasyGrpcServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<EasyGrpcServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<EasyGrpcServiceFutureStub>() {
        @Override
        public EasyGrpcServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new EasyGrpcServiceFutureStub(channel, callOptions);
        }
      };
    return EasyGrpcServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class EasyGrpcServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void call(EasyGrpcRequest request,
                     io.grpc.stub.StreamObserver<EasyGrpcResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCallMethod(), responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<EasyGrpcRequest> callStream(
        io.grpc.stub.StreamObserver<EasyGrpcResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getCallStreamMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCallMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                EasyGrpcRequest,
                EasyGrpcResponse>(
                  this, METHODID_CALL)))
          .addMethod(
            getCallStreamMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                EasyGrpcRequest,
                EasyGrpcResponse>(
                  this, METHODID_CALL_STREAM)))
          .build();
    }
  }

  /**
   */
  public static final class EasyGrpcServiceStub extends io.grpc.stub.AbstractAsyncStub<EasyGrpcServiceStub> {
    private EasyGrpcServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected EasyGrpcServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EasyGrpcServiceStub(channel, callOptions);
    }

    /**
     */
    public void call(EasyGrpcRequest request,
                     io.grpc.stub.StreamObserver<EasyGrpcResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCallMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<EasyGrpcRequest> callStream(
        io.grpc.stub.StreamObserver<EasyGrpcResponse> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getCallStreamMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class EasyGrpcServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<EasyGrpcServiceBlockingStub> {
    private EasyGrpcServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected EasyGrpcServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EasyGrpcServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public EasyGrpcResponse call(EasyGrpcRequest request) {
      return blockingUnaryCall(
          getChannel(), getCallMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class EasyGrpcServiceFutureStub extends io.grpc.stub.AbstractFutureStub<EasyGrpcServiceFutureStub> {
    private EasyGrpcServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected EasyGrpcServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new EasyGrpcServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<EasyGrpcResponse> call(
        EasyGrpcRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCallMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CALL = 0;
  private static final int METHODID_CALL_STREAM = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final EasyGrpcServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(EasyGrpcServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CALL:
          serviceImpl.call((EasyGrpcRequest) request,
              (io.grpc.stub.StreamObserver<EasyGrpcResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CALL_STREAM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.callStream(
              (io.grpc.stub.StreamObserver<EasyGrpcResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class EasyGrpcServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    EasyGrpcServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return EasyGrpcProto.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("EasyGrpcService");
    }
  }

  private static final class EasyGrpcServiceFileDescriptorSupplier
      extends EasyGrpcServiceBaseDescriptorSupplier {
    EasyGrpcServiceFileDescriptorSupplier() {}
  }

  private static final class EasyGrpcServiceMethodDescriptorSupplier
      extends EasyGrpcServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    EasyGrpcServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (EasyGrpcServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new EasyGrpcServiceFileDescriptorSupplier())
              .addMethod(getCallMethod())
              .addMethod(getCallStreamMethod())
              .build();
        }
      }
    }
    return result;
  }
}
