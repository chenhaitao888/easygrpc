package com.cht.easygrpc.loadbalance;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import io.grpc.LoadBalancer;
import io.grpc.Status;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author : chenhaitao934
 */
public class EmptyPicker extends AbstractEasyGrpcPicker {

    private final Status status;

    EmptyPicker(@Nonnull Status status) {
        this.status = Preconditions.checkNotNull(status, "status");
    }

    @Override
    boolean isEquivalentTo(AbstractEasyGrpcPicker picker) {
        return picker instanceof EmptyPicker && (Objects.equal(status, ((EmptyPicker) picker).status)
                || (status.isOk() && ((EmptyPicker) picker).status.isOk()));
    }

    @Override
    public LoadBalancer.PickResult pickSubchannel(LoadBalancer.PickSubchannelArgs args) {
        return status.isOk() ? LoadBalancer.PickResult.withNoResult() : LoadBalancer.PickResult.withError(status);
    }
}
