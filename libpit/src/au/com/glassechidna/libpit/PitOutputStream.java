package au.com.glassechidna.libpit;

import java.io.IOException;
import java.io.OutputStream;

public class PitOutputStream
{
	private OutputStream outputStream;
	private byte[] writeBuffer = new byte[4];
	
	public PitOutputStream(OutputStream outputStream)
	{
		this.outputStream = outputStream;
	}

	public void writeInt(int value) throws IOException
	{
		writeBuffer[0] = (byte)(value & 0xFF);
		writeBuffer[1] = (byte)((value >> 8) & 0xFF);
		writeBuffer[2] = (byte)((value >> 16) & 0xFF);
		writeBuffer[3] = (byte)(value >> 24);
		
		outputStream.write(writeBuffer);
	}

	public void writeShort(short value) throws IOException
	{
		writeBuffer[0] = (byte)(value & 0xFF);
		writeBuffer[1] = (byte)(value >> 8);
		
		outputStream.write(writeBuffer, 0, 2);
	}
	
	public void write(byte[] buffer, int offset, int length) throws IOException
	{
		outputStream.write(buffer, offset, length);
	}
}
