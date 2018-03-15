package org.trident.net.packet;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.trident.net.packet.Packet.PacketType;
import org.trident.util.IsaacRandom;

public class PacketDecoder extends FrameDecoder {
	
	private final IsaacRandom random;
	
	private int opcode = -1;
	private int size = -1;

	public PacketDecoder(IsaacRandom random) {
		this.random = random;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
		if (opcode == -1) {
			if (buffer.readable()) {
				int encryptedOpcode = buffer.readUnsignedByte();
				opcode = (encryptedOpcode - random.nextInt()) & 0xFF;
				size = PACKET_SIZES[opcode];
			} else {
				return null;
			}
		}
		if (size == -1) {
			if (buffer.readable()) {
				size = buffer.readUnsignedByte();
			} else {
				return null;
			}
		}
		if (buffer.readableBytes() >= size) {
			final byte[] data = new byte[size];
			buffer.readBytes(data);
			final ChannelBuffer payload = ChannelBuffers.buffer(size);
			payload.writeBytes(data);
			try {
				return new Packet(opcode, PacketType.FIXED, payload);
			} finally {
				opcode = -1;
				size = -1;
			}
		}
		return null;
	}

	/**
	 * The size of packets sent from client to the server 
	 * used to decode them.
	 */
	public final static int[] PACKET_SIZES = {
		0, 0, 2, 1, -1, -1, 4, 4, 4, 4, //0
		4, -1, -1, -1, 8, 0, 6, 2, 2, 0,  //10
		0, 2, 0, 6, 0, 12, 0, 0, 0, 0, //20
		9, 0, 0, 0, 0, 8, 4, 0, 0, 2,  //30
		2, 6, 0, 6, 0, -1, 0, 0, 0, 1, //40
		0, 0, 0, 12, 0, 0, 0, 8, 8, 0, //50
		-1, 8, 0, 0, 0, 0, 0, 0, 0, 0,  //60
		6, 0, 2, 2, 8, 6, 0, -1, 0, 6, //70
		-1, 0, 0, 0, 0, 1, 4, 6, 0, 0,  //80
		0, 0, 0, 0, 0, 3, 0, 0, -1, 0, //90
		0, 13, 0, -1, 0, 0, 0, 0, 0, 0,//100
		0, 0, 0, 0, 0, 0, 0, 6, 0, 0,  //110
		1, 0, 6, 0, 0, 0, -1, 0, 2, 6, //120
		0, 4, 6, 8, 0, 6, 0, 0, 0, 2,  //130
		0, 0, 0, 0, 0, 6, 0, 0, 0, 0,  //140
		0, 0, 1, 2, 0, 2, 6, 0, 0, 0,  //150
		0, 0, 0, 0, -1, -1, 0, 0, 0, 0,//160
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  //170
		0, 8, 0, 3, 0, 2, 0, 0, 8, 1,  //180
		0, 0, 12, 0, 0, 0, 0, 0, 0, 0, //190
		2, 0, 0, 0, 0, 0, 0, 0, 4, 0,  //200
		4, 0, 0, 0, 7, 8, 0, 0, 10, 0, //210
		0, 0, 0, 0, 0, 0, -1, 0, 6, 0, //220
		1, 0, 0, 0, 6, 0, 6, 8, 1, 0,  //230
		0, 4, 0, 0, 0, 0, -1, 0, -1, 4,//240
		0, 0, 6, 6, 0, 0           //250
	};
}
