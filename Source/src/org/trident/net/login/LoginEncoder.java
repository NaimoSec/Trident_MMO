package org.trident.net.login;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.trident.net.packet.Packet;

/**
 * Handles login encoding requests
 * @author Gabbe
 */
public class LoginEncoder extends OneToOneEncoder {

	@Override
	protected Object encode(ChannelHandlerContext context, Channel channel, Object message) throws Exception {
		return ((Packet)message).getBuffer();
	}

}
