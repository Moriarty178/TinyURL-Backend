package tiny_url.app.backend.component;

public class SnowflakeIdGenerator {
    private static final long EPOCH = 1672531200000L; // 2023-01-01 00:00:00 GMT
    private static final long TIMESTAMP_BITS = 41;
    private static final long DATACENTER_BITS = 5;
    private static final long MACHINE_BITS = 5;
    private static final long SEQUENCE_BITS = 12;

    private static final long MAX_DATACENTER_ID = (1L << DATACENTER_BITS) - 1;
    private static final long MAX_MACHINE_ID = (1L << MACHINE_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    // Số lượng bits cần dịch để lấy từng thành phần của Snowflake ID
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = MACHINE_BITS + SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = DATACENTER_BITS + MACHINE_BITS + SEQUENCE_BITS;

    private final long datacenterId;
    private final long machineId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_ID) {
            throw new IllegalArgumentException("Datacenter ID vượt quá giới hạn: " + MAX_DATACENTER_ID);
        }
        if (machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException("Machine ID vượt quá giới hạn: " + MAX_MACHINE_ID);
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Đợi thời gian đồng bộ!");
        }

        if (currentTimestamp == lastTimestamp) {// 1. Nếu curr = last, kiểu như có 1000 ông cùng create ID trong 1 millis
            sequence = (sequence + 1) & MAX_SEQUENCE; // 2. Khi đó sequence sẽ được tăng thêm 1 và "&" đảm bảo <= MAX_SEQUENCE. Nếu vi phạm squence sẽ = 0 - hay nói cách khác lượng ID mà có thể tạo ra trong 1 Millis đã hêt slot.
            if (sequence == 0) { // 3. Khi đã hết lượt tạo "sequence" trong Millis hiện tại (curr) thì phải đợi đến Millis tiếp theo (curr mới > last). Lúc trong Snowflake ID: curr sẽ là Millis mới để có thể tạo ra một SnowflakeId.(sequence = 0)
                while (currentTimestamp <= lastTimestamp) {
                    currentTimestamp = System.currentTimeMillis();
                }
            }
        } else { // có nghĩa là đã có một khoảng thời gian trôi qua kể từ khi Snowflake ID cuối cùng được tạo MÀ "sequence" chỉ đếm trong mỗi Millis nên nó cần ặt lại về 0, đsẵn sàng tạo ra một chuỗi Snowflake ID mới với sequence = [0, 4095].
            sequence = 0;
        }
        // Còn trong TH thông thường, luồng như sau:
        // Nếu curr = last -> tăng sequence -> tạo Snowflake ID: curr - epoch | datacenter | machine | sequence
        // Nếu curr > last -> sequence = 0 -> tạo Snowflake ID: curr - epoch | datacenter | machine | sequence

        //4. Cập nhật, thời gian cuối cùng 1 Snowflake ID được tạo ra
        lastTimestamp = currentTimestamp; // lastTime luôn chứa giá trị của thời gian cuối cùng 1 Snowflake ID được tạo ra. Nó được dùng  kiểm tra xem có cần tạo ra một Snowflake ID mới hay không trong tương lai.

        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT) |
                (datacenterId << DATACENTER_ID_SHIFT) |
                (machineId << MACHINE_ID_SHIFT) |
                sequence;
    }
}



//
//package tiny_url.app.backend.component;
//
//public class SnowflakeIdGenerator {
//    private static final long EPOCH = 1672531200000L; // 2023-01-01 00:00:00 GMT
//    private static final long MACHINE_ID_BITS = 10;
//    private static final long SEQUENCE_BITS = 12;
//
//    private static final long MAX_MACHINE_ID = (1L << MACHINE_ID_BITS) - 1;
//    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;
//
//    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
//    private static final long TIMESTAMP_SHIFT = MACHINE_ID_BITS + SEQUENCE_BITS;
//
//    private final long machineId;
//    private long lastTimestamp = -1L;
//    private long sequence = 0L;
//
//    public SnowflakeIdGenerator(long machineId) {
//        if (machineId > MAX_MACHINE_ID) {
//            throw new IllegalArgumentException("Machine ID vượt quá giới hạn: " + MAX_MACHINE_ID);
//        }
//        this.machineId = machineId;
//    }
//
//    public synchronized long nextId() {
//        long currentTimestamp = System.currentTimeMillis();
//
//        if (currentTimestamp < lastTimestamp) {
//            throw new RuntimeException("Clock moved backwards. Đợi thời gian đồng bộ!");
//        }
//
//        if (currentTimestamp == lastTimestamp) {
//            sequence = (sequence + 1) & MAX_SEQUENCE;
//            if (sequence == 0) {
//                while (currentTimestamp <= lastTimestamp) {
//                    currentTimestamp = System.currentTimeMillis();
//                }
//            }
//        } else {
//            sequence = 0;
//        }
//
//        lastTimestamp = currentTimestamp;
//        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT) | (machineId << MACHINE_ID_SHIFT) | sequence;
//    }
//}




//Toán tử:
//<< (Shif trái): được sử dụng để dịch các bits của một số nguyên sang trái vd: x= 5 (101) khi đó x << 3 <=> 101 000 = 40
//& (And): thực hiện phép toán AND bit-by-bit giữa 2 số nguyên. Kết quả là một số nguyên z có các bit được đặt thành 1 khi và chỉ khi 2 it tương ứng của x và y đều là 1.
//    vd: x = 5 (101), y = 3 (011)
//    z = x & y = (001) = 1
//| (Or) giống phép cộng: thực hiện phép toán Or bit-by-bit giữa 2 số nguyên. Kết quả là số nguyên z với các bit là 1 khi 1 trong 2 bit tương ứng của x, y là 1.
//    vd: x = 4 (100), y = 2 (010)
//    z = x | y = (110) = 6