package org.trident.net;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.trident.engine.task.TaskManager;
import org.trident.engine.task.impl.ClientExitTask;
import org.trident.net.packet.Packet;
import org.trident.net.packet.PacketExecutor;
import org.trident.world.entity.player.Player;

/**
 * An implementation of netty's {@link SimpleChannelUpstreamHandler} to handle
 * all of netty's incoming events.
 * @author Gabbe
 */
public class ChannelHandler extends SimpleChannelHandler {

	private Player player;
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) { 
	
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		if (e.getMessage() != null) {
			Object message = e.getMessage();
			if (message instanceof Player) {
				if(player == null)
					player = (Player) e.getMessage();
			} else if (message.getClass() == Packet.class) {
				Packet packet = (Packet) message;
				PacketExecutor.parse(player, packet);
			}
		}
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		if(player != null && !player.getAttributes().loggedOut() && !player.getAttributes().isClientExitTaskActive()) {
			player.getAttributes().setClientExitTaskActive(true);
			TaskManager.submit(new ClientExitTask(player));
		}
	}

}
