package com.demo.digitalproduct.actuator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Repository
@Slf4j
public class TraceRepository implements HttpTraceRepository {

    AtomicReference<HttpTrace> lastTrace = new AtomicReference<>();

    @Override
    public List<HttpTrace> findAll() {
        return Collections.singletonList(lastTrace.get());
    }

    @Override
    public void add(HttpTrace trace) {
        lastTrace.set(trace);
        log.info("Trace: Timestamp = <{}>, Time taken = <{}> ms, Request = <{}>, Response = <{}>",
                trace.getTimestamp(),
                trace.getTimeTaken(),
                ToStringBuilder.reflectionToString(trace.getRequest(), ToStringStyle.JSON_STYLE),
                ToStringBuilder.reflectionToString(trace.getResponse(), ToStringStyle.JSON_STYLE));
    }

}