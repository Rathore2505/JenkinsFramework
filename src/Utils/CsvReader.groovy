package Utils
class CsvReader {
	List ReadCSVFile(String FileLoc, String header)
	{
		int row,col,rowCount,colCount = 0;
		def file = new File(FileLoc)
		def Arrayvalues = [];
		String[] lines = file.text.split('\n')
		rowCount = lines.size();
		for(int i=0; i<rowCount; i++)
		{
			if(lines[i].contains(header))
			{
				row = i+1
				break
			}
		}
		if(row > 1)
		{
			for(int j=row; j<rowCount; j++)
			{
				if(lines[j].contains("@Stage") || lines[j] == "<EOF>" || lines[j] == '\r' )
					break
				else
					Arrayvalues.add(lines[j].toString())
			}
			int arrLength = Arrayvalues.size()
			println"arrLength:$arrLength"
			for(int k=0; k<arrLength; k++)
			{
				Arrayvalues[k] = Arrayvalues[k].replace('(', ',')
				Arrayvalues[k] = Arrayvalues[k].replace(')', '')
				println"Arrayvalues[k] : "+Arrayvalues[k]
			}
		}

		return Arrayvalues
	}
	
	Map<String,List> argumentGetter(String header)
	{
		try
		{
			Map<String,List> InputMap = new HashMap<String,List>();
			List Arrayvalues = new ArrayList()
		    String[] tempkey = header.split('\\$')
			for(int i=1;i<tempkey.size();i++)
			{
				Arrayvalues.add(tempkey[i].toString())
			}
			for(String str in Arrayvalues)
			{
				List value = new ArrayList()
				String[] temp = str.split(',')
				String key = temp[0]
				for(int i=1;i<temp.size();i++)
				{
					value.add(temp[i])
				}
				InputMap.put(key, value)
			}
			return InputMap
		}
		catch(Exception ex)
		{
			println "Exception in Method argumentGetter:$ex"
		}
	}
}
