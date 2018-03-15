package org.trident.net;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.Timer;
import org.trident.net.login.LoginDecoder;
import org.trident.net.login.LoginEncoder;

/**
 * The channel's events
 * @author Gabbe
 */
public class PipelineFactory implements ChannelPipelineFactory {

	private final Timer timer;

	public PipelineFactory(Timer timer) {
		this.timer = timer;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		final ChannelPipeline pipeline = new DefaultChannelPipeline();
		pipeline.addLast("timeout", new ReadTimeoutHandler(timer, 10));
		pipeline.addLast("encoder", new LoginEncoder()); //Replaced by the PacketEncoder after login
		pipeline.addLast("decoder", new LoginDecoder()); //Replaced by the PacketDecoder after login
		pipeline.addLast("handler", new ChannelHandler());
		return pipeline;
	}
}
