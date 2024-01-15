import net.openhft.chronicle.bytes.HeapBytesStore;
import net.openhft.chronicle.bytes.OnHeapBytes;

import java.util.Objects;

/**
 * @author lzn
 * @date 2023/10/14 16:55
 * @description
 */
public class MetricBuffer extends OnHeapBytes {

    public MetricBuffer(int initialCapacity) throws IllegalStateException {
        super(HeapBytesStore.wrap(new byte[initialCapacity]), false);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        MetricBuffer that = (MetricBuffer) obj;
        return Objects.equals(readUtf8(), that.readUtf8()) && Objects.equals(readUtf8(), that.readUtf8()) && readInt() == that.readInt();
    }

    @Override
    public int hashCode() {
        return Objects.requireNonNull(readUtf8()).hashCode() + Objects.requireNonNull(readUtf8()).hashCode() + readInt();
    }
}
