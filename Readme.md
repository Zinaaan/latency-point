Latency point
--------------------------

High performance latency measurement harness, extremely low memory footprint and easy to use

Design:

1. core.LatencyPoint: Only one instance exist, so start and stop method should be static
2. How to initialize? Three types: COUNT, LATENCY, GUAGE
3. How to store metrics, which data structures should we choose? 
    1. ConcurrentHashMap<Event, Metrics>?  We could because it's the easiest way, but it will have latency impact under time-and-latency-sensitive scenarios.
    2. ThreadSafe Collections (e.g. CopyOnWriteArrayList)? We could, but we also need to introduce lock as combined with many other operations rather than just read and write to this collection.
    3. LatencyElements[LatencyElements<Event, Metrics>] with lock mechanism? **Might be the best option** because of faster element lookup and flexibility.
4. Array must have a capacity. However, we won't know what capacity is suitable for various scenarios, what should we do?
    1. Initiate a very big array? For memory efficiency and flexibility, no.
    2. Do we create a new event every time? We might be, but we most likely will capture and measure the latency for existing events.
        
        If the creation is not very often, we can initiate an array with 0 or 1 capacity, and create a new array to replace the existing array via Arrays.copy();
5. But how to find the event and compute the latency if we use array instead of hash map? 
    1. We can use the index of each element of the element array as id, and bind this id with each element to find it.
    
        What if we capture the same event for multiple times? How does array know that the event are the same one (We shouldn't add a new event every time)?
        1. By compare with event name? Apparently, String is the best option to represent **Event** as it's easier to define and read, and equals method of String is not very efficient.
        2. For every use cases, they should cache those events rather than invoke the start and stop directly as it will have so many duplicate events in the internal array, will cause memory leak sooner or later.
6. Since it's a global latency measurement harness, some unexpected issue happens if multiple threads that wants to measure a same event
    1. Bind each event to currentThread via thread name? Complex.
    2. Use ThreadLocal to store latency elements? **Best option**.
    