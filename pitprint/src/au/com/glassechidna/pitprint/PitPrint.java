package au.com.glassechidna.pitprint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import au.com.glassechidna.libpit.PitData;
import au.com.glassechidna.libpit.PitEntry;
import au.com.glassechidna.libpit.PitInputStream;

public class PitPrint
{
	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			System.out.print("Usage:\npitprint <filename>\n");
			return;
		}
				
		try
		{
			PitInputStream pitInputStream = new PitInputStream(new FileInputStream(args[0]));
			
			PitData pitData = new PitData();
			
			if (pitData.unpack(pitInputStream))
			{
				System.out.printf("Entry Count: %d\n", pitData.getEntryCount());

				System.out.printf("Unknown 1: %d\n", pitData.getUnknown1());
				System.out.printf("Unknown 2: %d\n", pitData.getUnknown2());
				System.out.printf("Unknown 3: %d\n", pitData.getUnknown3());
				System.out.printf("Unknown 4: %d\n", pitData.getUnknown4());
				System.out.printf("Unknown 5: %d\n", pitData.getUnknown5());
				System.out.printf("Unknown 6: %d\n", pitData.getUnknown6());
				System.out.printf("Unknown 7: %d\n", pitData.getUnknown7());
				System.out.printf("Unknown 8: %d\n", pitData.getUnknown8());

				for (int i = 0; i < pitData.getEntryCount(); i++)
				{
					PitEntry entry = pitData.getEntry(i);

					System.out.printf("\n\n--- Entry #%d ---\n", i);
					System.out.printf("Unused: %s\n", (entry.getUnused()) ? "Yes" : "No");

					String partitionTypeText = "Unknown";

					if (entry.getPartitionType() == PitEntry.PARTITION_TYPE_RFS)
						partitionTypeText = "RFS";
					else if (entry.getPartitionType() == PitEntry.PARTITION_TYPE_EXT4)
						partitionTypeText = "EXT4";

					System.out.printf("Partition Type: %d (%s)\n", entry.getPartitionType(), partitionTypeText);

					System.out.printf("Partition Identifier: %d\n", entry.getPartitionIdentifier());

					System.out.printf("Partition Flags: %d (", entry.getPartitionFlags());

					if ((entry.getPartitionFlags() & PitEntry.PARTITION_FLAG_WRITE) != 0)
						System.out.printf("R/W");
					else
						System.out.printf("R");

					System.out.printf(")\n");

					System.out.printf("Unknown 1: %d\n", entry.getUnknown1());

					System.out.printf("Partition Block Size: %d\n", entry.getPartitionBlockSize());
					System.out.printf("Partition Block Count: %d\n", entry.getPartitionBlockCount());

					System.out.printf("Unknown 2: %d\n", entry.getUnknown2());
					System.out.printf("Unknown 3: %d\n", entry.getUnknown3());

					System.out.printf("Partition Name: %s\n", entry.getPartitionName());
					System.out.printf("Filename: %s\n", entry.getFilename());
				}

				System.out.println("");
			}
			else
			{
				System.out.println("\"" + args[0] + "\" is not a valid PIT file!");
			}
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found: " + args[0]);
		}
	}
}
