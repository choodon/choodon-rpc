package com.choodon.rpc.transport.netty.common;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public final class NettyChannelFactory<T extends Channel> implements ChannelFactory<T> {

	public static ChannelFactory<ServerChannel> NIO_SERVER = new NettyChannelFactory<>(TypeIO.NIO, KindChannel.SERVER);
	public static ChannelFactory<ServerChannel> NATIVE_SERVER = new NettyChannelFactory<>(TypeIO.NATIVE,
			KindChannel.SERVER);

	public static ChannelFactory<Channel> NIO_CLIENT = new NettyChannelFactory<>(TypeIO.NIO, KindChannel.CLIENT);
	public static ChannelFactory<Channel> NATIVE_CLIENT = new NettyChannelFactory<>(TypeIO.NATIVE, KindChannel.CLIENT);

	public NettyChannelFactory(TypeIO typeIO, KindChannel kindChannel) {
		this.typeIO = typeIO;
		this.kindChannel = kindChannel;
	}

	private final TypeIO typeIO;
	private final KindChannel kindChannel;

	@SuppressWarnings("unchecked")
	@Override
	public T newChannel() {
		switch (kindChannel) {
		case SERVER:
			switch (typeIO) {
			case NIO:
				return (T) new NioServerSocketChannel();
			case NATIVE:
				return (T) new EpollServerSocketChannel();
			default:
				throw new IllegalStateException("invalid type IO: " + typeIO);
			}
		case CLIENT:
			switch (typeIO) {
			case NIO:
				return (T) new NioSocketChannel();
			case NATIVE:
				return (T) new EpollSocketChannel();
			default:
				throw new IllegalStateException("invalid type IO: " + typeIO);
			}
		default:
			throw new IllegalStateException("invalid kind channel: " + kindChannel);
		}
	}

	public enum TypeIO {
		NIO, NATIVE
	}

	public enum KindChannel {
		SERVER, CLIENT
	}
}