package net.swordie.ms.connection;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.swordie.ms.ServerConfig;
import net.swordie.ms.util.Position;
import net.swordie.ms.util.Rect;
import net.swordie.ms.util.Util;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Created on 2/18/2017.
 */
public class InPacket extends Packet {
    private final ByteBuf byteBuf;

    /**
     * Creates a new InPacket with a given buffer.
     *
     * @param byteBuf The buffer this InPacket has to be initialized with.
     */
    public InPacket(ByteBuf byteBuf) {
        super(byteBuf.array());
        this.byteBuf = byteBuf.copy();
    }

    /**
     * Creates a new InPacket with no data.
     */
    public InPacket() {
        this(Unpooled.buffer());
    }

    /**
     * Creates a new InPacket with given data.
     *
     * @param data The data this InPacket has to be initialized with.
     */
    public InPacket(byte[] data) {
        this(Unpooled.copiedBuffer(data));
    }

    @Override
    public int getLength() {
        return byteBuf.readableBytes();
    }

    @Override
    public byte[] getData() {
        return byteBuf.array();
    }

    @Override
    public InPacket clone() {
        return new InPacket(byteBuf);
    }

    /**
     * Reads a single byte of the ByteBuf.
     *
     * @return The byte that has been read.
     */
    public byte decodeByte() {
        return byteBuf.readableBytes() >= 1 ? byteBuf.readByte() : 0;
    }

    public short decodeUByte() {
        return byteBuf.readableBytes() >= 1 ? byteBuf.readUnsignedByte() : 0;
    }

    /**
     * Reads an <code>amount</code> of bytes from the ByteBuf.
     *
     * @param amount The amount of bytes to read.
     * @return The bytes that have been read.
     */
    public byte[] decodeArr(int amount) {
        if (byteBuf.readableBytes() < amount) return new byte[0];

        byte[] arr = new byte[amount];
        IntStream.range(0, amount).forEach(i -> arr[i] = byteBuf.readByte());
        return arr;
    }

    /**
     * Reads an integer from the ByteBuf.
     *
     * @return The integer that has been read.
     */
    public int decodeInt() {
        return byteBuf.readableBytes() >= 4 ? byteBuf.readIntLE() : 0;
    }

    /**
     * Reads a short from the ByteBuf.
     *
     * @return The short that has been read.
     */
    public short decodeShort() {
        return byteBuf.readableBytes() >= 2 ? byteBuf.readShortLE() : 0;
    }

    /**
     * Reads a char array of a given length of this ByteBuf.
     *
     * @param amount The length of the char array
     * @return The char array as a String
     */
    public String decodeString(int amount) {
        if (byteBuf.readableBytes() < amount) return "";

        byte[] bytes = decodeArr(amount);
        return new String(bytes, ServerConfig.ASCII);

/*        char[] chars = new char[amount];
        for(int i = 0; i < amount; i++) {
            chars[i] = (char) bytes[i];
        }
        return String.valueOf(chars);*/
    }

    /**
     * Reads a String, by first reading a short, then reading a char array of that length.
     *
     * @return The char array as a String
     */
    public String decodeString() {
        if (byteBuf.readableBytes() < 2) return "";

        int amount = decodeShort();
        return decodeString(amount);
    }

    @Override
    public String toString() {
        return Util.readableByteArray(Arrays.copyOfRange(getData(), getData().length - getUnreadAmount(), getData().length)); // Substring after copy of range xd
    }


    /**
     * Reads and returns a long from this net.swordie.ms.connection.packet.
     *
     * @return The long that has been read.
     */
    public long decodeLong() {
        return byteBuf.readableBytes() >= 8 ? byteBuf.readLongLE() : 0;
    }

    /**
     * Reads a position (short x, short y) and returns this.
     *
     * @return The position that has been read.
     */
    public Position decodePosition() {
        return byteBuf.readableBytes() >= 4 ? new Position(decodeShort(), decodeShort()) : null;
    }

    /**
     * Reads a rectangle (short l, short t, short r, short b) and returns this.
     *
     * @return The rectangle that has been read.
     */
    public Rect decodeShortRect() {
        return byteBuf.readableBytes() >= 8 ? new Rect(decodePosition(), decodePosition()) : null;
    }

    /**
     * Reads a rectangle (int l, int t, int r, int b) and returns this.
     *
     * @return The rectangle that has been read.
     */
    public Position decodePositionInt() {
        return byteBuf.readableBytes() >= 8 ? new Position(decodeInt(), decodeInt()) : null;
    }

    /**
     * Returns the amount of bytes that are unread.
     *
     * @return The amount of bytes that are unread.
     */
    public int getUnreadAmount() {
        return byteBuf.readableBytes();
    }

    public void release() {
        byteBuf.release();
    }

    /**
     * Reads a rectangle (int l, int t, int r, int b) and returns this.
     *
     * @return The rectangle that has been read.
     */
    public Rect decodeIntRect() {
        return new Rect(decodePositionInt(), decodePositionInt());
    }
}
