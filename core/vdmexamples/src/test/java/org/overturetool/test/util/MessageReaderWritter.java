/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others. Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version. Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public
 * License along with Overture. If not, see <http://www.gnu.org/licenses/>. The Overture Tool web-site:
 * http://overturetool.org/
 *******************************************************************************/
package org.overturetool.test.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.overturetool.test.framework.examples.IMessage;
import org.overturetool.test.framework.examples.Message;

public class MessageReaderWritter
{

	enum MsgType
	{
		Warning, Error
	};

	public static String WARNING_LABEL = "WARNING";
	public static String ERROR_LABEL = "ERROR";

	final Set<IMessage> errors = new HashSet<IMessage>();
	final Set<IMessage> warnings = new HashSet<IMessage>();
	final File file;

	public MessageReaderWritter(File file)
	{
		this.file = file;

	}

	public MessageReaderWritter(String path)
	{
		this(new File(path));
	}

	public void setWarningsAndErrors(Set<IMessage> errors,
			Set<IMessage> warnings)
	{
		this.errors.clear();
		this.warnings.clear();
		this.errors.addAll(errors);
		this.warnings.addAll(warnings);
	}

	public Set<IMessage> getErrors()
	{
		return errors;
	}

	public Set<IMessage> getWarnings()
	{
		return warnings;
	}

	public boolean exists()
	{
		return file.exists();
	}

	public boolean load()
	{
		errors.clear();
		warnings.clear();

		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line;
			while ((line = in.readLine()) != null)
			{
				MsgType type = null;
				if (line.startsWith(ERROR_LABEL))
				{
					type = MsgType.Error;
					line = line.substring(ERROR_LABEL.length() + 1);
				} else if (line.startsWith(WARNING_LABEL))
				{
					type = MsgType.Warning;
					line = line.substring(WARNING_LABEL.length() + 1);
				} else
				{
					return false;
				}

				String[] splitLine = line.split(":");

				if (splitLine.length != 3)
				{
					return false;
				}

				int number = Integer.parseInt(splitLine[0]);

				String[] position = splitLine[1].split(",");

				if (position.length != 2)
				{
					return false;
				}

				int startLine = Integer.parseInt(position[0]);
				int startCol = Integer.parseInt(position[1]);

				String message = splitLine[2];

				IMessage msg = new Message(number, startLine, startCol, message);

				switch (type)
				{
					case Error:
						errors.add(msg);
						break;
					case Warning:
						warnings.add(msg);
						break;
				}

			}

		} catch (FileNotFoundException e)
		{
			return false;
		} catch (IOException e)
		{
			return false;
		}

		return true;
	}

	public boolean save()
	{
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			for (IMessage warning : warnings)
			{
				StringBuffer sb = new StringBuffer();
				sb.append(WARNING_LABEL);
				sb.append(":");
				sb.append(warning.getNumber());
				sb.append(":");
				sb.append(warning.getLine());
				sb.append(",");
				sb.append(warning.getCol());
				sb.append(":");
				sb.append(warning.getMessage());
				out.write(sb.toString());
				out.newLine();
			}

			for (IMessage error : errors)
			{
				StringBuffer sb = new StringBuffer();
				sb.append(ERROR_LABEL);
				sb.append(":");
				sb.append(error.getNumber());
				sb.append(":");
				sb.append(error.getLine());
				sb.append(",");
				sb.append(error.getCol());
				sb.append(":");
				sb.append(error.getMessage());
				out.write(sb.toString());
				out.newLine();
			}

			out.flush();
			out.close();
		} catch (IOException e)
		{
			return false;
		}

		return true;
	}

}
