package au.com.glassechidna.libpit;

import java.io.IOException;
import java.io.InputStream;

public class PitInputStream
{
	private InputStream inputStream;
	
	public PitInputStream(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}

	public int readInt() throws IOException
	{		
		return (inputStream.read() | (inputStream.read() << 8) | (inputStream.read() << 16)
			| (inputStream.read() << 24));
	}

	public short readShort() throws IOException
	{
		return ((short)(inputStream.read() | (inputStream.read() << 8)));
	}
	
	public int read(byte[] buffer, int offset, int length) throws IOException
	{
		return (inputStream.read(buffer, offset, length));
	}
}
