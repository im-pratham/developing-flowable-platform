package com.flowable.training.dp.t60;

import org.flowable.engine.delegate.event.FlowableActivityEvent;
import org.flowable.engine.delegate.event.FlowableSequenceFlowTakenEvent;

public interface ActivityEventListener {

    void observe(FlowableActivityEvent event);

    void observeSequenceFlowTaken(FlowableSequenceFlowTakenEvent event);
}
