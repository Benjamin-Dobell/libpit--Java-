package au.com.glassechidna.libpit;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PitData
{
	public static final int FILE_IDENTIFIER = 0x12349876;
	public static final int HEADER_DATA_SIZE = 28;

	private int entryCount; // 0x04
	private int unknown1;   // 0x08
	private int unknown2;   // 0x0C

	private short unknown3; // 0x10 (7508 = I9000, 7703 = I9100 & P1000)?
	private short unknown4; // 0x12 (Always 65, probably flags of some sort)

	private short unknown5; // 0x14
	private short unknown6; // 0x16

	private short unknown7; // 0x18
	private short unknown8; // 0x1A

	// Entries start at 0x1C
	private ArrayList<PitEntry> entries = new ArrayList<PitEntry>();

	public PitData()
	{
	}

	public boolean unpack(PitInputStream pitInputStream)
	{
		try
		{			
			if (pitInputStream.readInt() != FILE_IDENTIFIER)
				return (false);

			entries.clear();
	
			entryCount = pitInputStream.readInt();
	
			entries.ensureCapacity(entryCount);
	
			unknown1 = pitInputStream.readInt();
			unknown2 = pitInputStream.readInt();
	
			unknown3 = pitInputStream.readShort();
			unknown4 = pitInputStream.readShort();
	
			unknown5 = pitInputStream.readShort();
			unknown6 = pitInputStream.readShort();
	
			unknown7 = pitInputStream.readShort();
			unknown8 = pitInputStream.readShort();
	
			int integerValue;
			byte[] buffer = new byte[Math.max(PitEntry.PARTITION_NAME_MAX_LENGTH, PitEntry.FILENAME_MAX_LENGTH)];
			char[] partitionName = new char[PitEntry.PARTITION_NAME_MAX_LENGTH];
			char[] filename = new char[PitEntry.FILENAME_MAX_LENGTH];
	
			for (int i = 0; i < entryCount; i++)
			{
				PitEntry entry = new PitEntry();
				entries.add(entry);
	
				integerValue = pitInputStream.readInt();
				entry.setUnused((integerValue != 0) ? true : false);
	
				integerValue = pitInputStream.readInt();
				entry.setPartitionType(integerValue);
	
				integerValue = pitInputStream.readInt();
				entry.setPartitionIdentifier(integerValue);
	
				integerValue = pitInputStream.readInt();
				entry.setPartitionFlags(integerValue);
	
				integerValue = pitInputStream.readInt();
				entry.setUnknown1(integerValue);
	
				integerValue = pitInputStream.readInt();
				entry.setPartitionBlockSize(integerValue);
	
				integerValue = pitInputStream.readInt();
				entry.setPartitionBlockCount(integerValue);
	
				integerValue = pitInputStream.readInt();
				entry.setUnknown2(integerValue);
	
				integerValue = pitInputStream.readInt();
				entry.setUnknown3(integerValue);
				
				pitInputStream.read(buffer, 0, PitEntry.PARTITION_NAME_MAX_LENGTH);
				
				int length = 0;
				for (int j = 0; j < PitEntry.PARTITION_NAME_MAX_LENGTH; j++)
				{
					if (buffer[j] == 0)
					{
						length = j;
						break;
					}
					
					partitionName[j] = (char)buffer[j];
				}
				
				entry.setPartitionName(new String(partitionName, 0, length));
				
				pitInputStream.read(buffer, 0, PitEntry.FILENAME_MAX_LENGTH);
				
				length = 0;
				for (int j = 0; j < PitEntry.FILENAME_MAX_LENGTH; j++)
				{
					if (buffer[j] == 0)
					{
						length = j;
						break;
					}

					filename[j] = (char)buffer[j];
				}
				
				entry.setFilename(new String(filename, 0, length));
			}
	
			return (true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return (false);
		}
	}

	public boolean pack(DataOutputStream dataOutputStream)
	{
		try
		{
			dataOutputStream.writeInt(FILE_IDENTIFIER);
	
			dataOutputStream.writeInt(entryCount);
	
			dataOutputStream.writeInt(unknown1);
			dataOutputStream.writeInt(unknown1);
			
			dataOutputStream.writeShort(unknown3);
			dataOutputStream.writeShort(unknown4);
			
			dataOutputStream.writeShort(unknown5);
			dataOutputStream.writeShort(unknown6);
			
			dataOutputStream.writeShort(unknown7);
			dataOutputStream.writeShort(unknown8);
	
			for (int i = 0; i < entryCount; i++)
			{
				PitEntry entry = entries.get(i);
				
				dataOutputStream.writeInt((entry.getUnused()) ? 1 : 0);
	
				dataOutputStream.writeInt(entry.getPartitionType());
				dataOutputStream.writeInt(entry.getPartitionIdentifier());
				dataOutputStream.writeInt(entry.getPartitionFlags());
	
				dataOutputStream.writeInt(entry.getUnknown1());
	
				dataOutputStream.writeInt(entry.getPartitionBlockSize());
				dataOutputStream.writeInt(entry.getPartitionBlockCount());
	
				dataOutputStream.writeInt(entry.getUnknown2());
				dataOutputStream.writeInt(entry.getUnknown3());
	
				byte[] partitionName = entry.getPartitionName().getBytes();
				dataOutputStream.write(partitionName);
				
				if (partitionName.length < PitEntry.PARTITION_NAME_MAX_LENGTH - 1)
				{
					byte[] blankData = new byte[PitEntry.PARTITION_NAME_MAX_LENGTH - 1 - partitionName.length];
					dataOutputStream.write(blankData);
				}
				
				byte[] filename = entry.getFilename().getBytes();
				dataOutputStream.write(filename);
				
				if (filename.length < PitEntry.PARTITION_NAME_MAX_LENGTH - 1)
				{
					byte[] blankData = new byte[PitEntry.PARTITION_NAME_MAX_LENGTH - 1 - filename.length];
					dataOutputStream.write(blankData);
				}
			}
			
			return (true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return (false);
		}
	}

	public boolean matches(PitData otherPitData)
	{
		if (entryCount == otherPitData.entryCount && unknown1 == otherPitData.unknown1 && unknown2 == otherPitData.unknown2
			&& unknown3 == otherPitData.unknown3 && unknown4 == otherPitData.unknown4 && unknown5 == otherPitData.unknown5
			&& unknown6 == otherPitData.unknown6 && unknown7 == otherPitData.unknown7 && unknown8 == otherPitData.unknown8)
		{
			for (int i = 0; i < entryCount; i++)
			{
				if (!entries.get(i).matches(otherPitData.entries.get(i)))
					return (false);
			}

			return (true);
		}
		else
		{
			return (false);
		}
	}

	public void clear()
	{
		entryCount = 0;

		unknown1 = 0;
		unknown2 = 0;

		unknown3 = 0;
		unknown4 = 0;

		unknown5 = 0;
		unknown6 = 0;

		unknown7 = 0;
		unknown8 = 0;

		entries.clear();
	}

	public PitEntry getEntry(int index)
	{
		return (entries.get(index));
	}

	public PitEntry findEntry(String partitionName)
	{
		for (int i = 0; i < entries.size(); i++)
		{
			PitEntry entry = entries.get(i);
			
			if (!entry.getUnused() && entry.getPartitionName().equals(partitionName))
				return (entry);
		}

		return (null);
	}

	public PitEntry findEntry(int partitionIdentifier)
	{
		for (int i = 0; i < entries.size(); i++)
		{
			PitEntry entry = entries.get(i);
			
			if (!entry.getUnused() && entry.getPartitionIdentifier() == partitionIdentifier)
				return (entry);
		}

		return (null);
	}
	
	public void addEntry(PitEntry entry)
	{
		entries.add(entryCount++, entry);
	}

	public int getEntryCount()
	{
		return (entryCount);
	}

	public int getUnknown1()
	{
		return (unknown1);
	}

	public int getUnknown2()
	{
		return (unknown2);
	}

	public short getUnknown3()
	{
		return (unknown3);
	}

	public short getUnknown4()
	{
		return (unknown4);
	}

	public short getUnknown5() 
	{
		return (unknown5);
	}

	public short getUnknown6()
	{
		return (unknown6);
	}

	public short getUnknown7()
	{
		return (unknown7);
	}

	public short getUnknown8()
	{
		return (unknown8);
	}
}
