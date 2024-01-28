package common;

import org.agrona.DirectBuffer;
import org.agrona.collections.ArrayUtil;
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
        wrap(ArrayUtil.EMPTY_BYTE_ARRAY);
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

    public int getLenOfThreadName() {
        return getByte(0);
    }

    public String getBlockName(){
        return getStringUtf8(blIdx, getLenOfBlockName());
    }

    public int getLenOfBlockName() {
        return getByte(tnIdx + getLenOfThreadName());
    }

    public PointType getType(){
        return PointType.values()[getInt(pointIdx)];
    }

    public long getLatency(){
        return getLong(latencyIdx);
    }

    /**
     *
     * @param threadName Name of current thread
     * @param blockName Name of current block
     * @param type Type of latency point
     * @return Metric buffer with format: length of thread name + thread name + length of block name + block name + ordinal of type
     */
    public MetricBuffer createKey(String threadName, String blockName, PointType type){
        int idx = 0;
        this.wrap(ByteBuffer.allocate(2 + threadName.length() + blockName.length() + Integer.BYTES +  Long.BYTES));
        putByte(idx++, (byte) threadName.length());
        putStringWithoutLengthAscii(idx, threadName);
        idx += threadName.length();
        putByte(idx++, (byte) blockName.length());
        putStringWithoutLengthAscii(idx, blockName);
        idx += blockName.length();
        putInt(idx, type.ordinal());
        init();
        return this;
    }

    private void init(){
        tnIdx = 1;
        blIdx = tnIdx + getLenOfThreadName();
        pointIdx = blIdx + getLenOfBlockName() + 1;
        latencyIdx = pointIdx + Integer.BYTES;
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
