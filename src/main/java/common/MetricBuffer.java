package common;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * @author lzn
 * @date 2023/10/14 16:55
 * @description
 */
public class MetricBuffer extends UnsafeBuffer {

    /**
     * Index of thread name
     */
    private int tnIdx = 1;
    /**
     * Index of thread name
     */
    private int blIdx = 1;
    /**
     * Index of type
     */
    private int pointIdx = 1;
    /**
     * Index of latency
     */
    private int latencyIdx = 1;

    public MetricBuffer() {
        wrapBytes(new byte[]{});
    }

    public MetricBuffer(int initialCapacity) {
        wrapBytes(initialCapacity);
    }

    public MetricBuffer(byte[] buffer) {
        wrapBytes(buffer);
    }

    public MetricBuffer(ByteBuffer buffer) {
        wrapBytes(buffer);
    }
    public MetricBuffer(DirectBuffer buffer) {
        wrapBytes(buffer);
    }

    public MetricBuffer wrapBytes(int initialCapacity){
        return wrapBytes(ByteBuffer.allocate(initialCapacity));
    }

    public MetricBuffer wrapBytes(byte[] bytes){
        wrap(bytes);
        init();
        return this;
    }

    public MetricBuffer wrapBytes(ByteBuffer buffer){
        wrap(buffer);
        init();
        return this;
    }

    public MetricBuffer wrapBytes(DirectBuffer buffer){
        wrap(buffer);
        init();
        return this;
    }

    public String getThreadName(){
        return getStringUtf8(tnIdx, getLenOfThreadName());
    }

    private int getLenOfThreadName() {
        return getInt(0);
    }

    public String getBlockName(){
        return getStringUtf8(blIdx, getLenOfBlockName());
    }

    private int getLenOfBlockName() {
        return getInt(tnIdx + getLenOfThreadName());
    }

    private PointType getType(){
        return PointType.values()[getInt(pointIdx)];
    }

    private long getLatency(){
        return getLong(latencyIdx);
    }

    public MetricBuffer createKey(String threadName, String blockName, PointType type){
        int idx = 0;
        wrap(ByteBuffer.allocate(2 + threadName.length() + blockName.length() + Integer.BYTES +  Long.BYTES));

        return this;
    }

    private void init(){
        tnIdx = 1;
        blIdx = 1;
        pointIdx = 1;
        latencyIdx = 1;
    }
    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(getClass() != obj.getClass()){
            return false;
        }

        MetricBuffer that = (MetricBuffer) obj;
        return tnIdx == that.tnIdx && blIdx == that.blIdx && pointIdx == that.pointIdx && getLenOfThreadName() == that.getLenOfThreadName() && getLenOfBlockName() == that.getLenOfBlockName()
                && getType() == that.getType() && getThreadName().equals(that.getThreadName()) && getBlockName().equals(that.getThreadName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getThreadName(), getBlockName());
    }
}
