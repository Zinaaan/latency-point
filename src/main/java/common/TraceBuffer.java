package common;

import org.agrona.ExpandableArrayBuffer;

import java.util.Objects;

/**
 * @author lzn
 * @date 2023/10/14 16:55
 * @description
 */
public class TraceBuffer extends ExpandableArrayBuffer {

    /**
     * Index of request id
     */
    private int reqIdIdx = 1;
    /**
     * Index of block name
     */
    private int blIdx = 1;
    /**
     * Index of parent block name
     */
    private int parentIdx = 1;
    /**
     * Index of latency
     */
    private int latencyIdx = 1;

    private int length;

    public TraceBuffer() {
        super();
    }

    public TraceBuffer(int initialCapacity) {
        super(initialCapacity);
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getRequestId() {
        reqIdIdx = length + 1;
        length += 1 + getLenOfRequestId();
        return getStringWithoutLengthUtf8(reqIdIdx, getLenOfRequestId());
    }

    public int getLenOfRequestId() {
        return getByte(reqIdIdx - 1);
    }

    public String getBlockName() {
        blIdx = reqIdIdx + getLenOfRequestId() + 1;
        length += 1 + getLenOfBlockName();
        return getStringWithoutLengthUtf8(blIdx, getLenOfBlockName());
    }

    public int getLenOfBlockName() {
        return getByte(blIdx - 1);
    }

    public String getParentName() {
        parentIdx = blIdx + getLenOfBlockName() + 1;
        length += 1 + getLenOfParentName();
        return getStringWithoutLengthUtf8(parentIdx, getLenOfParentName());
    }

    public int getLenOfParentName() {
        return getByte(parentIdx - 1);
    }

    public long getLatency() {
        latencyIdx = parentIdx + getLenOfParentName();
        length += Long.BYTES;
        return getLong(latencyIdx);
    }

    public TraceBuffer putRequestId(String requestId) {
        reqIdIdx = length + 1;
        putByte(reqIdIdx - 1, (byte) requestId.length());
        putStringWithoutLengthUtf8(reqIdIdx, requestId);
        length += requestId.length() + 1;
        return this;
    }

    public TraceBuffer putRequestId(int idx, String requestId) {
        putByte(idx++, (byte) requestId.length());
        reqIdIdx = idx;
        putStringWithoutLengthUtf8(idx, requestId);
        length += requestId.length() + 1;
        return this;
    }

    public TraceBuffer putBlockName(String blockName) {
        blIdx = reqIdIdx + getLenOfRequestId();
        putByte(blIdx++, (byte) blockName.length());
        putStringWithoutLengthUtf8(blIdx, blockName);
        length += blockName.length() + 1;
        return this;
    }

    public TraceBuffer putBlockName(int idx, String blockName) {
        putByte(idx++, (byte) blockName.length());
        blIdx = idx;
        putStringWithoutLengthUtf8(idx, blockName);
        length += blockName.length() + 1;
        return this;
    }

    public TraceBuffer putParentName(String parentName) {
        parentIdx = blIdx + getLenOfBlockName();
        putByte(parentIdx++, (byte) parentName.length());
        putStringWithoutLengthUtf8(parentIdx, parentName);
        length += parentName.length() + 1;
        return this;
    }

    public TraceBuffer putParentName(int idx, String parentName) {
        putByte(idx++, (byte) parentName.length());
        parentIdx = idx;
        putStringWithoutLengthUtf8(idx, parentName);
        length += parentName.length() + 1;
        return this;
    }

    public TraceBuffer putLatency(long latency) {
        latencyIdx = parentIdx + getLenOfParentName();
        putLong(latencyIdx, latency);
        length += Long.BYTES;
        return this;
    }

    public TraceBuffer putLatency(int idx, long latency) {
        putLong(idx, latency);
        latencyIdx = idx;
        length += Long.BYTES;
        return this;
    }

    public TraceBuffer writeLatency(long latency) {
        latencyIdx = parentIdx + getLenOfParentName();
        putLong(latencyIdx, latency);
        return this;
    }

    public TraceBuffer writeLatency(int idx, long latency) {
        putLong(idx, latency);
        latencyIdx = idx;
        return this;
    }

    public TraceBuffer putBytesData(int idx, byte[] bytes, int offset, int length){
        putBytes(idx, bytes, offset, length);
        this.length = length;
        return this;
    }

    public int getLength() {
        return length;
    }

    public int skipOffset() {
        reqIdIdx = length + 1;
        return reqIdIdx;
    }

    public TraceBuffer wrap(TraceBuffer buffer) {
        putBytes(length, buffer.byteArray, 0, buffer.length);
        length += buffer.length;
        return this;
    }

    public void init() {
        reqIdIdx = 1;
        blIdx = reqIdIdx + getLenOfRequestId() + 1;
        parentIdx = blIdx + getLenOfBlockName() + 1;
        latencyIdx = parentIdx + getLenOfParentName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        TraceBuffer that = (TraceBuffer) obj;
        return reqIdIdx == that.reqIdIdx && blIdx == that.blIdx && getLenOfRequestId() == that.getLenOfRequestId() && getLenOfBlockName() == that.getLenOfBlockName()
                && getBlockName().equals(that.getBlockName()) && getRequestId().equals(that.getRequestId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRequestId(), getBlockName());
    }

//    @Override
//    public String toString() {
//        return "TraceBuffer{" +
//                "parentName=" + getParentName() +
//                ", requestId=" + getRequestId() +
//                ", blockName=" + getBlockName() +
//                ", latency=" + getLatency() +
//                '}';
//    }
}
