Latency point
--------------------------

Zero GC, ultra-low latency and very easy to use latency detection and monitoring tool that automatically detects the current container type (springboot or regular java project) and collects and calculates the latency, supports Prometheus access and visualize on Grafana dashboard.

Design:

1. LatencyPoint: Only one instance exist, and just simpliy use start and stop method to measure the processing of events
2. How to initialize? Intialize with three types: COUNT, LATENCY, GUAGE
3. How to store metrics, which data structures should we choose? 
    1. ConcurrentHashMap<Event, Metrics>?  We could, because it's the easiest way, but it will have latency impact under time-and-latency-sensitive scenarios.
    2. ThreadSafe Collections (e.g. CopyOnWriteArrayList)? We could, but we also need to introduce lock as combined with many other operations rather than just read and write to this collection.
    3. **ThreadLocal<Map<String, LatencyPoint>>**? We isolate the event among multiple threads, so each metrics will be measured indenpendently.
4. For ultrl-low latency, utilized a customized byte buffer - **MetricBuffer** which extended from Agrona UnsafeBuffer to encode/decode metrics in bytes.
    
