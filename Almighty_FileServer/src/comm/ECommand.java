package comm;

/**
 *
 * @author Bryden
 */
public enum ECommand {
    READ(1),
    WRITE(2),
    DELETE(3),
    CREATE(4),
    MOVE(5),
    MONITOR(6),
    ACK(7),
    ERROR(8),
    UPDATE(9);

    private final int intCode;

    ECommand(int pIntCode) {
        intCode = pIntCode;
    }

    public int getCode() {
        return intCode;
    }

    public static ECommand getCommand(int pIntCode) {
        for (ECommand objType : ECommand.values()) {
            if (objType.getCode() == pIntCode) {
                return objType;
            }
        }
        return null;
    }
}
